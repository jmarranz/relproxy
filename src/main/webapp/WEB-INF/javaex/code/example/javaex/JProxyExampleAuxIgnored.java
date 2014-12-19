package example.javaex;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAuxIgnored 
{
    public static void log()
    {
        System.out.println("JProxyExampleAuxIgnored: 1 " + JProxyExampleAuxIgnored.class.getClassLoader().hashCode());
    }
}

