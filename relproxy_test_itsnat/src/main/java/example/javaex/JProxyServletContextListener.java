package example.javaex;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.jproxy.JProxy;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxyServletContextListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        System.out.println("ServletContextListener contextInitialized");
        
        ServletContext context = sce.getServletContext();
        String realPath = context.getRealPath("/");
        String[] inputPaths = new String[] 
        { realPath + "/WEB-INF/javaex/code/", 
          realPath + "/WEB-INF/javaex/code2/", 
          realPath + "/../../src/main/java/example/javaex/hotreload/" };
             
        JProxyInputSourceFileExcludedListener excludedListener = new JProxyInputSourceFileExcludedListener()
        {
            @Override
            public boolean isExcluded(File file, File rootFolderOfSources)
            {
                String absPath = file.getAbsolutePath();                
                if (file.isDirectory())
                {
                    return absPath.contains(File.separatorChar + "nothotreload" + File.separatorChar); // In src folder
                }
                else
                {
                    return absPath.endsWith(JProxyExampleAuxIgnored.class.getSimpleName() + ".java") &&  // In folder below WEB-INF/
                           !absPath.contains(File.separatorChar + "hotreload" + File.separatorChar);  // In src folder
                }
            }            
        };
        

        String classFolder = null; // Optional: context.getRealPath("/") + "/WEB-INF/classes";
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        long scanPeriod = 200;
        
        RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
            @Override
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }        
        };
        
        JProxyCompilerListener compilerListener = new JProxyCompilerListener(){
            @Override
            public void beforeCompile(File file)
            {
                System.out.println("Before compile: " + file);
            }

            @Override
            public void afterCompile(File file)
            {
                System.out.println("After compile: " + file);
            } 
        };   
        
        JProxyDiagnosticsListener diagnosticsListener = new JProxyDiagnosticsListener()
        {
            @Override
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
                .setInputPaths(inputPaths)
                .setJProxyInputSourceFileExcludedListener(excludedListener)
                .setScanPeriod(scanPeriod)
                .setClassFolder(classFolder)
                .setCompilationOptions(compilationOptions)
                .setJProxyCompilerListener(compilerListener)                
                .setJProxyDiagnosticsListener(diagnosticsListener);
        
        JProxy.init(jpConfig);        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        System.out.println("ServletContextListener contextDestroyed");
        JProxy.stop();
    }
    
}
