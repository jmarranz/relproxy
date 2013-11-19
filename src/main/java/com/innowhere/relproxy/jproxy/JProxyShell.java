package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class JProxyShell
{
    public static final String NAME = "_JProxyShellMainRoot_";    
    
    public static void main(String[] args)
    {       
        File file = new File(args[0]);
        File parentDir = JReloaderUtil.getParentDir(file.getAbsolutePath());
        
        //String mainSource = readTextFile(file,"UTF");
        //System.out.println(mainSource);        
    }
    

}
