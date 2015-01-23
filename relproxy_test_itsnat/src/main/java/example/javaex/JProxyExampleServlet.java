
package example.javaex;

import com.innowhere.relproxy.jproxy.JProxy;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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

