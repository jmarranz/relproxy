package com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc;

import com.innowhere.relproxy.impl.FileExt;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileJava extends ClassDescriptorSourceUnit
{
    public ClassDescriptorSourceFileJava(JProxyEngine engine,String className, SourceFileJavaNormal sourceFile, long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
    }
    
    public SourceFileJavaNormal getSourceFileJavaNormal()
    {
        return (SourceFileJavaNormal)sourceUnit;
    }
    
    public FileExt getSourceFile()
    {
        return getSourceFileJavaNormal().getFileExt();
    }    
     
}
