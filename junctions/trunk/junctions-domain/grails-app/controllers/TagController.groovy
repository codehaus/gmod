import grails.converters.*
class TagController {
    def scaffold = Tag

    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max)params.max = 10
        [ tagList: Tag.list( params ) ]
    }

    def show = {
        if (params.id && Tag.exists(params.id)) {
            def tag = Tag.get(params.id)
            render tag as XML
        } else {
            def all = Tag.list() as Item[]
            render all as XML
        }
    }

    def delete = {      
        def tag = Tag.get( params.id )
        if(tag) {
            tag.delete()
        }
        else {
            render "Tag not found with id ${params.id}"
        }
    }

    def update = {
        def tag = Tag.get( params.id )
        if(tag) {
             tag.properties = params
            if(tag.save()) {
                flash.message = "Tag ${params.id} updated."
                redirect(action:show,id:tag.id)
            }
            else {
                render(view:'edit',model:[tag:tag])
            }
        }
        else {
            flash.message = "Tag not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
      def tag = new Tag()
      tag.properties = params
      render tag as XML
    }

    def save = {
        def tag = new Tag()
        tag.properties = params
        if (tag.save()) {
            render tag as XML
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