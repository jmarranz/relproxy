package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.RelProxyException;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public abstract class SourceUnit
{
    protected String className;
        
    public SourceUnit(String className)
    {
        this.className = className;
    }
    
    public abstract long lastModified();
    
    protected static String buildClassNameFromFile(File sourceFile,FolderSourceList sourceList)
    {
        return sourceList.buildClassNameFromFile(sourceFile);
    }               
    
    protected static String buildClassNameFromFile(File sourceFile,File rootFolderOfSources)
    {
        return FolderSourceList.buildClassNameFromFile(sourceFile,rootFolderOfSources);
    }                   
    
    public String getClassName()
    {
        return className;
    }             
  
}
