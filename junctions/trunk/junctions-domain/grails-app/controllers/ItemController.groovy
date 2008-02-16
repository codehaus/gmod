import grails.converters.*

class ItemController {

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max)params.max = 10
        [ itemList: Item.list( params ) ]
    }

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
            def feed = Item.get(params.id)
            render feed as XML
        } else {
            def all = Item.list() as Item[]
            render all as XML
        }
    }

    def delete = {
        def item = Item.get( params.id )
        if(item) {
            item.delete()
            //flash.message = "Item ${params.id} deleted."
            //redirect(action:list)
        }
        else {
           render "Item not found with id ${params.id}"
        }
    }

    def update = {
        def item = Item.get( params.id )
        if(item) {
             item.properties = params
            if(item.save()) {
                flash.message = "Item ${params.id} updated."
                redirect(action:show,id:item.id)
            }
            else {
                render(view:'edit',model:[item:item])
            }
        }
        else {
            render "Item not found with id ${params.id}"
        }
    }

    def create = {
      def item = new Item()
      item.properties = params
      render item as XML
    }

    def save = {
        def item = new Item()
        item.properties = params
        if (item.save()) {
            render item as XML
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
