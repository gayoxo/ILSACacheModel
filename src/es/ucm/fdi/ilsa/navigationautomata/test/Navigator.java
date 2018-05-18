package es.ucm.fdi.ilsa.navigationautomata.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.roaringbitmap.RoaringBitmap;

public class Navigator {
    
   private BufferedReader input;
   private NavigationSystem ns;
   public Navigator(NavigationSystem ns) {
      this.ns = ns; 
      this.input = new BufferedReader(new InputStreamReader(System.in));   
   }
   private int readOption() throws IOException {
       return new Integer(input.readLine());
   }
   
   private void showNavigationState() {
       System.out.println("OBJECTS:"+ns.getFilteredResources());
       System.out.println("ACTIVE:"+ns.getActiveTags());
       System.out.println("SELECTABLE:"+ns.getSelectableTags());
   }
   
   private int selectTag(RoaringBitmap tags) throws IOException {
      int tag;
      System.out.print(">");
      do {
         tag = readOption();
     }
     while(! tags.contains(tag));
     return tag;  
   }
   
   
   private boolean navigate() throws IOException {
       System.out.println("1. Add");
       System.out.println("2. Remove");
       System.out.println("3. Shutdown");
       int option;
       do {
          option = readOption();
       }
       while(option<1||option>3);
       switch(option) {
           case 1: {
               int tag = selectTag(ns.getSelectableTags());
               System.out.println("ADDING "+tag);  
               ns.run(new AddNA(tag));
               break;                
           }
           case 2: {
               int tag = selectTag(ns.getActiveTags());
               System.out.println("REMOVING "+tag);  
               ns.run(new RemoveNA(tag));
               break;                
           }
           case 3: {break;}
       }
       showNavigationState();
       return option !=3;
   }
   
    
   public void run() throws IOException {
      ns.init();
      showNavigationState();
      while(navigate());
    }
   
   public static void main(String[] args) throws IOException {
       DCollection col = new DCollection();     
       col.load(new FileReader(args[0]));
       new Navigator(new CachedNavigationSystem(col,false)).run();
   } 
}
