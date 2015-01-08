package com.innowhere.relproxy.impl.jproxy.screngine;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.core.JProxyImpl;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRoot;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.SourceScriptRootInMemory;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.comp.JProxyCompilationException;
import com.innowhere.relproxy.impl.jproxy.shell.JProxyShellClassLoader;
import java.io.File;
import javax.script.ScriptContext;
import javax.script.ScriptException;

/**
 *
 * @author jmarranz
 */
public class JProxyScriptEngineDelegateImpl extends JProxyImpl
{
    protected JProxyScriptEngineImpl parent;
    protected ClassDescriptorSourceScript classDescSourceScript;
    protected long codeBufferModTimestamp = 0;     
    protected long lastCodeCompiledTimestamp = 0;
    
    public JProxyScriptEngineDelegateImpl(JProxyScriptEngineImpl parent,JProxyConfigImpl config)
    {
        this.parent = parent;
        
        SourceScriptRoot sourceFileScript = SourceScriptRootInMemory.createSourceScriptInMemory("");

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
    
    private SourceScriptRootInMemory getSourceScriptInMemory()
    {
        return (SourceScriptRootInMemory)classDescSourceScript.getSourceScript();
    }    
    
    public Object execute(String code,ScriptContext context) throws ScriptException
    {    
        JProxyEngine jproxyEngine = getJProxyEngine();
        
        Class scriptClass;
        synchronized(jproxyEngine)
        {
            if (!getSourceScriptInMemory().getScriptCode().equals(code))  
            {
                this.codeBufferModTimestamp = System.currentTimeMillis();

                getSourceScriptInMemory().setScriptCode(code,codeBufferModTimestamp);
                // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms

                ClassDescriptorSourceScript classDescSourceScript2 = null;
                try
                {
                    classDescSourceScript2 = jproxyEngine.detectChangesInSourcesAndReload();
                }
                catch(JProxyCompilationException ex) 
                {
                    throw new ScriptException(ex);
                }

                if (classDescSourceScript2 != classDescSourceScript)
                    throw new RelProxyException("Internal Error");

                this.lastCodeCompiledTimestamp = System.currentTimeMillis();  
                if (lastCodeCompiledTimestamp == codeBufferModTimestamp) // Demasiado rápido compilando
                {
                    // Aseguramos que el siguiente código se ejecuta si o si con un codeBufferModTimestamp mayor que el timestamp de la compilación
                    try { Thread.sleep(1); } catch (InterruptedException ex) { throw new RelProxyException(ex);  }
                }
            }

            scriptClass = classDescSourceScript.getLastLoadedClass();
        }
        
        try
        {
            return ClassDescriptorSourceScript.callMainMethod(scriptClass,parent,context);    
        }
        catch(Throwable ex)
        {
            Exception ex2 = (ex instanceof Exception) ? (Exception)ex : new RelProxyException(ex);
            throw new ScriptException(ex2);
        }        
    }          
}
