package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.jproxy.util.JProxyTestUtil;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jmarranz
 */
public class JProxyJavaShellTest
{
   
     
    
    public JProxyJavaShellTest()
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
     public void test_java_shell() 
     {          
        File projectFolder = JProxyTestUtil.getProjectFolder();         
        File inputFolderFile = new File(projectFolder,JProxyTestUtil.RESOURCES_FOLDER);         
        File cacheClassFolderFile = new File(projectFolder,JProxyTestUtil.CACHE_CLASS_FOLDER);         
        
        String inputPath = inputFolderFile.getAbsolutePath();                       
        String cacheClassFolder = cacheClassFolderFile.getAbsolutePath();
        String compilationOptions = "-source 1.6  -target 1.6";

        
        String[] args = new String[] 
        { 
            inputPath + "/example_java_shell",
            "HELLO ",
            "WORLD!",
            "-DcacheClassFolder=" + cacheClassFolder,
            "-DcompilationOptions=" + compilationOptions
        };

        JProxyShell.main(args);
     }
     

}
