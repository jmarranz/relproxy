package com.innowhere.relproxy.impl.jproxy.core;

import com.innowhere.relproxy.impl.GenericProxyVersionedObject;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import java.lang.reflect.Field;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class JProxyVersionedObject extends GenericProxyVersionedObject
{    
    protected String className;    

    public JProxyVersionedObject(Object obj,JProxyInvocationHandler parent)
    {
        super(obj,parent);
        this.className = obj.getClass().getName();
    }        

    public JProxyInvocationHandler getJProxyInvocationHandler()
    {
        return (JProxyInvocationHandler)parent;
    }    
    
    @Override
    protected Class<?> reloadClass() 
    {
        JProxyEngine engine = getJProxyInvocationHandler().getJProxyImpl().getJProxyEngine();        
        engine.reloadWhenChanged();
        return (Class<?>)engine.findClass(className);           
    }
   
    @Override
    protected boolean ignoreField(Field field)
    {
        return false; // Todos cuentan (útil en Groovy no en Java)
    }    
}