#!/bin/bash

source _shared_test_java_shell_launcher.sh

jproxysh $PROJECT/src/test/resources/example_normal_class.java "HELLO " "WORLD!"

