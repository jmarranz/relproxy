package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxyImpl extends GenericProxyImpl
{
    protected JReloaderEngine engine;
    
    public void init(boolean enabled,ProxyListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        super.init(enabled, relListener);
        
        JReloaderEngine engine = null;
        if (enabled)
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();      
            engine = new JReloaderEngine(classLoader,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);          
        }
        
        this.engine = engine;        
    }    
    
    public JReloaderEngine getJReloaderEngine()
    {
        return engine;
    }

    @Override
    public <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj)    
    {
        return new JProxyInvocationHandler<T>(obj,this);
    }
}
