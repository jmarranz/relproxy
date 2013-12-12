package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptInMemory extends SourceScript
{
    protected String className;
    protected String codeBody;
    
    public SourceScriptInMemory(String className,String codeBody)
    {
        this.className = className;
        this.codeBody = codeBody;
    }

    @Override
    public long lastModified()
    {
        return System.currentTimeMillis(); // Siempre ha sido modificado
    }     

    @Override
    public String getCodeBody(String encoding)
    {
        return codeBody;
    }
    
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        return className;
    }
}
