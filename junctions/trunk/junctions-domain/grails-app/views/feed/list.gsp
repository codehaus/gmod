  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Feed List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Feed</g:link></span>
        </div>
        <div class="body">
            <h1>Feed List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <g:sortableColumn property="url" title="Url" />
                        
                   	        <g:sortableColumn property="author" title="Author" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${feedList}" status="i" var="feed">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${feed.id}">${feed.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${feed.name?.encodeAsHTML()}</td>
                        
                            <td>${feed.url?.encodeAsHTML()}</td>
                        
                            <td>${feed.author?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${Feed.count()}" />
            </div>
        </div>
    </body>
</html>
