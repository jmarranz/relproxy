
package inexp.groovyex;

import org.itsnat.core.event.ItsNatServletRequestListener;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.ItsNatServletResponse;
import inexp.groovyex.FalseDB;

class GroovyExampleLoadListener implements ItsNatServletRequestListener
{
    def db

    GroovyExampleLoadListener() 
    { 
    }
    
    GroovyExampleLoadListener(FalseDB db) // Explicit type tells Groovy to reload FalseDB class when changed
    {
        this.db = db;
    }

    void processRequest(ItsNatServletRequest request, ItsNatServletResponse response)
    { 
        println("GroovyExampleLoadListener 3 ");
        
        new inexp.groovyex.GroovyExampleDocument(request.getItsNatDocument(),db);
    }
}
