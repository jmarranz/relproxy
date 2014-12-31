package example.javaex.nothotreload;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAuxIgnored2 
{
    public static void log()
    {
        System.out.println("JProxyExampleAuxIgnored2: 2 " + JProxyExampleAuxIgnored2.class.getClassLoader().hashCode());
    }
}

