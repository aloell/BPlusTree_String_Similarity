package BPlusTree;
import java.util.*;
public class GCinstance {
	int zvalue;
	//used to store all strings with same z value
	LinkedList<String> store;
	int[] gramNumber;
	int gramL=2;
	public GCinstance(String s)
	{
		store=new LinkedList<String>();
		store.add(s);
		gramNumber=gramHashVector(s,gramL);
		zvalue=zorder(gramNumber);
	}
	// to merge those GCinstance with same z value
	public void merger(GCinstance t)
	{
		String temp=t.store.poll();
		this.store.add(temp);
	}
	
	public String toString()
	{
		return (new Integer(zvalue)).toString();
	}
	
	// to generate bucket vector that is how many grams in each buckets
	public int[] gramHashVector(String s,int n)
	{
		String[] gramSet=new String[s.length()+n-1];
		char[] temp=s.toCharArray();
		char[] processed=new char[s.length()+2*(n-1)];
		int i=0;
		for(i=0;i<processed.length;i++)
		{
			if(i<n-1)
			{
				processed[i]='#';
			}
			else if((i<(s.length()+n-1))&&(i>=(n-1)))
			{
				processed[i]=temp[i-(n-1)];
			}
			else
			{
				processed[i]='#';
			}
		}
		temp=new char[n];
		int j=0;
		int k=0;
		for(i=0;i<gramSet.length;i++)
		{
			for(j=0;j<n;j++)
			{
				k=i+j;
				temp[j]=processed[k];
			}
			gramSet[i]=new String(temp);
		}
		
		
		int[] buckets=new int[4];
		int hashValue;
		for(i=0;i<gramSet.length;i++)
		{
			hashValue=innerHash(gramSet[i]);
			buckets[hashValue]++;
		}
		return buckets;
	}
	
	//to generate zorder value from 4 buckets
	public int zorder(int[] buckets)
	{
		int i=0;
		int j=0;
		String[] temp=new String[4];
		for(i=0;i<4;i++)
		{
			temp[i]=Integer.toBinaryString(buckets[i]);
		}
		//longest is the length of longest binary presentation of a integer
		int longest=0;
		for(i=0;i<4;i++)
		{
			if(longest<temp[i].length())
			{
				longest=temp[i].length();
			}
		}
		//origin will be 4 binary strings with same length
		char[][] origin=new char[4][longest];
		int[] numberZeros=new int[4];
		int token=0;
		for(i=0;i<4;i++)
		{
			numberZeros[i]=longest-temp[i].length();
			token=numberZeros[i];
			for(j=0;j<longest;j++)
			{
				if(token>0)
				{
					origin[i][j]='0';
					token--;
				}
				else
				{
					origin[i][j]=(temp[i].toCharArray())[j-numberZeros[i]];
				}
			}
		}
		char[] zvalue=new char[4*longest];
		int bucketNum=0;
		int innerBucket=0;
		for(i=0;i<zvalue.length;i++)
		{
			bucketNum=i%4;
			innerBucket=i/4;
			zvalue[i]=origin[bucketNum][innerBucket];
		}
		return Integer.parseInt(new String(zvalue),2);
	}
	//hash any string into 4 buckets
	public int innerHash(String gram)
	{
		int i=0;
		char[] temp=gram.toCharArray();
		int hashValue=temp[0];
		for(i=1;i<temp.length;i++)
		{
			hashValue=37*hashValue+temp[i];
		}
		return hashValue%4;
	}
}
