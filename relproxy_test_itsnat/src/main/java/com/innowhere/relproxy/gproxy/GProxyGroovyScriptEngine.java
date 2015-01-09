package com.innowhere.relproxy.gproxy;

/**
 * Interface to implement the object implementing the <code>GroovyScriptEngine</code> wrapper used to reload Groovy classes.
 * 
 * <p>The following is a very simple example of the required implementation, <code>groovyEngine</code> is the <code>groovy.util.GroovyScriptEngine</code>
 * object:</p>
   <code>
   def gproxyGroovyEngine = {
             String scriptName -> return (java.lang.Class)groovyEngine.loadScriptByName(scriptName) 
        } as GProxyGroovyScriptEngine;
   </code> 
 *   
 * 
 * @see GProxyConfig#setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine) 
 * @author Jose Maria Arranz Santamaria
 */
public interface GProxyGroovyScriptEngine 
{
    /**
     * The class implementing this method must call the method <code>groovy.util.GroovyScriptEngine.loadScriptByName(String scriptName)</code> passing
     * the <code>scriptName</code>.
     * 
     * <p>This method is called by <code>GProxy</code> when it needs to get the Class associated to the specified Groovy script/class to check if this class
     * has changed because Groovy has reloaded the class when a source code has been detected.
     * 
     * @param scriptName the name of the Groovy script/class.
     * @return the class associated to the specified Groovy script.
     */
    public Class loadScriptByName(String scriptName);
}
