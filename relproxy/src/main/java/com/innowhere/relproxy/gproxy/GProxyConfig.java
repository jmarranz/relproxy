
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;

/**
 * Interface implemented by the configuration object needed to initialize <code>GProxy</code>.
 * 
 * 
 * @see GProxy#init(GProxyConfig)
 * @author Jose Maria Arranz Santamaria
 */
public interface GProxyConfig
{
    /**
     * Sets whether automatic detection of source code changes is enabled.
     * 
     * <p>If set to false other configuration parameters are ignored, there is no automatic source code change detection/reload and original objects are returned
     * instead of proxies, performance penalty is zero. Setting to false is recommended in production whether source code change detection/reload is not required.</p>
     * 
     * @param enabled whether automatic source code change detection and reload is enabled. By default is true.
     * @return this object for flow API use.
     */
    public GProxyConfig setEnabled(boolean enabled);

    /**
     * Sets the class reload listener.
     * 
     * @param relListener the class reload listener. By default is null.
     * @return this object for flow API use.
     */
    public GProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener);

    /**
     * Sets the object implementing the <code>GroovyScriptEngine</code> wrapper used to reload Groovy classes.
     * 
     * <p>This parameter is required otherwise there is no bridge between RelProxy and Groovy because there is no explicit Groovy dependency in RelProxy.
     * 
     * @param engine the <code>GroovyScriptEngine</code> wrapper.
     * @return this object for flow API use.
     */
    public GProxyConfig setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine);
}
