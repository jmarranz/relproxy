package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author jmarranz
 */
public class FileExt
{
    protected File file;
    protected String cannonicalPath; // El obtener el cannonicalPath exige acceder al sistema de archivos, por eso nos inventamos esta clase, para evitar sucesivas llamadas a File.getCanonicalPath()
            
    public FileExt(File file)
    {
        this.file = file;
        try { this.cannonicalPath = file.getCanonicalPath(); }
        catch (IOException ex) { throw new RelProxyException(ex); }        
    }
    
    public File getFile()
    {
        return file;
    }
    
    public String getCanonicalPath()
    {
        return cannonicalPath;
    }
}
