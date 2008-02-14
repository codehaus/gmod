  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create Item</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Item List</g:link></span>
        </div>
        <div class="body">
            <h1>Create Item</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${item}">
            <div class="errors">
                <g:renderErrors bean="${item}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='author'>Author:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'author','errors')}'>
                                    <input type="text" id='author' name='author' value="${fieldValue(bean:item,field:'author')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='content'>Content:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'content','errors')}'>
                                    <input type="text" id='content' name='content' value="${fieldValue(bean:item,field:'content')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='feed'>Feed:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'feed','errors')}'>
                                    <g:select optionKey="id" from="${Feed.list()}" name='feed.id' value="${item?.feed?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='publishedDate'>Published Date:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'publishedDate','errors')}'>
                                    <g:datePicker name='publishedDate' value="${item?.publishedDate}" ></g:datePicker>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='read'>Read:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'read','errors')}'>
                                    <g:checkBox name='read' value="${item?.read}" ></g:checkBox>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='title'>Title:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'title','errors')}'>
                                    <input type="text" id='title' name='title' value="${fieldValue(bean:item,field:'title')}"/>
                                </td>
                            </tr> 
                        
                            <tr class='prop'>
                                <td valign='top' class='name'>
                                    <label for='url'>Url:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'url','errors')}'>
                                    <input type="text" id='url' name='url' value="${fieldValue(bean:item,field:'url')}"/>
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
