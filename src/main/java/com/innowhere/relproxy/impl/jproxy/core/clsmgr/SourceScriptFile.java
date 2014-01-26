package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public abstract class SourceScriptFile extends SourceScript
{
    protected File sourceFile;
    
    public SourceScriptFile(File sourceFile)
    {
        this.sourceFile = sourceFile;         
    }
    
    public static SourceScriptFile createSourceScriptFile(File sourceFile)
    {
        String ext = JProxyUtil.getFileExtension(sourceFile); // Si no tiene extensión devuelve ""
        if ("java".equals(ext))
            return new SourceScriptFileJavaExt(sourceFile);
        else
            return new SourceScriptFileOtherExt(sourceFile);
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
    public String getClassNameFromSourceFileScriptAbsPath(File rootPathOfSourcesFile)
    {
        String path = sourceFile.getAbsolutePath();
        String rootPathOfSources = rootPathOfSourcesFile.getAbsolutePath();
        // path es absoluto, preferentemente obtenido con File.getAbsolutePath()
        int pos = path.indexOf(rootPathOfSources); 
        if (pos != 0) // DEBE SER 0, NO debería ocurrir
            return null;
        path = path.substring(rootPathOfSources.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
        // Puede no tener extensión o bien ser .java o bien ser una inventada (ej .jsh), la quitamos si existe
        pos = path.lastIndexOf('.');        
        if (pos != -1) 
            path = path.substring(0, pos);        
        
        path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
        return path;
    }     
}
