class Folder {

    static constraints = {
		name(unique:true)
	}

    static hasMany = [feeds : Feed]
    String name

    String toString() { name }
}
