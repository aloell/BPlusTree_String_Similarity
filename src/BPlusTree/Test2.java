package BPlusTree;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class Test2 {

   private class ConfigHandler extends DefaultHandler {

        private Locator locator;

        private String Value;;
        private boolean insidePerson;
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startElement(String namespaceURI, String localName,
                String rawName, Attributes atts) throws SAXException {
            String k;
            
            if (insidePerson = (rawName.equals("author") )) {
                Value = "";
                return;
            }
        }

        public void endElement(String namespaceURI, String localName,
                String rawName) throws SAXException {
            if (rawName.equals("author")) {

                Person p;
                if (Person.searchPerson(Value)==false) {
                    p = new Person(Value);
                }
                return;
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (insidePerson)
                Value += new String(ch, start, length);
        }

        private void Message(String mode, SAXParseException exception) {
            System.out.println(mode + " Line: " + exception.getLineNumber()
                    + " URI: " + exception.getSystemId() + "\n" + " Message: "
                    + exception.getMessage());
        }

        public void warning(SAXParseException exception) throws SAXException {

            Message("**Parsing Warning**\n", exception);
            throw new SAXException("Warning encountered");
        }

        public void error(SAXParseException exception) throws SAXException {

            Message("**Parsing Error**\n", exception);
            throw new SAXException("Error encountered");
        }

        public void fatalError(SAXParseException exception) throws SAXException {

            Message("**Parsing Fatal Error**\n", exception);
            throw new SAXException("Fatal Error encountered");
        }
    }
   
   Test2(String uri) {
      try {
	     SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	     SAXParser parser = parserFactory.newSAXParser();
	     ConfigHandler handler = new ConfigHandler();
         parser.getXMLReader().setFeature(
	          "http://xml.org/sax/features/validation", true);
         parser.parse(new File(uri), handler);
      } catch (IOException e) {
         System.out.println("Error reading URI: " + e.getMessage());
      } catch (SAXException e) {
         System.out.println("Error in parsing: " + e.getMessage());
      } catch (ParserConfigurationException e) {
         System.out.println("Error in XML parser configuration: " +
			    e.getMessage());
      }
   }

   public static void main(String[] args) {
	  
	  Test2 t = new Test2("hdblp.xml");
      long tstamp2;
      long tstamp3;
      int numberName=0;
      String personName;
      long tstamp1;
      long tstamp4;
	  tstamp1=System.currentTimeMillis();
	  System.out.println("Starting time: "+tstamp1);
	  Iterator<String> p=Person.iterator();
      BPlusTree<String> bt=new BPlusTree(5,new MyComparator(),1);
      while(p.hasNext())
      {
    	  	numberName++;
    	  	personName=p.next();
    	  	if(personName!=null)
    	  	{
    	  		bt.insert(personName);
    	  	}
      }
      System.out.println("Total number of names inserted: "+numberName);
      
      
      String[] fuzzyTarget=new String[4];
      int i=0;
      int[] threshold=new int[4];
      threshold[0]=1;
      threshold[1]=1;
      threshold[2]=1;
      threshold[3]=1;
      String target="Timo Partala";
      fuzzyTarget[0]="Tamo Partala";
      fuzzyTarget[1]="Timo Partalb";
      fuzzyTarget[2]="TimoPartala";
      fuzzyTarget[3]="Timo Paartala";
      
      tstamp2=System.currentTimeMillis();
      long timeInterval;
      timeInterval=tstamp2-tstamp1;
      System.out.println("Building time: "+timeInterval);
      for(i=0;i<4;i++)
      {
    	  System.out.println();
    	  tstamp3=System.currentTimeMillis();
      bt.rangeQuery(fuzzyTarget[i], bt.root, bt.findMin(), bt.findMax(), threshold[i]);
      tstamp4=System.currentTimeMillis();
      timeInterval=tstamp4-tstamp3;
      System.out.println("This range query execution time: "+timeInterval);
      System.out.println("leafVisited: "+bt.leafVisited);
      bt.leafVisited=0;
      Iterator<String> itr=bt.result.iterator();
      System.out.println("fuzzy query:  "+fuzzyTarget[i]);
      System.out.println("fuzzy query result: ");
      while(itr.hasNext())
      {
    	  	System.out.println(itr.next());
      }
      bt.RQreset();
      }
   }
}