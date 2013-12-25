package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyImpl
{
    protected RelProxyOnReloadListener reloadListener;
    
    public GenericProxyImpl()
    {
    }

    protected static void checkSingletonNull(GenericProxyImpl singleton)
    {
        if (singleton != null) 
            throw new RelProxyException("Already initialized");
    }
    
    protected static void checkSingletonExists(GenericProxyImpl singleton)
    {
        if (singleton == null) 
            throw new RelProxyException("Execute first the init method");
    }    
    
    protected void init(GenericProxyConfigBaseImpl config)
    {
        this.reloadListener = config.getRelProxyOnReloadListener(); 
    }    
    
    public RelProxyOnReloadListener getRelProxyOnReloadListener()
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
