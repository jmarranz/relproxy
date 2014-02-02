<%@page import="javax.script.ScriptContext"%>
<%@page import="com.innowhere.relproxy.jproxy.JProxyScriptEngineFactory"%>
<%@page import="com.innowhere.relproxy.impl.jproxy.core.JProxyImpl" %>
<%@page import="com.innowhere.relproxy.RelProxyOnReloadListener" %>
<%@page import="com.innowhere.relproxy.jproxy.JProxy" %>
<%@page import="com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener" %>
<%@page import="com.innowhere.relproxy.jproxy.JProxyConfig" %>
<%@page import="com.innowhere.relproxy.jproxy.JProxyScriptEngineFactory" %>
<%@page import="com.innowhere.relproxy.jproxy.JProxyScriptEngine" %>

<%@page import="javax.tools.Diagnostic" %>
<%@page import="javax.tools.DiagnosticCollector" %>
<%@page import="javax.tools.JavaFileObject" %>
<%@page import="java.util.Arrays" %>
<%@page import="java.lang.reflect.Method" %>
<%@page import="java.util.List" %>
<%@page import="javax.script.ScriptEngineManager" %>
<%@page import="javax.script.ScriptEngine" %>
<%@page import="javax.script.Bindings" %>



<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>ScriptEngine support example (Java)</title>
    </head>
    <body>
        <h1>ScriptEngine support example (Java)</h1>

        <%
            // This code is internal stuff just to make this test workable
            if (JProxyImpl.SINGLETON != null)
            {
                JProxyImpl.SINGLETON.getJProxyEngine().stop = true;
            }
            JProxyImpl.SINGLETON = null;


            String inputPath = application.getRealPath("/") + "/WEB-INF/javashellex/code/";
            String classFolder = null; // Optional: context.getRealPath("/") + "/WEB-INF/classes";
            Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
            long scanPeriod = -1;

            RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
                public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                    System.out.println("Reloaded " + objNew + " Calling method: " + method);
                }
            };

            JProxyDiagnosticsListener diagnosticsListener = new JProxyDiagnosticsListener()
            {
                public void onDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics)
                {
                    List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();
                    int i = 1;
                    for (Diagnostic diagnostic : diagList)
                    {
                       System.err.println("Diagnostic " + i);
                       System.err.println("  code: " + diagnostic.getCode());
                       System.err.println("  kind: " + diagnostic.getKind());
                       System.err.println("  line number: " + diagnostic.getLineNumber());
                       System.err.println("  column number: " + diagnostic.getColumnNumber());
                       System.err.println("  start position: " + diagnostic.getStartPosition());
                       System.err.println("  position: " + diagnostic.getPosition());
                       System.err.println("  end position: " + diagnostic.getEndPosition());
                       System.err.println("  source: " + diagnostic.getSource());
                       System.err.println("  message: " + diagnostic.getMessage(null));
                       i++;
                    }
                }
            };

            JProxyConfig jpConfig = JProxy.createJProxyConfig();
            jpConfig.setEnabled(true)
                    .setRelProxyOnReloadListener(proxyListener)
                    .setInputPath(inputPath)
                    .setScanPeriod(scanPeriod)
                    .setClassFolder(classFolder)
                    .setCompilationOptions(compilationOptions)
                    .setJProxyDiagnosticsListener(diagnosticsListener);

            JProxyScriptEngineFactory factory = JProxyScriptEngineFactory.create(jpConfig);

            ScriptEngineManager manager = new ScriptEngineManager();
            manager.registerEngineName("Java", factory);
            
            manager.getBindings().put("msg","HELLO GLOBAL WORLD!");
            
            ScriptEngine engine = manager.getEngineByName("Java");

            Bindings bindings = engine.createBindings();
            bindings.put("msg","HELLO SCOPE WORLD!");
           
            
            StringBuilder code = new StringBuilder();
            code.append( " javax.script.Bindings bindings = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE); \n");
            code.append( " String msg = (String)bindings.get(\"msg\"); \n");
            code.append( " System.out.println(msg); \n");
            code.append( " bindings = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE); \n");
            code.append( " msg = (String)bindings.get(\"msg\"); \n");
            code.append( " System.out.println(msg); \n");            
            code.append( " example.javashellex.JProxyShellExample.exec(engine); \n");
            code.append( " return \"SUCESS\";");

            String result = (String)engine.eval( code.toString() , bindings);
            System.out.println("RETURNED: " + result);

            bindings = engine.createBindings();
            bindings.put("msg","HELLO SCOPE WORLD 2!");

            code = new StringBuilder();
            code.append( "public static Object main(javax.script.ServiceEngine engine,javax.script.ServiceContext context) {  \n");           
            code.append( "   javax.script.Bindings bindings = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE); \n");
            code.append( "   String msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");
            code.append( "   bindings = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE); \n");
            code.append( "   msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");            
            code.append( "   example.javashellex.JProxyShellExample.exec(engine); \n");
            code.append( "   return \"SUCESS\";");            
            code.append( "}");        
            
            result = (String)engine.eval( code.toString() , bindings);
            System.out.println("RETURNED 2: " + result);

            ((JProxyScriptEngine)engine).stop(); // Necessary if scanPeriod > 0 was defined
            
        %>

        <p>See your console!!<p>

        <br />
        <p>This test interrupts the automatic detection of changed classes of "JProxy example"</p>
    </body>
</html>
