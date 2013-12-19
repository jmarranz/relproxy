package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Alguna inspiración: http://groovy.codehaus.org/Groovy+Shell
 * 
 * @author jmarranz
 */
public class JProxyShellInteractiveImpl extends JProxyShellImpl
{
    protected boolean test = false;
    
    public void init(String[] args)
    {       
        ClassDescriptorSourceScript script = super.init(args, null);
        
        SourceScriptInMemory sourceScript = (SourceScriptInMemory)script.getSourceScript();

        if (test) 
        { 
            try { Thread.sleep(2); } catch (InterruptedException ex){  }
            execute("System.out.println(\"Hello World\");",script,sourceScript);
            return;
        }
        
        loop(script,sourceScript);
    }      
    
    @Override
    public ClassDescriptorSourceScript init(JProxyConfigImpl config,SourceScript scriptFile,ClassLoader classLoader)
    {    
        ClassDescriptorSourceScript script = super.init(config, scriptFile, classLoader);
        
        this.test = config.isTest();
        
        return script;
    }
    
    private void loop(ClassDescriptorSourceScript script,SourceScriptInMemory sourceScript)
    {
        Scanner sc = new Scanner(System.in);        
        while(true)
        {
            System.out.print(">");
            String line = sc.nextLine();
            execute(line,script,sourceScript);
        }
    }
    
    private void execute(String code,ClassDescriptorSourceScript script,SourceScriptInMemory sourceScript)
    {
        sourceScript.setScriptCode(code);
        // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms
        
        JProxyEngine engine = getJProxyEngine();
        if (engine.detectChangesInSources() != script)
            throw new RelProxyException("Internal Error");
        
        script.callMainMethod(new LinkedList<String>());    
    }
    
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
    protected SourceScript getSourceScript(String[] args,LinkedList<String> argsToScript) 
    {
        return new SourceScriptInMemory("_jproxyShellInMemoryClass_",""); // La primera vez no hace nada, sirve para "calentar" la app
    }    
    
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }    

}
