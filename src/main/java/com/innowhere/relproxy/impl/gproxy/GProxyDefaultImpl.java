package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.RelProxyOnReloadListener;

/**
 *
 * @author jmarranz
 */
public class GProxyDefaultImpl extends GProxyImpl
{
    public static void initStatic(boolean enabled,RelProxyOnReloadListener relListener,GProxyGroovyScriptEngine engine)
    {
        if (!enabled) return;
        
        checkSingletonNull(SINGLETON);        
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
