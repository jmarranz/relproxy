package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.ProxyException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInMemory extends SimpleJavaFileObject 
{
    //protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    protected String binaryName;
    protected String source;
    protected String encoding;
    
    public JavaFileObjectInputSourceInMemory(String name,String source,String encoding) 
    {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);  // La extensión .java es necesaria aunque sea falsa sino da error
        
        this.binaryName = name;
        this.source = source;
        this.encoding = encoding;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException 
    {
        return source;
    }    
    
    public byte[] getBytes() 
    {
        try
        {
            return source.getBytes(encoding);
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new ProxyException(ex);
        }
    }
    
    @Override
    public InputStream openInputStream() throws IOException 
    {
        return new ByteArrayInputStream(getBytes());
    }    
    
    @Override
    public OutputStream openOutputStream() throws IOException 
    {
        return null; // bos;
    }
    
    public String binaryName()
    {
        return binaryName;
    }

}
