RelProxy
========


Note: RelProxy v0.8 is finished but documentation and final packaging is still pending. v0.8 is just a prudential version, but the author, Jose María Arranz, thinks it is "production ready" in the current state.

RelProxy is a simple Java and Groovy hot class reloader, in spite of is a general purpose tool it was heavily inspired in ItsNat web framework to provide hot class reload in development time... and if you want also in production.

More info here:

http://www.innowhere.com/inexperiments/jreloadex?itsnat_doc_name=jreloadex
http://www.innowhere.com/inexperiments/groovyex?itsnat_doc_name=groovyex

Take a look source code of RelProxy and [ItsNat Experiments](https://github.com/jmarranz/itsnat/tree/master/inexperiments) for Maven and Ant configurations and code examples of how to use with ItsNat (Java and Groovy code) and standalone shell.

You can get a compiled jar file with the last snapshot (virtually the final) here:
https://github.com/jmarranz/itsnat/raw/master/inexperiments/web/WEB-INF/lib/relproxy-0.8-SNAPSHOT.jar

As bonus, RelProxy provides a shell environment to execute:

1) A pure Java archive packaged like a shell script file with no need of previous compilation, compilation is done on the fly and optionally .class can be saved in a cache to provide the fastest "scripting" language of the world. Code in the initial archive can call to other normal Java files, again with compilation on the fly. [Example 1](https://github.com/jmarranz/relproxy/blob/master/src/main/webapp/WEB-INF/javashellex/code/test_java_shell) and [example 2](https://github.com/jmarranz/relproxy/blob/master/src/main/webapp/WEB-INF/javashellex/code/test_java_shell_2)

2) Execution of Java code snippets in command line (no need of packaging in an archive). [Example](https://github.com/jmarranz/relproxy/blob/master/test_cmd/test_java_shell_snippet_launcher.sh).

3) Interactive console to edit and execute Java code (ever compilation on the fly). [Example of launcher](https://github.com/jmarranz/relproxy/blob/master/test_cmd/test_java_shell_interactive_launcher.sh)

Yes, Java is also a dynamic and scripting language with no need of manual compilation and extremely fast and robust  :)
