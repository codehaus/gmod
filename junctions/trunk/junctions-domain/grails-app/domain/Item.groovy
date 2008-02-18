class Item {
    static hasMany = [tags: Tag, bookmarks: Bookmark]
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

    String toString() {title}
}
