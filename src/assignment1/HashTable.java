package assignment1;

public class HashTable<V> {
	
	private String[] keys;
	private V[] values;
	
	private int size;
	private int capacity;
	private double maxLoadFactor;
	
	private int collisionCount;
	
	private boolean[] deleted; 	
	
	private String hashFunction;
	private String collisionMethod;
	
	private int secondaryPrime;
	
	
	public HashTable(int capacity, double maxLoadFactor, String hashFunction, String collisionMethod)
	{
		if(capacity <= 0) 
		{
			throw new  IllegalArgumentException("Capacity must be positive!");
		}
		
		if(maxLoadFactor <= 0 || maxLoadFactor >= 1) 
		{
			throw new  IllegalArgumentException("Invalid load factor!");
		}	
		
		if (!"SSF".equals(hashFunction) && !"PAF".equals(hashFunction)) 
		{
		    throw new IllegalArgumentException("Invalid hash function!");
		}

		if (!"LP".equals(collisionMethod) && !"DH".equals(collisionMethod)) 
		{
		    throw new IllegalArgumentException("Invalid collision method!");
		}
		
		if (collisionMethod.equals("DH") && (capacity <= 2 || !isPrime(capacity))) 
		{
		    throw new IllegalArgumentException("Capacity must be a prime number greater than 2 for Double Hashing!");
		}
		
		if (collisionMethod.equals("DH")) 
		{
		    secondaryPrime = getPreviousPrime(capacity);
		}
		
		this.capacity = capacity;
		this.maxLoadFactor = maxLoadFactor;
		
		this.hashFunction = hashFunction;
		this.collisionMethod = collisionMethod;
		
		keys = new String[capacity];
		values = (V[]) new Object[capacity];
		deleted = new boolean[capacity];
		
		size = 0;
		collisionCount = 0 ;
		
	}
		
	
	private int hashSSF(String key) {

	    int hash = 0;

	    for (int i = 0; i < key.length(); i++)
	    {
	        hash += key.charAt(i);
	    }

	    return Math.floorMod(hash, capacity);
	}
	
	
	private int hashPAF(String key) {

	    int hash = 0;
	    int z = 33;

	    key = key.toLowerCase();

	    for (int i = 0; i < key.length(); i++) 
	    {

	        int characterValue = key.charAt(i) - 'a' + 1;

	        hash = (hash * z + characterValue) % capacity;
	    }

	    return hash;
	}
	
	
	private int hash(String key) {
		
	    if (hashFunction.equals("SSF")) 
	    {
	        return hashSSF(key);
	    }
	    
	    return hashPAF(key);
	}
	
	
	private boolean isPrime(int number) {
		
	    if (number < 2) 
	    {
	        return false;
	    }

	    for (int i = 2; i <= Math.sqrt(number); i++) {
	    	
	        if (number % i == 0) 
	        {
	            return false;
	        }
	    }

	    return true;
	}
	
	
	private int getPreviousPrime(int number) {
		
	    for (int i = number - 1; i >= 2; i--) 
	    {
	        if (isPrime(i)) 
	        {
	            return i;
	        }
	    }
	    
	    throw new IllegalArgumentException("No prime number found!");
	}
	
	
	private int getNextPrime(int number) {
		
	    while (!isPrime(number)) 
	    {
	        number++;
	    }
	    
	    return number;
	}
	
	
	private int hashModuloQ(String key) {
		
	    int hash = 0;

	    if (hashFunction.equals("SSF")) 
	    {
	        for (int i = 0; i < key.length(); i++) 
	        {
	            hash = (hash + key.charAt(i)) % secondaryPrime;
	        }
	    }
	    else 
	    {
	        int z = 33;
	        key = key.toLowerCase();

	        for (int i = 0; i < key.length(); i++) 
	        {
	            int characterValue = key.charAt(i) - 'a' + 1;
	            hash = (hash * z + characterValue) % secondaryPrime;
	        }
	    }

	    return hash;
	}
	
	
	private int getStep(String key) {
		
	    if (collisionMethod.equals("LP")) 
	    {
	        return 1;
	    }

	    return secondaryPrime - hashModuloQ(key);
	}
	
	
	private int findIndex(String key) {
		
	    int firstHash = hash(key);
	    int step = getStep(key);

	    for (int i = 0; i < capacity; i++)
	    {
	        int index = (firstHash + i * step) % capacity;

	        if (keys[index] == null && !deleted[index]) 
	        {
	            return -1;
	        }

	        if (keys[index] != null && keys[index].equals(key)) 
	        {
	            return index;
	        }
	    }

	    return -1;
	}

	
	public void put(String key, V value) {

	    if (key == null || value == null) 
	    {
	        throw new IllegalArgumentException("Key or value cannot be null!");
	    }

	    int existingIndex = findIndex(key);

	    if (existingIndex != -1)
	    {
	        values[existingIndex] = value;
	        return;
	    }

	    if ((double) (size + 1) / capacity >= maxLoadFactor) 
	    {
	        int newCapacity = capacity * 2;

	        if (collisionMethod.equals("DH")) 
	        {
	            newCapacity = getNextPrime(newCapacity);
	        }

	        resize(newCapacity);
	    }

	    insertWithoutResize(key, value);
	}
	
	
	private void insertWithoutResize(String key, V value) {

		int firstHash = hash(key);
	    int step = getStep(key);
	    
	    int firstDeleted = -1;

	    for (int i = 0; i < capacity; i++) 
	    {
	        int index = (firstHash + i * step) % capacity;

	        if (keys[index] == null) 
	        {

	            if (!deleted[index]) 
	            {

	                if (firstDeleted != -1) 
	                {
	                    index = firstDeleted;
	                }

	                keys[index] = key;
	                values[index] = value;
	                deleted[index] = false;
	                size++;
	                return;
	            }

	            if (firstDeleted == -1) 
	            {
	                firstDeleted = index;
	            }
	        }

	        else 
	        {
	        	if (keys[index].equals(key)) 
	        	{
	        		values[index] = value;
		            return;
	        	}
	        	
	        	 collisionCount++;

	        }

	    }

	    if (firstDeleted != -1) 
	    {

	        keys[firstDeleted] = key;
	        values[firstDeleted] = value;
	        deleted[firstDeleted] = false;
	        size++;
	        return;
	    }

	    throw new IllegalStateException("Hash table is full!");
	}
	
	
	public V get(String key) {
		
	    if (key == null)
	    {
	        throw new IllegalArgumentException("Key cannot be null!");
	    }

	    int index = findIndex(key);

	    if (index == -1)
	    {
	        return null;
	    }

	    return values[index];
	}
	
	
	public void remove(String key) {
		
	    if (key == null) 
	    {
	        throw new IllegalArgumentException("Key cannot be null!");
	    }

	    int index = findIndex(key);

	    if (index == -1) 
	    {
	        return;
	    }

	    keys[index] = null;
	    values[index] = null;
	    
	    deleted[index] = true;
	    size--;
	}
	
	
	public void resize(int newCapacity) {
		
	    if (newCapacity <= capacity)
	    {
	        throw new IllegalArgumentException("New capacity must be larger!");
	    }

	    if (collisionMethod.equals("DH")) 
	    {
	        if (!isPrime(newCapacity))
	        {
	            throw new IllegalArgumentException("New capacity must be prime for Double Hashing!");
	        }
	        
	        secondaryPrime = getPreviousPrime(newCapacity);
	    }

	    String[] oldKeys = keys;
	    V[] oldValues = values;

	    capacity = newCapacity;
	    keys = new String[capacity];
	    
	    values = (V[]) new Object[capacity];
	    deleted = new boolean[capacity];
	    size = 0;

	    for (int i = 0; i < oldKeys.length; i++)
	    {
	        if (oldKeys[i] != null) 
	        {
	            insertWithoutResize(oldKeys[i], oldValues[i]);
	        }
	    }
	}
	
	
	public int getCapacity() {
	    return capacity;
	}
	
	
	public int getCollisionCount() {
	    return collisionCount;
	}
	
	
	public int getTotalCollisionCount() {

	    int total = collisionCount;

	    for (int i = 0; i < capacity; i++) 
	    {

	        if (values[i] instanceof HashTable<?>)
	        {

	            HashTable<?> innerTable = (HashTable<?>) values[i];

	            total += innerTable.getCollisionCount();
	        }
	    }

	    return total;
	}

	
	public void printEntries() {
		
	    for (int i = 0; i < capacity; i++) 
	    {
	        if (keys[i] != null && !deleted[i])
	        {
	            System.out.println(keys[i] + " : " + values[i]);
	        }
	    }
	}
	
	
	
	
	
	
}
