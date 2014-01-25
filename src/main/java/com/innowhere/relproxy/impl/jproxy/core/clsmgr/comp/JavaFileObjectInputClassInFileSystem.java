package com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public abstract class JavaFileObjectInputClassInFileSystem implements JavaFileObject,JProxyJavaFileObjectInput
{
    protected final String binaryName;
    protected final URI uri;
    protected final String name;
    
    public JavaFileObjectInputClassInFileSystem(String binaryName, URI uri,String name) 
    {
        this.uri = uri;
        this.binaryName = binaryName;
        this.name = name;
    }   
    
    @Override
    public URI toUri() {
        return uri;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override    
    public String getBinaryName() {
        return binaryName;
    }    
    
    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }        
    
    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override // copied from SimpleJavaFileManager
    public boolean isNameCompatible(String simpleName, Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName())
                || getName().endsWith("/" + baseName));
    }

    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }
    
}
