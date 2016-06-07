package cgw_sample

class Book {
	String name
	String author
    static constraints = {
    }
	
	static mapping = {
        tablePerHierarchy(false)
		id generator: 'sequence', params: [sequence: 'PK_BOOK_SEQ']
		
        
    }
}
