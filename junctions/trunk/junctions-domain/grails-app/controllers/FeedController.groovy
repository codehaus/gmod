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

}
