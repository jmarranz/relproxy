
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.gproxy.GProxyConfigImpl;

/**
 *
 * @author jmarranz
 */
public class GProxyConfig
{
    protected GProxyConfigImpl configImpl = new GProxyConfigImpl();

    public GProxyConfig setEnabled(boolean enabled)
    {
        configImpl.setEnabled(enabled);
        return this;
    }

    public GProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        configImpl.setRelProxyOnReloadListener(relListener);
        return this;        
    }

    public GProxyConfig setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine)
    {
        configImpl.setGProxyGroovyScriptEngine(engine);
        return this;        
    }

}
