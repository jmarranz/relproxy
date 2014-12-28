package example.javaex;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAuxIgnored 
{
    public static void log()
    {
        System.out.println("JProxyExampleAuxIgnored: 2 " + JProxyExampleAuxIgnored.class.getClassLoader().hashCode());
    }
}

