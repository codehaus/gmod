import grails.converters.*
class TagController {
    def scaffold = Tag

    def show = {
        if (params.id && Tag.exists(params.id)) {
            def tag = Tag.get(params.id)
            render tag as XML
        } else {
            def all = Tag.list() as Item[]
            render all as XML
        }
    }

    def create = {
      def tag = new Tag()
      tag.properties = params
      render tag as XML
    }
}