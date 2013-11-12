package inexp.jreloadex;

import com.innowhere.relproxy.jproxy.JProxyListener;
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
public class JReloadExLoadApp
{
    public static void init(ItsNatHttpServlet itsNatServlet,ServletConfig config)
    {    
        ServletContext context = itsNatServlet.getItsNatServletContext().getServletContext();
        String pathInput = context.getRealPath("/") + "/WEB-INF/jreloadex/code/";           
        String classFolder = context.getRealPath("/") + "/WEB-INF/classes";
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        DiagnosticCollector<JavaFileObject> diagnostics = null;
        
        JProxy.init(true, pathInput,classFolder, 200,compilationOptions,diagnostics, new JProxyListener() {
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }
        });
        
        FalseDB db = new FalseDB();

        String pathPrefix = context.getRealPath("/") + "/WEB-INF/jreloadex/pages/";

        ItsNatDocumentTemplate docTemplate;
        docTemplate = itsNatServlet.registerItsNatDocumentTemplate("jreloadex","text/html", pathPrefix + "jreloadex.html");

        ItsNatServletRequestListener listener = JProxy.create(new inexp.jreloadex.JReloadExampleLoadListener(db), ItsNatServletRequestListener.class);
        docTemplate.addItsNatServletRequestListener(listener);
    } 
}




