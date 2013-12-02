package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JavaSourcesSearch 
{
    protected JProxyEngine engine;
    
    public JavaSourcesSearch(JProxyEngine engine)
    {
        this.engine = engine;
    }

    public ClassDescriptorSourceFileScript sourceFileSearch(File scriptFile,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {
        ClassDescriptorSourceFileScript scriptFileDesc = (scriptFile == null) ? null : processSourceFileScript(scriptFile,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        String pathSources = engine.getPathSources();
        String[] children = new File(pathSources).list(); 
        recursiveSourceFileJavaSearch(pathSources,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        if (oldSourceFileMap != null && !oldSourceFileMap.isEmpty())        
            deletedSourceFiles.addAll(oldSourceFileMap.getClassDescriptorSourceFileColl());
        
        return scriptFileDesc;
    }
    
    private void recursiveSourceFileJavaSearch(String parentPath,String[] relPathList,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {
        for(String relPath : relPathList)
        {
            File file = new File(parentPath + "/" + relPath);        
            if (file.isDirectory())
            {
                String[] children = file.list();   
                recursiveSourceFileJavaSearch(file.getAbsolutePath(),children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
            else
            {
                int pos = relPath.lastIndexOf('.');
                if (pos == -1) continue;

                String ext = relPath.substring(pos+1);
                if (!"java".equals(ext)) continue;
                //if (!"jsh".equals(ext)) continue;
                             
                processSourceFileJava(file,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
        }
    }    
    
    private ClassDescriptorSourceFileScript processSourceFileScript(File file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {    
        String path = file.getAbsolutePath();                
        String className = ClassDescriptorSourceFileScript.getClassNameFromSourceFileScriptAbsPath(path,engine.getPathSources());                
        return (ClassDescriptorSourceFileScript)processSourceFile(file,className,true,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }    
    
    private ClassDescriptorSourceFileJava processSourceFileJava(File file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {    
        String path = file.getAbsolutePath();                
        String className = ClassDescriptorSourceFileJava.getClassNameFromSourceFileJavaAbsPath(path, engine.getPathSources());                
        return (ClassDescriptorSourceFileJava)processSourceFile(file,className,false,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }
    
    private ClassDescriptorSourceFile processSourceFile(File file,String className,boolean script,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {
        long timestampSourceFile = file.lastModified();
        ClassDescriptorSourceFile sourceFile;
        if (oldSourceFileMap != null)
        {
            sourceFile = oldSourceFileMap.getClassDescriptorSourceFile(className);

            if (sourceFile != null) // Cambiado
            {
                long oldTimestamp = sourceFile.getTimestamp();
                if (timestampSourceFile > oldTimestamp)
                {
                    sourceFile.updateTimestamp(timestampSourceFile);
                    updatedSourceFiles.add(sourceFile);
                }

                oldSourceFileMap.removeClassDescriptorSourceFile(className); // Para que sólo queden las clases que han sido eliminadas
            }          
            else // Clase nueva
            {
                sourceFile = ClassDescriptorSourceFile.create(script,engine,className,file,timestampSourceFile);
                newSourceFiles.add(sourceFile);
            }
        }
        else  // Primera vez, vemos si el código fuente se ha cambiado respecto a los .class en el sistema de archivos
        {
            String relClassPath = ClassDescriptor.getRelativeClassFilePathFromClassName(className);
            ClassLoader parentClassLoader = engine.getRootClassLoader();
            URL urlClass = parentClassLoader.getResource(relClassPath);
            if (urlClass != null)
            {
                String urlClassExt = urlClass.toExternalForm();
                long timestampCompiledClass = urlClassExt.startsWith("file:") ? new File(urlClass.getPath()).lastModified() : 0;  // 0 cuando está en un JAR

                if (timestampSourceFile > timestampCompiledClass)
                {
                    // Si el .class está en un JAR no hay forma de saber si el fuente .java es más actual que el .class por lo que siempre se considerará que el archivo fuente ha sido modificado
                    sourceFile = ClassDescriptorSourceFile.create(script,engine,className,file,timestampSourceFile);
                    updatedSourceFiles.add(sourceFile);
//System.out.println("UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));
                }
                else
                {
                    // Esto es lo normal en carga si no hemos tocado el código tras el deploy, que el .class sea más reciente que el .java
                    sourceFile = ClassDescriptorSourceFile.create(script,engine,className,file,timestampCompiledClass);
                    byte[] classBytes = JProxyUtil.readURL(urlClass);
                    sourceFile.setClassBytes(classBytes);  
                    // Falta cargar las posibles inner classes, hay que tener en cuenta que este archivo NO se va a compilar porque no ha cambiado respecto a .class conocido

//System.out.println("NOT UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));                                    
                }

            }
            else // No hay .class, es un archivo fuente nuevo
            {
                sourceFile = ClassDescriptorSourceFile.create(script,engine,className,file,timestampSourceFile);
                newSourceFiles.add(sourceFile);
            }
        }

        newSourceFileMap.addClassDescriptorSourceFile(sourceFile);
        
        return sourceFile;
    }                

}
