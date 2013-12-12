package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptFile;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import com.innowhere.relproxy.jproxy.JProxyDiagnosticsListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Inspiraciones: http://groovy.codehaus.org/Running
 * 
 * @author jmarranz
 */
public class JProxyShellImpl extends JProxyImpl
{     
    public static void main(String[] args)
    {       
        SINGLETON = new JProxyShellImpl();
        ((JProxyShellImpl)SINGLETON).init(args);
    }    
    
    public void init(String[] args)
    {    
//System.out.println("ARG 0: " + args[0]);
        File scriptFile = new File(args[0]);
        boolean isScriptFile = scriptFile.exists();
        SourceScript sourceFileScript = null;
        String inputPath = null;
        if (isScriptFile)
        {
            sourceFileScript = new SourceScriptFile(scriptFile);
            File parentDir = JProxyUtil.getParentDir(((SourceScriptFile)sourceFileScript).getFile());        
            inputPath = parentDir.getAbsolutePath();        
        }
        else
        {
            String code = args[0];
            sourceFileScript = new SourceScriptInMemory("_jproxyshellinmemoryclass_",code);
        }
        
        String classFolder = null; 
        long scanPeriod = -1;
        Iterable<String> compilationOptions = null; // Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        
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
                
                if ("cacheClassFolder".equals(name))
                {
                    classFolder = value;
                }                
                else if ("scanPeriod".equals(name))
                {
                    scanPeriod = Long.parseLong(value);                 
                }
                else if ("compilationOptions".equals(name))
                {
                    compilationOptions = parseCompilationOptions(value);                    
                }
                else throw new RelProxyException("Unknown parameter: " + arg);
            }
            else
            {
                argsToScript.add(arg);
            }
        }
        
        JProxyShellClassLoader classLoader = null;
        if (classFolder != null)
            classLoader = new JProxyShellClassLoader(JProxyImpl.getDefaultClassLoader(),new File(classFolder));        
        
        // Esto quizás necesite una opción en plan "verbose" o "log" para mostrar por pantalla o nada
        RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }        
        };        
        
        JProxyDiagnosticsListener diagnostics = null; // Nos vale el log por defecto        
        
        JProxyConfigImpl config = new JProxyConfigImpl();
        config.setEnabled(true);
        config.setRelProxyOnReloadListener(proxyListener);
        config.setInputPath(inputPath);
        config.setClassFolder(classFolder);
        config.setScanPeriod(scanPeriod);
        config.setCompilationOptions(compilationOptions);
        config.setJProxyDiagnosticsListener(diagnostics);      
        
        ClassDescriptorSourceFileScript scriptFileDesc = super.init(config,sourceFileScript,classLoader);
        
        Class scriptClass = scriptFileDesc.getLastLoadedClass();
        if (scriptClass == null)
        {
            // Esto es esperable cuando especificamos un classFolder en donde está ya compilado el script lanzador y es más actual que el fuente
            // no ha habido necesidad de crear un class loader "reloader" ni de recargar todos los archivos fuente con él
            if (classLoader == null) throw new RelProxyException("INTERNAL ERROR");
            if (scriptFileDesc.getClassBytes() == null) throw new RelProxyException("INTERNAL ERROR");
            scriptClass = classLoader.defineClass(scriptFileDesc);
        }
        
        try
        {
            Object obj = scriptClass.newInstance();
            Method method = scriptClass.getDeclaredMethod("init",new Class[]{ String[].class });
            String[] argsToScriptArr = argsToScript.size() > 0 ? argsToScript.toArray(new String[argsToScript.size()]) : new String[0];
            method.invoke(obj, new Object[]{ argsToScriptArr }); 
        }
        catch (InstantiationException ex) { throw new RelProxyException(ex); }
        catch (IllegalAccessException ex) { throw new RelProxyException(ex); }
        catch (NoSuchMethodException ex) { throw new RelProxyException(ex); }
        catch (SecurityException ex) { throw new RelProxyException(ex); }
        catch (IllegalArgumentException ex) { throw new RelProxyException(ex); }
        catch (InvocationTargetException ex) { throw new RelProxyException(ex); }
    }
    
    private Iterable<String> parseCompilationOptions(String value)
    {
        // Ej -source 1.6 -target 1.6  se convertiría en Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        String[] options = value.split(" ");
        LinkedList<String> opCol = new LinkedList<String>();        
        for (String option : options)
        {
            String op = option.trim(); // Por si hubiera dos espacios
            if (op.isEmpty()) continue;
            opCol.add(op);
        }
        return opCol;
    }
}
