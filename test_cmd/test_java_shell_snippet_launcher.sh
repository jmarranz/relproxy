#!/bin/sh

PROJECT=`dirname $0`/..

export JAVA_HOME="/c/Archivos de programa/Java/jdk1.7.0_45/"

export PATH=$PATH:$PROJECT/cmd
export CLASSPATH=$PROJECT/target/classes
export JAVA_OPTS="-client -Xmx100m"
# Nothing really required in JAVA_OPTS, just to test

export JPROXYSH_COMPILATION_OPTIONS="-source 1.6 -target 1.6"

jproxysh 'System.out.print("This code snippet says: ");' \
         'System.out.println("Hello World!!");'

