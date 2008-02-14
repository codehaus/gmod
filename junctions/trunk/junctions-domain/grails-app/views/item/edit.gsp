  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Item</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">Item List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New Item</g:link></span>
        </div>
        <div class="body">
            <h1>Edit Item</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${item}">
            <div class="errors">
                <g:renderErrors bean="${item}" as="list" />
            </div>
            </g:hasErrors>
            <g:form controller="item" method="post" >
                <input type="hidden" name="id" value="${item?.id}" />
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
                                    <label for='tags'>Tags:</label>
                                </td>
                                <td valign='top' class='value ${hasErrors(bean:item,field:'tags','errors')}'>
                                    
<ul>
<g:each var='t' in='${item?.tags?}'>
    <li><g:link controller='tag' action='show' id='${t.id}'>${t}</g:link></li>
</g:each>
</ul>
<g:link controller='tag' params='["item.id":item?.id]' action='create'>Add Tag</g:link>

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
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
