package com.innowhere.relproxy.jproxy;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public interface JProxyDiagnosticsListener
{
    public void onDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics);
}
