package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.RelProxyException;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class FolderSourceList
{
    protected File[] sourceList;
    
    public FolderSourceList(String[] sourcePathList)
    {
        if (sourcePathList != null) // En el caso de shell interactivo es null
        {
            // El convertir siempre a File los paths es para normalizar paths
            this.sourceList = new File[sourcePathList.length];
            for(int i = 0; i < sourcePathList.length; i++) 
                sourceList[i] = new File(sourcePathList[i]);
        }
    }    
    
    public File[] getArray()
    {
        return sourceList; 
    }
    
    protected String buildClassNameFromFile(File sourceFile)
    {
        String path = sourceFile.getAbsolutePath();
        for(File rootFolderOfSources : sourceList)
        {
            String className = buildClassNameFromFile(sourceFile,rootFolderOfSources);
            if (className != null)
                return className;            
        }
        throw new RelProxyException("File not found in source folders: " + path);
    }           
    
    protected static String buildClassNameFromFile(File sourceFile,File rootFolderOfSources)
    {
        String path = sourceFile.getAbsolutePath();

        String rootFolderOfSourcesAbsPath = rootFolderOfSources.getAbsolutePath();
        int pos = path.indexOf(rootFolderOfSourcesAbsPath); 
        if (pos == 0) // Está en este source folder
        {
            path = path.substring(rootFolderOfSourcesAbsPath.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
            // Puede no tener extensión (script) o bien ser .java o bien ser una inventada (ej .jsh), la quitamos si existe
            pos = path.lastIndexOf('.');        
            if (pos != -1) 
                path = path.substring(0, pos);   
            path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
            return path;
        }
        return null;
    }               
    
    
    
}
