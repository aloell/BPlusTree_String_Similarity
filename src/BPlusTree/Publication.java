package BPlusTree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Publication {
    private static HashSet<Publication> ps= new HashSet<Publication>();
    private String key;
    private String title;
    private static int idBuffer=0;
    private int id;
    public Publication(String key, String title) {
        this.key = key;
        this.id=idBuffer;
        idBuffer++;
        this.title=title;
        ps.add(this);
    }
    
    public static int getNumberOfPublications() {
        return ps.size();
    }
    
    public String getKey()
    {
    	return key;
    }
    public int getID()
    {
    	return id;
    }
    public static HashSet<Publication> getPublicationSet()
    {
    	return ps;
    }
    public String getTitle()
    {
    	return title;
    }
    
    static Iterator<Publication> iterator() {
        return ps.iterator();
    }
}