import grails.util.DomainBuilder

class BootStrap {
    def init = {servletContext ->
        def db = new DomainBuilder()
        def unclassified = db.folder(name: 'unclassified')
        def groovy = db.folder(name: 'groovy') {
            feed(title: 'aboutGroovy.com',
                 url: 'http://aboutgroovy.com/item/atom',
                 link: 'http://aboutgroovy.com',
                 author: 'Scott Davis')
        }

        unclassified.save()
        groovy.save()

        new Bookmark(name: "delicious").save()
    }

    def destroy = {
    }
} 