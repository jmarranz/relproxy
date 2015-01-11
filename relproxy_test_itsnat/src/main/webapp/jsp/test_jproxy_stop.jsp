<%@page import="com.innowhere.relproxy.jproxy.JProxy"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Test JProxy class reloader STOP</title>
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
