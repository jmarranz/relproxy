package com.innowhere.relproxy.jproxy;

import java.io.File;
import java.net.URL;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmarranz
 */
public class JProxyTest
{
    public static boolean RESULT;
    
    public JProxyTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
        RESULT = false;        
    }
    
    @After
    public void tearDown()
    {
        RESULT = false;        
    }


     @Test
     public void test_code_snippet() 
     {
        String compilationOptions = "-source 1.6  -target 1.6";

        String[] args = new String[] 
            { 
                "-c", 
                "System.out.print(\"This code snippet says: \");",
                "System.out.println(\"Hello World!!\");",
                "com.innowhere.relproxy.jproxy.JProxyTest.RESULT = true;",                
                "-DcompilationOptions=" + compilationOptions                        
            };                

        JProxyShell.main(args);
        
        assertTrue(RESULT);
     }
     
     @Test
     public void test_code_snippet_complete_class() 
     {
        String compilationOptions = "-source 1.6  -target 1.6";

        String[] args = new String[] 
            { 
                "-c", 
                "public class _jproxyMainClass_ { ",
                "  public static void main(String[] args) { ",                  
                "    System.out.print(\"This code snippet says: \");",
                "    System.out.println(\"Hello World!!\");",
                "    com.innowhere.relproxy.jproxy.JProxyTest.RESULT = true;",                 
                "  }",                      
                "}",                    
                "-DcompilationOptions=" + compilationOptions                   
            };                

        JProxyShell.main(args);
        
        assertTrue(RESULT);        
     }     
   
     @Test
     public void test_java_shell_interactive() 
     {     
        String compilationOptions = "-source 1.6  -target 1.6";

        String[] args = new String[] 
        { 
            "", // El args[0] esperado
            "-DcompilationOptions=" + compilationOptions,
            "-Dtest=true"                        
        };

        JProxyShell.main(args);
     }
     
     @Test
     public void test_java_script_engine() 
     {
         /*
         new File(urlClass.getPath())
         
         String className = getClass().getName();
         URL url = this.getClass().getClassLoader().getResource(className.replace('.','/') + ".class");
         url.
         url = null;
         
         */
         
         /*
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
            code.append( "public class _jproxyMainClass_ { \n");                 
            code.append( "  public static Object main(javax.script.ScriptEngine engine,javax.script.ScriptContext context) {  \n");           
            code.append( "   javax.script.Bindings bindings = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE); \n");
            code.append( "   String msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");
            code.append( "   bindings = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE); \n");
            code.append( "   msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");            
            code.append( "   example.javashellex.JProxyShellExample.exec(engine); \n");
            code.append( "   return \"SUCESS\";");            
            code.append( "  }");   
            code.append( "}");             
            
            result = (String)engine.eval( code.toString() , bindings);
            System.out.println("RETURNED 2: " + result);

            ((JProxyScriptEngine)engine).stop(); // Necessary if scanPeriod > 0 was defined     
                 
        */
     }
}
