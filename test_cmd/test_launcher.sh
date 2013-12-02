#!/bin/sh

PROJECT=/C/trabajo/empresa/opensource/relproxy_dev

export JAVA_HOME="/C/Archivos de programa/Java/jdk1.7.0_45/"
export CLASS_PATH=$PROJECT/target/classes
export PATH=$PATH:$PROJECT/cmd

../src/main/webapp/WEB-INF/javashellex/code/test_java_shell
