package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;

/**
 *
 * @author jmarranz
 */
public class JProxyEngineDefault extends JProxyEngine
{
    public JProxyEngineDefault(ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        super(null,parentClassLoader, pathSources, classFolder, scanPeriod, compilationOptions, diagnosticsListener);
    }    
}
