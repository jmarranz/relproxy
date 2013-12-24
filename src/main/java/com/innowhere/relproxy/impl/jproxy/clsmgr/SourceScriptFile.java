package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.RelProxyException;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptFile extends SourceScript
{
    protected File sourceFile;
    
    public SourceScriptFile(File sourceFile)
    {
        this.sourceFile = sourceFile;
    }

    @Override
    public long lastModified()
    {
        return sourceFile.lastModified();
    }    

    public File getFile()
    {
        return sourceFile;
    }
    
    @Override
    public String getScriptCode(String encoding)
    {
        String codeBody = JProxyUtil.readTextFile(sourceFile,encoding);         
        // Eliminamos la primera línea #!  (debe estar en la primera línea y sin espacios antes)
        if (!codeBody.startsWith("#!"))
            throw new RelProxyException("The first line of the script must start with #!");
        
        int pos = codeBody.indexOf('\n');
        if (pos != -1) // Rarísimo que sólo esté el hash bang (script vacío)
        {
            codeBody = codeBody.substring(pos + 1);
        }    
        return codeBody;
    }    
    
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        String path = sourceFile.getAbsolutePath();
        String rootPathOfSources = rootPathOfSourcesFile.getAbsolutePath();
        // path es absoluto, preferentemente obtenido con File.getAbsolutePath()
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
