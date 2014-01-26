<%@page import="com.innowhere.relproxy.jproxy.JProxyShell" %>
<%@page import="com.innowhere.relproxy.impl.jproxy.core.JProxyImpl" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Test Java Shell</title>
    </head>
    <body>
        <h1>Test Java Shell into the web app (for lazy people). Test with a complete class.</h1>        

        <%
            // This code is internal stuff just to make this test workable
            if (JProxyImpl.SINGLETON != null)
            {
                JProxyImpl.SINGLETON.getJProxyEngine().stop = true;
            }

            String inputPath = application.getRealPath("/") + "/WEB-INF/javashellex/code/";                       
            String cacheClassFolder = application.getRealPath("/") + "/WEB-INF/classes/";
            String compilationOptions = "-source 1.6  -target 1.6";
                    
            String[] args = new String[] 
            { 
                inputPath + "example_normal_class.java",
                "HELLO WORLD!",
                "-DscanPeriod=-1",
                "-DcacheClassFolder=" + cacheClassFolder,
                "-DcompilationOptions=" + compilationOptions                
            };
            try
            {
                JProxyShell.main(args);
            }
            finally
            {
                JProxyImpl.SINGLETON = null; // This is not public API, just needed to mix two examples never going to be in the same context
            }
        %>

        <p>See your console!!<p>                          

        <br />
        <p>This test interrupts the automatic detection of changed classes of "JProxy example"</p>
    </body>
</html>
