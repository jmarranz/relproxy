package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileScript extends ClassDescriptorSourceFile
{
    protected String source;
    
    public ClassDescriptorSourceFileScript(JReloaderEngine engine,String name,File sourceFile,long timestamp)
    {
        super(engine,name, sourceFile, timestamp);
        
        // HACER: filtrar/ignorar el #!
        this.source = JReloaderUtil.readTextFile(sourceFile,engine.getSourceEncoding());        
    }
    
    @Override
    public void updateTimestamp(long timestamp)
    {
        long oldTimestamp = this.timestamp;
        if (oldTimestamp != timestamp)
            JReloaderUtil.readTextFile(sourceFile,engine.getSourceEncoding());   
        super.updateTimestamp(timestamp);
    }
    
    public String getSourceCode()
    {
        return source;
    }
    
    public static String getClassNameFromSourceFileScriptAbsPath(String path,String rootPathOfSources)
    {
        // path y rootPathOfSources son absolutos, preferentemente obtenidos con File.getAbsolutePath()
        int pos = path.indexOf(rootPathOfSources); 
        if (pos != 0) // DEBE SER 0, NO debería ocurrir
            return null;
        path = path.substring(rootPathOfSources.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
        // En teoría NO tiene extensión pero es posible que se le haya dado (ej .jsh), la quitamos si existe
        pos = path.lastIndexOf('.');        
        if (pos != -1) 
            path = path.substring(0, pos);        
        
        path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
        return path;
    }              
}
