
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.RelProxyListener;
import com.innowhere.relproxy.impl.gproxy.GProxyDefaultImpl;

/**
 *
 * @author jmarranz
 */
public class GProxy 
{
    public static void init(boolean enabled,RelProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        GProxyDefaultImpl.initStatic(enabled,relListener, engine);
    }
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyDefaultImpl.createStatic(obj, clasz);
    }   
}
