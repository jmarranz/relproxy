package com.innowhere.relproxy.jproxy;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * Is the interface to implement diagnostic listeners to capture compilation errors and warnings.
 * 
 * @see JProxyConfig#setJProxyDiagnosticsListener(JProxyDiagnosticsListener) 
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyDiagnosticsListener
{
    /**
     * This method is called when compilation Java code has generated diagnostics.
     * 
     * @param diagnostics the list of diagnostics.
     */
    public void onDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics);
}
