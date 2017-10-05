package BPlusTree;

import java.util.Comparator;

public class MyComparator implements Comparator<String> {
	public int compare(String e1, String e2)
	{
		return e1.compareTo(e2);
	}

}
