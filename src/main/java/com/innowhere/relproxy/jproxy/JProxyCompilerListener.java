package com.innowhere.relproxy.jproxy;

import java.io.File;

/**
 * Is the interface to monitor the files being compiled.
 * 
 * @see JProxyConfig#setJProxyCompilerListener(JProxyCompilerListener) 
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyCompilerListener
{
    public void beforeCompile(File file);
    public void afterCompile(File file);    
}
