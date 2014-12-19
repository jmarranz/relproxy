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
public class JProxyCodeSnippetTest
{
    public static boolean RESULT;
    
    public JProxyCodeSnippetTest()
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
        RESULT = false;        
    }
    
    @After
    public void tearDown()
    {
        RESULT = false;        
    }

     @Test
     public void test_code_snippet() 
     {
        String compilationOptions = "-source 1.6  -target 1.6";

        String[] args = new String[] 
            { 
                "-c", 
                "System.out.print(\"This code snippet says: \");",
                "System.out.println(\"Hello World!!\");",
                JProxyCodeSnippetTest.class.getName() + ".RESULT = true;", 
                "-DcompilationOptions=" + compilationOptions                        
            };                

        JProxyShell.main(args);
        
        assertTrue(RESULT);
     }
}
