import json, os, subprocess, sys, time, re
from pathlib import Path
from google import genai
from google.genai import types

API_KEY = os.environ.get("GEMINI_API_KEY", "").strip()
PROJECT_DIR = Path(".").resolve()
if not API_KEY: 
    print("❌ Missing API KEY")
    sys.exit(1)

client = genai.Client(api_key=API_KEY)

# Models priority
models_pool = ["gemini-3.8-flash", "gemini-pro-latest", "gemini-3.5-flash-lite"]
model_idx = 0

allowed_exts = {".kt", ".xml", ".gradle"}
blocked_dirs = {".git", ".github", "build", "gradle"}

def get_current_files():
    files_data = []
    for p in PROJECT_DIR.rglob("*"):
        if p.is_file() and not set(p.relative_to(PROJECT_DIR).parts).intersection(blocked_dirs) and p.suffix.lower() in allowed_exts:
            try:
                files_data.append(f"--- {p.relative_to(PROJECT_DIR).as_posix()} ---\n{p.read_text(encoding='utf-8')[:1000]}")
            except: pass
    return "\n".join(files_data[:30])

initial_prompt = f"""
Task: {os.environ.get('ISSUE_TITLE')}
Details: {os.environ.get('ISSUE_BODY')}

Current Project Files (Context):
{get_current_files()}

CRITICAL RULES:
1. Return ONLY valid JSON format: {{"summary": "...", "files": [{{"filepath": "...", "content": "..."}}]}}
2. Ensure all Kotlin imports are present. Ensure XML syntax is flawless.
"""

current_prompt = initial_prompt
max_iterations = 15
success = False

for attempt in range(1, max_iterations + 1):
    model_name = models_pool[model_idx % len(models_pool)]
    print(f"\n==================================================")
    print(f"🔄 [ATTEMPT {attempt}/{max_iterations}] Generating code using {model_name}...")
    print(f"==================================================")
    
    try:
        res = client.models.generate_content(
            model=model_name,
            contents=current_prompt,
            config=types.GenerateContentConfig(response_mime_type="application/json", temperature=0.1)
        )
        
        clean_text = res.text.strip()
        if clean_text.startswith("```json"): clean_text = clean_text[7:]
        if clean_text.startswith("```"): clean_text = clean_text[3:]
        if clean_text.endswith("```"): clean_text = clean_text[:-3]
        
        data = json.loads(clean_text.strip())
        
        # 🔥 FIX: BULLETPROOF JSON PARSER
        files_to_process = []
        if isinstance(data, list):
            files_to_process = data  # Flash-Lite sometimes returns a direct list
        elif isinstance(data, dict):
            files_to_process = data.get("files", [])
        
        if not files_to_process:
            raise ValueError("No files found in JSON output")

        # Apply Files
        for item in files_to_process:
            target = (PROJECT_DIR / item["filepath"]).resolve()
            if target.name == "AndroidManifest.xml":
                item["content"] = re.sub(r'\s*package="[^"]*"', '', item["content"])
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(item["content"], encoding="utf-8")
            print(f"📝 AI modified: {item['filepath']}")
            
        # Run Gradle Build
        print("🔨 Compiling AI Code...")
        build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
        
        if build.returncode == 0:
            print("✅ SUCCESS! The AI code is completely error-free.")
            success = True
            break
        else:
            error_log = build.stderr[:2000]
            print(f"❌ BUILD FAILED! Sending error back to AI for correction.")
            
            current_prompt = f"""
            Your previous code caused this Gradle Build Error. YOU MUST FIX IT.
            ERROR LOG:
            {error_log}
            Analyze the error and return the FULLY CORRECTED files in JSON format.
            """
            subprocess.run(["git", "checkout", "--", "."])
            time.sleep(5)
            
    except Exception as e:
        err_str = str(e)
        print(f"⚠️ ERROR: {err_str}")
        if "429" in err_str or "503" in err_str or "Quota" in err_str:
            print("🔄 Changing model due to traffic/quota...")
            model_idx += 1
            time.sleep(15)
        else:
            print("🔄 JSON Error or format issue. AI made a formatting mistake. Retrying...")
            current_prompt = "Your last output was invalid JSON. Return EXACTLY this structure: {\"summary\": \"...\", \"files\": [{\"filepath\": \"...\", \"content\": \"...\"}]}"
            subprocess.run(["git", "checkout", "--", "."])
            time.sleep(5)
            
if success:
    sys.exit(0)
else:
    print("❌ Maximum AI attempts reached without success.")
    sys.exit(1)
