
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.RelProxyListener;
import com.innowhere.relproxy.impl.jproxy.JProxyDefaultImpl;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxy 
{
    public static void init(boolean enabled,RelProxyListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        JProxyDefaultImpl.initStatic(enabled,relListener, pathInput, classFolder, scanPeriod, compilationOptions, diagnostics);
    }
     
    public static <T> T create(T obj,Class<T> clasz)
    {
        return JProxyDefaultImpl.createStatic(obj, clasz);
    }
    

}
