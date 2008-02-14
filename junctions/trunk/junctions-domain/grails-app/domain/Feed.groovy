import java.net.URL
import java.io.InputStreamReader
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndContent
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader

class Feed {
	static hasMany = [tags : Tag]
	static constraints = {
		url(blank:false)
	}

	String title = ''
	String url
	String author = ''
	

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
            item.content = (entry.getContents()[0]).getValue()
            item.author = entry.getAuthor()
            println item.validate()
            item.save()
        }
    }
}
