package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.jproxy.JProxyConfig;

/**
 *
 * @author jmarranz
 */
public class JProxyDefaultImpl extends JProxyImpl
{         
    public static JProxyConfig createJProxyConfig()
    {
        return new JProxyConfigImpl();
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

    public static boolean stopStatic()
    {
        if (SINGLETON == null) 
            return false;
        
        return SINGLETON.stop();
    }       
    
    public static boolean startStatic()
    {
        if (SINGLETON == null) 
            return false;
        
        return SINGLETON.start();
    }         
}
