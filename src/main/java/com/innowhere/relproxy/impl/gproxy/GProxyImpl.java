package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.gproxy.GProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public class GProxyImpl 
{
    protected GProxyGroovyScriptEngine engine;
    protected boolean developmentMode = false;
    protected GProxyListener reloadListener;
    
    public void init(boolean devMode,GProxyGroovyScriptEngine engine,GProxyListener relListener)
    {
        this.engine = engine;
        this.developmentMode = devMode;
        this.reloadListener = relListener; 
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }
    
    public GProxyListener getGProxyListener()
    {
        return reloadListener;
    }
    
    public <T> T create(T obj,Class<T> clasz)
    {
        if (!developmentMode || engine == null)
            return obj;
        
        if (obj == null) return null;
        
        InvocationHandler handler = new GProxyReloadableInvocationHandler<T>(obj,this);
        
        T proxy = (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(),new Class[] { clasz }, handler);   
        return proxy;
    }    
}
