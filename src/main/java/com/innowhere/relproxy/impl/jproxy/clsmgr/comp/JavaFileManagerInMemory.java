package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFile;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileRegistry;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;


/**
 * 
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileManagerInMemory extends ForwardingJavaFileManager 
{
    private final LinkedList<JavaFileObjectOutputClass> outputClassList = new LinkedList<JavaFileObjectOutputClass>();
    private final JavaFileObjectInputClassFinderByClassLoader classFinder;    
    private final ClassDescriptorSourceFileRegistry sourceRegistry;
    
    public JavaFileManagerInMemory(StandardJavaFileManager standardFileManager,ClassLoader classLoader,ClassDescriptorSourceFileRegistry sourceRegistry) 
    {
        super(standardFileManager);
        this.sourceRegistry = sourceRegistry;
        this.classFinder = new JavaFileObjectInputClassFinderByClassLoader(classLoader);        
    }

    public LinkedList<JavaFileObjectOutputClass> getJavaFileObjectOutputClassList()
    {
        return outputClassList;
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(Location location,String className, Kind kind, FileObject sibling) throws IOException 
    {
        // Normalmente sólo habrá un resultado pero se da el caso de compilar una clase con una o varias inner classes, el compilador las compila de una vez
        JavaFileObjectOutputClass outClass = new JavaFileObjectOutputClass(className, kind);
        outputClassList.add(outClass);
        return outClass;
    }

    @Override
    public Iterable list(Location location, String packageName, Set kinds, boolean recurse) throws IOException 
    {
        if (location == StandardLocation.PLATFORM_CLASS_PATH) // let standard manager hanfle         
            return super.list(location, packageName, kinds, recurse);  // En este caso nunca (con PLATFORM_CLASS_PATH) va a encontrar nuestros sources ni .class
        else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) 
        {
            if (packageName.startsWith("java.") || packageName.startsWith("javax."))  // a hack to let standard manager handle locations like "java.lang" or "java.util". Estrictamente no es necesario pero derivamos la inmensa mayoría de las clases estándar al método por defecto
                return super.list(location, packageName, kinds, recurse);
            else
            {
                // El StandardJavaFileManager al que hacemos forward es "configurado" por el compilador al que está asociado cuando hay una tarea de compilación
                // dicha configuración es por ejemplo el classpath tanto para encontrar .class como .java
                // En nuestro caso no disponemos del classpath de los .class, disponemos del ClassLoader a través del cual podemos obtener "a mano" via resources los 
                // JavaFileObject de los .class que necesitamos.
                // Ahora bien, no es el caso de los archivos fuente en donde sí tenemos un path claro el cual pasamos como classpath al compilador y por tanto un super.list(location, packageName, kinds, recurse)
                // nos devolverá los .java (como JavaFileObject claro) si encuentra archivos correspondientes al package buscado.
                               
                LinkedList<JavaFileObject> result = new LinkedList<JavaFileObject>();                
                
                Iterable inFileMgr = super.list(location, packageName, kinds, recurse); // Esperamos o archivos fuente o .class de clases no recargables
                if (inFileMgr instanceof Collection)
                {
                    result.addAll((Collection)inFileMgr);
                }
                else
                {
                    for(Iterator it = inFileMgr.iterator(); it.hasNext(); )
                    {
                        JavaFileObject file = (JavaFileObject)it.next();
                        result.add(file);
                    }
                }
                
                List<JavaFileObjectInputClassInFileSystem> classList = classFinder.find(packageName);
                
                // Reemplazamos los .class de classList que son los que están en archivo "deployados" que pueden ser más antiguos que los que están en memoria
                for(JavaFileObjectInputClassInFileSystem fileObj : classList)
                {
                    String className = fileObj.getBinaryName();
                    ClassDescriptorSourceFile sourceFileDesc = sourceRegistry.getClassDescriptorSourceFile(className);
                    if (sourceFileDesc != null && sourceFileDesc.getClassBytes() != null)
                    {
                        JavaFileObjectInputClassInMemory fileInput = new JavaFileObjectInputClassInMemory(className,sourceFileDesc.getClassBytes(),sourceFileDesc.getTimestamp());
                        result.add(fileInput);
                    }
                    else
                    {
                        result.add(fileObj);
                    }
                }
                
                // Los JavaFileObject de archivos fuente pueden ser los mimas clases que los de .class, el compilador se encargará de comparar los timestamp y elegir el .class o el source

                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) 
    {
        if (file instanceof JProxyJavaFileObjectInput)
            return ((JProxyJavaFileObjectInput)file).getBinaryName();

        return super.inferBinaryName(location, file);
    }

}