class BookmarksController {
    DeliciousBookmarksService  deliciousBookmarksService

    def index = {
       def item = Item.get( params.itemId )
       if( item ){
          try{
             this."${params.serviceId}BookmarksService".bookmark( item )
             render(contentType: "text/xml") {
                 response {
                     code( 'OK' ) 
                 }
             }
          }catch( Exception e ){
             render(contentType: "text/xml") {
                 response {
                     code( 'ERROR' )
                     cause( e.message )
                  }
             } 
          }
       }else{
           render(contentType: "text/xml") {
                 response {
                     code( 'ERROR' )
                     cause( "No such post ${itemId}" )
                 }
           }
       }
    }
}
