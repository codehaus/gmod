class Bookmark {
    static constraints = {
        name(nullable: false, unique: true)
    }

    String name

    String toString() {name}
}
