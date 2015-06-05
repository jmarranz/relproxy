package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRootFileJavaExt;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceUnit;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptor;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceFileRegistry;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceFileJava;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JavaSourcesSearch 
{
    protected final JProxyEngineChangeDetectorAndCompiler parent;
    
    public JavaSourcesSearch(JProxyEngineChangeDetectorAndCompiler parent)
    {
        this.parent = parent;
    }

    public JProxyEngineChangeDetectorAndCompiler getJProxyEngineChangeDetectorAndCompiler()
    {
        return parent;
    }
    
    public ClassDescriptorSourceScript sourceFileSearch(SourceScriptRoot scriptFile,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        ClassDescriptorSourceScript scriptFileDesc = (scriptFile == null) ? null : processSourceFileScript(scriptFile,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        FileExt[] folderSourceList = parent.getFolderSourceList().getArray();
        if (folderSourceList == null) // Es el caso de shell interactivo o code snippet
            return scriptFileDesc;
        
        boolean allEmpty = true;

        String scriptFileJavaCannonPath = (scriptFile != null && (scriptFile instanceof SourceScriptRootFileJavaExt)) ? ((SourceScriptRootFileJavaExt)scriptFile).getFileExt().getCanonicalPath() : null;

        for(int i = 0; i < folderSourceList.length; i++)
        {
            FileExt rootFolderOfSources = folderSourceList[i];
            String[] children = rootFolderOfSources.getFile().list(); 
            if (children == null) continue; // El que ha configurado los rootFolders es tonto y ha puesto alguno nulo o no es válido el path
            if (children.length == 0) continue; // Empty

            if (allEmpty) allEmpty = false;
            recursiveSourceFileJavaSearch(scriptFileJavaCannonPath, i ,rootFolderOfSources,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);           
        }

        if (oldSourceFileMap != null && !oldSourceFileMap.isEmpty())        
            deletedSourceFiles.addAll(oldSourceFileMap.getClassDescriptorSourceFileColl());         
        
        if (allEmpty)
            throw new RelProxyException("All specified input source folders are empty");

        return scriptFileDesc;
    }
    
    private void recursiveSourceFileJavaSearch(String scriptFileJavaCannonPath,int rootFolderOfSourcesIndex,FileExt parentPath,String[] relPathList,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        FileExt rootFolderOfSources = parent.getFolderSourceList().getArray()[rootFolderOfSourcesIndex];           
        JProxyInputSourceFileExcludedListener listener = parent.getJProxyInputSourceFileExcludedListener();        
        
        for(String relPath : relPathList)
        {
            File file = new File(parentPath.getCanonicalPath() + "/" + relPath);
            FileExt fileExt = new FileExt(file);        
            if (file.isDirectory())
            {
                if (listener != null && listener.isExcluded(file,rootFolderOfSources.getFile())) 
                    continue;                
                
                String[] children = file.list();  // Si está vacío el array está vacío pero existe
                recursiveSourceFileJavaSearch(scriptFileJavaCannonPath,rootFolderOfSourcesIndex,fileExt,children,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
            else
            {
                String ext = JProxyUtil.getFileExtension(file); // Si no tiene extensión devuelve ""
                if (!"java".equals(ext)) continue;
                //if (!"jsh".equals(ext)) continue;

                String cannonPath = JProxyUtil.getCanonicalPath(file);
                if (scriptFileJavaCannonPath != null && scriptFileJavaCannonPath.equals(cannonPath))
                    continue; // Es el propio archivo script inicial que es .java, así evitamos considerarlo dos veces
                
                if (listener != null && listener.isExcluded(file,rootFolderOfSources.getFile())) 
                    continue;
                                
                SourceFileJavaNormal sourceFile = new SourceFileJavaNormal(fileExt,rootFolderOfSources);
                processSourceFileJava(sourceFile,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
            }
        }
    }    
    
    private ClassDescriptorSourceScript processSourceFileScript(SourceScriptRoot file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {             
        return (ClassDescriptorSourceScript)processSourceFile(file,true,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }    
    
    private ClassDescriptorSourceFileJava processSourceFileJava(SourceFileJavaNormal file,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {    
        return (ClassDescriptorSourceFileJava)processSourceFile(file,false,oldSourceFileMap,newSourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);        
    }
    
    private ClassDescriptorSourceUnit processSourceFile(SourceUnit file,boolean script,ClassDescriptorSourceFileRegistry oldSourceFileMap,ClassDescriptorSourceFileRegistry newSourceFileMap,LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles,LinkedList<ClassDescriptorSourceUnit> newSourceFiles,LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles)
    {
        JProxyEngine engine = parent.getJProxyEngine();
        String className = file.getClassName(); 
        
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
                // Si el .class está en un JAR podríamos obtener el timestamp del archivo dentro del jar pero que haya un .java "fuera" reloadable indica que queremos "reemplazar" el del jar por lo que siempre se considerará que el archivo fuente ha sido modificado más reciente                
                long timestampCompiledClass = urlClassExt.startsWith("file:") ? new File(urlClass.getPath()).lastModified() : 0;  // 0 cuando está en un JAR

                if (timestampSourceFile > timestampCompiledClass)
                {
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
