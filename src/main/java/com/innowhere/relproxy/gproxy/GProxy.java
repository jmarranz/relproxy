
package com.innowhere.relproxy.gproxy;

import com.innowhere.relproxy.impl.gproxy.GProxyConfigImpl;
import com.innowhere.relproxy.impl.gproxy.GProxyDefaultImpl;

/**
 *
 * @author Jose Maria Arranz Santamaria
 */
public class GProxy 
{
    public static GProxyConfig createGProxyConfig()
    {
        return GProxyDefaultImpl.createGProxyConfig();
    }        
    
    public static void init(GProxyConfig config)
    {
        GProxyDefaultImpl.initStatic((GProxyConfigImpl)config);
    }   
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        return GProxyDefaultImpl.createStatic(obj, clasz);
    }   
}
