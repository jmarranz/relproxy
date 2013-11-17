package example.jreloadex;

/**
 *
 * @author jmarranz
 */
public class JReloadExampleDocumentBase
{
    public JReloadExampleDocumentBase()
    {
        System.out.println("JReloadExampleDocumentBase 7 " + this.getClass().getClassLoader().hashCode());          
    }    
}
