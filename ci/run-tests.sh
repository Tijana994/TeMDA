#!/usr/bin/env bash
# Builds TeMDA without Eclipse and runs its JUnit tests.
#
# Usage: ci/run-tests.sh <path-to-PrivacyDSLv3> [path-to-jacoco]
#   <path-to-PrivacyDSLv3>  checkout of https://github.com/Tijana994/PrivacyDSLv3
#   [path-to-jacoco]        unpacked JaCoCo distribution (contains lib/jacocoagent.jar);
#                           when given, a coverage report is written to build/coverage
#
# The Eclipse project files (.project, .classpath, MANIFEST.MF) are not used or changed.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PROJECT="$ROOT/TeMDA"
PRIVACY="$(cd "$1" && pwd)"
JACOCO="${2:-}"
BUILD="$ROOT/build"
SEP=":"; case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) SEP=";";; esac

join() { local IFS="$SEP"; echo "$*"; }

rm -rf "$BUILD"
mkdir -p "$BUILD/privacymodel" "$BUILD/main" "$BUILD/test"

LIBS=( "$PROJECT"/externallibs/*.jar "$PROJECT"/lib/java21/*.jar )

echo "== Compiling PrivacyModel"
# PrivacyModelTables.java is skipped: it needs the OCL version bundled with Eclipse,
# is not referenced by any other class and is not needed for validation.
find "$PRIVACY/src-gen" -name "*.java" ! -name "PrivacyModelTables.java" > "$BUILD/privacymodel.txt"
javac -nowarn -encoding UTF-8 -d "$BUILD/privacymodel" \
  -cp "$(join "${LIBS[@]}")" @"$BUILD/privacymodel.txt"

echo "== Compiling TeMDA"
CP="$(join "${LIBS[@]}" "$BUILD/privacymodel")"
find "$PROJECT/src" -name "*.java" ! -path "*/src/test/*" > "$BUILD/main.txt"
javac -nowarn -encoding UTF-8 -d "$BUILD/main" -cp "$CP" @"$BUILD/main.txt"
cp -r "$PROJECT/META-INF" "$BUILD/main/"

echo "== Compiling tests"
find "$PROJECT/src/test" -name "*.java" > "$BUILD/test.txt"
javac -nowarn -encoding UTF-8 -d "$BUILD/test" -cp "$CP$SEP$BUILD/main" @"$BUILD/test.txt"

TEST_CLASSES=$(cd "$PROJECT/src" && find test -name "*Test.java" | sed 's#\.java$##; s#/#.#g' | sort)
echo "== Running tests:"; echo "$TEST_CLASSES"

AGENT=()
if [ -n "$JACOCO" ]; then
  AGENT=( "-javaagent:$JACOCO/lib/jacocoagent.jar=destfile=$BUILD/jacoco.exec,includes=com.security.*" )
fi

set +e
java "${AGENT[@]}" --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp "$CP$SEP$BUILD/main$SEP$BUILD/test" org.junit.runner.JUnitCore $TEST_CLASSES
STATUS=$?
set -e

if [ -n "$JACOCO" ] && [ -f "$BUILD/jacoco.exec" ]; then
  echo "== Coverage report"
  mkdir -p "$BUILD/coverage"
  java -jar "$JACOCO/lib/jacococli.jar" report "$BUILD/jacoco.exec" \
    --classfiles "$BUILD/main" --sourcefiles "$PROJECT/src" \
    --xml "$BUILD/coverage/jacoco.xml" --csv "$BUILD/coverage/jacoco.csv" --html "$BUILD/coverage/html" --name TeMDA
fi

exit $STATUS
