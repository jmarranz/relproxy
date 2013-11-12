package com.innowhere.relproxy.impl.jproxy.comp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author jmarranz
 */
public class JavaFileObjectInputClassInJar extends JavaFileObjectInputClassInFileSystem  
{
    public JavaFileObjectInputClassInJar(String binaryName, URI uri) 
    {
        super(binaryName,uri,uri.getSchemeSpecificPart());        
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return uri.toURL().openStream(); // easy way to handle any URI!
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public String toString() {
        return "JavaFileObjectInputClassInJar{uri=" + uri + '}';
    }
}