package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngineShell;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderUtil;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxyShellImpl extends JProxyImpl
{
    public static JProxyShellImpl SINGLETON;       
    protected File scriptFile;
    
    public static void main(String[] args)
    {       
        SINGLETON = new JProxyShellImpl();
        SINGLETON.init(args);
    }    
    
    public void init(String[] args)
    {    
        this.scriptFile = new File(args[0]);
        File parentDir = JReloaderUtil.getParentDir(scriptFile.getAbsolutePath());        
        
        // TODO: PARAMETRIZAR POR LINEA DE COMANDOS
        
        boolean enabled = true;        
        String pathInput = parentDir.getAbsolutePath();           
        String classFolder = null; 
        long scanPeriod = -1;
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        DiagnosticCollector<JavaFileObject> diagnostics = null;
        
        // Esto quizás necesite una opción en plan "verbose" o "log" para mostrar por pantalla o nada
        ProxyListener proxyListener = new ProxyListener() {
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }        
        };        
        
        super.init(enabled,proxyListener,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);
    }
    
    @Override
    public JReloaderEngine createJReloaderEngine(ClassLoader parentClassLoader, String pathSources, String classFolder, long scanPeriod, Iterable<String> compilationOptions, DiagnosticCollector<JavaFileObject> diagnostics)
    {
        return new JReloaderEngineShell(scriptFile,parentClassLoader,pathSources,classFolder,scanPeriod,compilationOptions,diagnostics);  
    }    
}
