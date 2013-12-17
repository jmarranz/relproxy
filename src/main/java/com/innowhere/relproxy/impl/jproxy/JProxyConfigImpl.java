package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.GenericProxyConfigBaseImpl;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;

/**
 *
 * @author jmarranz
 */
public class JProxyConfigImpl extends GenericProxyConfigBaseImpl implements JProxyConfig
{
    protected String inputPath;
    protected String classFolder;
    protected long scanPeriod = -1;
    protected Iterable<String> compilationOptions;
    protected JProxyDiagnosticsListener diagnosticsListener;

    public JProxyConfig setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        this.relListener = relListener;  
        return this;        
    }    
    
    public JProxyConfig setInputPath(String inputPath)
    {
        this.inputPath = inputPath;   
        return this;
    }

    public JProxyConfig setClassFolder(String classFolder)
    {
        this.classFolder = classFolder;    
        return this;        
    }

    public JProxyConfig setScanPeriod(long scanPeriod)
    {
        if (scanPeriod == 0) throw new RelProxyException("scanPeriod cannot be zero");
        this.scanPeriod = scanPeriod;      
        return this;        
    }

    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions)
    {
        this.compilationOptions = compilationOptions;  
        return this;        
    }

    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener)
    {
        this.diagnosticsListener = diagnosticsListener;
        return this;        
    }    

    public String getInputPath()
    {
        return inputPath;
    }

    public String getClassFolder()
    {
        return classFolder;
    }

    public long getScanPeriod()
    {
        return scanPeriod;
    }

    public Iterable<String> getCompilationOptions()
    {
        return compilationOptions;
    }

    public JProxyDiagnosticsListener getJProxyDiagnosticsListener()
    {
        return diagnosticsListener;
    }
  
}
