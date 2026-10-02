import json, os, subprocess, sys, time, re
from pathlib import Path
from google import genai
from google.genai import types

API_KEY = os.environ.get("GEMINI_API_KEY", "").strip()
PROJECT_DIR = Path(".").resolve()

if not API_KEY:
    print("No API Key.")
    sys.exit(1)

client = genai.Client(api_key=API_KEY)

allowed = {".kt", ".xml", ".gradle"}
blocked = {".git", ".github", "build", "gradle"}
files = [p.relative_to(PROJECT_DIR).as_posix() for p in PROJECT_DIR.rglob("*") if p.is_file() and not set(p.relative_to(PROJECT_DIR).parts).intersection(blocked) and p.suffix.lower() in allowed]

initial_prompt = f"""
Task: {os.environ.get('ISSUE_TITLE')}
Details: {os.environ.get('ISSUE_BODY')}
Files: {", ".join(files[:50])}

CRITICAL RULES:
1. Return ONLY a valid JSON object: {{"summary": "...", "files": [{{"filepath": "...", "content": "..."}}]}}
2. Ensure the XML and Kotlin code is syntax error-free. Do NOT invent drawable icons. Use standard android.R.drawable icons or text.
"""

current_prompt = initial_prompt
# 🚨 FIX: Switched to 3.8-Flash to bypass the 429 Limit Error on Pro
model_name = "gemini-3.8-flash" 

max_attempts = 3
success = False

for attempt in range(max_attempts):
    print(f"\n🧠 [AI ATTEMPT {attempt + 1}/{max_attempts}] AI is thinking and writing code using {model_name}...")
    
    response_text = None
    try:
        completion = client.models.generate_content(
            model=model_name,
            contents=current_prompt,
            config=types.GenerateContentConfig(response_mime_type="application/json", temperature=0.1)
        )
        response_text = completion.text
    except Exception as e:
        print(f"⚠️ API Error: {e}. Cooling down for 15s...")
        time.sleep(15)
        continue
        
    if not response_text:
        continue
        
    try:
        clean_text = response_text.strip()
        if clean_text.startswith("```json"): clean_text = clean_text[7:]
        if clean_text.startswith("```"): clean_text = clean_text[3:]
        if clean_text.endswith("```"): clean_text = clean_text[:-3]
        data = json.loads(clean_text.strip())
        
        for item in data.get("files", []):
            target = (PROJECT_DIR / item["filepath"]).resolve()
            if target.name == "AndroidManifest.xml":
                item["content"] = re.sub(r'\s*package="[^"]*"', '', item["content"])
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(item["content"], encoding="utf-8")
            print(f"📝 AI Wrote: {item['filepath']}")
            
        print("🔨 Compiling AI Code...")
        build = subprocess.run(["./gradlew", "assembleDebug", "--no-daemon", "-x", "test"], capture_output=True, text=True)
        
        if build.returncode == 0:
            print("✅ AI BUILD SUCCESSFUL! The code works flawlessly.")
            success = True
            break
        else:
            error_log = build.stderr[:1500]
            print(f"❌ Build Failed. AI made a mistake. Sending error back to AI for auto-fix...")
            current_prompt = f"""
            Your previous code caused this Gradle Build Error:
            {error_log}
            
            PLEASE FIX THE ERROR. Return the fully corrected files in the exact JSON format. Pay close attention to XML syntax and Kotlin imports.
            """
            subprocess.run(["git", "checkout", "--", "."]) 
            time.sleep(5)
            
    except Exception as e:
        print(f"⚠️ JSON Parsing error. Retrying... {e}")
        current_prompt = f"Your last output was invalid JSON. Please return STRICT JSON format only."
        time.sleep(5)
        
if success:
    sys.exit(0)
else:
    print("❌ AI failed to fix the code after 3 attempts.")
    sys.exit(1)
