import java.net.URL
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader

class Feed {
	static hasMany = [tags : Tag, items: Item]
	static constraints = {
		url(blank:false,unique:true)
		author(nullable:true)
		folder(blank:false)
	}

	String title = ''
	String url
	String author
	Folder folder

	String toString() { title }

    def refreshFeed( SyndFeed feedSource ) {
        if( !feedSource ){
           SyndFeedInput input = new SyndFeedInput()
           feedSource = input.build(new XmlReader(url.toURL()))
        }
        
        feedSource.entries.each { entry ->
            def item = Item.findByTitle( entry.title )
            if( !item ){
                item = new Item( feed: this,
                                 url: entry.link,
                                 title: entry.title,
                                 author: entry.author,
                                 publishedDate: entry.publishedDate,
                                 content: entry.contents[0].value ?: entry.description?.value
                               )
                item.save()                
            }
        }
    }

    static addFeed( url ){
        SyndFeedInput input = new SyndFeedInput()
        SyndFeed feedSource = input.build(new XmlReader(url.toURL()))

        def feed = new Feed( folder: Folder.findByName("unclassified"),
                             url: url,
                             title: feedSource.title,
                             author: feedSource.author
                           )
        // save it
        feed.save()
        // save entries
        feed.refreshFeed( feedSource )       

        return feed                   
    }
}
