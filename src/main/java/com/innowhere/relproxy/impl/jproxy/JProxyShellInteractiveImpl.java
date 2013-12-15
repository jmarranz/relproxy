package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JProxyShellInteractiveImpl extends JProxyShellImpl
{
    public void init(String[] args)
    {       
        super.init(args, false,  null);
    }      
    
    protected void executeFirstTime(ClassDescriptorSourceFileScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        // La primera vez el script es vacío, no hay nada que ejecutar
    }    
    
    @Override
    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute in interactive mode");        
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
