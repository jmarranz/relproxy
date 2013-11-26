
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
        GProxyImpl.SINGLETON.init(enabled,relListener, engine);
    }
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyImpl.SINGLETON.create(obj, clasz);
    }   
}
