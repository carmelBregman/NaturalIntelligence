import com.textrazor.AnalysisException;
import com.textrazor.NetworkException;
import com.textrazor.TextRazor;
import com.textrazor.annotations.Entity;
import com.textrazor.annotations.AnalyzedText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.util.*;

import java.io.IOException;
import java.util.stream.IntStream;


public class Main {
  private static final String API_KEY = "ce055b4d6b358f332ec512b49bf5f14a0b6f0ef34a76c887f189e810";
  private static final String feedUrl = "http://feeds.feedburner.com/TechCrunch/";

  public static void main(String[] args) {
    //keyword rest api client
    TextRazor client = new TextRazor(API_KEY);
    client.addExtractor("entities");

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder builder = factory.newDocumentBuilder();

      //get the feed xml
      NodeList nList = builder.parse(feedUrl).getElementsByTagName("item");

      //extract items form xml
      IntStream.range(0, nList.getLength())
              .mapToObj(nList::item)
              .filter(nNode -> nNode.getNodeType() == Node.ELEMENT_NODE)
              .map(nNode -> (Element) nNode)
              .forEach(eElement -> {

                //get tile and url form item
                String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                String link = eElement.getElementsByTagName("link").item(0).getTextContent();

                try {
                  //cal client to get keywords
                  AnalyzedText response = client.analyze(title);
                  List<Entity> entitiesList = response.getResponse().getEntities();
                  String entities = entitiesList != null ? entitiesList.stream().map(Entity::getEntityId).reduce((c1, c2) -> c1 + ", " + c2).orElse("") : "";


                  System.out.println(title + ". Url: " + link + " - keywords: (" + entities + ")");

                } catch (NetworkException | AnalysisException e) {
                  System.out.println(title + ". Url: " + link + " error getting keywords " + e.getMessage());
                  e.printStackTrace();
                }
              });
    } catch (ParserConfigurationException | SAXException | IOException e) {
      e.printStackTrace();
    }


  }
}
