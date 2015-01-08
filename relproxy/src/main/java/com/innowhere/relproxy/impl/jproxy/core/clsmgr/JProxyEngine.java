package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.core.JProxyImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp.JProxyCompilerContext;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp.JProxyCompilerInMemory;
import com.innowhere.relproxy.jproxy.JProxyCompilerListener;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import com.innowhere.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author jmarranz
 */
public class JProxyEngine 
{
    protected JProxyImpl parent;
    protected JProxyCompilerInMemory compiler;    
    protected SourceScriptRoot scriptFile; // Puede ser nulo
    protected ClassLoader rootClassLoader;
    protected FolderSourceList folderSourceList;
    protected JProxyInputSourceFileExcludedListener excludedListener;
    protected JProxyCompilerListener compilerListener;
    protected JProxyClassLoader customClassLoader;
    protected JavaSourcesSearch sourcesSearch;
    protected String folderClasses; // Puede ser nulo (es decir NO salvar como .class los cambios)
    protected long scanPeriod;
    protected ClassDescriptorSourceFileRegistry sourceRegistry;
    protected LinkedList<ClassDescriptorSourceUnit> sourceFilesCompiled;    
    protected String sourceEncoding = "UTF-8"; // Por ahora, provisional
    public volatile boolean stop = false;
    protected TimerTask task;
    
    public JProxyEngine(JProxyImpl parent,SourceScriptRoot scriptFile,ClassLoader rootClassLoader,FolderSourceList folderSourceList,JProxyInputSourceFileExcludedListener excludedListener,
                JProxyCompilerListener compilerListener,String classFolder,long scanPeriod,Iterable<String> compilationOptions,JProxyDiagnosticsListener diagnosticsListener)
    {
        this.parent = parent;
        this.scriptFile = scriptFile;
        this.rootClassLoader = rootClassLoader;
        this.folderSourceList = folderSourceList; 
        this.excludedListener = excludedListener;
        this.compilerListener = compilerListener;
        this.folderClasses = classFolder;
        this.scanPeriod = scanPeriod;
        this.compiler = new JProxyCompilerInMemory(this,compilationOptions,diagnosticsListener);        
        this.customClassLoader = new JProxyClassLoader(this);
        this.sourcesSearch = new JavaSourcesSearch(this);       
    }
    
    public JProxyImpl getJProxy()
    {
        return parent;
    }
    
    public ClassDescriptorSourceScript init()
    {
        ClassDescriptorSourceScript scriptFileDesc = detectChangesInSources(); // Primera vez para detectar cambios en los .java respecto a los .class mientras el servidor estaba parado
        
        reloadWhenChanged(); // La primera vez cargamos pues el código fuente manda sobre los .class

        startScanner();
        
        return scriptFileDesc;
    }
    
    private boolean startScanner()
    {
        if (scanPeriod > 0)  // Si es 0 o negativo sólo se recargan una vez (la inicial ya ejecutada)
        {
            Timer timer = new Timer();  
            this.task = new TimerTask()
            {
                @Override
                public void run() 
                {
                    if (stop)
                    {
                        cancel();
                        return;
                    }
                    
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

            timer.schedule(task, scanPeriod, scanPeriod);  // Ojo, después de la primera llamada a detectChangesInSources() 
            return true;
        }      
        else
        {
            return false;
        }
    }
   
    
    public FolderSourceList getFolderSourceList()
    {
        return folderSourceList;
    }
    
    public JProxyInputSourceFileExcludedListener getJProxyInputSourceFileExcludedListener()
    {
        return excludedListener;
    }
    
    public JProxyCompilerListener getJProxyCompilerListener()
    {
        return compilerListener;
    }
    
    public ClassLoader getRootClassLoader()
    {
        return rootClassLoader;
    }
    
    public String getSourceEncoding()
    {
        return sourceEncoding;
    }
    
    public synchronized boolean isRunning()    
    {    
        return task != null && scanPeriod > 0;
    }
    
    public synchronized boolean stop()    
    {
        if (task != null)
        {
            this.stop = true;
            task.cancel();
            this.task = null;
            return true;
        }
        else
        {
            return false;
        }
    }    
    
    public synchronized boolean start()    
    {
        if (task == null)
        {
            this.stop = false;
            return startScanner();
        }
        else return false;
    }        
    
    private boolean isSaveClassesMode()
    {
        return (folderClasses != null);
    }
    
    public synchronized ClassDescriptor getClassDescriptor(String className)
    {
        return sourceRegistry.getClassDescriptor(className);
    }
            
    public synchronized <T> Class<?> findClass(String className)
    {     
        // Si ya está cargada la devuelve, y si no se cargó por ningún JProxyClassLoader se intenta cargar por el parent ClassLoader, por lo que siempre devolverá distinto de null si la clase está en el classpath, que debería ser lo normal       
        try 
        { 
            return customClassLoader.findClass(className); 
        }
        catch (ClassNotFoundException ex) 
        {
            return null;
        }
    }
    
    private void addNewClassLoader()
    {
        for(ClassDescriptorSourceUnit sourceFile : sourceRegistry.getClassDescriptorSourceFileColl())
        {
            sourceFile.resetLastLoadedClass(); // resetea también las innerclasses
        }
        
        this.customClassLoader = new JProxyClassLoader(this);               
    }
    
    private void cleanBeforeCompile(ClassDescriptorSourceUnit sourceFile)
    {
        if (isSaveClassesMode()) 
            deleteClasses(sourceFile); // Antes de que nos las carguemos en memoria la clase principal y las inner tras recompilar
            
        sourceFile.cleanOnSourceCodeChanged(); // El código fuente nuevo puede haber cambiado totalmente las innerclasses antiguas (añadido, eliminado) y por supuesto el bytecode necesita olvidarse   
    }
    
    private void compile(ClassDescriptorSourceUnit sourceFile,JProxyCompilerContext context)
    {       
        if (sourceFile.getClassBytes() != null)
            return; // Ya ha sido compilado seguramente por dependencia de un archivo compilado inmediatamente antes, recuerda que el atributo classBytes se pone a null antes de compilar los archivos cambiados/nuevos
        
        compiler.compileSourceFile(sourceFile,context,customClassLoader,sourceRegistry);      
    }        
    
    private void reloadSource(ClassDescriptorSourceUnit sourceFile,boolean detectInnerClasses)
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
            clasz.getDeclaredClasses(); // Provoca que las inner clases miembro indirectamente se procesen y carguen a través del JProxyClassLoader de la clase padre clasz
            
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
    
    private void saveClasses(ClassDescriptorSourceUnit sourceFile)
    {
        // Salvamos la clase principal
        {
            File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(sourceFile.getClassName(),folderClasses);
            JProxyUtil.saveFile(classFilePath,sourceFile.getClassBytes());
        }

        // Salvamos las innerclasses si hay, no hay problema de clases inner no detectadas pues lo están todas pues sólo se salva tras una compilación
        LinkedList<ClassDescriptorInner> innerClassDescList = sourceFile.getInnerClassDescriptors();            
        if (innerClassDescList != null && !innerClassDescList.isEmpty())
        {
            for(ClassDescriptorInner innerClassDesc : innerClassDescList)
            {
                File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(innerClassDesc.getClassName(),folderClasses);
                JProxyUtil.saveFile(classFilePath,innerClassDesc.getClassBytes());                
            }
        }                           
    }    
    
    private void deleteClasses(ClassDescriptorSourceUnit sourceFile)
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
        
        File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(sourceFile.getClassName(),folderClasses);        
        File parentDir = JProxyUtil.getParentDir(classFilePath);
        String[] fileNameList = parentDir.list(); // Es más ligero que listFiles() que crea File por cada resultado
        if (fileNameList != null) // Si es null es que el directorio no está creado
        {
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
    }          
   
    public synchronized ClassDescriptorSourceScript detectChangesInSourcesAndReload()
    {
        ClassDescriptorSourceScript res = detectChangesInSources();
        reloadWhenChanged();
        return res;
    }    
    
    private synchronized ClassDescriptorSourceScript detectChangesInSources()
    {
        // boolean firstTime = (sourceFileMap == null); // La primera vez sourceFileMap es null

        LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();
        LinkedList<ClassDescriptorSourceUnit> newSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();        
        LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();
        
        ClassDescriptorSourceFileRegistry oldSourceRegistry = this.sourceRegistry; // Puede ser null (la primera vez)
        ClassDescriptorSourceFileRegistry newSourceRegistry = new ClassDescriptorSourceFileRegistry();
        
        ClassDescriptorSourceScript scriptFileDesc = sourcesSearch.sourceFileSearch(scriptFile,oldSourceRegistry,newSourceRegistry,updatedSourceFiles,newSourceFiles,deletedSourceFiles);
        
        this.sourceRegistry = newSourceRegistry;

        if (!updatedSourceFiles.isEmpty() || !newSourceFiles.isEmpty() || !deletedSourceFiles.isEmpty()) // También el hecho de eliminar una clase debe implicar crear un ClassLoader nuevo para que dicha clase desaparezca de las clases cargadas aunque será muy raro que sólo eliminemos un .java y no añadamos/cambiemos otros, otro motico es porque si tenemos configurado el autosalvado de .class tenemos que eliminar en ese caso
        {                      
            LinkedList<ClassDescriptorSourceUnit> sourceFilesToCompile = new LinkedList<ClassDescriptorSourceUnit>();
            sourceFilesToCompile.addAll(updatedSourceFiles);
            sourceFilesToCompile.addAll(newSourceFiles);            
            
            updatedSourceFiles = null; // Ya no se necesita
            newSourceFiles = null; // Ya no se necesita
            
            if (!sourceFilesToCompile.isEmpty())             
            {
                // Eliminamos el estado de la anterior compilación de todas las clases que van a recompilarse antes de compilarlas porque al compilar una clase es posible que
                // se necesite recompilar al mismo tiempo una dependiente de otra (ej clase base) y luego se intente compilar la dependiente y sería un problema que limpiáramos antes de compilar cada archivo
                for(ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile)            
                    cleanBeforeCompile(sourceFile);   
                
           
                JProxyCompilerContext context = compiler.createJProxyCompilerContext();
                JProxyCompilerListener compilerListener = getJProxyCompilerListener();
                try
                {            
                    
                    for(ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile)            
                    {
                        File file = null;
                        if (compilerListener != null)
                        {                           
                            SourceUnit srcUnit = sourceFile.getSourceUnit();
                            if (srcUnit instanceof SourceFileJavaNormal)
                                file = ((SourceFileJavaNormal)srcUnit).getFileExt().getFile();
                            else if (srcUnit instanceof SourceScriptRootFile)
                                file = ((SourceScriptRootFile)srcUnit).getFileExt().getFile();
                            else if (srcUnit instanceof SourceScriptRootInMemory) // Caso de shell interactive y code snippet, en ese caso NO hay listener porque no hay forma de definirlo
                                file = null;
                        }
                        
                        if (compilerListener != null && file != null)
                            compilerListener.beforeCompile(file);                        
                        
                        compile(sourceFile,context);        
                        
                        if (compilerListener != null && file != null)
                            compilerListener.afterCompile(file);                        
                    }
                }
                finally
                {
                    context.close();
                }
                
                if (isSaveClassesMode())
                {
                    for(ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile)            
                    {
                        saveClasses(sourceFile);                     
                    }                
                }
            }

            if (isSaveClassesMode() && !deletedSourceFiles.isEmpty())
                for(ClassDescriptorSourceUnit sourceFile : deletedSourceFiles)
                    deleteClasses(sourceFile);                     
            
            deletedSourceFiles = null; // Ya no se necesita
              
            for(ClassDescriptorSourceUnit sourceFile : sourceRegistry.getClassDescriptorSourceFileColl())
            {
                if (sourceFilesToCompile.contains(sourceFile))
                    continue;
                // las clases deleted no están en sourceFileMap por lo que no hay que filtrarlas
                reloadSource(sourceFile,true); // Ponemos detectInnerClasses a true porque son archivos fuente que posiblemente nunca se hayan tocado desde la carga inicial y por tanto quizás se desconocen las innerclasses
            }
            
            this.sourceFilesCompiled = sourceFilesToCompile;
        }
        else
        {
            this.sourceFilesCompiled = null;
        }
        
        return scriptFileDesc;
    }
    
    public synchronized boolean reloadWhenChanged()
    {
        if (sourceFilesCompiled != null && !sourceFilesCompiled.isEmpty())
        {
            addNewClassLoader();
            
            for(ClassDescriptorSourceUnit sourceFile : sourceFilesCompiled)            
            {
                reloadSource(sourceFile,false); // No hace falta que detectemos las innerclasses porque al compilar se "descubren" todas                    
            }            
                        
            for(ClassDescriptorSourceUnit sourceFile : sourceRegistry.getClassDescriptorSourceFileColl())  // sourceRegistry NUNCA es nulo pues se ejecuta una primera vez en tiempo de inicialización
            {
                if (sourceFilesCompiled.contains(sourceFile))
                    continue;
                // las clases deleted no están en sourceFileMap por lo que no hay que filtrarlas
                reloadSource(sourceFile,true); // Ponemos detectInnerClasses a true porque son archivos fuente que posiblemente nunca se hayan tocado desde la carga inicial y por tanto quizás se desconocen las innerclasses
            }            
            
            this.sourceFilesCompiled = null;
            return true;
        }
        return false;         
    }
   
}
