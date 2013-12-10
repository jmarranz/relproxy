package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.gproxy.GProxyConfig;
import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.impl.GenericProxyConfigBaseImpl;

/**
 *
 * @author jmarranz
 */
public class GProxyConfigImpl extends GenericProxyConfigBaseImpl implements GProxyConfig
{
    protected GProxyGroovyScriptEngine engine;

    public GProxyConfig setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public GProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        this.relListener = relListener;  
        return this;        
    }
    
    public GProxyConfig setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine)
    {
        this.engine = engine;  
        return this;          
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }

}
