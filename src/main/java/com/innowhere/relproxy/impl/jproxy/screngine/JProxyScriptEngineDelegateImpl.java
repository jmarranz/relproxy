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
import javax.script.ScriptContext;
import javax.script.ScriptException;

/**
 *
 * @author jmarranz
 */
public class JProxyScriptEngineDelegateImpl extends JProxyImpl
{
    protected JProxyScriptEngineImpl engine;
    protected ClassDescriptorSourceScript classDescSourceScript;
    protected long codeBufferModTimestamp = 0;     
    protected long lastCodeCompiledTimestamp = 0;
    
    public JProxyScriptEngineDelegateImpl(JProxyScriptEngineImpl engine,JProxyConfigImpl config)
    {
        this.engine = engine;
        
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
            getSourceScriptInMemory().setScriptCode(code,codeBufferModTimestamp);
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
            if (lastCodeCompiledTimestamp == codeBufferModTimestamp) // Demasiado rápido compilando
            {
                // Aseguramos que el siguiente código se ejecuta si o si con un codeBufferModTimestamp mayor que el timestamp de la compilación
                try { Thread.sleep(1); } catch (InterruptedException ex) { throw new RelProxyException(ex);  }
            }
        }
        
        try
        {
            return classDescSourceScript.callMainMethod(engine,context);    
        }
        catch(Throwable ex)
        {
            Exception ex2 = (ex instanceof Exception) ? (Exception)ex : new RelProxyException(ex);
            throw new ScriptException(ex2);
        }
    }          
}
