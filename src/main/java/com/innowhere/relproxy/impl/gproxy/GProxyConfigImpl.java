package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.innowhere.relproxy.impl.GenericProxyConfigBaseImpl;

/**
 *
 * @author jmarranz
 */
public class GProxyConfigImpl extends GenericProxyConfigBaseImpl
{
    protected GProxyGroovyScriptEngine engine;

    public void setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine)
    {
        this.engine = engine;        
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }

}
