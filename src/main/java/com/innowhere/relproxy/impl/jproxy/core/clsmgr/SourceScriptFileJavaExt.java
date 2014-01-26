package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptFileJavaExt extends SourceScriptFile
{
    public SourceScriptFileJavaExt(File sourceFile)
    {
        super(sourceFile);
    }   
    
    @Override
    public String getScriptCode(String encoding,boolean[] hasHashBang)
    {
        hasHashBang[0] = false;        
        return JProxyUtil.readTextFile(sourceFile,encoding);         
    }       
}
