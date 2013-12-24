
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.JProxyConfigImpl;
import com.innowhere.relproxy.impl.jproxy.JProxyDefaultImpl;

/**
 *
 * @author jmarranz
 */
public class JProxy 
{
    public static JProxyConfig createJProxyConfig()
    {
        return JProxyDefaultImpl.createJProxyConfig();
    }    
    
    public static void init(JProxyConfig config)
    {
        JProxyDefaultImpl.initStatic((JProxyConfigImpl)config);
    }
     
    public static <T> T create(T obj,Class<T> clasz)
    {
        return JProxyDefaultImpl.createStatic(obj, clasz);
    }
    
    public static boolean stop()
    {
        return JProxyDefaultImpl.stopStatic();
    }
    
    public static boolean start()
    {
        return JProxyDefaultImpl.startStatic();
    }    
}
