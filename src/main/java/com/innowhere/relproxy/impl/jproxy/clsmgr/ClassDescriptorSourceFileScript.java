package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.ProxyException;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileScript extends ClassDescriptorSourceFile
{
    protected String source;
    
    public ClassDescriptorSourceFileScript(JReloaderEngine engine,String className,File sourceFile,long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
        
        
        String codeBody = JReloaderUtil.readTextFile(sourceFile,getEncoding());         
        // Eliminamos la primera línea #!  (debe estar en la primera línea y sin espacios antes)
        if (!codeBody.startsWith("#!"))
            throw new ProxyException("The first line of the script must start with #!");
        
        int pos = codeBody.indexOf('\n');
        if (pos != -1) // Rarísimo que sólo esté el hash bang (script vacío)
        {
            codeBody = codeBody.substring(pos + 1);
        }
        
        StringBuilder code = new StringBuilder();
        code.append("public class " + className + "\n");
        code.append("{\n");  
        code.append("  public void init(String[] args)\n");        
        code.append("  {\n");   
        code.append(codeBody);        
        code.append("  }\n");        
        code.append("}\n");         
        this.source = code.toString();        
    }
    
    public final String getEncoding()
    {
        return engine.getSourceEncoding();
    }
    
    @Override
    public void updateTimestamp(long timestamp)
    {
        long oldTimestamp = this.timestamp;
        if (oldTimestamp != timestamp)
            JReloaderUtil.readTextFile(sourceFile,getEncoding());   
        super.updateTimestamp(timestamp);
    }
    
    public String getSourceCode()
    {
        return source;
    }
    
    public static String getClassNameFromSourceFileScriptAbsPath(String path,String rootPathOfSources)
    {
        // path y rootPathOfSources son absolutos, preferentemente obtenidos con File.getAbsolutePath()
        int pos = path.indexOf(rootPathOfSources); 
        if (pos != 0) // DEBE SER 0, NO debería ocurrir
            return null;
        path = path.substring(rootPathOfSources.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
        // En teoría NO tiene extensión pero es posible que se le haya dado (ej .jsh), la quitamos si existe
        pos = path.lastIndexOf('.');        
        if (pos != -1) 
            path = path.substring(0, pos);        
        
        path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
        return path;
    }              
}
