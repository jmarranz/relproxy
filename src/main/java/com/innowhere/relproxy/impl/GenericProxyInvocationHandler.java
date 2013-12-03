package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyListener;
import com.innowhere.relproxy.impl.gproxy.GProxyVersionedObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyInvocationHandler<T> implements InvocationHandler
{
    protected GenericProxyImpl root;
    protected GenericProxyVersionedObject<T> verObj;
    
    public GenericProxyInvocationHandler(GenericProxyImpl root)
    {    
        this.root = root;
    }    
    
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        T oldObj = verObj.getCurrent();
        T obj = verObj.getNewVersion();

        RelProxyListener reloadListener = root.getProxyListener();
        if (oldObj != obj && reloadListener != null)
            reloadListener.onReload(oldObj,obj,proxy, method,args);  

        return method.invoke(obj, args);
    }            
}
