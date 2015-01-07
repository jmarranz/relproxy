
package example.javaex;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.http.HttpServletWrapper;
import org.itsnat.core.tmpl.ItsNatDocumentTemplate;
import org.w3c.dom.events.EventListener;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.jproxy.JProxy;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;


/**
 * 
 * @author jmarranz
 */
public class JProxyExampleServlet extends HttpServletWrapper
{  
    public JProxyExampleServlet()
    {
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);

        ServletContext context = getServletContext();
        String realPath = context.getRealPath("/");
        String inputPath = realPath + "/WEB-INF/javaex/code/";
        
        JProxyInputSourceFileExcludedListener excludedListener = new JProxyInputSourceFileExcludedListener()
        {
            @Override
            public boolean isExcluded(File file, File rootFolderOfSources)
            {
            	return false;
            }            
        };         

        String classFolder = null; // Optional: context.getRealPath("/") + "/WEB-INF/classes";
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        long scanPeriod = 300;
        
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
                .setInputPath(inputPath)
                .setJProxyInputSourceFileExcludedListener(excludedListener)
                .setScanPeriod(scanPeriod)
                .setClassFolder(classFolder)
                .setCompilationOptions(compilationOptions)
                .setJProxyCompilerListener(compilerListener)                
                .setJProxyDiagnosticsListener(diagnosticsListener);
        
        JProxy.init(jpConfig);

        
        String pathPrefix = context.getRealPath("/") + "/WEB-INF/javaex/pages/";

        ItsNatDocumentTemplate docTemplate;
        docTemplate = itsNatServlet.registerItsNatDocumentTemplate("javaex","text/html", pathPrefix + "javaex.html");

        FalseDB db = new FalseDB();        
        
        ItsNatServletRequestListener listener = JProxy.create(new example.javaex.JProxyExampleLoadListener(db), ItsNatServletRequestListener.class);
        docTemplate.addItsNatServletRequestListener(listener);   
    }    
 
}

