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
    protected final FalseDB db;
    protected static Integer testStatic = 10;
    protected static final Integer testStaticFinal = 11;

    public JProxyExampleLoadListener()
    {
        this(null);
    }

    public JProxyExampleLoadListener(FalseDB db)
    {
        this.db = db;
    }


    @Override
    public void processRequest(ItsNatServletRequest request, ItsNatServletResponse response)
    {
        System.out.println("JProxyExampleLoadListener 1 " + this.getClass().getClassLoader().hashCode());
        new example.javaex.JProxyExampleDocument(request,(ItsNatHTMLDocument)request.getItsNatDocument(),db);
    }
}

