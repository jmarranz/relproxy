package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.RelProxyOnReloadListener;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Inspiraciones: http://groovy.codehaus.org/Running
 *
 * @author jmarranz
 */
public abstract class JProxyShellImpl extends JProxyImpl
{
    public static void main(String[] args)
    {
        File scriptFile = new File(args[0]);
        if (scriptFile.exists())
        {
            SINGLETON = new JProxyShellScriptFileImpl(); 
            ((JProxyShellScriptFileImpl)SINGLETON).init(args,scriptFile);
        }
        else if (args[0].isEmpty()) 
        {
            // Esto tiene explicación: cuando invocamos jproxysh sin parámetros (o espacios da igual) invocamos dentro jproxysh con com.innowhere.relproxy.jproxy.JProxyShell "$@"
            // el parámetro "$@" se convierte en "" que es un parámetro de verdad que recibimos pero de cadena vacía, lo cual nos viene GENIAL para distinguir el caso shell interactive            
            SINGLETON = new JProxyShellInteractiveImpl();         
            ((JProxyShellInteractiveImpl)SINGLETON).init(args);
        }
        else
        {
            SINGLETON = new JProxyShellCodeSnippetImpl();
            ((JProxyShellCodeSnippetImpl)SINGLETON).init(args);            
        }       
    }

    public ClassDescriptorSourceFileScript init(String[] args,boolean isScriptFile,String inputPath)
    {
        // Esto quizás necesite una opción en plan "verbose" o "log" para mostrar por pantalla o nada
        RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }
        };

        JProxyConfigImpl config = new JProxyConfigImpl();
        config.setEnabled(true);
        config.setRelProxyOnReloadListener(proxyListener);
        config.setInputPath(inputPath);
        config.setJProxyDiagnosticsListener(null); // Nos vale el log por defecto y no hay manera de espeficar otra cosa via comando

        LinkedList<String> argsToScript = new LinkedList<String>();
        processConfigParams(args,argsToScript,config);

        SourceScript sourceFileScript = getSourceScript(args,argsToScript);

        JProxyShellClassLoader classLoader = getJProxyShellClassLoader(config);

        ClassDescriptorSourceFileScript scriptFileDesc = super.init(config,sourceFileScript,classLoader);

        executeFirstTime(scriptFileDesc,argsToScript,classLoader);
        
        return scriptFileDesc;
    }        

    
    protected abstract SourceScript getSourceScript(String[] args,LinkedList<String> argsToScript);
    protected abstract JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config);    
    protected abstract void executeFirstTime(ClassDescriptorSourceFileScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader);    
    
    private static Iterable<String> parseCompilationOptions(String value)
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

    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {
        String classFolder = null;
        long scanPeriod = -1;
        Iterable<String> compilationOptions = null;

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

        config.setClassFolder(classFolder);
        config.setScanPeriod(scanPeriod);
        config.setCompilationOptions(compilationOptions);
    }

}
