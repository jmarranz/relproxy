package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JReloaderEngineShell extends JReloaderEngine
{
    public JReloaderEngineShell(File scriptFile,ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        super(scriptFile,parentClassLoader, pathSources, classFolder, scanPeriod, compilationOptions, diagnostics);
    }        
}
