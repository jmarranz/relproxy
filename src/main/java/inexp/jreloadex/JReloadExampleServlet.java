
package inexp.jreloadex;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.itsnat.core.http.HttpServletWrapper;


/**
 * 
 * @author jmarranz
 */
public class JReloadExampleServlet extends HttpServletWrapper
{  
    public JReloadExampleServlet()
    {
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);

        JReloadExLoadApp.init(itsNatServlet, config);
    }    
 
}

