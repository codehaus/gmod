class Folder {
    static hasMany = [feeds : Feed]
    String name

    String toString() { name }
}
