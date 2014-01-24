package example.javashellex;

import com.innowhere.relproxy.jproxy.JProxy;

/**
 *
 * @author jmarranz
 */
public class JProxyShellExample
{
    public static void exec()
    {
        System.out.println("JProxyShellExample 1 ");  
        
        // Just to show JProxy is initialized, could be useful in a sort of "server" script, of course in this case provide a explicit positive scan period to detect source changes
        JProxyShellExampleListener listener = JProxy.create(new JProxyShellExampleListenerImpl(), JProxyShellExampleListener.class);        
        if (listener instanceof JProxyShellExampleListenerImpl) throw new RuntimeException("Unexpected");
        listener.exec();
    }
}
