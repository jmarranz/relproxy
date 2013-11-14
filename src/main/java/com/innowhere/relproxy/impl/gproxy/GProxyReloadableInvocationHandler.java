package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class GProxyReloadableInvocationHandler<T> implements InvocationHandler
{
    protected GProxyImpl root;
    protected GProxyVersionedObject<T> verObj;

    public GProxyReloadableInvocationHandler(T obj,GProxyImpl root)
    {
        this.root = root;
        this.verObj = new GProxyVersionedObject<T>(obj,this);
    }

    public GProxyImpl getGProxyImpl()
    {
        return root;
    }
    
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        T oldObj = verObj.getCurrent();
        T obj = verObj.getNewVersion();

        GProxyListener reloadListener = root.getGProxyListener();
        if (oldObj != obj && reloadListener != null)
            reloadListener.onReload(oldObj,obj,proxy, method,args);  

        return method.invoke(obj, args);
    }        
}