package assignment1;

public class InvertedIndex {

	private HashTable<HashTable<Integer>> index;
	
	private int capacity;
	private double maxLoadFactor;
	private String hashFunction;
	private String collisionMethod;
	
	
	public InvertedIndex(int capacity, double maxLoadFactor, String hashFunction, String collisionMethod) {
		
		this.capacity = capacity;
		this.maxLoadFactor = maxLoadFactor;
		this.hashFunction = hashFunction;
		this.collisionMethod = collisionMethod;

		index = new HashTable<>(capacity, maxLoadFactor, hashFunction, collisionMethod);
		
	}
	
	
	public void put(String word, String documentName) {

	    if (word == null || documentName == null) 
	    {
	        throw new IllegalArgumentException("Word or document name can't be null!");
	    }

	    HashTable<Integer> documents = index.get(word);

	    if (documents == null)
	    {

	        documents = new HashTable<>(capacity, maxLoadFactor, hashFunction, collisionMethod);

	        documents.put(documentName, 1);

	        index.put(word, documents);

	    } 
	    else 
	    {

	        Integer frequency = documents.get(documentName);

	        if (frequency == null) 
	        {
	            documents.put(documentName, 1);
	        } 
	        else 
	        {
	            documents.put(documentName, frequency + 1);
	        }
	    }
	}
	
	
	public HashTable<Integer> get(String word) {

	    if (word == null)
	    {
	        throw new IllegalArgumentException("Word can't be null!");
	    }

	    HashTable<Integer> documents = index.get(word);

	    return documents;
	}

	
	public void remove(String word) {

	    if (word == null)
	    {
	        throw new IllegalArgumentException("Word can't be null!");
	    }

	    index.remove(word);
	}
	
	
	public int getCapacity() {
	    return index.getCapacity();
	}
	
	
	public int getCollisionCount() {
	    return index.getTotalCollisionCount();
	}
	
	public int getOuterCollisionCount() {
	    return index.getCollisionCount();
	}


	
}