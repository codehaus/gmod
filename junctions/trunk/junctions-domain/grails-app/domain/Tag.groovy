class Tag {
	static belongsTo = [Feed]
	static hasMany = [feeds: Feed]
	String description

}
