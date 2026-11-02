import os
import subprocess
import sys
import shutil

ROOT_DIR = r"E:\AI Projects\Raqeem"
BRAG_DIR = os.path.join(ROOT_DIR, "brag-output")
os.makedirs(BRAG_DIR, exist_ok=True)
TEMP_DIR = os.path.join(BRAG_DIR, "temp_segments")
os.makedirs(TEMP_DIR, exist_ok=True)

AUDIO_TRACK = os.path.join(ROOT_DIR, ".agents", "skills", "brag", "assets", "music", "happy-beats-business-moves-vol-10-by-ende-dot-app.mp3")

# Define scenes
scenes = [
    {
        "id": "scene_01_splash",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_splash.jpg"),
        "is_portrait": True,
        "duration": 3.5,
    },
    {
        "id": "scene_02_onboarding",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_onboarding.jpg"),
        "is_portrait": True,
        "duration": 4.0,
    },
    {
        "id": "scene_03_dashboard",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_dashboard.jpg"),
        "is_portrait": True,
        "duration": 4.5,
    },
    {
        "id": "scene_04_voice_review",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_voice_review.jpg"),
        "is_portrait": True,
        "duration": 4.5,
    },
    {
        "id": "scene_05_bank_sms",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_bank_sms.jpg"),
        "is_portrait": True,
        "duration": 4.0,
    },
    {
        "id": "scene_06_gemini_settings",
        "image": os.path.join(ROOT_DIR, "raqeem_screen_gemini_settings.jpg"),
        "is_portrait": True,
        "duration": 4.0,
    },
    {
        "id": "scene_07_panoramic",
        "image": os.path.join(ROOT_DIR, "raqeem_panoramic_showcase.jpg"),
        "is_portrait": False,
        "duration": 4.5,
    },
]

print("=== Rendering 60 FPS Video Scenes ===")

segment_files = []

for idx, scene in enumerate(scenes):
    out_file = os.path.join(TEMP_DIR, f"{scene['id']}.mp4")
    dur = scene["duration"]
    img = scene["image"]
    
    print(f"[{idx+1}/{len(scenes)}] Rendering {scene['id']} ({dur}s @ 60fps)...")
    
    if scene["is_portrait"]:
        # Portrait screen layout: Ambient blurred background + sharp centered phone mockup
        filter_str = (
            f"[0:v]split=2[bg][fg];"
            f"[bg]scale=1920:1080:force_original_aspect_ratio=increase,crop=1920:1080,gblur=sigma=30,eq=brightness=-0.22:contrast=1.05[bg_blur];"
            f"[fg]scale=-1:1000[fg_scaled];"
            f"[bg_blur][fg_scaled]overlay=(W-w)/2:(H-h)/2,fps=60,format=yuv420p[v]"
        )
    else:
        # Landscape banner layout: Fitted cleanly in 1080p frame
        filter_str = (
            f"[0:v]scale=1920:1080:force_original_aspect_ratio=decrease,"
            f"pad=1920:1080:(ow-iw)/2:(oh-ih)/2:color=0x0F172A,fps=60,format=yuv420p[v]"
        )
        
    cmd = [
        "ffmpeg", "-y",
        "-loop", "1",
        "-t", str(dur),
        "-i", img,
        "-filter_complex", filter_str,
        "-map", "[v]",
        "-c:v", "libx264",
        "-preset", "fast",
        "-crf", "18",
        "-pix_fmt", "yuv420p",
        "-r", "60",
        out_file
    ]
    
    res = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    if res.returncode != 0:
        print(f"Error rendering {scene['id']}:\n{res.stderr.decode('utf-8', errors='ignore')}")
        sys.exit(1)
        
    segment_files.append(out_file)

print(f"Rendered {len(segment_files)} scene segments successfully.")

# Now chain them with xfade transitions
print("=== Compositing segments with xfade transitions ===")

TRANSITION_DUR = 0.5
total_dur = sum(s["duration"] for s in scenes) - (len(scenes) - 1) * TRANSITION_DUR
print(f"Total video duration will be: {total_dur:.2f}s")

# Build FFmpeg xfade command
inputs = []
for seg in segment_files:
    inputs.extend(["-i", seg])

# Build filter graph for xfade chain
# Transitions to alternate: fade, wipeleft, fade, slideleft, fade, wipeleft
transitions = ["fade", "wipeleft", "fade", "slideleft", "fade", "fade"]

filter_parts = []
current_v = "[0:v]"
current_offset = scenes[0]["duration"] - TRANSITION_DUR

for i in range(1, len(scenes)):
    next_v = f"[{i}:v]"
    out_v = f"[v{i}]" if i < len(scenes) - 1 else "[v_final]"
    trans = transitions[i - 1]
    filter_parts.append(f"{current_v}{next_v}xfade=transition={trans}:duration={TRANSITION_DUR}:offset={current_offset:.2f}{out_v}")
    current_v = out_v
    if i < len(scenes) - 1:
        current_offset += scenes[i]["duration"] - TRANSITION_DUR

filter_complex_str = ";".join(filter_parts)

# Audio filter with fade in and fade out
audio_filter = f"afade=t=in:st=0:d=0.5,afade=t=out:st={total_dur - 1.5:.2f}:d=1.5"

final_output_brag = os.path.join(BRAG_DIR, "raqeem_showcase_60fps.mp4")
final_output_root = os.path.join(ROOT_DIR, "raqeem_showcase_60fps.mp4")

cmd_final = [
    "ffmpeg", "-y",
    *inputs,
    "-ss", "0", "-t", f"{total_dur:.2f}",
    "-i", AUDIO_TRACK,
    "-filter_complex", filter_complex_str,
    "-map", "[v_final]",
    "-map", f"{len(segment_files)}:a",
    "-af", audio_filter,
    "-c:v", "libx264",
    "-preset", "medium",
    "-crf", "18",
    "-r", "60",
    "-pix_fmt", "yuv420p",
    "-c:a", "aac",
    "-b:a", "192k",
    "-ar", "48000",
    final_output_brag
]

print("Running final composite...")
res = subprocess.run(cmd_final, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
if res.returncode != 0:
    print(f"Error compositing:\n{res.stderr.decode('utf-8', errors='ignore')}")
    sys.exit(1)

print(f"Master video rendered: {final_output_brag}")

# Copy to root directory as well
shutil.copy2(final_output_brag, final_output_root)
print(f"Copied to workspace root: {final_output_root}")

# Generate Poster image (best frame at ~10.0s in Dashboard)
poster_output_brag = os.path.join(BRAG_DIR, "brag.jpg")
poster_output_root = os.path.join(ROOT_DIR, "raqeem_showcase_poster.jpg")
cmd_poster = [
    "ffmpeg", "-y",
    "-ss", "9.5",
    "-i", final_output_brag,
    "-vframes", "1",
    "-q:v", "2",
    poster_output_brag
]
subprocess.run(cmd_poster, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
shutil.copy2(poster_output_brag, poster_output_root)
print(f"Poster generated: {poster_output_root}")

# Also write share-copy.txt
share_copy_path = os.path.join(BRAG_DIR, "share-copy.txt")
share_copy = f"""🚀 أطلقنا تطبيق "رقيم Raqeem" — أول تطبيق عربي ذكي لتتبع المصاريف بالصوت! 🎙️✨

كل اللي عليك تقول:
"دفعت 140 ريال بنزين و 45 قهوة ومحمد حاسب عني"
وتطبيق رقيم يتكفل بالباقي:
🔹 تفكيك وتصنيف فوري للمصروفات عبر Gemini 2.5 Flash
🔹 رادار أسبوعي للميزانية ومين دفع كم
🔹 قراءة وتحليل إشعارات ورسائل البنوك تلقائياً (الراجحي، الأهلي، الإنماء...)
🔹 مبني بأحدث تقنيات Compose Multiplatform (CMP) وKotlin لعالم الأندرويد والمنصات المشتركة
🔹 خصوصية تامة وحفظ محلي بدون إعلانات أو مشاركة لبياناتك الصوتية

📺 شاهد استعراض شاشات التطبيق الكاملة في هذا الفيديو السلس (60 FPS):
🔗 كود المصدر والمشروع متاح بالكامل على GitHub

#AndroidDev #Kotlin #ComposeMultiplatform #GeminiAPI #Fintech #UIUX #ArabicApp
"""
with open(share_copy_path, "w", encoding="utf-8") as f:
    f.write(share_copy)

print("Share copy written successfully.")
print("=== All Brag 60 FPS Deliverables Completed! ===")

