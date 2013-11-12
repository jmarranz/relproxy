package com.innowhere.relproxy.impl.jproxy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author jmarranz
 */
public class JavaSourcesSearch 
{
    protected String pathSources;
    protected ClassLoader parentClassLoader;
    
    public JavaSourcesSearch(String pathSources,ClassLoader parentClassLoader)
    {
        this.pathSources = new File(pathSources).getAbsolutePath(); // Para normalizar
        this.parentClassLoader = parentClassLoader;
    }
        
    public Map<String,ClassDescriptorSourceFile> javaFileSearch(Map<String,ClassDescriptorSourceFile> oldSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {
        Map<String,ClassDescriptorSourceFile> newSourceFileMap = new HashMap<String,ClassDescriptorSourceFile>();
        String[] children = new File(pathSources).list(); 
        recursiveJavaFileSearch(pathSources,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        if (oldSourceFileMap != null && !oldSourceFileMap.isEmpty())        
            deletedSourceFiles.addAll(oldSourceFileMap.values());
        
        return newSourceFileMap;
    }
    
    private void recursiveJavaFileSearch(String parentPath,String[] relPathList,Map<String,ClassDescriptorSourceFile> oldSourceFileMap,Map<String,ClassDescriptorSourceFile> newSourceFileMap,LinkedList<ClassDescriptorSourceFile> updatedSourceFiles,LinkedList<ClassDescriptorSourceFile> newSourceFiles,LinkedList<ClassDescriptorSourceFile> deletedSourceFiles)
    {
        for(String relPath : relPathList)
        {
            File file = new File(parentPath + "/" + relPath);        
            if (file.isDirectory())
            {
                String[] children = file.list();   
                recursiveJavaFileSearch(file.getAbsolutePath(),children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
            else
            {
                int pos = relPath.lastIndexOf('.');
                if (pos > 0) 
                {
                    String ext = relPath.substring(pos+1);
                    if ("java".equals(ext))
                    {
                        String path = file.getAbsolutePath();
                        String className = ClassDescriptorSourceFile.getClassNameFromSourceFileAbsPath(path, pathSources);
                        long timestampSourceFile = file.lastModified();
                        ClassDescriptorSourceFile sourceFile;
                        if (oldSourceFileMap != null)
                        {
                            sourceFile = oldSourceFileMap.get(className);
                            
                            if (sourceFile != null) // Cambiado
                            {
                                long oldTimestamp = sourceFile.getTimestamp();
                                if (timestampSourceFile > oldTimestamp)
                                {
                                    sourceFile.updateTimestamp(timestampSourceFile);
                                    updatedSourceFiles.add(sourceFile);
                                }
                                
                                oldSourceFileMap.remove(className); // Para que sólo queden las clases que han sido eliminadas
                            }          
                            else // Clase nueva
                            {
                                sourceFile = new ClassDescriptorSourceFile(className,file,timestampSourceFile);
                                newSourceFiles.add(sourceFile);
                            }
                        }
                        else  // Primera vez, vemos si el código fuente se ha cambiado respecto a los .class en el sistema de archivos
                        {
                            String relClassPath = ClassDescriptor.getRelativeClassFilePathFromClassName(className);
                            URL urlClass = parentClassLoader.getResource(relClassPath);
                            if (urlClass != null)
                            {
                                String urlClassExt = urlClass.toExternalForm();
                                long timestampCompiledClass = urlClassExt.startsWith("file:") ? new File(urlClass.getPath()).lastModified() : 0;  // 0 cuando está en un JAR

                                if (timestampSourceFile > timestampCompiledClass)
                                {
                                    // Si el .class está en un JAR no hay forma de saber si el fuente .java es más actual que el .class por lo que siempre se considerará que el archivo fuente ha sido modificado
                                    sourceFile = new ClassDescriptorSourceFile(className,file,timestampSourceFile);
                                    updatedSourceFiles.add(sourceFile);
//System.out.println("UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));
                                }
                                else
                                {
                                    // Esto es lo normal en carga si no hemos tocado el código tras el deploy, que el .class sea más reciente que el .java
                                    sourceFile = new ClassDescriptorSourceFile(className,file,timestampCompiledClass);
                                    byte[] classBytes = JReloaderUtil.readURL(urlClass);
                                    sourceFile.setClassBytes(classBytes);  
                                    // Falta cargar las posibles inner classes, hay que tener en cuenta que este archivo NO se va a compilar porque no ha cambiado respecto a .class conocido
                                    
//System.out.println("NOT UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));                                    
                                }
                                
                            }
                            else // No hay .class, es un archivo fuente nuevo
                            {
                                sourceFile = new ClassDescriptorSourceFile(className,file,timestampSourceFile);
                                newSourceFiles.add(sourceFile);
                            }
                        }

                        newSourceFileMap.put(className,sourceFile);
                    }
                }
            }                
        }
    }    
    

}
