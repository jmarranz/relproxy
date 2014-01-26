<%@page import="com.innowhere.relproxy.jproxy.JProxy"%>
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
        <h1>Test JProxy class reloader STOP</h1>        

        <%
            boolean res = JProxy.stop();
            if (res)
            {
        %>
            <p>Source change detection is disabled!<p> 
                
        <%  } 
            else 
            {
        %>
            <p>Source change detection stop action is failed, maybe is already stopped!<p>            

        <%             
            }     
        %>


    </body>
</html>
