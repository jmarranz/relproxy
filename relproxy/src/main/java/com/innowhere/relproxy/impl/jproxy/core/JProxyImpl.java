package com.innowhere.relproxy.impl.jproxy.core;

import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;

/**
 *
 * @author jmarranz
 */
public abstract class JProxyImpl extends GenericProxyImpl
{
    public static JProxyImpl SINGLETON;      
    protected JProxyEngine engine;

    
    protected JProxyImpl()
    {
    }
    
    public static ClassLoader getDefaultClassLoader()
    {
        return Thread.currentThread().getContextClassLoader();
    }
    
    public ClassDescriptorSourceScript init(JProxyConfigImpl config)
    {    
        return init(config,null,null);
    }    
    
    public ClassDescriptorSourceScript init(JProxyConfigImpl config,SourceScriptRoot scriptFile,ClassLoader classLoader)
    {
        super.init(config);
        
        FolderSourceList folderSourceList = config.getFolderSourceList();
        FolderSourceList requiredExtraJarPaths = config.getRequiredExtraJarPaths();
        JProxyInputSourceFileExcludedListener excludedListener = config.getJProxyInputSourceFileExcludedListener();
        JProxyCompilerListener compilerListener = config.getJProxyCompilerListener();
        String classFolder = config.getClassFolder();
        long scanPeriod = config.getScanPeriod();
        Iterable<String> compilationOptions = config.getCompilationOptions();
        JProxyDiagnosticsListener diagnosticsListener = config.getJProxyDiagnosticsListener();
        boolean enabled = config.isEnabled();
        
        classLoader = classLoader != null ? classLoader : getDefaultClassLoader();      
        this.engine = new JProxyEngine(this,enabled,scriptFile,classLoader,folderSourceList,requiredExtraJarPaths,classFolder,scanPeriod,excludedListener,compilerListener,compilationOptions,diagnosticsListener);          
        
        return engine.init();
    }    
       
    public JProxyEngine getJProxyEngine()
    {
        return engine;
    }
    
    public boolean isEnabled()
    {
        return engine.isEnabled();
    }
    
    public boolean isRunning()
    {       
        return engine.isRunning();
    }       
    
    public boolean stop()
    {       
        return engine.stop();
    }                
    
    public boolean start()
    {       
        return engine.start();
    }     
    
    @Override
    public GenericProxyInvocationHandler createGenericProxyInvocationHandler(Object obj)    
    {
        return new JProxyInvocationHandler(obj,this);
    }
    
    public abstract Class getMainParamClass();
}
