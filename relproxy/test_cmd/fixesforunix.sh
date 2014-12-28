#!/bin/bash

PROJECT=`dirname $0`/..

# set PROJECT env as absolute path
TMP_PWD=`pwd`
cd $PROJECT
PROJECT=`pwd`
cd $TMP_PWD

dos2unix $PROJECT/test_cmd/*.sh
chmod +x $PROJECT/test_cmd/*.sh

dos2unix $PROJECT/bin/jproxysh
chmod +x $PROJECT/bin/jproxysh

dos2unix $PROJECT/src/test/resources/example_java_shell
chmod +x $PROJECT/src/test/resources/example_java_shell

dos2unix $PROJECT/src/test/resources/example_java_shell
chmod +x $PROJECT/src/test/resources/example_java_shell_complete_class

