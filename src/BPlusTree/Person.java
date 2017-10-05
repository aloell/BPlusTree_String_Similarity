package BPlusTree;

import java.util.*;

public class Person {
    private static int maxPublCount = 0;
    private static int maxNameLength = 0;
    private static HashSet<String> personMap = new HashSet<String>();
    
    public Person(String name) {
        personMap.add(name);
    }
    
    static public Iterator<String> iterator() {
        return personMap.iterator();
    }
    
    static public boolean searchPerson(String name) {
        return  personMap.contains(name);
    }
    
    static public int numberOfPersons() {
        return personMap.size();
    }
      
}
