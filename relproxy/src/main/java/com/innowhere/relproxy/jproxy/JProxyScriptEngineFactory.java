package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.screngine.JProxyScriptEngineFactoryImpl;
import javax.script.ScriptEngineFactory;

/**
 * Is the root class of JSR-223 Java Scripting API support.
 * 
 * @author Jose Maria Arranz Santamaria
 */
public abstract class JProxyScriptEngineFactory implements ScriptEngineFactory
{
    /**
     * Factory method to create a <code>JProxyScriptEngineFactory</code> based on the provided configuration.
     * 
     * <p><code>javax.script.ScriptEngine</code> returned by the same factory object calling <code>ScriptEngineFactory.getScriptEngine()</code> will be using the provided configuration.</p>
     * 
     * @param config the configuration object.
     * @return the new factory initialized with the provided configuration.
     */
    public static JProxyScriptEngineFactory create(JProxyConfig config)
    {
        return JProxyScriptEngineFactoryImpl.create(config);
    }
}
