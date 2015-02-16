package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class FolderSourceList
{
    protected FileExt[] sourceList;
    
    public FolderSourceList(String[] sourcePathList,boolean expectedDirectory)
    {
        if (sourcePathList != null) // En el caso de shell interactivo es null
        {
            // El convertir siempre a File los paths es para normalizar paths
            this.sourceList = new FileExt[sourcePathList.length];
            for(int i = 0; i < sourcePathList.length; i++) 
            {
                File folder = new File(sourcePathList[i]);
                if (!folder.exists())
                    throw new RelProxyException("Source folder does not exist: " + folder.getAbsolutePath());
                boolean isDirectory = folder.isDirectory();
                if (expectedDirectory)
                {
                    if (!isDirectory)
                        throw new RelProxyException("Source folder is not a directory: " + folder.getAbsolutePath());                
                }
                else
                {
                    if (isDirectory)
                        throw new RelProxyException("Expected a file not a directory: " + folder.getAbsolutePath());                    
                }
                sourceList[i] = new FileExt(folder);                
            }
        }
    }    
    
    public FileExt[] getArray()
    {
        return sourceList; 
    }
    
    public String buildClassNameFromFile(FileExt sourceFile)
    {
        for(FileExt rootFolderOfSources : sourceList)
        {
            String className = buildClassNameFromFile(sourceFile,rootFolderOfSources);
            if (className != null)
                return className;            
        }
        throw new RelProxyException("File not found in source folders: " + sourceFile.getFile().getAbsolutePath());
    }           
    
    public static String buildClassNameFromFile(FileExt sourceFile,FileExt rootFolderOfSources)
    {        
        String path = sourceFile.getCanonicalPath();

        String rootFolderOfSourcesAbsPath = rootFolderOfSources.getCanonicalPath();
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
