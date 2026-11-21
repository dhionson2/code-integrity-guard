#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$SCRIPT_DIR/../../.." && pwd)"
JDK="$REPO/.tools/temurin-21.0.13.1-linux"
ARCHIVE="$REPO/.tools/temurin.tar.gz"
PROBE=/tmp/cig-javadoc-default
EVIDENCE=$REPO/docs/release-evidence/javadoc-license-b8.9

mkdir -p "$REPO/.tools" "$EVIDENCE"
if [[ ! -f "$ARCHIVE" ]]; then
  curl -fL --retry 3 -sS \
    'https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.13.1%2B2/OpenJDK21U-jdk_x64_linux_hotspot_21.0.13.1_2.tar.gz' \
    -o "$ARCHIVE"
fi
printf '%s  %s\n' \
  'bb8a93f9ae6e7a693b2b721e85dbeff26ae1a77447985a8d7afcdd9c7cf6222e' \
  "$ARCHIVE" | sha256sum -c -
if [[ ! -x "$JDK/bin/javadoc" ]]; then
  mkdir -p "$JDK"
  printf '%s  %s\n' \
    "$(sha256sum "$ARCHIVE" | cut -d ' ' -f 1)" \
    "$ARCHIVE" | sha256sum -c -
  tar -xzf "$ARCHIVE" --strip-components=1 -C "$JDK"
fi

mkdir -p "$PROBE"
cat > "$PROBE/NoticeProbe.java" <<'EOF'
/** Probe for standard doclet legal notice behavior. */
public class NoticeProbe {
    /** no args */
    public static void main(String[] args) {}
}
EOF
"$JDK/bin/javadoc" -quiet -d "$PROBE/out" "$PROBE/NoticeProbe.java"
echo "DEFAULT_JAVADOC_EXIT=$?"
find "$PROBE/out/legal" -maxdepth 1 -type f -printf '%f %s bytes\n' | sort
sha256sum "$PROBE/out/legal"/* > "$EVIDENCE/temurin-default-javadoc-legal.sha256"
cat "$EVIDENCE/temurin-default-javadoc-legal.sha256"
{
  cat "$PROBE/out/legal/LICENSE"
  cat "$PROBE/out/legal/ADDITIONAL_LICENSE_INFO"
  cat "$PROBE/out/legal/ASSEMBLY_EXCEPTION"
} > "$EVIDENCE/temurin-default-javadoc-pointers.txt"

cd "$REPO"
export JAVA_HOME="$JDK"
export PATH="$JDK/bin:$PATH"
java -version 2>&1 | tee "$EVIDENCE/temurin-linux-version.txt"
for module in \
  code-integrity-guard-core \
  code-integrity-guard-testkit \
  code-integrity-guard-pit-adapter \
  code-integrity-guard-report \
  code-integrity-guard-maven-plugin; do
  rm -f "$REPO/$module/target/maven-javadoc-plugin-stale-data.txt"
done
set +e
./mvnw -B -ntp -Pcentral-release -DskipTests package > "$EVIDENCE/central-release-temurin-linux-final.log" 2>&1
rc=$?
set -e
tail -n 35 "$EVIDENCE/central-release-temurin-linux-final.log"
echo "PROFILE_PACKAGE_EXIT=$rc"
exit "$rc"
