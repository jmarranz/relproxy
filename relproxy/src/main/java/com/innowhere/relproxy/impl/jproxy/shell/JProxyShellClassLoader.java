package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 *
 * @author jmarranz
 */
public class JProxyShellClassLoader extends URLClassLoader
{   
    public JProxyShellClassLoader(ClassLoader parent,File classFolder)
    {
        super(toURLArray(classFolder),parent);
    }

    private static URL[] toURLArray(File file)
    {
        try { return new URL[]{file.toURI().toURL()}; } 
        catch (MalformedURLException ex) { throw new RelProxyException(ex); }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException
    {
        return super.findClass(name); 
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        return super.loadClass(name, resolve); 
    }

    public synchronized Class defineClass(ClassDescriptor classDesc)
    {    
        String className = classDesc.getClassName();
        byte[] classBytes = classDesc.getClassBytes();
        Class clasz = defineClass(className,classBytes, 0, classBytes.length);   
        classDesc.setLastLoadedClass(clasz);
        return clasz;
    }    
}
