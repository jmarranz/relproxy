
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;

/**
 *
 * @author jmarranz
 */
public class JProxyConfig
{
    protected JProxyConfigImpl configImpl = new JProxyConfigImpl();

    public JProxyConfig setEnabled(boolean enabled)
    {
        configImpl.setEnabled(enabled);
        return this;
    }

    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        configImpl.setRelProxyOnReloadListener(relListener);
        return this;        
    }

    public JProxyConfig setInputPath(String inputPath)
    {
        configImpl.setInputPath(inputPath);
        return this;        
    }

    public JProxyConfig setClassFolder(String classFolder)
    {
        configImpl.setClassFolder(classFolder);
        return this;        
    }

    public JProxyConfig setScanPeriod(long scanPeriod)
    {
        configImpl.setScanPeriod(scanPeriod);
        return this;        
    }

    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions)
    {
        configImpl.setCompilationOptions(compilationOptions);
        return this;        
    }

    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener)
    {
        configImpl.setJProxyDiagnosticsListener(diagnosticsListener);
        return this;        
    }
    
    
}
