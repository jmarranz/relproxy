package example.javaex;

/**
 *
 * @author jmarranz
 */
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.ItsNatServletResponse;
import org.itsnat.core.html.ItsNatHTMLDocument;

public class JProxyExampleLoadListener implements ItsNatServletRequestListener
{
    protected FalseDB db;

    public JProxyExampleLoadListener() 
    { 
    }
    
    public JProxyExampleLoadListener(FalseDB db) 
    {
        this.db = db;
    }

    @Override
    public void processRequest(ItsNatServletRequest request, ItsNatServletResponse response)
    { 
        new example.javaex.JProxyExampleDocument(request,(ItsNatHTMLDocument)request.getItsNatDocument(),db);
    }
}