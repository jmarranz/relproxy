package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;

/**
 *
 * @author jmarranz
 */
public abstract class JProxyImpl extends GenericProxyImpl
{
    public static JProxyImpl SINGLETON;      
    protected JProxyEngine engine;
    
    public static ClassLoader getDefaultClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }
    
    public ClassDescriptorSourceFileScript init(ClassLoader classLoader,JProxyConfigImpl config)
    {
        super.init(config);
        
        String pathInput = config.getInputPath();
        String classFolder = config.getClassFolder();
        long scanPeriod = config.getScanPeriod();
        Iterable<String> compilationOptions = config.getCompilationOptions();
        JProxyDiagnosticsListener diagnosticsListener = config.getJProxyDiagnosticsListener();
        
        classLoader = classLoader != null ? classLoader : getDefaultClassLoader();      
        this.engine = createJProxyEngine(classLoader,pathInput,classFolder,scanPeriod,compilationOptions,diagnosticsListener);          
        return engine.init();
    }    
   
    
    public JProxyEngine getJProxyEngine()
    {
        return engine;
    }

    @Override
    public <T> GenericProxyInvocationHandler<T> createGenericProxyInvocationHandler(T obj)    
    {
        return new JProxyInvocationHandler<T>(obj,this);
    }
    
    public abstract JProxyEngine createJProxyEngine(ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener);
}
