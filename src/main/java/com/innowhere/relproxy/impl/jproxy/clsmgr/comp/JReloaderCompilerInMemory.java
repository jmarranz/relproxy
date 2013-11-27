package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.ProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorInner;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFile;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileJava;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderClassLoader;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 *
 * @author jmarranz
 */
public class JReloaderCompilerInMemory
{
    protected JavaCompiler compiler;
    protected Iterable<String> compilationOptions; // puede ser null
    protected DiagnosticCollector<JavaFileObject> diagnostics; // puede ser null
    protected boolean outDefaultDiagnostics = false;
            
    public JReloaderCompilerInMemory(Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        this.compilationOptions = compilationOptions;
        this.diagnostics = diagnostics;
        this.compiler = ToolProvider.getSystemJavaCompiler();
        
        if (diagnostics == null)
        {
            this.diagnostics = new DiagnosticCollector<JavaFileObject>();
            outDefaultDiagnostics = true;
        }        
    }
    
    public void compileSourceFile(ClassDescriptorSourceFile sourceFileDesc,JReloaderClassLoader customClassLoader,Map<String,ClassDescriptorSourceFile> sourceFileMap)
    {
        //File sourceFile = sourceFileDesc.getSourceFile();
        LinkedList<JavaFileObjectOutputClass> outClassList = compile(sourceFileDesc,customClassLoader,sourceFileMap);
        
        if (outClassList == null) 
            throw new ProxyException("Cannot reload class: " + sourceFileDesc.getClassName());
        
        String className = sourceFileDesc.getClassName();        
        
        // Puede haber más de un resultado cuando hay inner classes y/o clase privada en el mismo archivo
        for(JavaFileObjectOutputClass outClass : outClassList)
        {
            String currClassName = outClass.binaryName();
            byte[] classBytes = outClass.getBytes();      
            ClassDescriptorInner innerClass = sourceFileDesc.getInnerClassDescriptor(currClassName,true);
            if (innerClass != null)
            {               
                innerClass.setClassBytes(classBytes);                       
            }
            else
            {
                if (!className.equals(currClassName))
                {
                    // Seguramente es debido a que el archivo java tiene una clase privada autónoma declarada en el mismo archivo .java, no permitimos estas clases porque sólo podemos
                    // detectarlas cuando cambiamos el código fuente, pero no si el código fuente no se ha tocado, por ejemplo no tenemos
                    // forma de conseguir que se recarguen de forma determinista y si posteriormente se cargara via ClassLoader al usarse no podemos reconocer que es una clase
                    // "hot reloadable" (quizás a través del package respecto a las demás clases hot pero no es muy determinista pues nada impide la mezcla de hot y no hot en el mismo package)
                    // Es una limitación mínima.
                    throw new ProxyException("Unexpected class when compiling: " + currClassName + " maybe it is an autonomous private class declared in the same java file of the principal class, this kind of classes are not supported in hot reload");
                }
                
                sourceFileDesc.setClassBytes(classBytes);                              
            }
        }
    }        
    
    private LinkedList<JavaFileObjectOutputClass> compile(ClassDescriptorSourceFile sourceFileDesc,ClassLoader classLoader,Map<String,ClassDescriptorSourceFile> sourceFileMap)
    {
        // http://stackoverflow.com/questions/12173294/compiling-fully-in-memory-with-javax-tools-javacompiler
        // http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
        // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/6-b14/com/sun/tools/javac/util/JavacFileManager.java?av=h#JavacFileManager
        // http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7-b147/javax/tools/StandardLocation.java
        // http://books.brainysoftware.com/java6_sample/chapter2.pdf
        // http://atamur.blogspot.com.es/2009/10/using-built-in-javacompiler-with-custom.html
        // http://www.javablogging.com/dynamic-in-memory-compilation/ Si no queremos generar archivos
        // http://atamur.blogspot.com.es/2009/10/using-built-in-javacompiler-with-custom.html
        // http://stackoverflow.com/questions/264828/controlling-the-classpath-in-a-servlet?rq=1
        // http://stackoverflow.com/questions/1563909/how-to-set-classpath-when-i-use-javax-tools-javacompiler-compile-the-source
        // http://stackoverflow.com/questions/10767048/javacompiler-with-custom-classloader-and-filemanager


        StandardJavaFileManager fileManager = null;
        try
        {
            fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            
            Iterable<? extends JavaFileObject> compilationUnits;
            
            if (sourceFileDesc instanceof ClassDescriptorSourceFileJava)
            {
                List<File> sourceFileList = new ArrayList<File>();
                sourceFileList.add(sourceFileDesc.getSourceFile());            
                compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFileList);
            }
            else if (sourceFileDesc instanceof ClassDescriptorSourceFileScript)
            {
                ClassDescriptorSourceFileScript sourceFileDescScript = ((ClassDescriptorSourceFileScript)sourceFileDesc);
                LinkedList<JavaFileObject> compilationUnitsList = new LinkedList<JavaFileObject>();            
                String code = sourceFileDescScript.getSourceCode();
                compilationUnitsList.add(new JavaFileObjectInputSourceInMemory(sourceFileDesc.getClassName(),code,sourceFileDescScript.getEncoding()));            
                compilationUnits = compilationUnitsList;                
            }
            else
            {
                throw new ProxyException("Internal error");
            }

            JavaFileManagerInMemory fileManagerInMemory = new JavaFileManagerInMemory(fileManager,classLoader,sourceFileMap);

            boolean success = compile(compilationUnits,fileManagerInMemory);
            if (!success) return null;

            LinkedList<JavaFileObjectOutputClass> classObj = fileManagerInMemory.getJavaFileObjectOutputClassList();
            return classObj;
        }
        finally
        {
           if (fileManager != null) try { fileManager.close(); } catch(IOException ex) { throw new ProxyException(ex); }
        }
    }

    private boolean compile(Iterable<? extends JavaFileObject> compilationUnits,JavaFileManager fileManager)
    {
        /*
        String systemClassPath = System.getProperty("java.class.path");
        String[] compileOptions = new String[]
            {"-classpath",engine.getPathSources()}; // No hacen falta los demás (sistema, Tomcat, /classes /lib etc) porque se obtienen via ClassLoader
        */

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, compilationOptions,null, compilationUnits);
        boolean success = task.call();

        if (outDefaultDiagnostics)
        {
            List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();
            if (!diagList.isEmpty())
            {
                System.out.println("Problems compiling: " + compilationUnits);
                int i = 1;
                for (Diagnostic diagnostic : diagList)
                {
                   System.out.println(" Diagnostic " + i);
                   System.out.println("  code: " + diagnostic.getCode());
                   System.out.println("  kind: " + diagnostic.getKind());
                   System.out.println("  position: " + diagnostic.getPosition());
                   System.out.println("  start position: " + diagnostic.getStartPosition());
                   System.out.println("  end position: " + diagnostic.getEndPosition());
                   System.out.println("  source: " + diagnostic.getSource());
                   System.out.println("  message: " + diagnostic.getMessage(null));
                   i++;
                }
            }
        }

        return success;
    }


}
