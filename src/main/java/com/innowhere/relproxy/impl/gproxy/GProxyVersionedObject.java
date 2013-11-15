package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.impl.GenericProxyVersionedObject;
import java.lang.reflect.Field;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class GProxyVersionedObject<T> extends GenericProxyVersionedObject<T>
{    
    protected String path;    
    
    public GProxyVersionedObject(T obj,GProxyInvocationHandler parent)
    {
        super(obj,parent);
        this.path = obj.getClass().getName().replace('.','/');
    }        

    public T getCurrent()
    {
        return obj;
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
