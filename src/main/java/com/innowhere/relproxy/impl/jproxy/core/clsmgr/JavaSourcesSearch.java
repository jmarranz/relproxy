package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
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

    public ClassDescriptorSourceScript sourceFileSearch(SourceScript scriptFile,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        ClassDescriptorSourceScript scriptFileDesc = (scriptFile == null) ? null : processSourceFileScript(scriptFile,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        File folderSources = engine.getFolderSources();
        if (folderSources != null) // Si es null es el caso de shell interactivo o code snippet
        {
            String[] children = folderSources.list(); // No esperamos que no exista
            
            String scriptFileJavaAbsPath = (scriptFile != null && (scriptFile instanceof SourceScriptFileJavaExt)) ? ((SourceScriptFileJavaExt)scriptFile).getFile().getAbsolutePath() : null;
            
            recursiveSourceFileJavaSearch(scriptFileJavaAbsPath,folderSources,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            if (oldSourceFileMap != null && !oldSourceFileMap.isEmpty())        
                deletedSourceFiles.addAll(oldSourceFileMap.getClassDescriptorSourceFileColl());
        }
        return scriptFileDesc;
    }
    
    private void recursiveSourceFileJavaSearch(String scriptFileJavaAbsPath,File parentPath,String[] relPathList,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        for(String relPath : relPathList)
        {
            File file = new File(parentPath + "/" + relPath);        
            if (file.isDirectory())
            {
                String[] children = file.list();   
                recursiveSourceFileJavaSearch(scriptFileJavaAbsPath,file,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
            else
            {
                String ext = JProxyUtil.getFileExtension(file); // Si no tiene extensión devuelve ""
                if (!"java".equals(ext)) continue;
                //if (!"jsh".equals(ext)) continue;

                if (scriptFileJavaAbsPath != null && scriptFileJavaAbsPath.equals(file.getAbsolutePath()))
                    continue; // Es el propio archivo script inicial que es .java, así evitamos considerarlo dos veces
                
                SourceFileJavaNormal sourceFile = new SourceFileJavaNormal(file);
                processSourceFileJava(sourceFile,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
        }
    }    
    
    private ClassDescriptorSourceScript processSourceFileScript(SourceScript file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {             
        String className = file.getClassNameFromSourceFileScriptAbsPath(engine.getFolderSources()); 
        return (ClassDescriptorSourceScript)processSourceFile(file,className,true,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }    
    
    private ClassDescriptorSourceFileJava processSourceFileJava(SourceFileJavaNormal file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {    
        String className = file.getClassNameFromSourceFileJavaAbsPath(engine.getFolderSources());              
        return (ClassDescriptorSourceFileJava)processSourceFile(file,className,false,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }
    
    private ClassDescriptorSourceUnit processSourceFile(SourceUnit file,String className,boolean script,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        long timestampSourceFile = file.lastModified();
        ClassDescriptorSourceUnit sourceFile;
        if (oldSourceFileMap != null)
        {
            sourceFile = oldSourceFileMap.getClassDescriptorSourceUnit(className);

            if (sourceFile != null) // Cambiado
            {
                long oldTimestamp = sourceFile.getTimestamp();
                if (timestampSourceFile > oldTimestamp)
                {
                    sourceFile.updateTimestamp(timestampSourceFile);
                    updatedSourceFiles.add(sourceFile);
                }

                oldSourceFileMap.removeClassDescriptorSourceUnit(className); // Para que sólo queden las clases que han sido eliminadas
            }          
            else // Clase nueva
            {
                sourceFile = ClassDescriptorSourceUnit.create(script,engine,className,file,timestampSourceFile);
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
                    sourceFile = ClassDescriptorSourceUnit.create(script,engine,className,file,timestampSourceFile);
                    updatedSourceFiles.add(sourceFile);
//System.out.println("UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));
                }
                else
                {
                    // Esto es lo normal en carga si no hemos tocado el código tras el deploy, que el .class sea más reciente que el .java
                    sourceFile = ClassDescriptorSourceUnit.create(script,engine,className,file,timestampCompiledClass);
                    byte[] classBytes = JProxyUtil.readURL(urlClass);
                    sourceFile.setClassBytes(classBytes);  
                    // Falta cargar las posibles inner classes, hay que tener en cuenta que este archivo NO se va a compilar porque no ha cambiado respecto a .class conocido

//System.out.println("NOT UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));                                    
                }

            }
            else // No hay .class, es un archivo fuente nuevo
            {
                sourceFile = ClassDescriptorSourceUnit.create(script,engine,className,file,timestampSourceFile);
                newSourceFiles.add(sourceFile);
            }
        }

        newSourceFileMap.addClassDescriptorSourceUnit(sourceFile);
        
        return sourceFile;
    }                

}
