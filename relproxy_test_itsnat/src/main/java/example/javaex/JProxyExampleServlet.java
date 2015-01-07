
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
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.http.HttpServletWrapper;
import org.itsnat.core.tmpl.ItsNatDocumentTemplate;


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

        ServletContext context = config.getServletContext();
        
        String pathPrefix = context.getRealPath("/") + "/WEB-INF/javaex/pages/";

        ItsNatDocumentTemplate docTemplate;
        docTemplate = itsNatServlet.registerItsNatDocumentTemplate("javaex","text/html", pathPrefix + "javaex.html");

        FalseDB db = new FalseDB();        
        
        ItsNatServletRequestListener listener = JProxy.create(new example.javaex.JProxyExampleLoadListener(db), ItsNatServletRequestListener.class);
        docTemplate.addItsNatServletRequestListener(listener);
    }    
 
}

