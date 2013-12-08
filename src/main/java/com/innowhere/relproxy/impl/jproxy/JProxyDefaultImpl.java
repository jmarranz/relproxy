package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngineDefault;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;

/**
 *
 * @author jmarranz
 */
public class JProxyDefaultImpl extends JProxyImpl
{     
    public static void initStatic(boolean enabled,RelProxyOnReloadListener relListener,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        if (!enabled) return;
        
        checkSingletonNull(SINGLETON);
        SINGLETON = new JProxyDefaultImpl();
        SINGLETON.init(null,relListener, pathInput, classFolder, scanPeriod, compilationOptions,diagnosticsListener);
    }    
    
    public static <T> T createStatic(T obj,Class<T> clasz)
    {
        if (SINGLETON == null) 
            return obj; // No se ha llamado al init o enabled = false
        
        return SINGLETON.create(obj, clasz);
    }    

    @Override
    public JProxyEngine createJProxyEngine(ClassLoader parentClassLoader, String pathSources, String classFolder, long scanPeriod, Iterable<String> compilationOptions, JProxyDiagnosticsListener diagnosticsListener)
    {
        return new JProxyEngineDefault(parentClassLoader,pathSources,classFolder,scanPeriod,compilationOptions,diagnosticsListener);  
    }
}
