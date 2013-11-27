
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.gproxy.GProxyImpl;

/**
 *
 * @author jmarranz
 */
public class GProxy 
{
    public static void init(boolean enabled,ProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        GProxyImpl.initStatic(enabled,relListener, engine);
    }
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyImpl.createStatic(obj, clasz);
    }   
}
