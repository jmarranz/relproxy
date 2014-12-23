package example.javaex.hotreload;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAux2 
{
    public static void log()
    {
        System.out.println("JProxyExampleAux2: 1 " + JProxyExampleAux2.class.getClassLoader().hashCode());
    }
}

