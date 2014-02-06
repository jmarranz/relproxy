
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;

/**
 *
 * @author Jose Maria Arranz Santamaria
 */
public interface GProxyConfig
{
    public GProxyConfig setEnabled(boolean enabled);

    public GProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener);

    public GProxyConfig setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine);
}
