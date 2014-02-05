#!/bin/bash

PROJECT=`dirname $0`/..

# set PROJECT env as absolute path
TMP_PWD=`pwd`
cd $PROJECT
PROJECT=`pwd`
cd $TMP_PWD

chmod +x $PROJECT/cmd_examples/*.sh

chmod +x $PROJECT/cmd_examples/code/example_java_shell
chmod +x $PROJECT/cmd_examples/code/example_java_shell_complete_class

chmod +x $PROJECT/bin/jproxysh
