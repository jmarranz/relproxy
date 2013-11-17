package com.innowhere.relproxy.impl.jproxy.clsmgr;

import com.innowhere.relproxy.impl.jproxy.clsmgr.comp.JReloaderCompilerInMemory;
import java.io.File;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JReloaderEngine 
{
    protected JReloaderCompilerInMemory compiler;
    protected ClassLoader parentClassLoader;
    protected JReloaderClassLoader customClassLoader;
    protected JavaSourcesSearch sourcesSearch;
    protected Map<String,ClassDescriptorSourceFile> sourceFileMap;
    protected String classFolder; // Puede ser nulo (es decir NO salvar como .class los cambios)
    
    public JReloaderEngine(ClassLoader parentClassLoader,String pathSources,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics)
    {
        this.parentClassLoader = parentClassLoader;
        this.classFolder = classFolder;
        this.compiler = new JReloaderCompilerInMemory(compilationOptions,diagnostics);        
        this.customClassLoader = new JReloaderClassLoader(this,parentClassLoader);
        this.sourcesSearch = new JavaSourcesSearch(pathSources,parentClassLoader);       
        detectChangesInSources(); // Primera vez para detectar cambios en los .java respecto a los .class mientras el servidor estaba parado
        
        
        Timer timer = new Timer();        
        TimerTask task = new TimerTask()
        {
            @Override
            public void run() 
            {
                try
                {
                    detectChangesInSources();
                }        
                catch(Exception ex)
                {
                    ex.printStackTrace(System.err); // Si dejamos subir la excepción se acabó el timer
                }
            }
        };                
        timer.schedule(task, scanPeriod, scanPeriod);  // Ojo, después de recursiveJavaFileSearch()      
    }
    
    /*
    public synchronized boolean isHotLoadableClass(String className)
    {
        // Las innerclasses no están como tales en sourceFileMap pues sólo está la clase contenedora pero también la consideramos hotloadable
        int pos = className.lastIndexOf('$');
        if (pos != -1) className = className.substring(0, pos);

        return sourceFileMap.containsKey(className);
    }
    */
    
    public boolean isSaveClassesMode()
    {
        return (classFolder != null);
    }
    
    public synchronized ClassDescriptor getClassDescriptor(String className)
    {
        // Puede ser el de una innerclass
        // Las innerclasses no están como tales en sourceFileMap pues sólo está la clase contenedora pero también la consideramos hotloadable
        String parentClassName;
        int pos = className.lastIndexOf('$');        
        boolean inner;        
        if (pos != -1)
        {
            parentClassName = className.substring(0, pos);
            inner = true;
        }
        else
        {
            parentClassName = className;
            inner = false;
        }
        ClassDescriptorSourceFile sourceDesc = sourceFileMap.get(parentClassName);        
        if (!inner) return sourceDesc;
        return sourceDesc.getInnerClassDescriptor(className,true);
    }
            
    public synchronized <T> Class<?> findClass(String className)
    {     
        // Si ya está cargada la devuelve, y si no se cargó por ningún JReloaderClassLoader se intenta cargar por el parent ClassLoader, por lo que siempre devolverá distinto de null si la clase está en el classpath, que debería ser lo normal       
        try 
        { 
            return customClassLoader.findClass(className); 
        }
        catch (ClassNotFoundException ex) 
        {
            return null;
        }
    }
    
    private synchronized void addNewClassLoader()
    {
        for(ClassDescriptorSourceFile sourceFile : sourceFileMap.values())
        {
            sourceFile.resetLastLoadedClass(); // resetea también las innerclasses
        }
        
        this.customClassLoader = new JReloaderClassLoader(this,parentClassLoader);               
    }
    
    private void cleanBeforeCompile(ClassDescriptorSourceFile sourceFile)
    {
        if (isSaveClassesMode()) 
            deleteClasses(sourceFile); // Antes de que nos las carguemos en memoria la clase principal y las inner tras recompilar
            
        sourceFile.cleanOnSourceCodeChanged(); // El código fuente nuevo puede haber cambiado totalmente las innerclasses antiguas (añadido, eliminado) y por supuesto el bytecode necesita olvidarse   
    }
    
    private void compile(ClassDescriptorSourceFile sourceFile)
    {       
        compiler.compileSourceFile(sourceFile,customClassLoader,sourceFileMap);      
    }        
    
    private void reloadAndSaveSource(ClassDescriptorSourceFile sourceFile)
    {       
        reloadSource(sourceFile,false); // No hace falta que detectemos las innerclasses porque al compilar se "descubren" todas

        if (isSaveClassesMode()) saveClasses(sourceFile);        
    }       
    
    private void reloadSource(ClassDescriptorSourceFile sourceFile,boolean detectInnerClasses)
    {
        Class clasz = customClassLoader.loadClass(sourceFile,true);      

        LinkedList<ClassDescriptorInner> innerClassDescList = sourceFile.getInnerClassDescriptors(); 
        if (innerClassDescList != null && !innerClassDescList.isEmpty())
        {
            for(ClassDescriptorInner innerClassDesc : innerClassDescList)
            {
                customClassLoader.loadClass(innerClassDesc,true);           
            }
        }        
        else if (detectInnerClasses)
        {
            // Aprovechando la carga de la clase, hacemos el esfuerzo de cargar todas las clases dependientes lo más posible
            clasz.getDeclaredClasses(); // Provoca que las inner clases miembro indirectamente se procesen y carguen a través del JReloaderClassLoader de la clase padre clasz
            
            // Ahora bien, lo anterior NO sirve para las anonymous inner classes, afortunadamente en ese caso podemos conocer y cargar por fuerza bruta
            // http://stackoverflow.com/questions/1654889/java-reflection-how-can-i-retrieve-anonymous-inner-classes?rq=1
            
            for(int i = 1; i < Integer.MAX_VALUE; i++)
            {
                String anonClassName = sourceFile.getClassName() + "$" + i;                 
                Class innerClasz = customClassLoader.loadInnerClass(sourceFile,anonClassName);
                if (innerClasz == null) break; // No hay más o no hay ninguna (si i es 1)
            } 
            
            // ¿Qué es lo que queda por cargar pero que no podemos hacer explícitamente?
            // 1) Las clases privadas autónomas que fueron definidas en el mismo archivo que la clase principal: no las soportamos pues no podemos identificar en el ClassLoader que es una clase "hot reloadable", no son inner classes en el sentido estricto
            // 2) Las clases privadas "inner" locales, es decir no anónimas declaradas dentro de un método, se cargarán la primera vez que se usen, no podemos conocerlas a priori
            //    porque siguen la notación className$NclassName  ej. JReloadExampleDocument$1AuxMemberInMethod. No hay problema con que se carguen con un class loader antiguo pues
            //    el ClassLoader de la clase padre contenedora será el encargado de cargarla en cuanto se pase por el método que la declara.
        }     
    }    
    
    private void saveClasses(ClassDescriptorSourceFile sourceFile)
    {
        // Salvamos la clase principal
        {
            String classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(sourceFile.getClassName(),classFolder);
            JReloaderUtil.saveFile(classFilePath,sourceFile.getClassBytes());
        }

        // Salvamos las innerclasses si hay, no hay problema de clases inner no detectadas pues lo están todas pues sólo se salva tras una compilación
        LinkedList<ClassDescriptorInner> innerClassDescList = sourceFile.getInnerClassDescriptors();            
        if (innerClassDescList != null && !innerClassDescList.isEmpty())
        {
            for(ClassDescriptorInner innerClassDesc : innerClassDescList)
            {
                String classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(innerClassDesc.getClassName(),classFolder);
                JReloaderUtil.saveFile(classFilePath,innerClassDesc.getClassBytes());                
            }
        }                           
    }    
    
    private void deleteClasses(ClassDescriptorSourceFile sourceFile)
    {
        // Puede ocurrir que esta clase nunca se haya cargado y se ha modificado el código fuente y queramos limpiar los .class correspondientes pues se van a recrear
        // como no conocemos qué inner clases están asociadas para saber que .class hay que eliminar, pues lo que hacemos es directamente obtener los .class que hay 
        // en el directorio con el fin de eliminar todos .class que tengan el patrón de ser inner classes del source file de acuerdo a su nombre
        // así conseguimos por ejemplo también eliminar las local classes (inner clases con nombre declaradas dentro de un método) que no hay manera de conocer 
        // a través de la carga de la clase
        
        // Hay un caso en el que puede haber .class que ya no están en el código fuente y es cuando tocamos el código fuente ANTES de cargar y eliminamos algún .java,
        // al cargar como no existe el archivo no lo relacionamos con los .class
        // La solución sería en tiempo de carga forzar una carga de todas las clases y de ahí deducir todos los .class que deben existir (excepto las clases locales
        // que no podríamos detectarlas), pero el que haya .class sobrantes antiguos no es gran problema.
        
        String classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(sourceFile.getClassName(),classFolder);        
        File parentDir = JReloaderUtil.getParentDir(classFilePath);
        String[] fileNameList = parentDir.list(); // Es más ligero que listFiles() que crea File por cada resultado
        for (String fileName : fileNameList) 
        {
            int pos = fileName.lastIndexOf(".class");
            if (pos == -1) continue;
            String simpleClassName = fileName.substring(0, pos);
            if (sourceFile.getSimpleClassName().equals(simpleClassName) ||
                sourceFile.isInnerClass(sourceFile.getPackageName() + simpleClassName))
            {
                new File(parentDir,fileName).delete();
            }
        }
    }          
    
    private synchronized void detectChangesInSources()
    {
        // boolean firstTime = (sourceFileMap == null); // La primera vez sourceFileMap es null

        LinkedList<ClassDescriptorSourceFile> updatedSourceFiles = new LinkedList<ClassDescriptorSourceFile>();
        LinkedList<ClassDescriptorSourceFile> newSourceFiles = new LinkedList<ClassDescriptorSourceFile>();        
        LinkedList<ClassDescriptorSourceFile> deletedSourceFiles = new LinkedList<ClassDescriptorSourceFile>();
        
        this.sourceFileMap = sourcesSearch.javaFileSearch(sourceFileMap,updatedSourceFiles,newSourceFiles,deletedSourceFiles);  

        if (!updatedSourceFiles.isEmpty() || !newSourceFiles.isEmpty() || !deletedSourceFiles.isEmpty()) // También el hecho de eliminar una clase debe implicar crear un ClassLoader nuevo para que dicha clase desaparezca de las clases cargadas aunque será muy raro que sólo eliminemos un .java y no añadamos/cambiemos otros, otro motico es porque si tenemos configurado el autosalvado de .class tenemos que eliminar en ese caso
        {   
            addNewClassLoader();
                        
            LinkedList<ClassDescriptorSourceFile> sourceFilesToRecompile = new LinkedList<ClassDescriptorSourceFile>();
            sourceFilesToRecompile.addAll(updatedSourceFiles);
            sourceFilesToRecompile.addAll(newSourceFiles);            
            
            updatedSourceFiles = null;
            newSourceFiles = null;
            
            if (!sourceFilesToRecompile.isEmpty())             
            {
                // Eliminamos el estado de la anterior compilación de todas las clases que van a recompilarse antes de compilarlas porque al compilar una clase es posible que
                // se necesite recompilar al mismo tiempo una dependiente de otra (ej clase base) y luego se intente compilar la dependiente y sería un problema que limpiáramos antes de compilar cada archivo
                for(ClassDescriptorSourceFile sourceFile : sourceFilesToRecompile)            
                    cleanBeforeCompile(sourceFile);   
                
                for(ClassDescriptorSourceFile sourceFile : sourceFilesToRecompile)            
                    compile(sourceFile);        
                
                for(ClassDescriptorSourceFile sourceFile : sourceFilesToRecompile)            
                    reloadAndSaveSource(sourceFile);                
            }

            if (isSaveClassesMode() && !deletedSourceFiles.isEmpty())
                for(ClassDescriptorSourceFile sourceFile : deletedSourceFiles)
                    deleteClasses(sourceFile);                     
            
            
            for(Map.Entry<String,ClassDescriptorSourceFile> entry : sourceFileMap.entrySet())
            {
                //String className = entry.getKey();
                ClassDescriptorSourceFile sourceFile = entry.getValue();
                if (sourceFilesToRecompile.contains(sourceFile))
                    continue;
                // las clases deleted no están en sourceFileMap por lo que no hay que filtrarlas
                reloadSource(sourceFile,true); // Ponemos detectInnerClasses a true porque son archivos fuente que posiblemente nunca se hayan tocado desde la carga inicial y por tanto quizás se desconocen las innerclasses
            }
         
        }
    }
    
}
