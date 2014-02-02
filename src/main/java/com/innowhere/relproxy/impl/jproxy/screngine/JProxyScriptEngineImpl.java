package com.innowhere.relproxy.impl.jproxy.screngine;

import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.jproxy.JProxyScriptEngine;
import java.io.Reader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/**
 *
 * @author jmarranz
 */
public class JProxyScriptEngineImpl extends AbstractScriptEngine implements JProxyScriptEngine
{
    protected JProxyScriptEngineFactoryImpl factory;
    protected JProxyScriptEngineDelegateImpl delegate;

    public JProxyScriptEngineImpl(JProxyScriptEngineFactoryImpl factory,JProxyConfigImpl config)
    {
        this.factory = factory;
        this.delegate = new JProxyScriptEngineDelegateImpl(this,config);
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException
    {
        return delegate.execute(script,context);
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
        return delegate.create(obj, clasz);
    }

    @Override
    public boolean start()
    {
        return delegate.start();
    }

    @Override
    public boolean stop()
    {
        return delegate.stop();
    }
}
