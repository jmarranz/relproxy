package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.ProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyImpl
{
    protected boolean enabled = false;    
    protected ProxyListener reloadListener;
    
    public GenericProxyImpl()
    {
    }

    protected void init(boolean enabled,ProxyListener relListener)
    {
        this.enabled = enabled;
        this.reloadListener = relListener; 
    }    
    
    public ProxyListener getProxyListener()
    {
        return reloadListener;
    }    
    
    public <T> T create(T obj,Class<T> clasz)
    {
        if (!enabled)
            return obj;
        
        if (obj == null) return null;
        
        InvocationHandler handler = createGenericProxyInvocationHandler(obj);
        
        T proxy = (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(),new Class[] { clasz }, handler);   
        return proxy;
    }        
    
    public abstract <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj);    
}
