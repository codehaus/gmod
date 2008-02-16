class Item {
	static hasMany = [tags: Tag]
	static constraints = {
		//url(unique:true)
	}
	Feed feed
	boolean read
	String title
	String url
	String content
	String author
	Date publishedDate
}
