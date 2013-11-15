package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 */
public class GProxyImpl extends GenericProxyImpl
{
    protected GProxyGroovyScriptEngine engine;
    
    public void init(boolean enabled,ProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        super.init(enabled, relListener);
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
