package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptInMemory extends SourceScript
{
    public static final String DEFAULT_CLASS_NAME = "_jproxyMainClass_";  // OJO NO CAMBIAR, está ya documentada
    
    protected String className;
    protected String code;
    protected long timestamp;
            
    private SourceScriptInMemory(String className,String code)
    {
        this.className = className;
        setScriptCode(code,System.currentTimeMillis());
    }
    
    public static SourceScriptInMemory createSourceScriptInMemory(String code)
    {
        return new SourceScriptInMemory(DEFAULT_CLASS_NAME,code); 
    }
    
    @Override
    public long lastModified()
    {
        return timestamp; // Siempre ha sido modificado
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
    
    public final void setScriptCode(String code,long timestamp)
    {
        this.code = code;
        this.timestamp = timestamp;
    }    
    
    @Override
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        return className;
    }
}
