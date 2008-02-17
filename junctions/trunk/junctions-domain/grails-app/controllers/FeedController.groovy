import grails.converters.*

class FeedController {
    def scaffold = Feed

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
                [feedList: Feed.list(params)]
    }

    def show = {
        if (params.id && Feed.exists(params.id)) {
            def feed = Feed.get(params.id)
            render feed as XML
        } else {
            def all = Feed.list() as Feed[]
            render all as XML
        }
    }
    
    def findFeedByTitle = {
    	def feed = Feed.findByTitle(params.title)
    	render feed.id as XML
    }

    def delete = {
        println "delete"
        println params.id
        def feed = Feed.get(params.id)
        if (feed) {
            def items = Item.findByFeed(feed)
            for (item in items)
                item.delete()
            feed.delete()
            flash.message = "Feed ${params.id} deleted."
        }
        else {
            render "Feed not found with id ${params.id}" as XML
        }
    }

    def update = {
        def feed = Feed.get(params.id)
        if (feed) {
            feed.properties = params
            if (feed.save()) {
                flash.message = "Feed ${params.id} updated."
                redirect(action: show, id: feed.id)
            }
            else {
                render(view: 'edit', model: [feed: feed])
            }
        }
        else {
            render "Feed not found with id ${params.id}"
        }
    }

    def create = {
        def feed = new Feed()
        feed.properties = params
        feed.folder = Folder.findByName('unclassified')
        feed.save()
        feed.parseItems(feed)
        println feed.errors
        render feed as XML
    }

    def save = {
        def feed = new Feed(params['feed'])

        if (feed.save()) {
            render feed as XML
        }
        else {
            def errors = p.errors.allErrors.collect {g.message(error: it)}
            render(contentType: "text/xml") {
                error {
                    for (err in errors) {
                        message(error: err)
                    }
                }
            }
        }
    }

    def refresh = {
        def feed = params.id ? Feed.get(params.id) :
                      params.title ? Feed.findByTitle(params.title) : null

        if( !feed ){
           render(contentType: "text/xml") {
              response {
                  code( 'ERROR' )
                  cause( "No such feed: ${params.id?:params.title}" )
              }
           }
        }else{
            try{
               feed.refreshFeed()
               def items = Item.findAllByFeed(feed) as Item[]
               render items as XML
            }catch( Exception e ){
                render(contentType: "text/xml") {
                   response{
                       code( 'ERROR' )
                       cause( "Error while refreshing feed\n${feed.title}" )
                   }
                }
            }
        }
    }

    def add = {
        def feed = Feed.findByUrl(params.url)
        if( feed ){
           render(contentType: "text/xml") {
              response{
                  code( 'ERROR' )
                  cause( "Feed already exists" )
              }    
           }
        }else{
            feed = Feed.addFeed(params.url)
            render feed as XML
        }
    }
}
