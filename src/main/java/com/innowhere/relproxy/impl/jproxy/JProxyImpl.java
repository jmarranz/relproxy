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
public abstract class JProxyImpl extends GenericProxyImpl
{
    protected JReloaderEngine engine;
    
    public void init(boolean enabled,ProxyListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        super.init(enabled, relListener);
        
        if (enabled)
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();      
            this.engine = createJReloaderEngine(classLoader,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);          
        }
        else this.engine = null;
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
    
    public abstract JReloaderEngine createJReloaderEngine(ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics);
}
