package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JProxyShellCodeSnippetImpl extends JProxyShellImpl
{
    public void init(String[] args)
    {       
        super.init(args, null);
    }      
    
    @Override    
    protected void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        scriptFileDesc.callMainMethod(argsToScript);
    }    
    
    @Override
    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute a code snippet");        
    }        
    
    @Override    
    protected SourceScript getSourceScript(String[] args,LinkedList<String> argsToScript) 
    {
        StringBuilder code = new StringBuilder();
        code.append(args[0]);  // En argsToScript no está el args[0]
        for(String chunk : argsToScript)
            code.append(chunk);
        return new SourceScriptInMemory("_jproxyShellInMemoryClass_",code.toString());
    }    
   
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }

}
