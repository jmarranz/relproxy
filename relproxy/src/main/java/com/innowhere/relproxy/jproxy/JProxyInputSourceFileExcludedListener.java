package com.innowhere.relproxy.jproxy;

import java.io.File;

/**
 * This interface is provided to developers to implement excluding rules to filter source files not to be part of the hot reloading system in spite of included in input paths
 * 
 * @see JProxyConfig#setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener) 
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyInputSourceFileExcludedListener
{
    /**
     * This method is called per file when going to be managed by the hot reloading system.
     * 
     * @param file the file to be managed.
     * @param rootFolderOfSources the folder root of sources where this file is located.
     * @return true whether the file must be ignored.
     */
    public boolean isExcluded(File file,File rootFolderOfSources);    
}
