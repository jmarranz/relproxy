
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;

/**
 *
 * @author jmarranz
 */
public interface JProxyConfig
{
    public JProxyConfig setEnabled(boolean enabled);

    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener);

    public JProxyConfig setInputPath(String inputPath);

    public JProxyConfig setClassFolder(String classFolder);

    public JProxyConfig setScanPeriod(long scanPeriod);
    
    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions);

    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener);
}
