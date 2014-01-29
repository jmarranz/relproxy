package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

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
    
    public static SourceScriptInMemory createSourceScriptInMemory(String code)
    {
        return new SourceScriptInMemory("_jproxyShellInMemoryClass_",code);
    }
    
    @Override
    public long lastModified()
    {
        return System.currentTimeMillis(); // Siempre ha sido modificado
    }     

    @Override
    public String getScriptCode(String encoding,boolean[] hasHashBang)
    {
        hasHashBang[0] = false;
        return code;
    }
    
    public String getScriptCode()
    {
        return code;
    }
    
    public void setScriptCode(String code)
    {
        this.code = code;
    }    
    
    @Override
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        return className;
    }
}
