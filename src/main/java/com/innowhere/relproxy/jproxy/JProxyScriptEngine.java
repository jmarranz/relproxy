package com.innowhere.relproxy.jproxy;

import javax.script.ScriptEngine;

/**
 *
 * @author jmarranz
 */
public interface JProxyScriptEngine extends ScriptEngine
{  
    public <T> T create(T obj,Class<T> clasz);

    public boolean start();

    public boolean stop();
}
