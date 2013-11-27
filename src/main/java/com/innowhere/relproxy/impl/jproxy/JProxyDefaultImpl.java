package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngineDefault;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxyDefaultImpl extends JProxyImpl
{     
    public static void initStatic(boolean enabled,ProxyListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        if (!enabled) return;
        
        SINGLETON = new JProxyDefaultImpl();
        SINGLETON.init(relListener, pathInput, classFolder, scanPeriod, compilationOptions, diagnostics);
    }    
    
    public static <T> T createStatic(T obj,Class<T> clasz)
    {
        if (SINGLETON == null) 
            return obj; // No se ha llamado al init o enabled = false
        
        return SINGLETON.create(obj, clasz);
    }    

    @Override
    public JReloaderEngine createJReloaderEngine(ClassLoader parentClassLoader, String pathSources, String classFolder, long scanPeriod, Iterable<String> compilationOptions, DiagnosticCollector<JavaFileObject> diagnostics)
    {
        return new JReloaderEngineDefault(parentClassLoader,pathSources,classFolder,scanPeriod,compilationOptions,diagnostics);  
    }
}
