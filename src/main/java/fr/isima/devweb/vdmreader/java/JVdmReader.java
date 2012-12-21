/**
 * The MIT License
 *
 * Copyright (c) 2012, Daniel Petisme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.isima.devweb.vdmreader.java;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.*;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Daniel Petisme
 * @version 1.0
 */
public class JVdmReader {
   private static Logger LOGGER = Logger.getLogger(JVdmReader.class.getName());
   private static Map<String, Vdm> VDMS = new HashMap<String, Vdm>();
   private static File output = new File("vdm.out");

   private static String formatDate(String it) throws ParseException {
      return new SimpleDateFormat("dd-MM-YYY HH:mm").format(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(it));
   }

   private static String key(String date, String author) throws ParseException {
      StringBuilder outputBuilder = new StringBuilder("");
      outputBuilder.append(formatDate(date));
      outputBuilder.append("-");
      outputBuilder.append(author.toUpperCase());
      return outputBuilder.toString();
   }


   public static void main(String[] args) throws Exception {

      Runnable pollerRunner = new Runnable() {
         @Override
         public void run() {
            LOGGER.log(Level.INFO, "Polling VDM");
            try {
               //Using the DOM api
               DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
               DocumentBuilder db = dbf.newDocumentBuilder();
               Document doc = db.parse(new URL("http://feeds.uri.lv/viedemerde").openStream());

               NodeList entry = doc.getElementsByTagName("entry");

               Map<String, Vdm> newVdms = new HashMap<String, Vdm>();
               for (int i = 0; i < entry.getLength(); i++) {
                  Node node = entry.item(i);
                  if (node.getNodeType() == Node.ELEMENT_NODE) {
                     Element it = (Element) node;
                     String date = it.getElementsByTagName("updated").item(0).getChildNodes().item(0).getNodeValue();
                     //The author name is in the name tag under the author tag...
                     String author = ((Element) it.getElementsByTagName("author").item(0)).getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
                     String content = it.getElementsByTagName("content").item(0).getChildNodes().item(0).getNodeValue();
                     LOGGER.log(Level.FINE, "Date:{0}, author: {1}, content: {2}", new Object[]{date, author, content});
                     if (VDMS.get(key(date, author)) == null) {
                        newVdms.put(key(date, author), new Vdm(formatDate(date), author, content));
                     }
                  }
                  VDMS.putAll(newVdms);
               }
            } catch (SAXException e) {
               LOGGER.log(Level.SEVERE, e.getMessage());
            } catch (IOException e) {
               LOGGER.log(Level.SEVERE, e.getMessage());
            } catch (ParserConfigurationException e) {
               LOGGER.log(Level.SEVERE, e.getMessage());
            } catch (ParseException e) {
               LOGGER.log(Level.SEVERE, e.getMessage());
            }
            LOGGER.log(Level.INFO, "Grand Total of VDMs: " + VDMS.size());
         }
      };

      Runnable prompterRunner = new Runnable() {
         private BufferedWriter out = new BufferedWriter(new FileWriter(output));
         @Override
         public void run() {
            LOGGER.log(Level.INFO, "Randoming a VDM");
            Object[] keys = VDMS.keySet().toArray();
            String key = keys[new Random().nextInt(keys.length + 1)].toString();

            Vdm vdm = VDMS.get(key);

            StringBuilder outputBuilder = new StringBuilder("");
            outputBuilder.append("--\n");
            outputBuilder.append("  ");
            outputBuilder.append(vdm.getDate());
            outputBuilder.append("\n  ");
            outputBuilder.append(vdm.getContent());
            outputBuilder.append("\n          ");
            outputBuilder.append(vdm.getAuthor());
            outputBuilder.append("\n--\n");

            System.out.println(outputBuilder.toString());
            try {
               out.append(outputBuilder.toString());
               out.flush();
            } catch (IOException e) {
               LOGGER.log(Level.SEVERE, e.getMessage());
            }

         }

      };


      Executors.newScheduledThreadPool(1).scheduleAtFixedRate(pollerRunner, 0, 5, SECONDS);
      Executors.newScheduledThreadPool(1).scheduleAtFixedRate(prompterRunner, 10, 10, SECONDS);
   }

   public static class Vdm {
      private String date;
      private String author;
      private String content;

      public Vdm(String date, String author, String content) {
         this.date = date;
         this.author = author;
         this.content = content;
      }
      public String getDate() {
         return date;
      }
      public void setDate(String date) {
         this.date = date;
      }
      public String getAuthor() {
         return author.toUpperCase();
      }
      public void setAuthor(String author) {
         this.author = author;
      }
      public String getContent() {
         return content;
      }
      public void setContent(String content) {
         this.content = content;
      }
   }

}
