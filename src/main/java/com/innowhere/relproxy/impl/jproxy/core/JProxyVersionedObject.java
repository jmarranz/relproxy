package com.innowhere.relproxy.impl.jproxy.core;

import com.innowhere.relproxy.impl.GenericProxyVersionedObject;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
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

    @Override
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
        JProxyEngine engine = getJProxyInvocationHandler().getJProxyImpl().getJProxyEngine();        
        return (Class<T>)engine.findClass(className);           
    }
   
    @Override
    protected boolean ignoreField(Field field)
    {
        return false; // Todos cuentan (útil en Groovy no en Java)
    }    
}