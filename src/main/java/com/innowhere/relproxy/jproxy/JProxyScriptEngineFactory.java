package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.screngine.JProxyScriptEngineFactoryImpl;
import javax.script.ScriptEngineFactory;

/**
 *
 * @author Jose Maria Arranz Santamaria
 */
public abstract class JProxyScriptEngineFactory implements ScriptEngineFactory
{
    public static JProxyScriptEngineFactory create(JProxyConfig config)
    {
        return JProxyScriptEngineFactoryImpl.create(config);
    }
}
