package com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;

/**
 *
 * @author jmarranz
 */
public abstract class SourceScriptRootFile extends SourceScriptRoot
{
    protected FileExt sourceFile;
    
    public SourceScriptRootFile(FileExt sourceFile,FolderSourceList folderSourceList)
    {
        super(buildClassNameFromFile(sourceFile,folderSourceList));
        this.sourceFile = sourceFile;         
    }
    
    public static SourceScriptRootFile createSourceScriptRootFile(FileExt sourceFile,FolderSourceList folderSourceList)
    {
        String ext = JProxyUtil.getFileExtension(sourceFile.getFile()); // Si no tiene extensión devuelve ""
        if ("java".equals(ext))
            return new SourceScriptRootFileJavaExt(sourceFile,folderSourceList);
        else
            return new SourceScriptRootFileOtherExt(sourceFile,folderSourceList); // Caso de archivo script inicial sin extensión .java (puede ser sin extensión)
    }
    
    @Override
    public long lastModified()
    {
        return sourceFile.getFile().lastModified();
    }    

    public FileExt getFileExt()
    {
        return sourceFile;
    }

}
