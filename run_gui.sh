#!/usr/bin/env bash
# ==============================================================================
# CampusCare+ – Graphical User Interface (Swing) Launcher
# ==============================================================================

set -e

if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    elif [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME=$(/usr/libexec/java_home -v 17+ 2>/dev/null || true)
    fi
fi

JAVA="${JAVA_HOME:+$JAVA_HOME/bin/}java"

if [ ! -d "bin/com/campuscare" ]; then
    echo "Compiled classes not found in bin/. Compiling now..."
    ./build_and_run.sh
fi

echo "Launching CampusCare+ GUI..."
"$JAVA" -cp bin com.campuscare.Main
