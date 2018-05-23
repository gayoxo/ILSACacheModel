package es.ucm.fdi.ilsa.navigationautomata.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.roaringbitmap.RoaringBitmap;

public class DCollection {
   private RoaringBitmap resources;
   private Map<Integer,RoaringBitmap> tagsFor;
   private RoaringBitmap tags;
   public DCollection() {
     this.resources = new RoaringBitmap(); 
     this.tagsFor = new HashMap<>();
     this.tags = new RoaringBitmap();
   }
   public void removeTag(int t) {
       tags.remove(t);
   }
   public void addObject(int o,RoaringBitmap tags) {
     resources.add(o); 
     tagsFor.put(o,tags);
   }
   public RoaringBitmap getResources() {
     return resources;  
   }
   public RoaringBitmap getTags() {
     return tags;  
   }
   public RoaringBitmap getTagsFor(int obj) {
     return tagsFor.get(obj);  
   }
   
   public void load(Reader in) throws IOException {
     BufferedReader reader = new BufferedReader(in);
     String line;
     int o=0;
     do {
       line = reader.readLine();
       if (line != null) {
         RoaringBitmap otags = new RoaringBitmap(); 
         Scanner s = new Scanner(new StringReader(line));
         while (s.hasNextInt()) {
           int t = s.nextInt();
           tags.add(t);
           otags.add(t);
         }
         addObject(o++,otags);
         s.close();
       }
     }
     while (line != null);
    
   }
 public String toString() {
   return resources.toString(); 
 }
 
 public void addTags(RoaringBitmap input)
 {
	 for (Integer t : input)
		 tags.add(t);
	
 }
public void removeObject(Integer recurso, RoaringBitmap tagsFor2) {
   resources.remove(recurso);
   tagsFor.remove(recurso);
//resources.remove(resources.rank(recurso));
}
 

}   