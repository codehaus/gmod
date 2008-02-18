import del.icio.us.Delicious

class DeliciousBookmarksService {
    boolean transactional = false

    Delicious deliciousService

    private def connect() {
        if( !deliciousService ){
           // TODO store login/passwd in db
           def username = "junctions"
           def passwd = "groovy123"
           deliciousService = new Delicious( username, passwd )
        }
    }

    def bookmark( Item item ) {
        if( item ){
           connect() 
           deliciousService.addPost( item.url,
                                     item.title,
                                     // TODO should be smarter than this
                                     // what about html tags ?
                                     item.content.size() > 500 ? item.content[0..500] : item.content,
                                     item.feed.folder.name, // TODO replace with tags
                                     new Date() )
        }
    }
}
