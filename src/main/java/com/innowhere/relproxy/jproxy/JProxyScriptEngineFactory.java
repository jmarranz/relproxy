package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.JProxyScriptEngineFactoryImpl;
import javax.script.ScriptEngineFactory;

/**
 *
 * @author jmarranz
 */
public abstract class JProxyScriptEngineFactory implements ScriptEngineFactory
{
    public static ScriptEngineFactory create(JProxyConfig config)
    {
        return JProxyScriptEngineFactoryImpl.create(config);
    }


}
