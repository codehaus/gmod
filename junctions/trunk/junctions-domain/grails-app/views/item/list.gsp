  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Item List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New Item</g:link></span>
        </div>
        <div class="body">
            <h1>Item List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="author" title="Author" />
                        
                   	        <g:sortableColumn property="content" title="Content" />
                        
                   	        <th>Feed</th>
                   	    
                   	        <g:sortableColumn property="publishedDate" title="Published Date" />
                        
                   	        <g:sortableColumn property="read" title="Read" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${itemList}" status="i" var="item">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${item.id}">${item.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${item.author?.encodeAsHTML()}</td>
                        
                            <td>${item.content?.encodeAsHTML()}</td>
                        
                            <td>${item.feed?.encodeAsHTML()}</td>
                        
                            <td>${item.publishedDate?.encodeAsHTML()}</td>
                        
                            <td>${item.read?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${Item.count()}" />
            </div>
        </div>
    </body>
</html>
