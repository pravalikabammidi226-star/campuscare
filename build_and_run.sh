#!/usr/bin/env bash
# ==============================================================================
# CampusCare+ – Build and Automated Test Execution Script
# Compatible with Java 17+ and Java 21 LTS
# ==============================================================================

set -e

# Detect Java 21 or installed JDK
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME=$(/usr/libexec/java_home -v 17+ 2>/dev/null || true)
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    JAVAC="$JAVA_HOME/bin/javac"
    JAVA="$JAVA_HOME/bin/java"
else
    JAVAC="javac"
    JAVA="java"
fi

echo "======================================================================"
echo " Building CampusCare+ Management System"
echo " Using Compiler: $JAVAC"
echo " Using Runtime:  $JAVA"
echo "======================================================================"

mkdir -p bin
mkdir -p data

echo "[1/2] Compiling Core Java source files and test suites..."
"$JAVAC" -d bin $(find src test -name "*.java")
echo "      Compilation successful!"

echo "[2/2] Running automated regression & integration test suite..."
"$JAVA" -cp bin test.TestRunner

echo ""
echo "======================================================================"
echo " BUILD & VERIFICATION SUCCESSFUL!"
echo " To launch the graphical user interface (GUI), run:"
echo "   ./run_gui.sh"
echo " Or execute directly:"
echo "   \"$JAVA\" -cp bin com.campuscare.Main"
echo "======================================================================"
