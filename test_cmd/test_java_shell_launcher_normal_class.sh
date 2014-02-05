#!/bin/sh

source _shared_test_java_shell_launcher.sh

jproxysh $PROJECT/src/main/webapp/WEB-INF/javashellex/code/example_normal_class.java "HELLO " "WORLD!"
