
package example.javaex;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.itsnat.core.http.HttpServletWrapper;


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

        JProxyExLoadApp.init(itsNatServlet, config);
    }    
 
}

