package BPlusTree;

import java.util.Comparator;

public class GCcomparator implements Comparator<GCinstance> {
	public int compare(GCinstance g1, GCinstance g2)
	{
		if(g1.zvalue>g2.zvalue)
		{
			return 1;
		}
		else if(g1.zvalue<g2.zvalue)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
