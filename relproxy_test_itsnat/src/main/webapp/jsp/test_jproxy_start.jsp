<%@page import="com.innowhere.relproxy.jproxy.JProxy"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Test JProxy class reloader RE-START</title>
    </head>
    <body>
        <h1>Test JProxy class reloader RE-START</h1>        

        <%
            boolean res = JProxy.start();
            if (res)
            {
        %>
            <p>Source change detection was re-started!<p> 
                
        <%  } 
            else 
            {
        %>
            <p>Source change detection start action is failed, maybe is already running!<p>            

        <%             
            }     
        %>


    </body>
</html>
