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
public abstract class JavaFileObjectInputSourceBase extends SimpleJavaFileObject implements JProxyJavaFileObjectInput
{
    protected String binaryName;
    protected String encoding;
    
    public JavaFileObjectInputSourceBase(String name,String encoding) 
    {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);  // La extensión .java es necesaria aunque sea falsa sino da error
        
        this.binaryName = name;
        this.encoding = encoding;
    }
   
    protected abstract String getSource();
   
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException 
    {
        return getSource();
    }    
    
    public byte[] getBytes() 
    {
        try
        {
            return getSource().getBytes(encoding);
        }
        catch (UnsupportedEncodingException ex) { throw new ProxyException(ex); }
    }    
    
    @Override
    public InputStream openInputStream() throws IOException 
    {
        return new ByteArrayInputStream(getBytes());
    }    
    
    @Override
    public OutputStream openOutputStream() throws IOException 
    {
        throw new UnsupportedOperationException();
    }
    
    public String getBinaryName()
    {
        return binaryName;
    }

}
