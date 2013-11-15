package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.GenericProxyVersionedObject;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import java.lang.reflect.Field;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class JProxyVersionedObject<T> extends GenericProxyVersionedObject<T>
{    
    protected String className;    

    public JProxyVersionedObject(T obj,JProxyInvocationHandler parent)
    {
        super(obj,parent);
        this.className = obj.getClass().getName();
    }        

    public T getCurrent()
    {
        return obj;
    }

    public JProxyInvocationHandler getJProxyInvocationHandler()
    {
        return (JProxyInvocationHandler)parent;
    }    
    
    @Override
    protected <T> Class<T> reloadClass() 
    {
        JReloaderEngine engine = getJProxyInvocationHandler().getJProxyImpl().getJReloaderEngine();        
        return (Class<T>)engine.findClass(className);           
    }
   
    @Override
    protected boolean ignoreField(Field field)
    {
        return false; // Todos cuentan (útil en Groovy no en Java)
    }    
}