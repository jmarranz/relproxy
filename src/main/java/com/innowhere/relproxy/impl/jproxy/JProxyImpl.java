package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import com.innowhere.relproxy.jproxy.JProxyListener;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxyImpl 
{
    protected JReloaderEngine engine;
    protected boolean developmentMode = false;
    protected JProxyListener reloadListener;
    
    public void init(boolean devMode,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics,JProxyListener relListener)
    {
        JReloaderEngine engine = null;
        if (devMode)
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();      
            engine = new JReloaderEngine(classLoader,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);          
        }
        
        this.engine = engine;        
        this.developmentMode = devMode;
        this.reloadListener = relListener;
    }    
    
    public JReloaderEngine getJReloaderEngine()
    {
        return engine;
    }
    
    public JProxyListener getJProxyListener()
    {
        return reloadListener;
    }    
    
    public <T> T create(T obj,Class<T> clasz)
    {
        if (!developmentMode || engine == null)
            return obj;
        
        if (obj == null) return null;
        
        InvocationHandler handler = new JProxyReloadableInvocationHandler<T>(obj,this);
        
        T proxy = (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(),new Class[] { clasz }, handler);   
        return proxy;
    }    
}
