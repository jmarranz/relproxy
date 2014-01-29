package com.innowhere.relproxy.impl.jproxy.screngine;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.core.JProxyImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptInMemory;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp.JProxyCompilationException;
import com.innowhere.relproxy.impl.jproxy.shell.JProxyShellClassLoader;
import java.io.File;
import java.util.LinkedList;
import javax.script.ScriptContext;
import javax.script.ScriptException;

/**
 *
 * @author jmarranz
 */
public class JProxyScriptEngineDelegateImpl extends JProxyImpl
{
    protected ClassDescriptorSourceScript classDescSourceScript;
    protected long codeBufferModTimestamp = 0;     
    protected long lastCodeCompiledTimestamp = 0;
    
    public JProxyScriptEngineDelegateImpl(JProxyConfigImpl config)
    {
        SourceScript sourceFileScript = SourceScriptInMemory.createSourceScriptInMemory("");

        JProxyShellClassLoader classLoader = null;
        String classFolder = config.getClassFolder();
        if (classFolder != null)
            classLoader = new JProxyShellClassLoader(getDefaultClassLoader(),new File(classFolder));    

        this.classDescSourceScript = init(config,sourceFileScript,classLoader);       
    }    
    
    @Override
    public Class getMainParamClass()
    {
        return ScriptContext.class;
    }
    
    public SourceScriptInMemory getSourceScriptInMemory()
    {
        return (SourceScriptInMemory)classDescSourceScript.getSourceScript();
    }    
    
    public Object execute(String code,ScriptContext context) throws ScriptException
    {    
        // INTENTAR UNIFICAR CODIGO CON  JProxyShellProcessor
        
        if (!getSourceScriptInMemory().getScriptCode().equals(code))
        {
            this.codeBufferModTimestamp = System.currentTimeMillis();
        }
        
        if (codeBufferModTimestamp > lastCodeCompiledTimestamp)  
        {
            getSourceScriptInMemory().setScriptCode(code);
            // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms

            JProxyEngine engine = getJProxyEngine();

            ClassDescriptorSourceScript classDescSourceScript2 = null;
            try
            {
                classDescSourceScript2 = engine.detectChangesInSources();
            }
            catch(JProxyCompilationException ex) 
            {
                throw new ScriptException(ex);
            }

            if (classDescSourceScript2 != classDescSourceScript)
                throw new RelProxyException("Internal Error");
            
            this.lastCodeCompiledTimestamp = System.currentTimeMillis();  
            if (lastCodeCompiledTimestamp == codeBufferModTimestamp) // Demasiado rápido
            {
                try { Thread.sleep(1); } catch (InterruptedException ex) { throw new RelProxyException(ex);  }
                this.lastCodeCompiledTimestamp = System.currentTimeMillis(); // Así aseguramos que es posterior a codeBufferModTimestamp
            }
        }
        
        try
        {
            return classDescSourceScript.callMainMethod(context);    
        }
        catch(Throwable ex)
        {
            Exception ex2 = (ex instanceof Exception) ? (Exception)ex : new RelProxyException(ex);
            throw new ScriptException(ex2);
        }
    }          
}
