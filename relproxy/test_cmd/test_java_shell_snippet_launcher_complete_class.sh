#!/bin/bash

source _shared_test_java_shell_inter_and_snippet_launcher.sh

jproxysh -c 'public class _jproxyMainClass_ { '  \
            ' public static void main(String[] args) { ' \
            '    System.out.print("This code snippet says: ");' \
            '    System.out.println("Hello World!!");' \
            '  }' \
            '}'
