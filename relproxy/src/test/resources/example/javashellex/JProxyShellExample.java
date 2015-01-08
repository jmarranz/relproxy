package example.javashellex;

import javax.script.ScriptEngine;

/**
 *
 * @author jmarranz
 */
public class JProxyShellExample
{
    public static void exec()
    {
        System.out.println("JProxyShellExample exec() 1 ");  
        
        // Just to show JProxy is initialized, could be useful in a sort of "server" script, of course in this case provide a explicit positive scan period to detect source changes
        /*
        JProxyShellExampleListener listener = JProxy.create(new JProxyShellExampleListenerImpl(), JProxyShellExampleListener.class);        
        if (listener instanceof JProxyShellExampleListenerImpl) throw new RuntimeException("Unexpected");
        listener.exec();
        */
    }
    
    public static void exec(ScriptEngine engine)
    {
        System.out.println("JProxyShellExample exec(ScriptEngine) 1 ");  

        // Just to show that ScriptEngine is a JProxyScriptEngine object and is already initialized, could be useful in a sort of "server" script, of course in this case provide a explicit positive scan period to detect source changes
        /*
        JProxyShellExampleListener listener = ((JProxyScriptEngine)engine).create(new JProxyShellExampleListenerImpl(), JProxyShellExampleListener.class);        
        if (listener instanceof JProxyShellExampleListenerImpl) throw new RuntimeException("Unexpected");
        listener.exec();
        */
    }    
}
