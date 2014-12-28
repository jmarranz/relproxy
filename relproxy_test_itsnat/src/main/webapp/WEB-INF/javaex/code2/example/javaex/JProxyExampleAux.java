package example.javaex;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAux 
{
    public static void log()
    {
        System.out.println("JProxyExampleAux: 1 " + JProxyExampleAux.class.getClassLoader().hashCode());
    }
}

