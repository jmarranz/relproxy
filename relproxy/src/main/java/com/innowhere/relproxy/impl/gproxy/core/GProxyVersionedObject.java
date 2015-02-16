package com.innowhere.relproxy.impl.gproxy.core;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.impl.GenericProxyVersionedObject;
import java.lang.reflect.Field;

/**
 *
 * @author jmarranz
 */
public class GProxyVersionedObject extends GenericProxyVersionedObject
{    
    protected String path;    
    
    public GProxyVersionedObject(Object obj,GProxyInvocationHandler parent)
    {
        super(obj,parent);
        this.path = obj.getClass().getName().replace('.','/');
    }        


    public GProxyInvocationHandler getGProxyInvocationHandler()
    {
        return (GProxyInvocationHandler)parent;
    }

    @Override    
    protected <T> Class<T> reloadClass() 
    {
        GProxyGroovyScriptEngine engine = getGProxyInvocationHandler().getGProxyImpl().getGProxyGroovyScriptEngine();
        
        try
        {   
            return engine.loadScriptByName(path + ".groovy");  //Ej: example/groovyex/GroovyExampleLoadListener.groovy
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err);
            return null;
        }                  
    }
    
    @Override
    protected boolean ignoreField(Field field)
    {
        return field.getName().startsWith("__timeStamp__"); // Este atributo cambia de nombre en cada reload, no lo consideramos
    }
}
