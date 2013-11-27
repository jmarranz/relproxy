<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Test Java Shell</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <%
            String pathInput = application.getRealPath("/") + "/WEB-INF/javaex/code/"; 
            // String classFolder = application.getRealPath("/") + "/WEB-INF/classes";                        
            
            String[] args = new String[] { pathInput + "/test_java_shell" };
            com.innowhere.relproxy.jproxy.JProxyShell.main(args);
        %>
    </body>
</html>
