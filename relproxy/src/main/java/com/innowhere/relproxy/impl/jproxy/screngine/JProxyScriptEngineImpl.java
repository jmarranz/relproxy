package com.innowhere.relproxy.impl.jproxy.screngine;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.GenericProxyImpl;
import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.jproxy.JProxyConfig;
import com.innowhere.relproxy.jproxy.JProxyScriptEngine;
import java.io.Reader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/**
 * Methods of this class are similar to JProxyDefaultImpl
 * 
 * @author jmarranz
 */
public class JProxyScriptEngineImpl extends AbstractScriptEngine implements JProxyScriptEngine
{
    protected JProxyScriptEngineDelegateImpl jproxy;
    protected JProxyScriptEngineFactoryImpl factory;

    public JProxyScriptEngineImpl(JProxyScriptEngineFactoryImpl factory)
    {
        this.factory = factory;
    }

    @Override    
    public void init(JProxyConfig config)    
    {
        JProxyConfigImpl configImpl = (JProxyConfigImpl)config;
        if (!configImpl.isEnabled()) return; // jproxy quedará null       

        GenericProxyImpl.checkSingletonNull(jproxy);        
        this.jproxy = new JProxyScriptEngineDelegateImpl(this);
        jproxy.init(configImpl);        
    }
    

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException
    {
        if (jproxy == null) 
            throw new RelProxyException("Engine is disabled");
        
        return jproxy.execute(script,context);
    }

    @Override
    public Object eval(Reader reader, ScriptContext context) throws ScriptException
    {
        String script = JProxyUtil.readTextFile(reader);
        return eval(script,context);
    }

    @Override
    public Bindings createBindings()
    {
        return new BindingsImpl();
    }

    @Override
    public ScriptEngineFactory getFactory()
    {
        return factory;
    }

    @Override
    public <T> T create(T obj,Class<T> clasz)
    {
        if (jproxy == null) 
            return obj; // No se ha llamado al init o enabled = false
        return jproxy.create(obj, clasz);
    }

    @Override
    public Object create(Object obj,Class<?>[] classes)
    {
        if (jproxy == null) 
            return obj; // No se ha llamado al init o enabled = false
        return jproxy.create(obj, classes);
    }    
    
    @Override    
    public boolean isEnabled()
    {
        if (jproxy == null) 
            return false;
        
        return jproxy.isEnabled();
    }    
    
    @Override    
    public boolean isRunning()
    {
        if (jproxy == null) 
            return false;
        
        return jproxy.isRunning();
    }        
    
    @Override
    public boolean start()
    {
        if (jproxy == null) 
            return false;
        
        return jproxy.start();
    }

    @Override
    public boolean stop()
    {
        if (jproxy == null) 
            return false;
        
        return jproxy.stop();
    }
}
