<html>
    <head>
        <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
    </head>
    <body>
    <%  if (user) { 
    %>
        <p>Hello, <%= user.nickname %>! (You can
        <a href="<%= userService.createLogoutURL(request.requestURI) %>">sign out</a>.)</p>
    <%  }
        else { 
    %>
        <p>Hello!
        <a href="<%= userService.createLoginURL(request.requestURI) %>">Sign in</a> to include your name with greetings you post.</p>
    <%  } 
    %>
    <% include("greetings.gtpl") %>
    <form action="/sign.groovy" method="post">
      <div><textarea name="content" rows="3" cols="60"></textarea></div>
      <div><input type="submit" value="Post Greeting" /></div>
    </form>
  </body>
</html>
