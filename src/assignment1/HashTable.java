package assignment1;

public class HashTable<V> {
	
	private String[] keys;
	private V[] values;
	
	private int size;
	private int capacity;
	private double maxLoadFactor;
	
	private int collisionCount;
	
	private boolean[] deleted; 	
	
	
	public HashTable(int capacity, double maxLoadFactor)
	{
		if(capacity <= 0) 
		{
			throw new  IllegalArgumentException("Capacity must be positive!");
		}
		
		if(maxLoadFactor <= 0 || maxLoadFactor >= 1) 
		{
			throw new  IllegalArgumentException("Invalid load factor!");
		}			
		
		this.capacity = capacity;
		this.maxLoadFactor = maxLoadFactor;
		
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
	
	
	public void put(String key, V value) {

	    if (key == null || value == null) 
	    {
	        throw new IllegalArgumentException("Key or value cannot be null!");
	    }

	    int index = hashSSF(key);
	    int firstDeleted = -1;

	    for (int i = 0; i < capacity; i++) 
	    {

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

	        else if (keys[index].equals(key)) 
	        {
	            values[index] = value;
	            
	            return;
	        }

	        collisionCount++;
	        
	        index = (index + 1) % capacity;
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
		
		if(key == null)
		{
			throw new IllegalArgumentException("Key cannot be null!");
		}	
		
		int index = hashSSF(key);
		
		for(int i = 0; i < capacity; i++) 
		{
			 if(keys[index] == null && !deleted[index]) 
			 {
				 return null;
			 }
			 
			 if(keys[index] != null && keys[index].equals(key)) 
			 {
				 return values[index];
			 }
			 
			 index = (index + 1) % capacity;	     
		}	
		
	    return null;
	}
	
	
	public void remove(String key) {
		
		if(key == null)
		{
			throw new IllegalArgumentException("Key cannot be null!");
		}	
		
		int index = hashSSF(key);
		
		for(int i = 0; i < capacity; i++) 
		{
			if(keys[index] == null && !deleted[index]) 
			{
				return;
			}
			
			if(keys[index] != null && keys[index].equals(key)) 
			{
				keys[index] = null; 	
				values[index] = null; 	
				deleted[index] = true; 	
				
				size--;
				
				return;
			}
			
			index = (index + 1) % capacity;
		}
		
	}
	
	
	
	
}
