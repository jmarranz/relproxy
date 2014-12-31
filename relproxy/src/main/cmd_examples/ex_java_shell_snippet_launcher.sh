#!/bin/sh

RELPROXY_JAR=relproxy-0.8.2.jar

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

export JPROXYSH_COMPILATION_OPTIONS="-source 1.6 -target 1.6"

jproxysh -c 'System.out.print("This code snippet says: ");' \
            'System.out.println("Hello World!!");'

