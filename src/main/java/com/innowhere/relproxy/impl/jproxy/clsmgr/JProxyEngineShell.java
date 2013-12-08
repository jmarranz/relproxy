package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class JProxyEngineShell extends JProxyEngine
{
    public JProxyEngineShell(File scriptFile,ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        super(scriptFile,parentClassLoader, pathSources, classFolder, scanPeriod, compilationOptions, diagnosticsListener);
    }        
}
