  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Feed</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Feed List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Feed</g:link></span>
        </div>
        <div class="body">
            <h1>Edit Feed</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${feed}">
            <div class="errors">
                <g:renderErrors bean="${feed}" as="list" />
            </div>
            </g:hasErrors>
            <g:form controller="feed" method="post" >
                <input type="hidden" name="id" value="${feed?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='name'>Name:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:feed,field:'name','errors')}'>
                                    <input type="text" id='name' name='name' value="${fieldValue(bean:feed,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='url'>Url:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:feed,field:'url','errors')}'>
                                    <input type="text" id='url' name='url' value="${fieldValue(bean:feed,field:'url')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='author'>Author:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:feed,field:'author','errors')}'>
                                    <input type="text" id='author' name='author' value="${fieldValue(bean:feed,field:'author')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='tags'>Tags:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:feed,field:'tags','errors')}'>
                                    
<ul>
<g:each var='t' in='${feed?.tags?}'>
    <li><g:link controller='tag' action='show' id='${t.id}'>${t}</g:link></li>
</g:each>
</ul>
<g:link controller='tag' params='["feed.id":feed?.id]' action='create'>Add Tag</g:link>

                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
