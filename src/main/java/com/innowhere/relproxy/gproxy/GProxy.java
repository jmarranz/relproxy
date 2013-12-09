
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.impl.gproxy.GProxyDefaultImpl;

/**
 *
 * @author jmarranz
 */
public class GProxy 
{
    public static GProxyConfig createGProxyConfig()
    {
        return GProxyDefaultImpl.createGProxyConfig();
    }        
    
    public static void init(GProxyConfig config)
    {
        GProxyDefaultImpl.initStatic(config.configImpl);
    }   
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyDefaultImpl.createStatic(obj, clasz);
    }   
}
