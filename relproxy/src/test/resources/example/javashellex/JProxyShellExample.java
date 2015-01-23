package example.javashellex;

import javax.script.ScriptEngine;
import com.innowhere.relproxy.jproxy.JProxyScriptEngine;

/**
 *
 * @author jmarranz
 */
public class JProxyShellExample
{
    public static void exec()
    {
        System.out.println("JProxyShellExample exec() 1 ");         
    }
    
    public static void exec(ScriptEngine engine)
    {
        JProxyScriptEngine jengine = ((JProxyScriptEngine)engine); // Just to show that ScriptEngine is a JProxyScriptEngine object
        System.out.println("JProxyShellExample exec(ScriptEngine) 1 ");  
    }    
}
