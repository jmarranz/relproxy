package com.innowhere.relproxy.impl.jproxy.clsmgr;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JReloaderEngineDefault extends JReloaderEngine
{
    public JReloaderEngineDefault(ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        super(parentClassLoader, pathSources, classFolder, scanPeriod, compilationOptions, diagnostics);
    }    
}
