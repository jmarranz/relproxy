
package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.jproxy.JProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 */
public class JProxyReloadableInvocationHandler<T> implements InvocationHandler
{
    protected JProxyImpl root;    
    protected JProxyVersionedObject<T> verObj;

    public JProxyReloadableInvocationHandler(T obj,JProxyImpl root)
    {
        this.root = root;
        this.verObj = new JProxyVersionedObject<T>(obj,this);
    }

    public JProxyImpl getJProxyImpl()
    {
        return root;
    }    
    
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        T oldObj = verObj.getCurrent();
        T obj = verObj.getNewVersion();

        JProxyListener reloadListener = root.getJProxyListener();        
        if (oldObj != obj && reloadListener != null)
            reloadListener.onReload(oldObj,obj,proxy, method,args);  

        return method.invoke(obj, args);
    }        
}