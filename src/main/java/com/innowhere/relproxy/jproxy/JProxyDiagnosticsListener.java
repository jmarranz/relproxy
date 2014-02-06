package com.innowhere.relproxy.jproxy;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyDiagnosticsListener
{
    public void onDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics);
}
