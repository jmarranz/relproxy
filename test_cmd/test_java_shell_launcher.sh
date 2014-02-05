#!/bin/bash

source _shared_test_java_shell_launcher.sh

echo BIEN $PATH

$PROJECT/src/main/webapp/WEB-INF/javashellex/code/example_java_shell "HELLO " "WORLD!"

