#!/bin/sh

PROJECT=/c/trabajo/empresa/opensource/relproxy_dev
CACHE_CLASS_FOLDER=/c/tmp/java_shell_test_classes

export JAVA_HOME="/c/Archivos de programa/Java/jdk1.7.0_45/"

export PATH=$PATH:$PROJECT/cmd
export CLASSPATH=$PROJECT/target/classes
export JAVA_OPTS="-client -Xmx80m"
# Nothing really required in JAVA_OPTS, just to test


../src/main/webapp/WEB-INF/javashellex/code/test_java_shell \
    -DscanPeriod=-1 -DcacheClassFolder=$CACHE_CLASS_FOLDER  \
    -DcompilationOptions="-source 1.6 -target 1.6"  \
    "HELLO WORLD!"
