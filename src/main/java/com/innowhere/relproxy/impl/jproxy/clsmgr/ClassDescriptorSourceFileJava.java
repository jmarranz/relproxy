package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileJava extends ClassDescriptorSourceFile
{
    public ClassDescriptorSourceFileJava(JReloaderEngine engine,String className, File sourceFile, long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
    }
    
}
