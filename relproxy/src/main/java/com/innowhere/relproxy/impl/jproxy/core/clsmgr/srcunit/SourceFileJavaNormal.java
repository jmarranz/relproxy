package com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.innowhere.relproxy.impl.FileExt;

/**
 *
 * @author jmarranz
 */
public class SourceFileJavaNormal extends SourceUnit
{
    protected final FileExt sourceFile;
    
    public SourceFileJavaNormal(FileExt sourceFile,FileExt rootFolderOfSources)
    {
        super(buildClassNameFromFile(sourceFile,rootFolderOfSources));        
        this.sourceFile = sourceFile;      
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
