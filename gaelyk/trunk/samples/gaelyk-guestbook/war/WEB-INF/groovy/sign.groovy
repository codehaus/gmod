import javax.jdo.PersistenceManager
import groovy.gaelyk.samples.guestbook.Greeting
import groovy.gaelyk.samples.guestbook.PMF

def content = params.content
def date = new Date()
def greeting = new Greeting(user, content, date);

def pm = PMF.get().getPersistenceManager()
try {
    pm.makePersistent(greeting)
}
finally {
    pm.close()
}

redirect("guestbook.gtpl")
