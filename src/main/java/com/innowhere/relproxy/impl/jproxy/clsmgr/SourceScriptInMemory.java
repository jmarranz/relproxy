package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptInMemory extends SourceScript
{
    protected String className;
    protected String code;
    
    public SourceScriptInMemory(String className,String code)
    {
        this.className = className;
        this.code = code;
    }
    
    @Override
    public long lastModified()
    {
        return System.currentTimeMillis(); // Siempre ha sido modificado
    }     

    @Override
    public String getScriptCode(String encoding)
    {
        return code;
    }
    
    public void setScriptCode(String code)
    {
        this.code = code;
    }    
    
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        return className;
    }
}
