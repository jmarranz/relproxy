package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceFileJavaNormal extends SourceUnit
{
    protected File sourceFile;
    
    public SourceFileJavaNormal(File sourceFile)
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
        
    public String getClassNameFromSourceFileJavaAbsPath(File rootPathOfSourcesFile)
    {
        String path = sourceFile.getAbsolutePath();
        String rootPathOfSources = rootPathOfSourcesFile.getAbsolutePath();
        int pos = path.indexOf(rootPathOfSources); 
        if (pos != 0) // DEBE SER 0, NO debería ocurrir
            return null;
        path = path.substring(rootPathOfSources.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
        // Quitamos la extensión (.java)
        pos = path.lastIndexOf('.');        
        if (pos == -1) return null; // NO debe ocurrir
        path = path.substring(0, pos);        
        path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
        return path;
    }         
}
