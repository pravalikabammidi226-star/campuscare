#!/usr/bin/env bash
# ==============================================================================
# CampusCare+ – Fast Test Suite Runner
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

"$JAVA" -cp bin test.TestRunner
