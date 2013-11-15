package example.jreloadex;

/**
 *
 * @author jmarranz
 */
import example.jreloadex.FalseDB;
import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.ItsNatServletResponse;
import org.itsnat.core.html.ItsNatHTMLDocument;

public class JReloadExampleLoadListener implements ItsNatServletRequestListener
{
    protected FalseDB db;

    public JReloadExampleLoadListener() 
    { 
    }
    
    public JReloadExampleLoadListener(FalseDB db) 
    {
        this.db = db;
    }

    public void processRequest(ItsNatServletRequest request, ItsNatServletResponse response)
    { 
        System.out.println("JReloadExampleLoadListener 3 " + this.getClass().getClassLoader().hashCode());
        
        new example.jreloadex.JReloadExampleDocument(request,(ItsNatHTMLDocument)request.getItsNatDocument(),db);
    }
}