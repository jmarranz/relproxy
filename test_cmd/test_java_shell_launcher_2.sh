#!/bin/sh

PROJECT=`dirname $0`/..

if [ -z "$JAVA_HOME" ]; then JAVA_HOME="/c/Archivos de programa/Java/jdk1.7.0_45/"
fi

export PATH=$PATH:$PROJECT/cmd
export CLASSPATH=$PROJECT/target/classes
export JAVA_OPTS="-client -Xmx100m"
# Nothing really required in JAVA_OPTS, just to test

export JPROXYSH_SCAN_PERIOD=-1
export JPROXYSH_CACHE_CLASS_FOLDER="$PROJECT/tmp/java_shell_test_classes"
export JPROXYSH_COMPILATION_OPTIONS="-source 1.6 -target 1.6"

$PROJECT/src/main/webapp/WEB-INF/javashellex/code/test_java_shell_2 "HELLO WORLD!"
