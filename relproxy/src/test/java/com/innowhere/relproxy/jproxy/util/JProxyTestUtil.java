
package com.innowhere.relproxy.jproxy.util;

import java.io.File;
import java.net.URL;

/**
 *
 * @author jmarranz
 */
public class JProxyTestUtil
{
    public static final String RESOURCES_FOLDER = "src/test/resources";
    public static final String CACHE_CLASS_FOLDER = "tmp/java_shell_test_classes";     
    
    public static File getProjectFolder()
    {
         String className = JProxyTestUtil.class.getName(); // com.innowhere.relproxy.jproxy.util.JProxyTestUtil
         URL urlClass = JProxyTestUtil.class.getClassLoader().getResource(className.replace('.','/') + ".class");         
         File fileClass = new File(urlClass.getPath());
         File projectFolder = fileClass.getParentFile();
         for(int i = 0; i < 7; i++)
            projectFolder = projectFolder.getParentFile();
         return projectFolder;
    }    
}
