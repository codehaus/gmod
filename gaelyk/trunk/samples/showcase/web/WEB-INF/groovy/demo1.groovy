import com.google.appengine.api.datastore.Entity

mailService.send to: 'glaforge@gmail.com',
        subject: 'Hello World',
        htmlBody: '<bold>Hello</bold>'

include 'someTemplate.gpl'

Entity entity = new Entity("person")

datastoreService.withTransaction {

    entity['name'] = "Guillaume Laforge"
    println entity['name']

    entity.age = 31
    println entity.age

    println entity.kind
    println entity.key
    println entity.parent
    println entity.properties

}

entity.save()
entity.delete()
