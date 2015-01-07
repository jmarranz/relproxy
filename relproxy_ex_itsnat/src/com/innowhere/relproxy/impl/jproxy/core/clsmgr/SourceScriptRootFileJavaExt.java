package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptRootFileJavaExt extends SourceScriptRootFile
{
    public SourceScriptRootFileJavaExt(File sourceFile,FolderSourceList folderSourceList)
    {
        super(sourceFile,folderSourceList);
    }   
    
    @Override
    public String getScriptCode(String encoding,boolean[] hasHashBang)
    {
        hasHashBang[0] = false;        
        return JProxyUtil.readTextFile(sourceFile,encoding);         
    }       
}
