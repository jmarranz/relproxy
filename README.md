RelProxy
========

News
------

- 2015-1-30 [Slides](http://www.slideshare.net/jmarranz/relproxy-class-reload-and-easy-java-scripting) in English and [video](http://autentia.com/2015/01/30/relproxy-easy-class-reload-and-scripting-with-java-por-jose-maria-arranz/#) in Spanish of the RelProxy presentation in MadridJUG on January 28!
- 2015-1-23 v0.8.2 Released! [Release Notes](https://github.com/jmarranz/relproxy/blob/master/relproxy/CHANGES.txt)


Download Binaries and Docs
------

Last version: v0.8.2

[Download](https://sourceforge.net/projects/relproxy/files/)

Distribution file includes binaries, examples, manual and javadocs.

Overview
------

RelProxy is a simple Java and Groovy hot class reloader for Java and Groovy providing transparent compilation and class reload on the fly, and scripting support and shell of pure 
Java code.

RelProxy is:

1) A simple Java and Groovy hot class reloader for Java and Groovy providing transparent compilation and class reload on the fly with no need of a bootstrap class loader agent and 
avoiding context reloading. Reloading happens only in memory. Class reloading can be used in development phase and optionally in production (if source code can be uploaded to 
production).

2) A scripting environment to execute Java code snippets the same as a shell script. There is no new language, is Java compiled on the fly, code in the initial archive can call 
to other normal Java files. Optionally .class can be saved in a cache to provide the fastest "scripting" language of the world.

3) Execution of Java code snippets in command line (no need of packaging into an archive).

4) A simple shell to code, edit and execute code snippets in Java interactively.

5) JSR 223 Scripting API implementation for "Java" as the target scripting language. You can embed and execute Java code as scripting into your Java program.



In case of Java "scripting", there is no a new language, is pure Java code with compilation on the fly.

In spite of RelProxy is a general purpose tool it was conceived for [ItsNat web framework](http://www.itsnat.org) to provide hot class reload in development time... 
and if you want also in production. RelProxy is standalone and has no dependency on ItsNat.

RelProxy Manual explains how to configure and use RelProxy in NetBeans and Eclipse avoiding class reloading (of course other IDEs are possible).

There are several examples of how to use RelProxy with most popular Java web frameworks ready to run into [RelProxy Examples](https://github.com/jmarranz/relproxy_examples) 
repository. Also [ItsNat Experiments](https://github.com/jmarranz/itsnat/tree/master/inexperiments) includes an ItsNat example using NetBeans with Ant, to reload Java and Groovy code. 
The most complex example is [relproxy_test_itsnat](https://github.com/jmarranz/relproxy/tree/master/relproxy_test_itsnat) created for internal testing.

Besides fast and custom Java (and Groovy) class reloading, RelProxy provides a Java shell scripting environment to execute:

1) A pure Java archive packaged like a shell script file with no need of previous compilation, compilation is done on the fly and optionally .class can be saved in a 
cache to provide the fastest "scripting" language of the world. Code in the initial archive can call to other normal Java files, again with compilation on the fly 
and optional compilation caching as .class files. [Example 1](https://github.com/jmarranz/relproxy/blob/master/relproxy/src/test/resources/example_java_shell) 
and [example 2](https://github.com/jmarranz/relproxy/blob/master/relproxy/src/test/resources/example_java_shell_complete_class) or just
a conventional [Java source file](https://github.com/jmarranz/relproxy/blob/master/relproxy/src/test/resources/example_normal_class.java) (yes you can execute a conventional
JavaSE application from source code).

2) Execution of Java code snippets in command line (no need of packaging in an archive). [Example](https://github.com/jmarranz/relproxy/blob/master/relproxy/test_cmd/test_java_shell_snippet_launcher.sh).

3) Interactive console to edit and execute Java code (ever compilation on the fly). [Example of launcher](https://github.com/jmarranz/relproxy/blob/master/relproxy/test_cmd/test_java_shell_interactive_launcher.sh)

Finally RelProxy implements the official JSR-223 [Java Scripting API](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html) as found in Java 1.6 for "Java" language.
By using this API you can embed Java as any other scripting language in your Java code.

Yes, Java is also a dynamic scripting language with no need of manual compilation, and it is extremely fast and robust.

Online Docs Last Version
------

[Manual](http://relproxy.sourceforge.net/docs/manual/manual.html)

[JavaDocs](http://relproxy.sourceforge.net/docs/javadoc/)



Questions and discussions
------

There is a [Google Group](https://groups.google.com/forum/#!forum/relproxy) for RelProxy.

Bug Reporting
------

Use this GitHub project.


Latest Doc
------

You can read the [in progress latest documentation here](https://github.com/jmarranz/relproxy/blob/master/relproxy/src/main/asciidoc/manual.asciidoc)

Articles/Presentations
------

- Jan 28,2015 [Slides](http://www.slideshare.net/jmarranz/relproxy-class-reload-and-easy-java-scripting) in English and [video](http://autentia.com/2015/01/30/relproxy-easy-class-reload-and-scripting-with-java-por-jose-maria-arranz/#) in Spanish of the RelProxy presentation in MadridJUG

- Dec 31,2014 [RelProxy v0.8.1 reduce el n�mero de redeploys en GWT-RPC y otros Java web frameworks](http://www.javahispano.org/portada/2014/12/31/relproxy-v081-reduce-el-numero-de-redeploys-en-gwt-rpc-y-otr.html) Published at javaHispano

- Feb 15,2014 [v0.8 announce at JavaLobby](http://java.dzone.com/articles/presenting-relproxy-hot-class)

- Feb 12,2014 [v0.8 announce at javaHispano](http://www.javahispano.org/portada/2014/2/12/publicado-relproxy-v08-hot-class-reloader-y-scripting-para-j.html)

