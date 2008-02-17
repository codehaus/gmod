import java.net.URL
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader

class Feed {
	static hasMany = [tags : Tag, items: Item]
	static constraints = {
		url(blank:false)
		author(nullable:true)
		folder(blank:false)
	}

	String title = ''
	String url
	String author
	Folder folder

	String toString() { title }

    def refreshFeed() {
        SyndFeedInput input = new SyndFeedInput()
        SyndFeed feedSource = input.build(new XmlReader(url.toURL()))
        feedSource.entries.each { entry ->
            def item = Item.findByTitle( entry.title )
            if( !item ){
                item = new Item( feed: this,
                                 url: entry.uri,
                                 title: entry.title,
                                 author: entry.author,
                                 publishedDate: entry.publishedDate,
                                 content: entry.contents[0].value ?: entry.description?.value
                               )
                item.save()                
            }
        }
    }

	static parseItems(feed) {
        SyndFeedInput input = new SyndFeedInput()
        SyndFeed feedSource = input.build(new XmlReader(new URL(feed.url)))
        def entries = feedSource.getEntries()
        feed.title = feedSource.getTitle()
        feed.author = feedSource.getAuthor()
        
        for (entry in entries) {
            def item = new Item()
            item.feed = feed
            item.title = entry.getTitle()
            item.url = entry.getUri()
            item.publishedDate = entry.getPublishedDate()
            def content = (entry.getContents()[0])?.getValue() ?: entry.getDescription()?.getValue()
            item.content = content
            item.author = entry.getAuthor()
            println item.validate()
            item.save()
        }
    }
}
