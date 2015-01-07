package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

/**
 *
 * @author jmarranz
 */
public class SourceScriptRootInMemory extends SourceScriptRoot
{
    public static final String DEFAULT_CLASS_NAME = "_jproxyMainClass_";  // OJO NO CAMBIAR, está ya documentada
    
    protected String code;
    protected long timestamp;
            
    private SourceScriptRootInMemory(String className,String code)
    {
        super(className);
        setScriptCode(code,System.currentTimeMillis());
    }
    
    public static SourceScriptRootInMemory createSourceScriptInMemory(String code)
    {
        return new SourceScriptRootInMemory(DEFAULT_CLASS_NAME,code); 
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
}
