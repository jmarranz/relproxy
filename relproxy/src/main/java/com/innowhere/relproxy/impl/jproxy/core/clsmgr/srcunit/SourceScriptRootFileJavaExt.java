package com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;

/**
 *
 * @author jmarranz
 */
public class SourceScriptRootFileJavaExt extends SourceScriptRootFile
{
    public SourceScriptRootFileJavaExt(FileExt sourceFile,FolderSourceList folderSourceList)
    {
        super(sourceFile,folderSourceList);
    }   
    
    @Override
    public String getScriptCode(String encoding,boolean[] hasHashBang)
    {
        hasHashBang[0] = false;        
        return JProxyUtil.readTextFile(sourceFile.getFile(),encoding);         
    }       
}
