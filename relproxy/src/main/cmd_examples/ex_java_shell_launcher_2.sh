#!/bin/sh

RELPROXY_JAR=relproxy-0.8.1.jar

PROJECT=`dirname $0`/..

# set PROJECT env as absolute path
TMP_PWD=`pwd`
cd $PROJECT
PROJECT=`pwd`
cd $TMP_PWD

if [ -z "$JAVA_HOME" ]; then 
    echo Missing JAVA_HOME environment variable, exiting...
    exit 1
fi

export PATH=$PATH:$PROJECT/bin
export CLASSPATH=$PROJECT/lib/$RELPROXY_JAR
export JAVA_OPTS="-client -Xmx100m"
# Nothing really required in JAVA_OPTS, just to test

export JPROXYSH_SCAN_PERIOD=-1
export JPROXYSH_CACHE_CLASS_FOLDER="$PROJECT/tmp/java_shell_test_classes"
export JPROXYSH_COMPILATION_OPTIONS="-source 1.6 -target 1.6"

jproxysh $PROJECT/cmd_examples/code/example_java_shell "HELLO " "WORLD!"
