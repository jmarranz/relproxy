package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.ProxyException;
import com.innowhere.relproxy.ProxyListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngineShell;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyUtil;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
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
    protected File scriptFile;
    
    public static void main(String[] args)
    {       
        SINGLETON = new JProxyShellImpl();
        ((JProxyShellImpl)SINGLETON).init(args);
    }    
    
    public void init(String[] args)
    {    
        this.scriptFile = new File(args[0]);
        File parentDir = JProxyUtil.getParentDir(scriptFile.getAbsolutePath());        
        
        // TODO: PARAMETRIZAR POR LINEA DE COMANDOS
                
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
        
        ClassDescriptorSourceFileScript scriptFileDesc = super.init(proxyListener,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);
        Class scriptClass = scriptFileDesc.getLastLoadedClass();
        try
        {
            Object obj = scriptClass.newInstance();
            Method method = scriptClass.getDeclaredMethod("init",new Class[]{ String[].class });
            method.invoke(obj, new Object[]{args}); // REVISAR EL PASO DE ARGUMENTOS pues estamos pasando las "options"
        }
        catch (InstantiationException ex) { throw new ProxyException(ex); }
        catch (IllegalAccessException ex) { throw new ProxyException(ex); }
        catch (NoSuchMethodException ex) { throw new ProxyException(ex); }
        catch (SecurityException ex) { throw new ProxyException(ex); }
        catch (IllegalArgumentException ex) { throw new ProxyException(ex); }
        catch (InvocationTargetException ex) { throw new ProxyException(ex); }
    }
    
    @Override
    public JProxyEngine createJProxyEngine(ClassLoader parentClassLoader, String pathSources, String classFolder, long scanPeriod, Iterable<String> compilationOptions, DiagnosticCollector<JavaFileObject> diagnostics)
    {
        return new JProxyEngineShell(scriptFile,parentClassLoader,pathSources,classFolder,scanPeriod,compilationOptions,diagnostics);  
    }    
}
