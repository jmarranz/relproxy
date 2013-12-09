package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.GenericProxyConfigBaseImpl;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;

/**
 *
 * @author jmarranz
 */
public class JProxyConfigImpl extends GenericProxyConfigBaseImpl
{
    protected String inputPath;
    protected String classFolder;
    protected long scanPeriod = -1;
    protected Iterable<String> compilationOptions;
    protected JProxyDiagnosticsListener diagnosticsListener;

    public void setInputPath(String inputPath)
    {
        this.inputPath = inputPath;        
    }

    public void setClassFolder(String classFolder)
    {
        this.classFolder = classFolder;       
    }

    public void setScanPeriod(long scanPeriod)
    {
        this.scanPeriod = scanPeriod;      
    }

    public void setCompilationOptions(Iterable<String> compilationOptions)
    {
        this.compilationOptions = compilationOptions;   
    }

    public void setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener)
    {
        this.diagnosticsListener = diagnosticsListener;   
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
