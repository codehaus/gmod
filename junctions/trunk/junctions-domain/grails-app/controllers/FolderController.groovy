import grails.converters.*

class FolderController {
    def scaffold = Folder

    def show = {
        if (params.id && Folder.exists(params.id)) {
            def folder = Folder.get(params.id)
            render folder as XML
        } else {
            def all = Folder.list() as Folder[]
            render all as XML
        }
    }
}