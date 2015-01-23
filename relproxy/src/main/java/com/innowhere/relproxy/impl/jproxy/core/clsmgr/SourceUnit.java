package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.FileExt;

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
