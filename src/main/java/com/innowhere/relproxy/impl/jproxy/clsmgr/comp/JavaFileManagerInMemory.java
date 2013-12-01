package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFile;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileRegistry;
import java.io.IOException;
import java.util.Collections;
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
                
                // No necesitamos llamar a super.list(location, packageName, kinds, recurse); para obtener los .class pues los obtendría de archivo y a través del ClassLoader por defecto
                // y nos interesa obtenerlos de archivo siempre que no haya una compilación más reciente guardada sólo en memoria, el problema es que a través de los JavaFileObject devueltos
                // no podemos conocer el archivo original etc, por eso lo hacemos "a mano" y obtenemos los .class con más control nosotros mismos a través del ClassLoader.
                // Por otra parte los archivos fuente tampoco se van a encontrar via super.list porque no se como pasarle el directorio de los archivos fuente,
                // podemos pasar en el classpath del compilador el path raiz donde están los .java pero no se hacerlo para obtener un StandardJavaFileManager

                
                List<JavaFileObject> result = new LinkedList<JavaFileObject>();
                
                List<JavaFileObjectInputClassInFileSystem> classList = classFinder.find(packageName);
                
                // Reemplazamos los .class de classList que son los que están en archivo "deployados" que pueden ser más antiguos que los que están en memoria
                for(JavaFileObjectInputClassInFileSystem fileObj : classList)
                {
                    String className = fileObj.getBinaryName();
                    ClassDescriptorSourceFile sourceFileDesc = sourceRegistry.getClassDescriptorSourceFile(className);
                    if (sourceFileDesc != null && sourceFileDesc.getClassBytes() != null)
                    {
                        JavaFileObjectInputClassInMemory fileInput = new JavaFileObjectInputClassInMemory(className);
                        fileInput.openOutputStream().write(sourceFileDesc.getClassBytes());
                        fileInput.openOutputStream().close();
                        result.add(fileInput);
                    }
                    else
                    {
                        result.add(fileObj);
                    }
                }
                

/*                
ClassDescriptorSourceFile pruebaDesc = sourceFileMap.get("example.javashellex.JProxyShellExample");
JavaFileObjectInputSourceInFile prueba = new JavaFileObjectInputSourceInFile(pruebaDesc.getClassName(),pruebaDesc.getSourceFile(),pruebaDesc.getEncoding());
result.add(prueba);
*/                
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