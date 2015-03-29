package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyInvocationHandler implements InvocationHandler
{
    protected GenericProxyImpl root;
    protected GenericProxyVersionedObject verObj;
    
    public GenericProxyInvocationHandler(GenericProxyImpl root)
    {    
        this.root = root;
    }    
    
    private Object getCurrent()
    {
        return verObj.getCurrent();
    }
    
    private Object getNewVersion() throws Throwable
    {
        return verObj.getNewVersion();
    }    
    
    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        Object oldObj = getCurrent();
        Object obj = getNewVersion();

        RelProxyOnReloadListener reloadListener = root.getRelProxyOnReloadListener();
        if (oldObj != obj && reloadListener != null)
            reloadListener.onReload(oldObj,obj,proxy,method,args);  

        if (args != null && args.length == 1)
        {
            // Conseguimos que en proxy1.equals(proxy2) se usen los objetos asociados no los propios proxies, para ello obtenemos el objeto asociado al parámetro 
            // No hace falta que equals forme parte de la interface
            Object param = args[0];
            if (param instanceof Proxy &&  // Si es una clase generada com.sun.proxy.$ProxyN (N=1,2...) es también derivada de Proxy
                method.getName().equals("equals") && 
                method.getReturnType().equals(boolean.class)) 
            {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 1 && paramTypes[0].equals(Object.class))
                {
                    InvocationHandler paramInvHandler = Proxy.getInvocationHandler(param);
                    if (paramInvHandler instanceof GenericProxyInvocationHandler)
                    {
                        args[0] = ((GenericProxyInvocationHandler)paramInvHandler).getCurrent(); // reemplazamos el Proxy por el objeto asociado
                    }
                }
            }
        }

        return method.invoke(obj, args);
    }            
}
