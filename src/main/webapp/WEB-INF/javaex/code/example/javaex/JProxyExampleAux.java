package example.javaex;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAux 
{
    public static void log()
    {
        System.out.println("JProxyExampleAux: 49 " + JProxyExampleAux.class.getClassLoader().hashCode());
    }
}

