package BPlusTree;

import java.util.LinkedList;
import java.util.Stack;
import java.util.Comparator;

public class BPlusTree<E> {
	treeNode root;
	int degree;
	int minDegree;
	int minKeys;
	int numberNull=0;
	int numberDuplicate=0;
	int queryType;
	int leafVisited=0;
	Comparator<? super E> comparator;
	LinkedList<String> result=new LinkedList<String>();
	LinkedList<GCinstance> result2=new LinkedList<GCinstance>();
	public BPlusTree(int degree, Comparator<? super E> comparator,int queryType)
	{
		this.comparator=comparator;
		this.degree=degree;
		minDegree=(degree+1)/2;
		minKeys=minDegree-1;
		root=new treeNode(true);
		this.queryType=queryType;
	}
	
	public E find(E target)
	{
		treeNode currentNode=root;
		int i=0;
		while(currentNode.leaf!=true)
		{
			for(i=0;i<currentNode.ksize;i++)
			{
				if(comparator.compare(target, currentNode.key[i])<0)
				{
					currentNode=currentNode.child[i];
					break;
				}
				if(i==currentNode.ksize-1)
				{
					currentNode=currentNode.child[i+1];
					break;
				}
			}
		}
		for(i=0;i<currentNode.ksize;i++)
		{
			if(comparator.compare(target, currentNode.key[i])==0)
				return currentNode.key[i];
		}
		return null;
	}
	//find the lowest key in the B plus tree
	public E findMin()
	{
		if(root.ksize==0)
			return null;
		treeNode currentNode=root;
		while(currentNode.leaf==false)
		{
			currentNode=currentNode.child[0];
		}
		return currentNode.key[0];
	}
	//find max key in the b plus tree
	public E findMax()
	{
		if(root.ksize==0)
			return null;
		treeNode currentNode=root;
		while(currentNode.leaf==false)
		{
			currentNode=currentNode.child[currentNode.csize-1];
		}
		return currentNode.key[currentNode.ksize-1];
	}
	//reset the result set after each range query (dictionary order)
	public void RQreset()
	{
		result=new LinkedList<String>();
		leafVisited=0;
	}
	//reset the result set after each range query (gram counting order)
	public void GCRQreset()
	{
		result2=new LinkedList<GCinstance>();
		leafVisited=0;
	}

	
	public void GCrangeQuery(GCinstance query, treeNode node, GCinstance min, GCinstance max, int threshold)
	{
		int i=0;
		if(node.leaf==true)
		{
			leafVisited++;
			for(i=0;i<node.ksize;i++)
			{
				if(GCEDcheck(query,(GCinstance)node.key[i],threshold))
				{
					result2.add((GCinstance)node.key[i]);
				}
			}
		}
		else
		{
			if(GClowerbound(query,min,(GCinstance)node.key[0],threshold))   
			{
				//System.out.println("nodes visitived: "+"min");
				//System.out.println(node);
				GCrangeQuery(query,node.child[0],min, (GCinstance)node.key[0], threshold);
			}
			for(i=0;i<node.ksize-1;i++)
			{
				if(GClowerbound(query,(GCinstance)node.key[i],(GCinstance)node.key[i+1],threshold))   
				{
					//System.out.println("nodes visitived: "+i);
					//System.out.println(node+" node height: "+node.height);
					GCrangeQuery(query,node.child[i+1],(GCinstance)node.key[i], (GCinstance)node.key[i+1], threshold);
				}
			}
			if(GClowerbound(query,(GCinstance)node.key[node.ksize-1],max,threshold))   
			{
				//System.out.println("nodes visitived: "+i);
				//System.out.println(node+" node height: "+node.height);
				GCrangeQuery(query,node.child[node.csize-1],(GCinstance)node.key[node.ksize-1], max, threshold);
			}
		}
	}
	
	//check minimal possible edit distance between two GCinstance
	public boolean GCEDcheck(GCinstance g1, GCinstance g2,int threshold)
	{
		int minED=EDvector(g1.gramNumber,g2.gramNumber)/g1.gramL;
		if(minED<threshold)
			return true;
		else
			 return false;
	}
	//used in GCEDcheck only
	public int EDvector(int[] g1, int[] g2)
	{
		int ed1=0;
		int ed2=0;
		int i=0;
		for(i=0;i<4;i++)
		{
			if(g1[i]>g2[i])
			{
				ed1=g1[i]-g2[i]+ed1;
			}
			if(g2[i]>g1[i])
			{
				ed2=g2[i]-g1[i]+ed2;
			}
		}
		if(ed2>ed1)
		{
			return ed2;
		}
		else
		{
			return ed1;
		}
	}
	
	//check minimal possible edit distance between one GCinstance and a pair of GCinstance
	public boolean GClowerbound(GCinstance q, GCinstance low, GCinstance high, int threshold)
	{
		char[] lowZvalue=(Integer.toBinaryString(low.zvalue)).toCharArray();
		char[] highZvalue=(Integer.toBinaryString(high.zvalue)).toCharArray();
		//expand binary presentation of length of 4's multiple
		lowZvalue=expandIntoFour(lowZvalue);
		highZvalue=expandIntoFour(highZvalue);
		int diff=0;
		int i=0;
		diff=highZvalue.length-lowZvalue.length;
		//to ensure the two binary presentation has same length
		if(diff!=0)
		{
			char[] temp=new char[highZvalue.length];
			for(i=0;i<temp.length;i++)
			{
				if(i<diff)
				{
					temp[i]='0';
				}
				else
				{
					temp[i]=lowZvalue[i-diff];
				}
			}
			lowZvalue=temp;
		}
		
		int comPrefixLength=0;
		for(i=0;i<lowZvalue.length;i++)
		{
			if(lowZvalue[i]!=highZvalue[i])
			{
				break;
			}
		}
		comPrefixLength=i;
		char[] comPrefix;
		if(comPrefixLength>0)
		{
			comPrefix=new char[comPrefixLength];
		}
		else
		{
			comPrefix=null;
		}
		for(i=0;i<comPrefixLength;i++)
		{
			comPrefix[i]=lowZvalue[i];
		}
		int bitsPerBucket=lowZvalue.length/4;
		int[] finalLow=lbVector(comPrefix,bitsPerBucket);
		int[] finalHigh=hbVector(comPrefix,bitsPerBucket);
		int finalED;
		finalED=minPossibleED(finalLow,finalHigh,q.gramNumber,q.gramL);
		//System.out.println("query vector: "+q.gramNumber[0]+" "+q.gramNumber[1]+" "+q.gramNumber[2]+" "+q.gramNumber[3]);
		//System.out.println("lowZvalue: "+new String(lowZvalue)+"highZvalue: "+new String(highZvalue));
		//System.out.println("low: "+low+"high: "+high+"finalED: "+finalED);
		if(finalED<=threshold)
			return true;
		else
			return false;
	}
	
	//check minimal edit distance between query vector and low vector, high vector pair
	public int minPossibleED(int[] low, int[] high, int[] q, int n)
	{
		int i=0;
		int ed=0;
		int diff,diff1,diff2;
		for(i=0;i<4;i++)
		{
			if((q[i]<low[i])||(q[i]>high[i]))
			{
				diff1=Math.abs(low[i]-q[i]);
				diff2=Math.abs(q[i]-high[i]);
				diff=diff1<diff2?diff1:diff2;
			}
			else
			{
				diff=0;
			}
			ed=ed+diff;
		}
		return ed/n;
	}
	
	
	//the samllest gram counting buckets vector
	public int[] lbVector(char[] comPrefix, int bitsPerBucket)
	{
		if(comPrefix==null)
		{
			return new int[4];
		}
		//the gramSignature's binary presentation
		char[][] lbBuckets=new char[4][bitsPerBucket];
		int i=0;
		int j=0;
		int row;
		int column;
		//initialize with all zeros
		for(i=0;i<4;i++)
		{
			for(j=0;j<bitsPerBucket;j++)
			{
				lbBuckets[i][j]='0';
			}
		}
		for(i=0;i<comPrefix.length;i++)
		{
			row=i%4;
			column=i/4;
			lbBuckets[row][column]=comPrefix[i];
		}
		int[] gramSignature=new int[4];
		String intoBucket;
		for(i=0;i<4;i++)
		{
			intoBucket=new String(lbBuckets[i]);
			gramSignature[i]=Integer.parseInt(intoBucket,2);
		}
		return gramSignature;
	}
	//the largest gram counting buckets vector
	public int[] hbVector(char[] comPrefix, int bitsPerBucket)
	{
		int max=1;
		int i=0;
		int j=0;
		int row;
		int column;
		int[] gramSignature=new int[4];
		for(i=0;i<bitsPerBucket;i++)
		{
			max=2*max;
		}
		max--;
		for(i=0;i<4;i++)
		{
			gramSignature[i]=max;
		}
		if(comPrefix==null)
		{
			return gramSignature;
		}
		//initialize hbBuckets with all 1s
		//hbBukcets are binary presentation of gramSignature
		char[][] hbBuckets=new char[4][bitsPerBucket];
		for(i=0;i<4;i++)
		{
			for(j=0;j<bitsPerBucket;j++)
			{
				hbBuckets[i][j]='1';
			}
		}
		for(i=0;i<comPrefix.length;i++)
		{
			row=i%4;
			column=i/4;
			hbBuckets[row][column]=comPrefix[i];
		}
		
		for(i=0;i<4;i++)
		{
			gramSignature[i]=Integer.parseInt((new String(hbBuckets[i])),2);
		}
		return gramSignature;
	}
	
	//expand the binary string length into multiple of four
	public char[] expandIntoFour(char[] binaryString)
	{
		int quotient=binaryString.length/4;
		int remainder=binaryString.length%4;
		int newLength=0;
		int diff=0;
		char[] newBinaryString;
		int i=0;
		if(remainder!=0)
		{
			newLength=(quotient+1)*4;
			diff=newLength-binaryString.length;
			newBinaryString=new char[newLength];
			for(i=0;i<newLength;i++)
			{
				if(i<diff)
				{
					newBinaryString[i]='0';
				}
				else
				{
					newBinaryString[i]=binaryString[i-diff];
				}
			}
			return newBinaryString;
		}
		else
		{
			return binaryString;
		}
	}
	
	//gram counting order top k query
	public void GCtopK(GCinstance query, treeNode node, GCinstance min, GCinstance max, int k)
	{
		int threshold=1;
		while(result2.size()<=k)
		{
			GCRQreset();
			GCrangeQuery(query, node, min, max, threshold);
			threshold++;
		}
	}
	
	private int smallest(int[] target)
	{
		int l=target.length;
		int i=0;
		int s=target[0];
		for(i=0;i<l;i++)
		{
			if(s>target[i])
				s=target[i];
		}
		return s;
	}
	
	//calculate the lower bound value in dictionary order
	public boolean lowerBound(char[] query, char[] min, char[] max, int threshold)
	{
		int i=0;
		char lastMin;
		char lastMax;
		//the index that characters are first different in min and max
		int smaller;
		smaller=min.length<max.length?min.length:max.length;
		if((new String(min)).equals(new String(max)))
		{
			return EDcheck(min,query,threshold);
		}
		for(i=0;i<smaller;i++)
		{
			if(min[i]!=max[i])
				break;
		}
		//use lastMin as the lowest character in unicode
		if(i==smaller)
		{
			lastMin='"';
			lastMax=max[i];
		}
		else
		{
			lastMin=min[i];
			lastMax=max[i];
		}
		
		//row is query string,column is prefix common string plus null and lastmin or last max
		int[][] matrix=new int[i+2][query.length+1];
		int rows=i+2;
		int columns=query.length+1;
		int d1,d2,d3;
		char[] preComChar=new char[i];
		for(i=0;i<preComChar.length;i++)
		{
			preComChar[i]=min[i];
		}
		int j=0;
		for(j=0;j<columns;j++)
		{
			matrix[0][j]=j;
		}
		for(i=0;i<rows;i++)
		{
			matrix[i][0]=i;
		}
		
		//dynamic programming to calculate distance
		int[] distArray=new int[3];
		for(i=1;i<rows;i++)
		{
			for(j=1;j<columns;j++)
			{
				d1=matrix[i][j-1]+1;
				if(i!=(rows-1))
				{
					if(preComChar[i-1]==query[j-1])
					{
						d2=matrix[i-1][j-1];
					}
					else
					{
						d2=matrix[i-1][j-1]+1;
					}
				}
				else
				{
					if((query[j-1]<=lastMax)||(query[j-1]>=lastMin))
					{
						d2=matrix[i-1][j-1];
					}
					else
					{
						d2=matrix[i-1][j-1]+1;
					}
				}
				d3=matrix[i-1][j]+1;
				distArray[0]=d1;
				distArray[1]=d2;
				distArray[2]=d3;
				matrix[i][j]=smallest(distArray);
			}
		}
		int[] lastRow=new int[query.length+1];
		for(i=0;i<query.length+1;i++)
		{
			lastRow[i]=matrix[rows-1][i];
		}
		if(smallest(lastRow)<=threshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//calculate edit distance between two strings
	public boolean EDcheck(char[] s, char[] query,int threshold)
	{
		int[][] matrix=new int[s.length+1][query.length+1];
		int i,j;
		for(i=0;i<s.length+1;i++)
		{
			matrix[i][0]=i;
		}
		for(j=0;j<query.length+1;j++)
		{
			matrix[0][j]=j;
		}
		int d1,d2,d3;
		int[] distArray=new int[3];
		for(i=1;i<s.length+1;i++)
		{
			for(j=1;j<query.length+1;j++)
			{
				d1=matrix[i][j-1]+1;
				if(s[i-1]==query[j-1])
				{
					d2=matrix[i-1][j-1];
				}
				else
				{
					d2=matrix[i-1][j-1]+1;
				}
				d3=matrix[i-1][j]+1;
				distArray[0]=d1;
				distArray[1]=d2;
				distArray[2]=d3;
				matrix[i][j]=smallest(distArray);
			}
		}
		if(matrix[s.length][query.length]<=threshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	//range query for dictionary order
	public void rangeQuery(String query, treeNode node, String min, String max, int threshold)
	{
		int i=0;
		if(node.leaf==true)
		{
			leafVisited++;
			for(i=0;i<node.ksize;i++)
			{
				//System.out.println("what is in leave: ");
				//System.out.println(node.key[i]);
				if(EDcheck(((String)node.key[i]).toCharArray(),query.toCharArray(),threshold))
				{
					result.add((String)node.key[i]);
				}
			}
		}
		else
		{
			if(lowerBound(query.toCharArray(),min.toCharArray(),((String)node.key[0]).toCharArray(),threshold))   
			{
				//System.out.println("nodes visitived: "+"min");
				//System.out.println(node);
				rangeQuery(query,node.child[0],min, (String)node.key[0], threshold);
			}
			for(i=0;i<node.ksize-1;i++)
			{
				if(lowerBound(query.toCharArray(),((String)node.key[i]).toCharArray(),((String)node.key[i+1]).toCharArray(),threshold))   
				{
					//System.out.println("nodes visitived: "+i);
					//System.out.println(node+" node height: "+node.height);
					rangeQuery(query,node.child[i+1],(String)node.key[i], (String)node.key[i+1], threshold);
				}
			}
			if(lowerBound(query.toCharArray(),((String)node.key[node.ksize-1]).toCharArray(),max.toCharArray(),threshold))   
			{
				//System.out.println("nodes visitived: "+i);
				//System.out.println(node+" node height: "+node.height);
				rangeQuery(query,node.child[node.csize-1],(String)node.key[node.ksize-1], max, threshold);
			}
		}
	}
	//B plus tree insert function
	public boolean insert(E newKey)
	{
		//System.out.println("The key: "+newKey+" is inserted.");
		int i=0;
		int j=0;
		treeNode currentNode=root;
		if(newKey==null)
		{
			numberNull++;
			return false;
		}
		if(root.ksize==0)
		{
			return insertIntoLeaf(newKey,root);
		}
		Stack<parent> parents=new Stack();
		while(currentNode.leaf==false)
		{
			for(i=0;i<currentNode.ksize;i++)
			{
				if(comparator.compare(newKey, currentNode.key[i])<0)
				{
					parents.push(new parent(currentNode,i));
					currentNode=currentNode.child[i];
					break;
				}
				if(i==(currentNode.ksize-1))
				{
					parents.push(new parent(currentNode,i+1));
					currentNode=currentNode.child[i+1];
					break;
				}
			}
		}
		if(currentNode.ksize<degree-1)
		{
			return insertIntoLeaf(newKey, currentNode);
		}
		else
		{
			if(!insertIntoLeaf(newKey,currentNode))
				return false;
			while(!parents.empty())
			{
				parent upperOne=parents.pop();
				treeNode upperNode=upperOne.node;
				int upperIndex=upperOne.index;
				//System.out.println(upperNode);
				//System.out.println(upperIndex);
				splitNode(upperNode, upperIndex);
				if(upperNode.ksize<=(degree-1))
					return true;
			}
			treeNode temp=new treeNode(false);
			temp.child[0]=root;
			temp.csize=1;
			temp.height=root.height+1;
			splitNode(temp,0);
			root=temp;
			return true;
		}
	}
	//just used for debug
	public void check()
	{
		int numberOfKeys=0;
		int i=0;
		treeNode currentNode=root;
		while(currentNode.leaf!=true)
		{
			currentNode=currentNode.child[0];
		}
		System.out.println("the first leave: "+currentNode);
		while(currentNode.child[currentNode.csize-1]!=null)
		{
			i++;
			/*System.out.println("check result at leaf level: ");
			System.out.println(currentNode);
			System.out.println(currentNode.csize);*/
			numberOfKeys=numberOfKeys+currentNode.ksize;
			currentNode=currentNode.child[currentNode.csize-1];
		}
		i++;
		//System.out.println("check result at leaf level: ");
		//System.out.println(currentNode);
		//System.out.println(currentNode.csize);
		numberOfKeys=numberOfKeys+currentNode.ksize;
		System.out.println("number of keys: "+numberOfKeys+" number of leaves visited: "+i);
	}
	// you can use this function to print the whole tree in level order
	// the level is the number of levels you want to print
	public void levelPrint(int level)
	{
		treeNode currentNode;
		LinkedList<treeNode> bfsList=new LinkedList();
		int numberLeaves=0;
		int numberKeys=0;
		int i=0;
		int lastNodeHeight;
		bfsList.add(root);
		lastNodeHeight=root.height;
		while(bfsList.peek()!=null)
		{
			currentNode=bfsList.poll();
			if(currentNode.height>root.height-level)
			{
				for(i=0;i<currentNode.csize;i++)
				{
					bfsList.add(currentNode.child[i]);
				}
				if(currentNode.height<lastNodeHeight)
				{
					System.out.print("\n"+currentNode);
				}
				else
				{
					System.out.print("   "+currentNode);
				}
				lastNodeHeight=currentNode.height;
				if(currentNode.height==0)
				{
					//System.out.print("following are leaves:   ");
					//System.out.println(currentNode);
					numberLeaves++;
					numberKeys=numberKeys+currentNode.ksize;
				}
			}
			else
			{
				break;
			}
		}
		System.out.println();
		System.out.println("height: "+ root.height);
		System.out.println("number Keys: "+numberKeys+" numberLeaves: "+numberLeaves);
		System.out.println("numberNull: "+numberNull+" numberDuplicate: "+numberDuplicate);
	}
	
	public int getHeight()
	{
		return root.height;
	}
	
	private void splitNode(treeNode upperNode, int upperIndex)
	{
		int i=0;
		int j=0;
		treeNode targetNode=upperNode.child[upperIndex];
		treeNode splitRight;
		for(i=upperNode.ksize-1;i>=upperIndex;i--)
		{
			upperNode.key[i+1]=upperNode.key[i];
			upperNode.child[i+2]=upperNode.child[i+1];
		}
		upperNode.ksize++;
		upperNode.csize++;
		//copy up keys if leaf
		if(targetNode.leaf==true)
		{
			j=0;
			splitRight=new treeNode(true);
			for(i=targetNode.ksize-(targetNode.ksize/2);i<targetNode.ksize;i++)
			{
				splitRight.key[j]=targetNode.key[i];
				j++;
			}
			splitRight.ksize=targetNode.ksize/2;
			splitRight.csize=splitRight.ksize+1;
			//sequential pointer reset at leaf level
			splitRight.child[splitRight.csize-1]=targetNode.child[targetNode.csize-1];
			targetNode.ksize=targetNode.ksize-splitRight.ksize;
			targetNode.csize=targetNode.ksize+1;
			//sequential pointer reset at leaf level
			targetNode.child[targetNode.csize-1]=splitRight;
			upperNode.child[upperIndex+1]=splitRight;
			upperNode.key[upperIndex]=splitRight.key[0];
		}
		//push up keys if not leaf
		else
		{
			j=0;
			splitRight=new treeNode(false);
			splitRight.height=targetNode.height;
			for(i=targetNode.ksize-(targetNode.ksize/2);i<targetNode.ksize;i++)
			{
				splitRight.key[j]=targetNode.key[i];
				splitRight.child[j]=targetNode.child[i];
				j++;
			}
			splitRight.child[j]=targetNode.child[targetNode.ksize];
			splitRight.ksize=targetNode.ksize/2;
			splitRight.csize=splitRight.ksize+1;
			targetNode.ksize=targetNode.ksize-splitRight.ksize-1;
			targetNode.csize=targetNode.ksize+1;
			upperNode.child[upperIndex+1]=splitRight;
			upperNode.key[upperIndex]=targetNode.key[targetNode.ksize];
		}
	}
	//insert into leaf node no matter it overflows, spiltNode function is responsible for splitting
	private boolean insertIntoLeaf(E newKey, treeNode currentNode)
	{
		int i=0;
		int j=0;
		for(i=0;i<currentNode.ksize;i++)
		{
			if(comparator.compare(newKey, currentNode.key[i])==0)
			{
				if(queryType==1)
				{
					numberDuplicate++;
					return false;
				}
				if(queryType==2)
				{
					GCmerge(currentNode.key[i],newKey);
					return false;
				}
			}
			if(comparator.compare(newKey, currentNode.key[i])<0)
			{
				for(j=currentNode.ksize-1;j>=i;j--)
				{
					currentNode.key[j+1]=currentNode.key[j];
					currentNode.child[j+2]=currentNode.child[j+1];
				}
				currentNode.ksize++;
				currentNode.csize++;
				currentNode.key[i]=newKey;
				return true;
			}
		}
		if(currentNode.ksize==0)
			currentNode.csize++;
		currentNode.ksize++;
		currentNode.csize++;
		currentNode.key[i]=newKey;
		currentNode.child[currentNode.csize-1]=currentNode.child[currentNode.csize-2];
		return true;
	}
	
	public void GCmerge(Object t1, Object t2)
	{
		((GCinstance)t1).merger((GCinstance)t2);
	}
	
	
	class treeNode
	{
		E[] key;
		treeNode[] child;
		int ksize;
		int csize;
		boolean leaf;
		int height;
		public treeNode(boolean leaf)
		{
			key=(E[])new Object[2*degree];
			child=(treeNode[])new BPlusTree.treeNode[2*degree];
			ksize=0;
			csize=0;
			height=0;
			this.leaf=leaf;
		}
		public String toString()
		{
			String specKeys=new String();
			int i=0;
			for(i=0;i<ksize-1;i++)
			{
				specKeys=specKeys+key[i].toString()+", ";
			}
			if(ksize>0)
				specKeys=specKeys+key[i].toString();
			specKeys="("+specKeys+")";
			return specKeys;
		}
	}
	private class parent
	{
		treeNode node;
		int index;
		public parent(treeNode t, int i)
		{
			node=t;
			index=i;
		}
	}
}
