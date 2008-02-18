import grails.converters.*

class ItemController {
    def scaffold = Item

    DeliciousBookmarksService deliciousBookmarksService

    def showFeed = {
        if (params.id && Feed.exists(params.id)) {
            def items = Item.findAllByFeed(Feed.get(params.id)) as Item[]
            render items as XML
        }
    }

    def refreshFeed = {
        if (params.id && Feed.exists(params.id)) {
            Feed.parseItems(Feed.get(params.id))
            def items = Item.findAllByFeed(Feed.get(params.id)) as Item[]
            render items as XML
        }
    }

    def show = {
        if (params.id && Item.exists(params.id)) {
            def item = Item.get(params.id)
            render item as XML
        } else {
            def all = Item.list() as Item[]
            render all as XML
        }
    }

    def create = {
        def item = new Item()
        item.properties = params
        render item as XML
    }

    def bookmark = {
        def item = Item.get(params.id)
        def bookmark = Bookmark.findByName(params.service)

        if (item && bookmark) {
            try {
                this."${bookmark.name}BookmarksService".bookmark(item)
                item.addToBookmarks(bookmark)
                item.save()
                render item as XML
            } catch (Exception e) {
                render(contentType: "text/xml") {
                    response {
                        code('ERROR')
                        cause(e.message)
                    }
                }
            }
        }
    }
}
