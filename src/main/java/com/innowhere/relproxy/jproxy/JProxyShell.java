package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.shell.JProxyShellImpl;

/**
 * Is the main class to execute shell scripting based on Java.
 * 
 * <p>You are not going to use directly this class, use instead <code>jproxysh</code> command line.</p>
 * 
 * @author Jose Maria Arranz Santamaria
 */
public class JProxyShell
{
    /**
     * The main method.
     * 
     * @param args arguments with the necessary data to initialize and executing the provided script.
     */
    public static void main(String[] args)
    {       
         JProxyShellImpl.main(args);
    }
}
