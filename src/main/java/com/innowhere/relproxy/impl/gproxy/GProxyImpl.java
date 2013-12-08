package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.RelProxyOnReloadListener;
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
    
    public void init(RelProxyOnReloadListener relListener,GProxyGroovyScriptEngine engine)
    {
        super.init(relListener);
        this.engine = engine;
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
