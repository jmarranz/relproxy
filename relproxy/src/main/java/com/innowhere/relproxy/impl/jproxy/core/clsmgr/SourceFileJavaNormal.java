package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.FileExt;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceFileJavaNormal extends SourceUnit
{
    protected FileExt sourceFile;
    
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
