package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.jproxy.JProxyConfig;

/**
 *
 * @author jmarranz
 */
public class JProxyDefaultImpl extends JProxyImpl
{         
    public static JProxyConfig createJProxyConfig()
    {
        return new JProxyConfig();
    }         
    
    public static void initStatic(JProxyConfigImpl config)
    {
        if (!config.isEnabled()) return;
        
        checkSingletonNull(SINGLETON);
        SINGLETON = new JProxyDefaultImpl();
        SINGLETON.init(config,null,null);
    }    
    
    public static <T> T createStatic(T obj,Class<T> clasz)
    {
        if (SINGLETON == null) 
            return obj; // No se ha llamado al init o enabled = false
        
        return SINGLETON.create(obj, clasz);
    }    

}
