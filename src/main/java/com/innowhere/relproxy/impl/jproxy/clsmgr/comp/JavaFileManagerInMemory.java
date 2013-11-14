package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFile;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    /**
    * Instance of JavaClassObject that will store the
    * compiled bytecode of our class
    */
    private LinkedList<JavaFileObjectOutputClass> outputClassList = new LinkedList<JavaFileObjectOutputClass>();
    private final ClassLoaderBasedJavaFileObjectFinder classFinder;    
    protected Map<String,ClassDescriptorSourceFile> sourceFileMap;
    
    public JavaFileManagerInMemory(StandardJavaFileManager standardManager,ClassLoader classLoader,Map<String,ClassDescriptorSourceFile> sourceFileMap) 
    {
        super(standardManager);
        this.sourceFileMap = sourceFileMap;
        this.classFinder = new ClassLoaderBasedJavaFileObjectFinder(classLoader);        
    }

    public LinkedList<JavaFileObjectOutputClass> getJavaFileObjectOutputClassList()
    {
        return outputClassList;
    }
    
    /**
    * Gives the compiler an instance of the JavaClassObject
    * so that the compiler can write the byte code into it.
    */
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
            return super.list(location, packageName, kinds, recurse);  // Aquí nunca (con PLATFORM_CLASS_PATH) va a encontrar nuestros sources         
        else if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) 
        {
            if (packageName.startsWith("java."))  // a hack to let standard manager handle locations like "java.lang" or "java.util". 
                return super.list(location, packageName, kinds, recurse);
            else
            {   // app specific classes are here
                
                // No necesitamos llamar a: super.list(location, packageName, kinds, recurse);  por ejemplo para obtener los archivos de código fuente
                // si hubiéramos pasado en el classpath del compilador el path raiz donde están los .java, pues tenemos el bytecode de la compilación última o de la inicial
                // en archivo (los .class) que obtenemos a través del ClassLoader.
                
                List<JavaFileObject> result = new LinkedList<JavaFileObject>();
                
                List<JavaFileObjectInputClassInFileSystem> classList = classFinder.find(packageName);
                
                // Reemplazamos los .class de classList que son los que están en archivo "deployados" que pueden ser más antiguos que los que están en memoria
                for(JavaFileObjectInputClassInFileSystem fileObj : classList)
                {
                    String className = fileObj.binaryName();
                    ClassDescriptorSourceFile sourceFileDesc = sourceFileMap.get(className);
                    if (sourceFileDesc != null && sourceFileDesc.getClassBytes() != null)
                    {
                        JavaFileObjectInputClassInMemory fileInput = new JavaFileObjectInputClassInMemory(className,Kind.CLASS);
                        fileInput.openOutputStream().write(sourceFileDesc.getClassBytes());
                        fileInput.openOutputStream().close();
                        result.add(fileInput);
                    }
                    else
                    {
                        result.add(fileObj);
                    }
                }
                
                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) 
    {
        if (file instanceof JavaFileObjectInputClassInFileSystem)
            return ((JavaFileObjectInputClassInFileSystem)file).binaryName();
        else if (file instanceof JavaFileObjectInputClassInMemory)
            return ((JavaFileObjectInputClassInMemory)file).binaryName();

        return super.inferBinaryName(location, file);
    }

}