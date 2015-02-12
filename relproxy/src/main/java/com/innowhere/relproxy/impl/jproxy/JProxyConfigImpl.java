package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.GenericProxyConfigBaseImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class JProxyConfigImpl extends GenericProxyConfigBaseImpl implements JProxyConfig
{
    protected File folderSources; 
    protected FolderSourceList folderSourceList;
    protected FolderSourceList requiredExtraJarPaths;
    protected JProxyInputSourceFileExcludedListener excludedListener;
    protected JProxyCompilerListener compilerListener;
    protected String classFolder;
    protected long scanPeriod = -1;
    protected Iterable<String> compilationOptions;
    protected JProxyDiagnosticsListener diagnosticsListener;
    protected boolean test = false;
    
    @Override
    public JProxyConfig setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    @Override
    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        this.relListener = relListener;  
        return this;        
    }    
    
    @Override
    public JProxyConfig setInputPath(String inputPath)
    {
        setInputPaths(inputPath != null ? new String[]{inputPath} : null); // inputPath es null en el caso de shell interactive
        return this;
    }

    @Override
    public JProxyConfig setInputPaths(String[] inputPaths)
    {
        this.folderSourceList = new FolderSourceList(inputPaths); // inputPaths es null en el caso de shell interactive
        return this;
    }    

    @Override    
    public JProxyConfig setRequiredExtraJarPaths(String[] inputJarPaths)  
    {
        this.requiredExtraJarPaths = new FolderSourceList(inputJarPaths); // inputPaths es null en el caso de shell interactive
        return this;
    }    
    
    @Override    
    public JProxyConfig setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener excludedListener)    
    {
        this.excludedListener = excludedListener;
        return this;
    }
    
    @Override    
    public JProxyConfig setJProxyCompilerListener(JProxyCompilerListener compilerListener)    
    {
        this.compilerListener = compilerListener;
        return this;
    }    
    
    @Override
    public JProxyConfig setClassFolder(String classFolder)
    {
        this.classFolder = classFolder;    
        return this;        
    }

    @Override
    public JProxyConfig setScanPeriod(long scanPeriod)
    {
        if (scanPeriod == 0) throw new RelProxyException("scanPeriod cannot be zero");
        this.scanPeriod = scanPeriod;      
        return this;        
    }

    @Override
    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions)
    {
        this.compilationOptions = compilationOptions;  
        return this;        
    }

    @Override
    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener)
    {
        this.diagnosticsListener = diagnosticsListener;
        return this;        
    }    
   
    public FolderSourceList getFolderSourceList()
    {
        return folderSourceList;
    }
    
    public FolderSourceList getRequiredExtraJarPaths()
    {
        return requiredExtraJarPaths;
    }        
    
    public JProxyInputSourceFileExcludedListener getJProxyInputSourceFileExcludedListener()
    {
        return excludedListener;
    }            
    
    public JProxyCompilerListener getJProxyCompilerListener()
    {
        return compilerListener;
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
  
    public boolean isTest()
    {
        return test;
    }

    public void setTest(boolean test)
    {
        this.test = test;
    }
    
}
