package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
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
        return null; // Por ahora
    }        
    
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }    
    
    @Override
    public void fixLastLoadedClass(ClassDescriptorSourceFileScript scriptFileDesc,JProxyShellClassLoader classLoader)
    {
        // Nada que arreglar, si el Class no está dará error más adelante y se considera un error inexperado
    }    
}
