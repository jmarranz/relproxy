package com.innowhere.relproxy.impl.jproxy.clsmgr.comp;

import com.innowhere.relproxy.RelProxyException;
import java.io.IOException;
import javax.tools.StandardJavaFileManager;

/**
 *
 * @author jmarranz
 */
public class JProxyCompilerContext
{
    protected StandardJavaFileManager standardFileManager;    
    
    public JProxyCompilerContext(StandardJavaFileManager standardFileManager)
    {
        this.standardFileManager = standardFileManager;
    }

    public StandardJavaFileManager getStandardFileManager()
    {
        return standardFileManager;
    }    
    
    public void close()
    {
        try { this.standardFileManager.close(); }
        catch (IOException ex) { throw new RelProxyException(ex); }
    }
}
