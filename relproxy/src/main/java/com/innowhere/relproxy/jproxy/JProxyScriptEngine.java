package com.innowhere.relproxy.jproxy;

import javax.script.ScriptEngine;

/**
 * Interface implemented by RelProxy to provide <code>javax.script.ScriptEngine</code> objects supporting Java as a scripting language.
 * 
 * <p>The method {@link JProxyScriptEngineFactory#getScriptEngine()} returns an implementation of this interface.</p>
 * 
 * @author Jose Maria Arranz Santamaria
 */
public interface JProxyScriptEngine extends ScriptEngine
{  
    /**
     * Initializes this <code>JProxyScriptEngine</code> instance with the provided configuration object.
     * 
     * @param config the configuration object.
     */    
    public void init(JProxyConfig config);
    
    /**
     * This method is the same as {@link JProxy#create(java.lang.Object, java.lang.Class)} but applied to this <code>JProxyScriptEngine</code>
     * 
     * @param <T> the interface implemented by the original object and proxy object returned.
     * @param obj the original object to proxy.
     * @param clasz the class of the interface implemented by the original object and proxy object returned.
     * @return the <code>java.lang.reflect.Proxy</code> object associated or the original object when <code>JProxy</code> is disabled.
     */
    public <T> T create(T obj,Class<T> clasz);

    /**
     * Informs whether <code>JProxy</code> is configured and started.
     * 
     * @return true if running. 
     */    
    public boolean isRunning();        
    
    /**
     * This method is the same as {@link JProxy#stop()} but applied to this <code>JProxyScriptEngine</code>
     * 
     * @return true if source change detection has been stopped, false if it is already stopped or this <code>JProxyScriptEngine</code> is not enabled or initialized.
     * @see #stop()
     */    
    public boolean stop();

    /**
     * This method is the same as {@link JProxy#start()} but applied to this <code>JProxyScriptEngine</code>
     * 
     * @return true if source change detection has been started again, false if it is already started or cannot start because this <code>JProxyScriptEngine</code> is not enabled or initialized or scan period is not positive.
     * @see #start()
     */
    public boolean start();    
}
