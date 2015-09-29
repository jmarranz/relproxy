package com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit;

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
    
    public boolean isEmptyCode()
    {
        // Si code es "" la clase especial se genera pero no hace nada simplemente devuelve un null.
        // Este es el caso en el que utilizamos RelProxy embebido en un framework utilizando la API ScriptEngine pero únicamente porque se usa una API basada 
        // en interfaces, pero tiene el inconveniente de generarse un SourceScriptRootInMemory inútil que no hace nada        
        return code.isEmpty();
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
