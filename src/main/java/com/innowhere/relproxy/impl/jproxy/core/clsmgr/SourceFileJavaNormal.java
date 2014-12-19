package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceFileJavaNormal extends SourceUnit
{
    protected File sourceFile;
    
    public SourceFileJavaNormal(File sourceFile,File rootFolderOfSources)
    {
        super(buildClassNameFromFile(sourceFile,rootFolderOfSources));        
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
}
