package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngineShell;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyUtil;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
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
        String pathInput = parentDir.getAbsolutePath();        
        
        String classFolder = null; 
        long scanPeriod = -1;
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        DiagnosticCollector<JavaFileObject> diagnostics = null;        
        
        LinkedList<String> argsToScript = new LinkedList<String>();
        for(int i = 1; i < args.length; i++) 
        {
            String arg = args[i];
            if (arg.startsWith("-D"))
            {
                String param = arg.substring(2);
                int pos = param.indexOf('=');
                if (pos == -1)
                    throw new RelProxyException("Bad parameter format: " + arg);
                String name = param.substring(0,pos);
                String value = param.substring(pos + 1);
                
                if ("classFolder".equals(name))
                {
                    classFolder = value;
                }                
                else if ("scanPeriod".equals(name))
                {
                    scanPeriod = Long.parseLong(value);                 
                }
                else throw new RelProxyException("Unknown parameter: " + arg);
            }
            else
            {
                argsToScript.add(arg);
            }
        }
        
        
        // Esto quizás necesite una opción en plan "verbose" o "log" para mostrar por pantalla o nada
        RelProxyListener proxyListener = new RelProxyListener() {
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
            method.invoke(obj, new Object[]{ argsToScript.toArray(new String[argsToScript.size()]) }); 
        }
        catch (InstantiationException ex) { throw new RelProxyException(ex); }
        catch (IllegalAccessException ex) { throw new RelProxyException(ex); }
        catch (NoSuchMethodException ex) { throw new RelProxyException(ex); }
        catch (SecurityException ex) { throw new RelProxyException(ex); }
        catch (IllegalArgumentException ex) { throw new RelProxyException(ex); }
        catch (InvocationTargetException ex) { throw new RelProxyException(ex); }
    }
    
    @Override
    public JProxyEngine createJProxyEngine(ClassLoader parentClassLoader, String pathSources, String classFolder, long scanPeriod, Iterable<String> compilationOptions, DiagnosticCollector<JavaFileObject> diagnostics)
    {
        return new JProxyEngineShell(scriptFile,parentClassLoader,pathSources,classFolder,scanPeriod,compilationOptions,diagnostics);  
    }    
}
