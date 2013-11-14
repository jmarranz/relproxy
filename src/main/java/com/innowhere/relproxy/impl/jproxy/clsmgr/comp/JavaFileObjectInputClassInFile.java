package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author jmarranz
 */
public class JavaFileObjectInputClassInFile extends JavaFileObjectInputClassInFileSystem 
{
    protected File file;
    
    public JavaFileObjectInputClassInFile(File file,String binaryName, URI uri) 
    {
        super(binaryName,uri,uri.getPath());
        this.file = file;
    }

    @Override
    public InputStream openInputStream() throws IOException 
    {
        // Podríamos hacer uri.toURL().openStream() pero si tenemos el File es para algo
        return new BufferedInputStream(new FileInputStream(file),10 * 1024); // easy way to handle any URI!
    }
    
    @Override
    public long getLastModified() 
    {
        return file.lastModified();
    }

    @Override
    public String toString() {
        return "JavaFileObjectInputClassInFile{uri=" + uri + '}';
    }
}