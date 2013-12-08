
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.jproxy.JProxyDefaultImpl;

/**
 *
 * @author jmarranz
 */
public class JProxy 
{
    public static void init(boolean enabled,RelProxyOnReloadListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        JProxyDefaultImpl.initStatic(enabled,relListener, pathInput, classFolder, scanPeriod, compilationOptions,diagnosticsListener);
    }
     
    public static <T> T create(T obj,Class<T> clasz)
    {
        return JProxyDefaultImpl.createStatic(obj, clasz);
    }
    

}
