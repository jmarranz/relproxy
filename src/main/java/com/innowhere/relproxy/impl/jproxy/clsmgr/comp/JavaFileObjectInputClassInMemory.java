package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectInputClassInMemory extends SimpleJavaFileObject 
{
    protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    protected String binaryName;
    
    /**
    * Registers the compiled class object under URI
    * containing the class full name
    *
    * @param name
    *            Full name of the compiled class
    * @param kind
    *            Kind of the data. It will be CLASS in our case
    */
    public JavaFileObjectInputClassInMemory(String name, Kind kind) 
    {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        
        if (Kind.SOURCE.equals(kind)) throw new RuntimeException("Unexpected");
        
        this.binaryName = name;
    }

    /**
    * Will be used by our file manager to get the byte code that
    * can be put into memory to instantiate our class
    *
    * @return compiled byte code
    */
    public byte[] getBytes() 
    {
        return bos.toByteArray();
    }
    
    @Override
    public InputStream openInputStream() throws IOException 
    {
        return new ByteArrayInputStream(getBytes());
    }    
    
    @Override
    public OutputStream openOutputStream() throws IOException 
    {
        return bos;
    }
    
    public String binaryName()
    {
        return binaryName;
    }

}
