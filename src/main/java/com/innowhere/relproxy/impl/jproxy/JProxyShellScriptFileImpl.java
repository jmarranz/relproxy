package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceFileScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptFile;
import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JProxyShellScriptFileImpl extends JProxyShellImpl
{
    protected File scriptFile;
    
    public void init(String[] args,File scriptFile)
    {
        this.scriptFile = scriptFile;

        File parentDir = JProxyUtil.getParentDir(scriptFile);
        String inputPath = parentDir.getAbsolutePath();        
        super.init(args, true, inputPath);
    }    
    
    protected void executeFirstTime(ClassDescriptorSourceFileScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        fixLastLoadedClass(scriptFileDesc,classLoader);
        
        scriptFileDesc.callMainMethod(argsToScript);
    }
    
    @Override    
    protected SourceScript getSourceScript(String[] args,LinkedList<String> argsToScript) 
    {
        return new SourceScriptFile(scriptFile);
    }    
    
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        String classFolder = config.getClassFolder();
        if (classFolder != null)
            return new JProxyShellClassLoader(getDefaultClassLoader(),new File(classFolder));    
        else
            return null;
    }
    
    protected void fixLastLoadedClass(ClassDescriptorSourceFileScript scriptFileDesc,JProxyShellClassLoader classLoader)
    {
        Class scriptClass = scriptFileDesc.getLastLoadedClass();
        if (scriptClass != null) return;
        
        // Esto es esperable cuando especificamos un classFolder en donde está ya compilado el script lanzador y es más actual que el fuente
        // no ha habido necesidad de crear un class loader "reloader" ni de recargar todos los archivos fuente con él
        if (classLoader == null) throw new RelProxyException("INTERNAL ERROR");
        if (scriptFileDesc.getClassBytes() == null) throw new RelProxyException("INTERNAL ERROR");
        scriptClass = classLoader.defineClass(scriptFileDesc);
        scriptFileDesc.setLastLoadedClass(scriptClass);    
    }
}
