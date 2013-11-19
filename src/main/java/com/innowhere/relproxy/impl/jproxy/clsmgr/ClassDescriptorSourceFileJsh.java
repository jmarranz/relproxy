package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileJsh extends ClassDescriptorSourceFile
{
    public static final String NAME = "_JProxyShellMainRoot_";
    protected String source;
    
    public ClassDescriptorSourceFileJsh(JReloaderEngine engine,String name,File sourceFile,long timestamp)
    {
        super(engine,name, sourceFile, timestamp);
        
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
}
