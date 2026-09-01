import os, glob, subprocess, shutil, zipfile

project_dir = os.path.dirname(os.path.abspath(__file__))
src_dir = os.path.join(project_dir, "src")
manifest_xml = os.path.join(src_dir, "AndroidManifest.xml")
res_dir = os.path.join(src_dir, "res")
src_java_dir = os.path.join(src_dir, "java")

build_dir = os.path.join(project_dir, "build")
if os.path.exists(build_dir): shutil.rmtree(build_dir)
os.makedirs(build_dir, exist_ok=True)
compiled_res_dir = os.path.join(build_dir, "compiled_res")
os.makedirs(compiled_res_dir, exist_ok=True)
gen_src_dir = os.path.join(build_dir, "gen")
os.makedirs(gen_src_dir, exist_ok=True)
bin_classes_dir = os.path.join(build_dir, "classes")
os.makedirs(bin_classes_dir, exist_ok=True)

android_sdk = os.environ.get("ANDROID_HOME", r"C:\Program Files (x86)\Android\android-sdk")
android_jar = os.path.join(android_sdk, "platforms", "android-36", "android.jar")
if not os.path.exists(android_jar):
    platforms = glob.glob(os.path.join(android_sdk, "platforms", "android-*", "android.jar"))
    if platforms: android_jar = platforms[-1]

r8_jar = os.path.join(android_sdk, "cmdline-tools", "latest", "lib", "r8.jar")

apktool_gui = r"C:\Users\Admin\Downloads\APK.Tool.GUI.v3.4.0.0\Resources"
aapt2 = os.path.join(apktool_gui, "aapt2.exe")
zipalign = os.path.join(apktool_gui, "zipalign.exe")
apksigner = os.path.join(apktool_gui, "apksigner.jar")
testkey_pk8 = os.path.join(apktool_gui, "testkey.pk8")
testkey_pem = os.path.join(apktool_gui, "testkey.x509.pem")

print("1. Compiling resources with AAPT2...")
res_files = [os.path.join(root, f) for root, _, files in os.walk(res_dir) for f in files if f.endswith(('.xml', '.png'))]
for rf in res_files:
    subprocess.run([aapt2, "compile", rf, "-o", compiled_res_dir], check=True)

print("2. Linking APK...")
flat_files = glob.glob(os.path.join(compiled_res_dir, "*.flat"))
unaligned_apk = os.path.join(build_dir, "unaligned.apk")
link_cmd = [aapt2, "link", "-I", android_jar, "--min-sdk-version", "28", "--target-sdk-version", "36", "--manifest", manifest_xml, "--java", gen_src_dir, "-o", unaligned_apk, "--auto-add-overlay"] + flat_files
subprocess.run(link_cmd, check=True)

print("3. Compiling Java sources...")
java_sources = [os.path.join(root, f) for d in [src_java_dir, gen_src_dir] for root, _, files in os.walk(d) for f in files if f.endswith('.java')]
subprocess.run(["javac", "-cp", android_jar, "-d", bin_classes_dir, "-source", "1.8", "-target", "1.8"] + java_sources, check=True)

print("4. Compiling DEX with D8...")
class_files = [os.path.join(root, f) for root, _, files in os.walk(bin_classes_dir) for f in files if f.endswith('.class')]
subprocess.run(["java", "-cp", r8_jar, "com.android.tools.r8.D8", "--lib", android_jar, "--output", build_dir, "--min-api", "28"] + class_files, check=True)

dex_file = os.path.join(build_dir, "classes.dex")
with zipfile.ZipFile(unaligned_apk, 'a') as zipf:
    zipf.write(dex_file, "classes.dex", compress_type=zipfile.ZIP_DEFLATED)

print("5. Zipaligning & Signing APK...")
aligned_apk = os.path.join(build_dir, "aligned.apk")
out_release = os.path.join(project_dir, "releases", "CyberClockHudWidget.apk")
subprocess.run([zipalign, "-f", "-v", "-p", "4", unaligned_apk, aligned_apk], check=True)
subprocess.run(["java", "-jar", apksigner, "sign", "--v1-signing-enabled", "true", "--v2-signing-enabled", "true", "--key", testkey_pk8, "--cert", testkey_pem, "--out", out_release, aligned_apk], check=True)

print(f"=== Build Complete! Artifact generated at: {out_release} ===")
