class FolderController {
    def scaffold = Folder

    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max)params.max = 10
        [ folderList: Folder.list( params ) ]
    }

    def show = {
        [ folder : Folder.get( params.id ) ]
    }

    def delete = {
        def folder = Folder.get( params.id )
        if(folder) {
            folder.delete()
            flash.message = "Folder ${params.id} deleted."
            redirect(action:list)
        }
        else {
            flash.message = "Folder not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def folder = Folder.get( params.id )

        if(!folder) {
                flash.message = "Folder not found with id ${params.id}"
                redirect(action:list)
        }
        else {
            return [ folder : folder ]
        }
    }

    def update = {
        def folder = Folder.get( params.id )
        if(folder) {
             folder.properties = params
            if(folder.save()) {
                flash.message = "Folder ${params.id} updated."
                redirect(action:show,id:folder.id)
            }
            else {
                render(view:'edit',model:[folder:folder])
            }
        }
        else {
            flash.message = "Folder not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
      def folder = new Folder()
      folder.properties = params
      return ['folder':folder]
    }

    def save = {
        def folder = new Folder()
        folder.properties = params
        if(folder.save()) {
            flash.message = "Folder ${folder.id} created."
            redirect(action:show,id:folder.id)
        }
        else {
            render(view:'create',model:[folder:folder])
        }
    }

}