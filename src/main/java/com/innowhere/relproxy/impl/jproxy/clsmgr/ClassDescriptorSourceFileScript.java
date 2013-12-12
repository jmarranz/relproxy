package com.innowhere.relproxy.impl.jproxy.clsmgr;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileScript extends ClassDescriptorSourceFile
{
    protected String source;
    
    public ClassDescriptorSourceFileScript(JProxyEngine engine,String className,SourceScript sourceFile,long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
                
        generateSourceCode();
    }
    
    public SourceScript getSourceFileScript()
    {
        return (SourceScript)sourceFile;
    }
    
    private void generateSourceCode()
    {
        String codeBody = getSourceFileScript().getCodeBody(getEncoding());         
        
        StringBuilder code = new StringBuilder();
        code.append("public class " + className + " { public void init(String[] args) {\n"); // Lo ponemos todo en una línea para que en caso de error la línea de error coincida con el script original pues hemos quitado la primera línea #!
        code.append(codeBody);        
        code.append("  }\n");        
        code.append("}\n");         
        this.source = code.toString();        
    }
    
    @Override
    public void updateTimestamp(long timestamp)
    {
        long oldTimestamp = this.timestamp;
        if (oldTimestamp != timestamp)
            generateSourceCode();   
        super.updateTimestamp(timestamp);
    }
    
    public String getSourceCode()
    {
        return source;
    }
    
             
}
