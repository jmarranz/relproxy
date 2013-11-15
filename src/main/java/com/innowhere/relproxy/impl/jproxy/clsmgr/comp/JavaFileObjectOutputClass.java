package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.ProxyException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectOutputClass extends SimpleJavaFileObject {

    /**
    * Byte code created by the compiler will be stored in this
    * ByteArrayOutputStream so that we can later get the
    * byte array out of it
    * and put it in the memory as an instance of our class.
    */
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
    public JavaFileObjectOutputClass(String name, Kind kind) 
    {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        
        if (Kind.SOURCE.equals(kind)) throw new ProxyException("Unexpected");
        this.binaryName = name;
    }

    public String binaryName()
    {
        return binaryName;
    }
    
    /**
    * Will be used by our file manager to get the byte code that
    * can be put into memory to instantiate our class
    *
    * @return compiled byte code
    */
    public byte[] getBytes() {
        return bos.toByteArray();
    }

    /**
    * Will provide the compiler with an output stream that leads
    * to our byte array. This way the compiler will write everything
    * into the byte array that we will instantiate later
    */
    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }

}
