<%  def pm = groovy.gaelyk.samples.guestbook.PMF.get().getPersistenceManager()
    def query = "select from " + groovy.gaelyk.samples.guestbook.Greeting.class.getName() + " order by date desc range 0, 5"
    def greetings = pm.newQuery(query).execute()
    if (!greetings) { 
%>
<p>The guestbook has no messages.</p>
<%  }
    else {
        greetings.each {
            if (!it.author) { 
%>
<p>An anonymous person wrote:</p>
<%          }
            else { 
%>
<p><b><%= it.author.nickname %></b> wrote:</p>
<%          } 
%>
<blockquote><%= it.content %></blockquote>
<%      }
    }
    pm.close()
%>
