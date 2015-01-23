package example.javaex.nothotreload;

/**
 *
 * @author jmarranz
 */
public class JProxyExampleAuxIgnored3 
{
    public static void log()
    {
        System.out.println("JProxyExampleAuxIgnored3: 1 " + JProxyExampleAuxIgnored3.class.getClassLoader().hashCode());
    }
}

