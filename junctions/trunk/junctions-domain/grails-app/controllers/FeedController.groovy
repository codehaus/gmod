import grails.converters.*

class FeedController {
    def scaffold = Feed

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

    def create = {
        def feed = new Feed()
        feed.properties = params
        feed.folder = Folder.findByName('unclassified')
        feed.save()
        feed.parseItems(feed)
        println feed.errors
        render feed as XML
    }

    def refresh = {
        def feed = params.id ? Feed.get(params.id) :
            params.title ? Feed.findByTitle(params.title) : null

        if (!feed) {
            render(contentType: "text/xml") {
                response {
                    code('ERROR')
                    cause("No such feed: ${params.id ?: params.title}")
                }
            }
        } else {
            try {
                feed.refreshFeed()
                def items = Item.findAllByFeed(feed) as Item[]
                render items as XML
            } catch (Exception e) {
                render(contentType: "text/xml") {
                    response {
                        code('ERROR')
                        cause("Error while refreshing feed\n${feed.title}")
                    }
                }
            }
        }
    }

    def add = {
        def feed = Feed.findByUrl(params.url)
        if (feed) {
            render(contentType: "text/xml") {
                response {
                    code('ERROR')
                    cause("Feed already exists")
                }
            }
        } else {
            try {
                feed = Feed.addFeed(params.url)
                render feed as XML
            } catch (Exception e) {
                render(contentType: "text/xml") {
                    response {
                        code('ERROR')
                        cause("Couldn't add subscription to\n${params.url}\n\nreason: $e")
                    }
                }
            }
        }
    }
}
