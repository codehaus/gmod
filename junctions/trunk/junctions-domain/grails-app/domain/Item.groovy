class Item {
	static hasMany = [tags: Tag]
	Feed feed
	boolean read
	String title
	String url
	String content
	String author
	Date publishedDate
}
