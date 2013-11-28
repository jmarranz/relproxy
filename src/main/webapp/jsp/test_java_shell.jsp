<%@page import="com.innowhere.relproxy.jproxy.JProxyShell" %>
<%@page import="com.innowhere.relproxy.impl.jproxy.JProxyImpl" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Test Java Shell</title>
    </head>
    <body>
        <h1>Test Java Shell into the web app (for lazy people)</h1>

        <% if (JProxyImpl.SINGLETON != null) { %>
            <p>Execute this test <b>before</b> the JProxy example (reload the server)<p>
        <% } else { %>
            <%
                String pathInput = application.getRealPath("/") + "/WEB-INF/javashellex/code/"; 
                // String classFolder = application.getRealPath("/") + "/WEB-INF/classes";                        

                String[] args = new String[] { pathInput + "/test_java_shell" };
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
        <% } %>            

    </body>
</html>
