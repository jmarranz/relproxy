package com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptor;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorInner;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceUnit;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceFileJava;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceFileRegistry;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyClassLoader;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
public class JProxyCompilerInMemory
{
    protected JProxyEngine engine;
    protected JavaCompiler compiler;
    protected Iterable<String> compilationOptions; // puede ser null
    protected JProxyDiagnosticsListener diagnosticsListener; // puede ser null

    public JProxyCompilerInMemory(JProxyEngine engine,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        this.engine = engine;
        this.compilationOptions = compilationOptions;
        this.diagnosticsListener = diagnosticsListener;
        this.compiler = ToolProvider.getSystemJavaCompiler();       
    }
    
    public JProxyCompilerContext createJProxyCompilerContext() 
    {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);   
        return new JProxyCompilerContext(standardFileManager,diagnostics,diagnosticsListener);
    }
    
    public void compileSourceFile(ClassDescriptorSourceUnit sourceFileDesc,JProxyCompilerContext context,JProxyClassLoader customClassLoader,ClassDescriptorSourceFileRegistry sourceRegistry)
    {
        //File sourceFile = sourceFileDesc.getSourceFile();
        LinkedList<JavaFileObjectOutputClass> outClassList = compile(sourceFileDesc,context,customClassLoader,sourceRegistry);
        
        if (outClassList == null) 
            throw new JProxyCompilationException(sourceFileDesc);
        
        String className = sourceFileDesc.getClassName();        
        
        // Puede haber más de un resultado cuando hay inner classes y/o clase privada en el mismo archivo o bien simplemente clases dependientes
        for(JavaFileObjectOutputClass outClass : outClassList)
        {
            String currClassName = outClass.binaryName();
            byte[] classBytes = outClass.getBytes();            
            if (className.equals(currClassName))            
            {
                sourceFileDesc.setClassBytes(classBytes); 
            }
            else
            {
                ClassDescriptorInner innerClass = sourceFileDesc.getInnerClassDescriptor(currClassName,true);
                if (innerClass != null)
                {            
                    innerClass.setClassBytes(classBytes);                       
                }
                else
                {
                    // Lo mismo es un archivo dependiente e incluso una inner class pero de otra clase que está siendo usada en el archivo compilado
                    ClassDescriptor dependentClass = sourceRegistry.getClassDescriptor(currClassName);
                    if (dependentClass != null)
                    {
                        dependentClass.setClassBytes(classBytes); 
                    }
                    else
                    {
                        // Seguramente es debido a que el archivo java tiene una clase privada autónoma declarada en el mismo archivo .java (las que se ponen después de la clase principal pública normal), no permitimos estas clases porque sólo podemos
                        // detectarlas cuando cambiamos el código fuente, pero no si el código fuente no se ha tocado, por ejemplo no tenemos
                        // forma de conseguir que se recarguen de forma determinista y si posteriormente se cargara via ClassLoader al usarse no podemos reconocer que es una clase
                        // "hot reloadable" (quizás a través del package respecto a las demás clases hot pero no es muy determinista pues nada impide la mezcla de hot y no hot en el mismo package)
                        // Es una limitación mínima.
                        
                        // También puede ser un caso de clase excluida por el listener de exclusión, no debería ocurrir, tengo un caso de test en donde ocurre a posta 
                        // (caso de JProxyExampleAuxIgnored cuando se cambia la JProxyExampleDocument que la usa) pero en programación normal no.

                        if (engine.getJProxyInputSourceFileExcludedListener() == null)
                            throw new RelProxyException("Unexpected class when compiling: " + currClassName + " maybe it is an autonomous private class declared in the same java file of the principal class, this kind of classes are not supported in hot reload");
                        else
                            System.out.println("Unexpected class when compiling: " + currClassName + " maybe it is an excluded class or is an autonomous private class declared in the same java file of the principal class, this kind of classes are not supported in hot reload");
                    }
                }
            }
        }
    }        
    
    private LinkedList<JavaFileObjectOutputClass> compile(ClassDescriptorSourceUnit sourceFileDesc,JProxyCompilerContext context,ClassLoader classLoader,ClassDescriptorSourceFileRegistry sourceRegistry)
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


        StandardJavaFileManager standardFileManager = context.getStandardFileManager(); // recuerda que el StandardJavaFileManager puede reutilizarse entre varias compilaciones consecutivas mientras se cierre al final
     
        Iterable<? extends JavaFileObject> compilationUnits;

        if (sourceFileDesc instanceof ClassDescriptorSourceFileJava)
        {
            List<File> sourceFileList = new ArrayList<File>();
            sourceFileList.add(((ClassDescriptorSourceFileJava)sourceFileDesc).getSourceFile());            
            compilationUnits = standardFileManager.getJavaFileObjectsFromFiles(sourceFileList);
        }
        else if (sourceFileDesc instanceof ClassDescriptorSourceScript)
        {
            ClassDescriptorSourceScript sourceFileDescScript = (ClassDescriptorSourceScript)sourceFileDesc;
            LinkedList<JavaFileObject> compilationUnitsList = new LinkedList<JavaFileObject>();            
            String code = sourceFileDescScript.getSourceCode();
            compilationUnitsList.add(new JavaFileObjectInputSourceInMemory(sourceFileDescScript.getClassName(),code,sourceFileDescScript.getEncoding(),sourceFileDescScript.getTimestamp()));            
            compilationUnits = compilationUnitsList;                
        }
        else
        {
            throw new RelProxyException("Internal error");
        }

        JavaFileManagerInMemory fileManagerInMemory = new JavaFileManagerInMemory(standardFileManager,classLoader,sourceRegistry);

        boolean success = compile(compilationUnits,fileManagerInMemory,context);
        if (!success) return null;

        LinkedList<JavaFileObjectOutputClass> classObj = fileManagerInMemory.getJavaFileObjectOutputClassList();
        return classObj;

    }

    private boolean compile(Iterable<? extends JavaFileObject> compilationUnits,JavaFileManager fileManager,JProxyCompilerContext context)
    {
        /*
        String systemClassPath = System.getProperty("java.class.path");
        */

        LinkedList<String> finalCompilationOptions = new LinkedList<String>();
        if (compilationOptions != null)        
            for(String option : compilationOptions) finalCompilationOptions.add(option);
        
        File[] folderSourceList = engine.getFolderSourceList().getArray();
        if (folderSourceList != null)
        {
            finalCompilationOptions.add("-classpath");
            StringBuilder classPath = new StringBuilder();
            for(int i = 0; i < folderSourceList.length; i++)
            {
                File folderSources = folderSourceList[i];
                classPath.append(folderSources.getAbsolutePath());
                if (i < folderSourceList.length - 1)
                    classPath.append(File.pathSeparatorChar);       
            }
            finalCompilationOptions.add(classPath.toString());               
        }
        
        DiagnosticCollector<JavaFileObject> diagnostics = context.getDiagnosticCollector();
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, finalCompilationOptions,null, compilationUnits);
        boolean success = task.call();
        
        return success;
    }


}
