package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public abstract class SourceScriptRootFile extends SourceScriptRoot
{
    protected File sourceFile;
    
    public SourceScriptRootFile(File sourceFile,FolderSourceList folderSourceList)
    {
        super(buildClassNameFromFile(sourceFile,folderSourceList));
        this.sourceFile = sourceFile;         
    }
    
    public static SourceScriptRootFile createSourceScriptRootFile(File sourceFile,FolderSourceList folderSourceList)
    {
        String ext = JProxyUtil.getFileExtension(sourceFile); // Si no tiene extensión devuelve ""
        if ("java".equals(ext))
            return new SourceScriptRootFileJavaExt(sourceFile,folderSourceList);
        else
            return new SourceScriptRootFileOtherExt(sourceFile,folderSourceList); // Caso de archivo script inicial sin extensión .java (puede ser sin extensión)
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

}
