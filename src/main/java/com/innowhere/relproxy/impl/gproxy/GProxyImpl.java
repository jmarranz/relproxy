package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;
import com.innowhere.relproxy.impl.jproxy.JProxyDefaultImpl;
import static com.innowhere.relproxy.impl.jproxy.JProxyImpl.SINGLETON;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class GProxyImpl extends GenericProxyImpl
{
    public static GProxyImpl SINGLETON = new GProxyImpl();    
    
    protected GProxyGroovyScriptEngine engine;
    
    public void init(ProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        super.init(relListener);
        this.engine = engine;
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }
    
    @Override
    public <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj)    
    {
        return new GProxyInvocationHandler<T>(obj,this);
    }
    
    public static void initStatic(boolean enabled,ProxyListener relListener,GProxyGroovyScriptEngine engine)
    {
        if (!enabled) return;
        
        SINGLETON = new GProxyImpl();
        SINGLETON.init(relListener,engine);
    }    
    
    public static <T> T createStatic(T obj,Class<T> clasz)
    {
        if (SINGLETON == null) 
            return obj; // No se ha llamado al init o enabled = false
        
        return SINGLETON.create(obj, clasz);
    }        
}
