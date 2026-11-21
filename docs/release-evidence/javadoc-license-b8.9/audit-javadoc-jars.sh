#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO="$(cd "$SCRIPT_DIR/../../.." && pwd)"
JDK="$REPO/.tools/temurin-21.0.13.1-linux"
OUT=$REPO/docs/release-evidence/javadoc-license-b8.9/final-javadoc-audit.txt
MODULES=(
  code-integrity-guard-core
  code-integrity-guard-testkit
  code-integrity-guard-pit-adapter
  code-integrity-guard-report
  code-integrity-guard-maven-plugin
)
JDK_ASSETS=(copy.svg link.svg script.js search.js search-page.js stylesheet.css)

cd "$REPO"
export PATH="$JDK/bin:$PATH"
exec > "$OUT"
echo "JDK=$("$JDK/bin/java" -version 2>&1 | head -n 1)"
echo "JDK archive SHA-256: bb8a93f9ae6e7a693b2b721e85dbeff26ae1a77447985a8d7afcdd9c7cf6222e"
echo "Root LICENSE SHA-256: $(sha256sum LICENSE | cut -d ' ' -f 1)"
echo "Root NOTICE SHA-256: $(sha256sum NOTICE | cut -d ' ' -f 1)"
echo

for module in "${MODULES[@]}"; do
  jar="$REPO/$module/target/$module-1.0.0-javadoc.jar"
  test -f "$jar"
  extracted="$(mktemp -d)"
  trap 'rm -rf "$extracted"' EXIT
  echo "=== $module ==="
  echo "JAR: ${jar#$REPO/}"
  sha256sum "$jar"
  "$JDK/bin/jar" tf "$jar" | grep -E '^(legal/|resources/(LICENSE|NOTICE|glass\.png|x\.png)$|script-dir/jquery|copy\.svg$|link\.svg$|script\.js$|search\.js$|search-page\.js$|stylesheet\.css$)' | sort
  (cd "$extracted" && "$JDK/bin/jar" xf "$jar" legal/LICENSE legal/ADDITIONAL_LICENSE_INFO legal/ASSEMBLY_EXCEPTION legal/jquery.md legal/jqueryUI.md resources/LICENSE resources/NOTICE script-dir/jquery-3.7.1.min.js script-dir/jquery-ui.min.css script-dir/jquery-ui.min.js resources/glass.png resources/x.png "${JDK_ASSETS[@]}")

  for entry in legal/LICENSE legal/ADDITIONAL_LICENSE_INFO legal/ASSEMBLY_EXCEPTION legal/jquery.md legal/jqueryUI.md resources/LICENSE resources/NOTICE; do
    case "$entry" in
      legal/LICENSE) source="$JDK/legal/java.base/LICENSE" ;;
      legal/ADDITIONAL_LICENSE_INFO) source="$JDK/legal/java.base/ADDITIONAL_LICENSE_INFO" ;;
      legal/ASSEMBLY_EXCEPTION) source="$JDK/legal/java.base/ASSEMBLY_EXCEPTION" ;;
      legal/jquery.md) source="$JDK/legal/jdk.javadoc/jquery.md" ;;
      legal/jqueryUI.md) source="$JDK/legal/jdk.javadoc/jqueryUI.md" ;;
      resources/LICENSE) source=LICENSE ;;
      resources/NOTICE) source=NOTICE ;;
    esac
    cmp "$source" "$extracted/$entry"
    printf '%s source_sha256=%s jar_sha256=%s\n' \
      "$entry" \
      "$(sha256sum "$source" | cut -d ' ' -f 1)" \
      "$(sha256sum "$extracted/$entry" | cut -d ' ' -f 1)"
  done
  for entry in "${JDK_ASSETS[@]}"; do
    cmp "$REPO/$module/target/reports/apidocs/$entry" "$extracted/$entry"
    printf '%s javadoc_output_sha256=%s jar_sha256=%s\n' \
      "$entry" \
      "$(sha256sum "$REPO/$module/target/reports/apidocs/$entry" | cut -d ' ' -f 1)" \
      "$(sha256sum "$extracted/$entry" | cut -d ' ' -f 1)"
  done
  for entry in script.js search.js search-page.js copy.svg link.svg; do
    grep -Fq 'GNU General Public License version 2 only' "$extracted/$entry"
    grep -Fq 'Classpath' "$extracted/$entry"
  done
  if grep -Fq 'Please see' "$extracted/legal/LICENSE" "$extracted/legal/ADDITIONAL_LICENSE_INFO" "$extracted/legal/ASSEMBLY_EXCEPTION"; then
    echo 'ERROR: unresolved legal pointer found'
    exit 1
  fi
  grep -Fq 'Permission is hereby granted' "$extracted/legal/jquery.md"
  grep -Fq 'Permission is hereby granted' "$extracted/legal/jqueryUI.md"
  grep -Fq 'CC0' "$extracted/legal/jqueryUI.md"
  if "$JDK/bin/jar" tf "$jar" | grep -qE '(^|/)demos/'; then
    echo 'ERROR: Javadoc demos unexpectedly present in the candidate artifact'
    exit 1
  fi
  echo 'CHECKS: seven legal/Apache/NOTICE files byte-identical to sources; six JDK standard-doclet files byte-identical to generated output; GPLv2/Classpath headers preserved; no java.base pointers; MIT and CC0 notice text present; no demos directory shipped'
  echo "jQuery header: $(head -c 100 "$extracted/script-dir/jquery-3.7.1.min.js")"
  echo "jQuery UI header: $(head -c 100 "$extracted/script-dir/jquery-ui.min.js")"
  echo
  rm -rf "$extracted"
  trap - EXIT
done

echo 'Example artifact is excluded from public candidates by central-release skipPublishing=true.'
