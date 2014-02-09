
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.impl.gproxy.GProxyConfigImpl;
import com.innowhere.relproxy.impl.gproxy.GProxyDefaultImpl;

/**
 * Is the class to create Java proxy objects based on Groovy objects and keep track of Groovy source code changes reloading Groovy classes when detected.
 * 
 * 
 * @author Jose Maria Arranz Santamaria
 */
public class GProxy 
{
    /**
     * Creates a {@link GProxyConfig} object to be used to configure <code>GProxy</code>.
     * 
     * @return a new configuration object.
     * @see #init(GProxyConfig)
     */
    public static GProxyConfig createGProxyConfig()
    {
        return GProxyDefaultImpl.createGProxyConfig();
    }        
    
    /**
     * Initializes <code>GProxy</code> with the provided configuration object.
     * 
     * @param config 
     */
    public static void init(GProxyConfig config)
    {
        GProxyDefaultImpl.initStatic((GProxyConfigImpl)config);
    }   
    
    /**
     * Creates a proxy object using <code>java.lang.reflect.Proxy</code> based on the provided Groovy object and the class of the implemented Java interface.
     * 
     * <p>If <code>GProxy</code> has been configured and is enabled this method returns a <code>java.lang.reflect.Proxy</code> object implementing instead of 
     * the original object provided, and method called in proxy object are received by <code>GProxy</code> and forwarded to the original object, if source code
     * managed by <code>GProxy</code> has been changed, the class of the original object is reloaded based on the new source and the original object
     * is recreated with the new class and fields are re-set in the new object, then the method is called on the new original object.</p>
     * 
     * <p>If <code>GProxy</code> is disabled returns the original object provided by parameter.</p>
     * 
     * @param <T> the interface implemented by the original object and proxy object returned.
     * @param obj the original object to proxy.
     * @param clasz the cñass of the interface implemented by the original object and proxy object returned.
     * @return the <code>java.lang.reflect.Proxy</code> object associated or the  original object when <code>GProxy</code> is disabled.
     */
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyDefaultImpl.createStatic(obj, clasz);
    }   
}
