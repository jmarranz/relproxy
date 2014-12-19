package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRoot;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRootInMemory;
import com.innowhere.relproxy.impl.jproxy.shell.inter.JProxyShellProcessor;
import java.io.File;
import java.util.LinkedList;

/**
 * Alguna inspiración: http://groovy.codehaus.org/Groovy+Shell
 * 
 * @author jmarranz
 */
public class JProxyShellInteractiveImpl extends JProxyShellImpl
{
    protected boolean test = false;
    protected JProxyShellProcessor processor = new JProxyShellProcessor(this);
    protected ClassDescriptorSourceScript classDescSourceScript;
    
    public void init(String[] args)
    {          
        this.classDescSourceScript = super.init(args, null);

        if (test) 
        { 
            processor.test();
            return;
        }
        
        processor.loop();
    }      
    
    public ClassDescriptorSourceScript getClassDescriptorSourceScript()
    {
        return classDescSourceScript;
    }
    
    public SourceScriptRootInMemory getSourceScriptInMemory()
    {
        return (SourceScriptRootInMemory)classDescSourceScript.getSourceScript();
    }
    
    @Override
    public ClassDescriptorSourceScript init(JProxyConfigImpl config,SourceScriptRoot scriptFile,ClassLoader classLoader)
    {    
        ClassDescriptorSourceScript script = super.init(config,scriptFile, classLoader);
        
        this.test = config.isTest();
        
        return script;
    }
        
    @Override
    protected void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        // La primera vez el script es vacío, no hay nada que ejecutar
    }    
    
    @Override
    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute in interactive mode");        
        
        // No tiene sentido especificar un tiempo de scan porque no hay directorio de entrada en el que escanear archivos
        if (config.getScanPeriod() >= 0) // 0 no puede ser porque da error antes pero lo ponemos para reforzar la idea
            throw new RelProxyException("scanPeriod positive value has no sense in interactive execution");        
    }    

    @Override    
    protected SourceScriptRoot createSourceScriptRoot(String[] args,LinkedList<String> argsToScript,FolderSourceList folderSourceList) 
    {
        return SourceScriptRootInMemory.createSourceScriptInMemory(""); // La primera vez no hace nada, sirve para "calentar" la app
    }    
    
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }    
}
