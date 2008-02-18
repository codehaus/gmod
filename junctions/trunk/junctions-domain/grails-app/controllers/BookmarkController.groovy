import grails.converters.*

class BookmarkController {
    def scaffold = Bookmark

    def show = {
        if (params.id && Bookmark.exists(params.id)) {
            def bookmark = Bookmark.get(params.id)
            render bookmark as XML
        } else {
            def all = Bookmark.list() as Bookmark[]
            render all as XML
        }
    }
}