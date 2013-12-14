package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.RelProxyException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

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
    
    public void callMainMethod(LinkedList<String> argsToScript)
    {       
        try
        {
            Class scriptClass = getLastLoadedClass();            
            Object obj = scriptClass.newInstance();
            Method method = scriptClass.getDeclaredMethod("init",new Class[]{ String[].class });
            String[] argsToScriptArr = argsToScript.size() > 0 ? argsToScript.toArray(new String[argsToScript.size()]) : new String[0];
            method.invoke(obj, new Object[]{ argsToScriptArr });
        }
        catch (InstantiationException ex) { throw new RelProxyException(ex); }
        catch (IllegalAccessException ex) { throw new RelProxyException(ex); }
        catch (NoSuchMethodException ex) { throw new RelProxyException(ex); }
        catch (SecurityException ex) { throw new RelProxyException(ex); }
        catch (IllegalArgumentException ex) { throw new RelProxyException(ex); }
        catch (InvocationTargetException ex) { throw new RelProxyException(ex); }     
    }     
             
}
