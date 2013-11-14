
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.impl.gproxy.GProxyImpl;

/**
 *
 * @author jmarranz
 */
public class GProxy 
{
    protected static GProxyImpl proxyImpl = new GProxyImpl();

    public static void init(boolean devMode,GProxyGroovyScriptEngine engine,GProxyListener relListener)
    {
        proxyImpl.init(devMode, engine, relListener);
    }
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return proxyImpl.create(obj, clasz);
    }   
}
