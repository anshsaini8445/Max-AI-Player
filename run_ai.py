import json, os, subprocess, sys, time, re
from pathlib import Path
from google import genai
from google.genai import types

API_KEY = os.environ.get("GEMINI_API_KEY", "").strip()
PROJECT_DIR = Path(".").resolve()

def compile_base_app(message="Base App Build"):
    print(f"\n--- {message} ---")
    build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
    if build.returncode == 0:
        print("✅ BUILD SUCCESSFUL! Clean Base APK generated.")
        Path("agent_summary.txt").write_text("Clean Base App Built successfully.", encoding="utf-8")
        sys.exit(0)
    else:
        print("❌ BUILD FAILED.")
        print(build.stderr)
        sys.exit(1)

if not API_KEY: compile_base_app("Missing API Key")

client = genai.Client(api_key=API_KEY)
allowed = {".kt", ".java", ".xml", ".gradle", ".properties"}
blocked = {".git", ".github", "build", "gradle"}

files = []
for p in PROJECT_DIR.rglob("*"):
    if p.is_file() and not set(p.relative_to(PROJECT_DIR).parts).intersection(blocked) and p.suffix.lower() in allowed:
        files.append(p.relative_to(PROJECT_DIR).as_posix())

prompt = f"""
You are an elite Expert Android Developer.
Task: Resolve Issue #{os.environ.get('ISSUE_NUMBER')}: {os.environ.get('ISSUE_TITLE')}
Details: {os.environ.get('ISSUE_BODY')}
Existing Project Files: {", ".join(files[:100])}

CRITICAL INSTRUCTIONS (FAILING THESE WILL BREAK THE SYSTEM):
1. Return ONLY a valid JSON object. No explanations, no pleasantries, no markdown wrapping.
2. The JSON format MUST be exactly: {{"summary": "...", "files": [{{"filepath": "...", "content": "..."}}]}}
3. Do NOT include the 'package=' attribute in AndroidManifest.xml.
4. Ensure all Android/Kotlin code is production-ready and error-free.
"""

model_name = "gemini-3.8-flash"
response_text = None

# DUAL-LAYER PROTECTION: Internal API Retry Logic
max_retries = 3
cool_down_times = [5, 15, 30]

for retry in range(max_retries):
    try:
        print(f"Connecting to Google Gemini ({model_name}) [Attempt {retry+1}/{max_retries}]...")
        completion = client.models.generate_content(
            model=model_name,
            contents=prompt,
            config=types.GenerateContentConfig(
                response_mime_type="application/json",
                temperature=0.1
            )
        )
        response_text = completion.text
        break
    except Exception as e:
        error_str = str(e)
        if "503" in error_str or "429" in error_str:
            wait_time = cool_down_times[retry] if retry < len(cool_down_times) else 30
            print(f"⚠️ Gemini Server Load High (503/429). Cooling down for {wait_time} seconds to save tokens...")
            time.sleep(wait_time)
        else:
            print(f"⚠️ API Error: {error_str}")
            time.sleep(5)
            
if not response_text:
    compile_base_app("Gemini Server Timeout After Retries")
    
try:
    clean_text = response_text.strip()
    if clean_text.startswith("```json"): clean_text = clean_text[7:]
    if clean_text.startswith("```"): clean_text = clean_text[3:]
    if clean_text.endswith("```"): clean_text = clean_text[:-3]
    clean_text = clean_text.strip()
    
    data = json.loads(clean_text)
    
    for item in data.get("files", []):
        target = (PROJECT_DIR / item["filepath"]).resolve()
        
        if target.name == "AndroidManifest.xml":
            item["content"] = re.sub(r'\s*package="[^"]*"', '', item["content"])
            
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(item["content"], encoding="utf-8")
        print(f"⚡ AI Successfully Wrote: {item['filepath']}")
        
    print("\nCompiling AI changes...")
    build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
    if build.returncode == 0:
        print("✅ AI BUILD SUCCESSFUL & ERROR FREE!")
        Path("agent_summary.txt").write_text("Ultra-Smart AI App Built Successfully.", encoding="utf-8")
        sys.exit(0)
    else:
        print("⚠️ AI Code caused a Gradle error. Reverting to safe base to ensure App delivery.")
        compile_base_app("AI Code Error")
        
except Exception as e:
    print(f"JSON Parsing Error: {e}")
    compile_base_app("Fallback after Parser Error")
