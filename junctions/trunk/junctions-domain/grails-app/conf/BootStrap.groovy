import grails.util.DomainBuilder

class BootStrap {

     def init = { servletContext ->
        /*
        def unclassified = Folder.findByName('unclassified')
        if( !unclassified ){
            unclassified = new Folder(name:'unclassified').save()
        }
        */
        def db = new DomainBuilder()
        def unclassified = db.folder( name: 'unclassified' )
        def groovy = db.folder( name: 'groovy' ){
            feed( title: 'aboutGroovy.com',
                  url: 'http://aboutgroovy.com/item/atom',
                  author: 'Scott Davis' )
        }

        unclassified.save()
        groovy.save()
     }
     
     def destroy = {
     }
} 