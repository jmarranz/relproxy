/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innowhere.relproxy.jproxy;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmarranz
 */
public class JProxyJavaShellInteractiveTest
{
    public JProxyJavaShellInteractiveTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
       
    }
    
    @After
    public void tearDown()
    {
       
    }

     @Test
     public void test_java_shell_interactive() 
     {     
        String compilationOptions = "-source 1.6  -target 1.6";

        String[] args = new String[] 
        { 
            "", // El args[0] esperado
            "-DcompilationOptions=" + compilationOptions,
            "-Dtest=true"                        
        };

        JProxyShell.main(args);
     }
}
