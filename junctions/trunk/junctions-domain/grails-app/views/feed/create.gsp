  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create Feed</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Feed List</g:link></span>
        </div>
        <div class="body">
            <h1>Create Feed</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${feed}">
            <div class="errors">
                <g:renderErrors bean="${feed}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
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
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create"></input></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
