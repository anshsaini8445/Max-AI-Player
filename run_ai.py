import json, os, subprocess, sys, time, re
from pathlib import Path
from google import genai
from google.genai import types

API_KEY = os.environ.get("GEMINI_API_KEY", "").strip()
PROJECT_DIR = Path(".").resolve()

def compile_base_app(message="Fallback to Working Video Player"):
    print(f"\n--- {message} ---")
    build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
    if build.returncode == 0:
        print("✅ BUILD SUCCESSFUL! Working Video Player APK generated.")
        Path("agent_summary.txt").write_text("Working Video Player App Built successfully.", encoding="utf-8")
        sys.exit(0)
    else:
        sys.exit(1)

if not API_KEY: compile_base_app("Missing API Key")

client = genai.Client(api_key=API_KEY)
allowed = {".kt", ".xml"}
blocked = {".git", ".github", "build", "gradle"}

files = []
for p in PROJECT_DIR.rglob("*"):
    if p.is_file() and not set(p.relative_to(PROJECT_DIR).parts).intersection(blocked) and p.suffix.lower() in allowed:
        files.append(p.relative_to(PROJECT_DIR).as_posix())

prompt = f"""
Task: Resolve Issue #{os.environ.get('ISSUE_NUMBER')}: {os.environ.get('ISSUE_TITLE')}
Details: {os.environ.get('ISSUE_BODY')}
Files: {", ".join(files[:50])}

CRITICAL RULES:
1. Return ONLY a valid JSON object: {{"summary": "...", "files": [{{"filepath": "...", "content": "..."}}]}}
2. Focus ONLY on styling activity_main.xml (colors, buttons). 
3. DO NOT change the PlayerView ID (@+id/player_view) in XML or MainActivity.kt.
4. DO NOT write or modify AndroidManifest.xml.
"""

model_name = "gemini-3.8-flash"
response_text = None

for retry in range(3):
    try:
        completion = client.models.generate_content(
            model=model_name,
            contents=prompt,
            config=types.GenerateContentConfig(response_mime_type="application/json", temperature=0.1)
        )
        response_text = completion.text
        break
    except Exception as e:
        time.sleep(10)
            
if not response_text:
    compile_base_app("Gemini Server Timeout")
    
try:
    clean_text = response_text.strip()
    if clean_text.startswith("```json"): clean_text = clean_text[7:]
    if clean_text.startswith("```"): clean_text = clean_text[3:]
    if clean_text.endswith("```"): clean_text = clean_text[:-3]
    data = json.loads(clean_text.strip())
    
    for item in data.get("files", []):
        target = (PROJECT_DIR / item["filepath"]).resolve()
        if target.name != "AndroidManifest.xml":
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(item["content"], encoding="utf-8")
        
    build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
    if build.returncode == 0:
        print("✅ AI BUILD SUCCESSFUL!")
        Path("agent_summary.txt").write_text("AI Styled App Built Successfully.", encoding="utf-8")
        sys.exit(0)
    else:
        print("⚠️ AI Code caused error. Reverting to Working Video Player.")
        subprocess.run(["git", "checkout", "--", "."])
        compile_base_app("Reverted to base player due to AI error")
        
except Exception as e:
    compile_base_app("JSON Error Fallback")
