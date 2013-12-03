#!/bin/sh

PROJECT=/C/trabajo/empresa/opensource/relproxy_dev

export PATH=$PATH:$PROJECT/cmd

export JAVA_HOME="/C/Archivos de programa/Java/jdk1.7.0_45/"
export CLASS_PATH=$PROJECT/target/classes
export JAVA_CMDPARAMS="-client -Xmx80m"
# Nothing really required in JAVA_CMDPARAMS, just to test

../src/main/webapp/WEB-INF/javashellex/code/test_java_shell -DclassFolder=/tmp/java_shell_test_classes -DscanPeriod=-1  "HELLO WORLD!"
