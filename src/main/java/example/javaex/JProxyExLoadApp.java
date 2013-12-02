package example.javaex;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.jproxy.JProxy;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import org.itsnat.core.http.ItsNatHttpServlet;
import org.itsnat.core.tmpl.ItsNatDocumentTemplate;
import org.itsnat.core.event.ItsNatServletRequestListener;

/**
 *
 * @author jmarranz
 */
public class JProxyExLoadApp
{
    public static void init(ItsNatHttpServlet itsNatServlet,ServletConfig config)
    {    
        ServletContext context = itsNatServlet.getItsNatServletContext().getServletContext();
        String pathInput = context.getRealPath("/") + "/WEB-INF/javaex/code/";           
        String classFolder = null; // context.getRealPath("/") + "/WEB-INF/classes";
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        DiagnosticCollector<JavaFileObject> diagnostics = null;
        long scanPeriod = 200;
        
        ProxyListener proxyListener = new ProxyListener() {
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }        
        };
        
        JProxy.init(true, proxyListener, pathInput,classFolder, scanPeriod,compilationOptions,diagnostics);

        
        FalseDB db = new FalseDB();

        String pathPrefix = context.getRealPath("/") + "/WEB-INF/javaex/pages/";

        ItsNatDocumentTemplate docTemplate;
        docTemplate = itsNatServlet.registerItsNatDocumentTemplate("javaex","text/html", pathPrefix + "javaex.html");

        ItsNatServletRequestListener listener = JProxy.create(new example.javaex.JProxyExampleLoadListener(db), ItsNatServletRequestListener.class);
        docTemplate.addItsNatServletRequestListener(listener);
    } 
}




