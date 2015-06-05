package com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;

/**
 *
 * @author jmarranz
 */
public abstract class SourceUnit
{
    protected final String className;
        
    public SourceUnit(String className)
    {
        this.className = className;
    }
    
    public abstract long lastModified();
    
    protected static String buildClassNameFromFile(FileExt sourceFile,FolderSourceList sourceList)
    {
        return sourceList.buildClassNameFromFile(sourceFile);
    }               
    
    protected static String buildClassNameFromFile(FileExt sourceFile,FileExt rootFolderOfSources)
    {
        return FolderSourceList.buildClassNameFromFile(sourceFile,rootFolderOfSources);
    }                   
    
    public String getClassName()
    {
        return className;
    }             
  
}
