package BPlusTree;

public class BTreeDebug {
	public static void main(String[] args)
	{
		BPlusTree<String> p=new BPlusTree<String>(5,new MyComparator(),1);
		int i=0;
		int j=0;
		for(i=0;i<30;i++)
		{
			j=(int)(Math.random()*100);
			p.insert((new Integer(i)).toString());
		}
		p.levelPrint(10);
		p.check();
	}
}
