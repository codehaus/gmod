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
           // use the first 100 chars available from content as description
           deliciousService.addPost( item.url, item.content[0..(item.content.size()>100?100:-1)] )
        }
    }
}
