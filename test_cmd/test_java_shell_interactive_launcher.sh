#!/bin/sh

PROJECT=/c/trabajo/empresa/opensource/relproxy_dev

export JAVA_HOME="/c/Archivos de programa/Java/jdk1.7.0_45/"

export PATH=$PATH:$PROJECT/cmd
export CLASSPATH=$PROJECT/target/classes
export JAVA_OPTS="-client -Xmx100m"
# Nothing really required in JAVA_OPTS, just to test

export JPROXYSH_SCAN_PERIOD=-1
export JPROXYSH_CACHE_CLASS_FOLDER="/c/tmp/java_shell_test_classes"
export JPROXYSH_COMPILATION_OPTIONS="-source 1.6 -target 1.6"

jproxysh


    
