package com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

/**
 * http://atamur.blogspot.com.es/2009/10/using-built-in-javacompiler-with-custom.html
 *
 * @author jmarranz
 */
public class JavaFileObjectInputClassFinderByClassLoader 
{
    private static final String CLASS_FILE_EXTENSION = ".class";

    private final ClassLoader classLoader;    
    
    public JavaFileObjectInputClassFinderByClassLoader(ClassLoader classLoader) 
    {
        this.classLoader = classLoader;
    }

    public List<JavaFileObjectInputClassInFileSystem> find(String packageName) throws IOException 
    {
        String javaPackageName = packageName.replaceAll("\\.", "/");

        List<JavaFileObjectInputClassInFileSystem> result = new ArrayList<JavaFileObjectInputClassInFileSystem>();

        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) 
        { // one URL for each jar on the classpath that has the given package
            URL packageFolderURL = urlEnumeration.nextElement();
            Collection<JavaFileObjectInputClassInFileSystem> files = listUnder(packageName,packageFolderURL);
            result.addAll(files);
        }

        return result;
    }

    private Collection<JavaFileObjectInputClassInFileSystem> listUnder(String packageName, URL packageFolderURL) 
    {
    	String pkgPath = packageFolderURL.toExternalForm(); //packageFolderURL.getFile(); El problema de getFile es que también está URL-encoded (un espacio es %20) lo cual no es compatible con File

        if (pkgPath.startsWith("file:"))
        {
            pkgPath = pkgPath.substring("file:".length());
        
            try { pkgPath = URLDecoder.decode(pkgPath, "UTF-8"); } // Detecté el problema con Vaadin en un path con "Documents%20and%20Settings" con %20 obviamente no es un path correcto, deben ser espacios
            catch (UnsupportedEncodingException ex) { throw new RelProxyException(ex); }
            
            File directory = new File(pkgPath);
            if (directory.isDirectory())  // browse local .class files - useful for local execution
                return processDir(packageName, directory);            
            else
                throw new RelProxyException("Internal Error:" + pkgPath);
        }
        else 
        { // browse a jar file
            return processJar(packageFolderURL);
        } // maybe there can be something else for more involved class loaders
    }

    private List<JavaFileObjectInputClassInFileSystem> processJar(URL packageFolderURL) 
    {
        List<JavaFileObjectInputClassInFileSystem> result = new ArrayList<JavaFileObjectInputClassInFileSystem>();
        try 
        {
            String jarUri = packageFolderURL.toExternalForm().split("!")[0];

            JarURLConnection jarConn = (JarURLConnection) packageFolderURL.openConnection();
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName.length() + 1;

            Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
            while (entryEnum.hasMoreElements()) 
            {
                JarEntry jarEntry = entryEnum.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1 && name.endsWith(CLASS_FILE_EXTENSION)) 
                {
                    URI uri = URI.create(jarUri + "!/" + name);
                    String binaryName = ClassDescriptor.getClassNameFromRelativeClassFilePath(name);
                    result.add(new JavaFileObjectInputClassInJar(binaryName, uri,jarEntry.getTime()));
                }
            }
        }
        catch (Exception e) 
        {
            throw new RelProxyException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
        }
        return result;
    }

    private List<JavaFileObjectInputClassInFileSystem> processDir(String packageName, File directory) 
    {
        List<JavaFileObjectInputClassInFileSystem> result = new ArrayList<JavaFileObjectInputClassInFileSystem>();

        File[] childFiles = directory.listFiles();
        for (File childFile : childFiles) 
        {
            if (!childFile.isFile()) continue;
            
            // We only want the .class files.
            String name = childFile.getName();
            if (name.endsWith(CLASS_FILE_EXTENSION)) 
            {
                String binaryName = ClassDescriptor.getClassNameFromPackageAndClassFileName(packageName,name);
                result.add(new JavaFileObjectInputClassInFile(childFile,binaryName, childFile.toURI()));
            }            
        }

        return result;
    }
}