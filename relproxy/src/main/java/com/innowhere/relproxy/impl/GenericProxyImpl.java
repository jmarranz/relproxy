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

    public static void checkSingletonNull(GenericProxyImpl singleton)
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
        
        return (T)create(obj,new Class[] { clasz });
    }
  
    public Object create(Object obj,Class[] classes)
    {       
        if (obj == null) return null;   
        
        InvocationHandler handler = createGenericProxyInvocationHandler(obj);
        
        Object proxy = Proxy.newProxyInstance(obj.getClass().getClassLoader(),classes, handler);   
        return proxy;
    }    
            
            
    public abstract GenericProxyInvocationHandler createGenericProxyInvocationHandler(Object obj);    
}
