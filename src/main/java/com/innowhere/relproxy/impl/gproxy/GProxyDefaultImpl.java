package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.RelProxyListener;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 */
public class GProxyDefaultImpl extends GProxyImpl
{
    public static void initStatic(boolean enabled,RelProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        if (!enabled) return;
        
        checkSingleton(SINGLETON);        
        SINGLETON = new GProxyDefaultImpl();
        SINGLETON.init(relListener,engine);
    }    
    
    public static <T> T createStatic(T obj,Class<T> clasz)
    {
        if (SINGLETON == null) 
            return obj; // No se ha llamado al init o enabled = false
        
        return SINGLETON.create(obj, clasz);
    }        
}
