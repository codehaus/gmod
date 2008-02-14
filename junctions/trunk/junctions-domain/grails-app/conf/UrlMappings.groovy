class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?" {
        }
	"/feed/$id?"(controller:"feed") {
	   action = [GET:"show", PUT:"update", DELETE:"delete", POST:"save"]
	}
	
	"/item/$id?"(controller:"item") {
	   action = [GET:"show", PUT:"update", DELETE:"delete", POST:"save"]
	}
    }	
}
