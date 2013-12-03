package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyImpl
{
    protected RelProxyListener reloadListener;
    
    public GenericProxyImpl()
    {
    }

    protected static void checkSingleton(GenericProxyImpl singleton)
    {
        if (singleton != null) 
            throw new RelProxyException("Already initialized");
    }
    
    protected void init(RelProxyListener relListener)
    {
        this.reloadListener = relListener; 
    }    
    
    public RelProxyListener getProxyListener()
    {
        return reloadListener;
    }    
    
    public <T> T create(T obj,Class<T> clasz)
    {
        if (obj == null) return null;
        
        InvocationHandler handler = createGenericProxyInvocationHandler(obj);
        
        T proxy = (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(),new Class[] { clasz }, handler);   
        return proxy;
    }        
    
    public abstract <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj);    
}
