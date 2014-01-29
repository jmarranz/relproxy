#!/bin/sh

source _shared_test_java_shell_launcher.sh

jproxysh $PROJECT/src/main/webapp/WEB-INF/javashellex/code/example_java_shell_complete_class "HELLO " "WORLD!"
