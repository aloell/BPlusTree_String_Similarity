package BPlusTree;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class Test {

   private class ConfigHandler extends DefaultHandler {

        private Locator locator;

        private String Value;
        private String key="";
        private String recordTag="";
        private String tempTitle;
        private boolean insidePerson;
        private boolean insideTitle;
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startElement(String namespaceURI, String localName,
                String rawName, Attributes atts) throws SAXException {
            String k;
            
            if(rawName.equals("title"))
            {
            	insideTitle=true;
            	Value="";
            	return;
            }
            if ((atts.getLength()>0) && ((k = atts.getValue("key"))!=null)&&(rawName.contains("proceedings"))) {
            	if(k.contains("conf/sigmod"))
            	{
            		key = k;
            		recordTag = rawName;
            	}
            	return;
        		}
        }

        public void endElement(String namespaceURI, String localName,
                String rawName) throws SAXException {
            if(rawName.equals("title"))
            {
            	insideTitle=false;
            	tempTitle=Value;
            	return;
            }
            if ((rawName.equals(recordTag))&&(key.contains("conf/sigmod"))) {
                Publication p = new Publication(key,tempTitle);
                tempTitle=null;
                recordTag="";
                key="";
                return;
            }
        }

        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (insidePerson||insideTitle)
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
   
   Test(String uri) {
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
	  
	  Test t = new Test("hdblp.xml");
      long tstamp2;
      long tstamp3;
      String insertTitle;
      long tstamp1;
      long tstamp4;
	  tstamp1=System.currentTimeMillis();
	  System.out.println("starting time: "+tstamp1);
      System.out.println("number of titles inserted: "+Publication.getNumberOfPublications());
      System.out.println("(duplicate titles will try to be inserted, but will not be inserted at leaf level)");
      Iterator<Publication> p=Publication.iterator();
      BPlusTree<String> bt=new BPlusTree(5,new MyComparator(),1);
      while(p.hasNext())
      {
    	  	insertTitle=p.next().getTitle();
    	  	if(insertTitle!=null)
    	  	{
    	  		bt.insert(insertTitle);
    	  	}
      }
      
      String[] fuzzyTarget=new String[4];
      int i=0;
      int[] threshold=new int[4];
      threshold[0]=1;
      threshold[1]=2;
      threshold[2]=4;
      threshold[3]=6;
      //String originStr="Proceedings of the ACM SIGMOD International Conference on Management of Data, Beijing, China, June 12-14, 2007";
      fuzzyTarget[0]="Proceedings of the ACM SIGMOD international Conference on Management of Data, Beijing, China, June 12-14, 2007";
      fuzzyTarget[1]="Proceedings of the AAM SIGMOD Internetional Conference on Management of Data, Beijing, China, June 12-14, 2007";
      fuzzyTarget[2]="Proceedings of the AAM SIGMOD nternational Conferenca on Management of Data, Beijing, China, June 22-14, 2007";
      fuzzyTarget[3]="Procaedings f the ACM SIGMQD Internttional Confarence on Management of Data, Beijing, China, June 12-14, 2907";

      long timeInterval;
      tstamp2=System.currentTimeMillis();
      timeInterval=tstamp2-tstamp1;
      System.out.println("Building time: "+timeInterval);
      for(i=0;i<4;i++)
      {
    	  tstamp3=System.currentTimeMillis();
      bt.rangeQuery(fuzzyTarget[i], bt.root, bt.findMin(), bt.findMax(), threshold[i]);
      System.out.println();
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
