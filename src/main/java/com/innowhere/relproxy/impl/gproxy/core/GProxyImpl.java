package com.innowhere.relproxy.impl.gproxy.core;

import com.innowhere.relproxy.impl.gproxy.GProxyConfigImpl;
import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;


/**
 *
 * @author jmarranz
 */
public abstract class GProxyImpl extends GenericProxyImpl
{
    public static GProxyImpl SINGLETON;    
    protected GProxyGroovyScriptEngine engine;
    
    public void init(GProxyConfigImpl config)
    {
        super.init(config);
        this.engine = config.getGProxyGroovyScriptEngine();
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }   
    
    @Override
    public <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj)    
    {
        return new GProxyInvocationHandler<T>(obj,this);
    }
}
