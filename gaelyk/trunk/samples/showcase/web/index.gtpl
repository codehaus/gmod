<html>
<head>
    <title>Simple template page</title>
</head>
<body>
    <% include 'someTemplate.gtpl' %>
    
    <%
        mailService.send to: 'glaforge@gmail.com',
        subject: 'Hello World',
        htmlBody: '<bold>Hello</bold>'

    %>
</body>
</html>