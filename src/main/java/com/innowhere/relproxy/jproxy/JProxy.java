
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.jproxy.JProxyImpl;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxy 
{
    protected static JProxyImpl proxyImpl = new JProxyImpl();    
    
    public static void init(boolean enabled,ProxyListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        proxyImpl.init(enabled,relListener, pathInput, classFolder, scanPeriod, compilationOptions, diagnostics);
    }
     
    public static <T> T create(T obj,Class<T> clasz)
    {
        return proxyImpl.create(obj, clasz);
    }
    

}
