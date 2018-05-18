package es.ucm.fdi.ilsa.navigationautomata.test;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import org.roaringbitmap.RoaringBitmap;

public class Main {  
//   private final static int NACTIONS=10000; 
   private final static int STEP=1000;
   private static final Random RAND=new Random();
private static final double ACTIONSMULTIPLICITY = 5;
    
    
 private static int grand(int top) {
     for(int i=0; i < top-1; i++) {
         if(RAND.nextInt(2)==0) return i;
     }
     return top; 
 }  
   
 private static NavigationAction makeAddAction(NavigationSystem ns,List<Integer> activeTags) {
     int tag = ns.getSelectableTags().select(RAND.nextInt(ns.getSelectableTags().getCardinality()));
     activeTags.add(0,tag);
     return new AddNA(tag);
 }   

 private static NavigationAction makeRemoveAction(NavigationSystem ns,List<Integer> activeTags) {
     int k = grand(ns.getActiveTags().getCardinality()-1);
     int tag = activeTags.get(k);
     activeTags.remove(k);
     return new RemoveNA(tag); 
 }   
 
 private static NavigationAction makeInsertAction(RoaringBitmap tagsFor, Integer recurso) {
	return new InsertNA(tagsFor,recurso);
}
 
 private static NavigationAction makeDeleteAction(int recurso) {
	 return new DeleteNA(recurso);
	}

 private static NavigationAction[] makeActions(DCollection colIni) {
	 DCollection col = new DCollection();
	 List<Integer> RecursosInsertados=new LinkedList<Integer>();
	 List<Integer> activeTags = new LinkedList<>();
     List<NavigationAction> actions = new LinkedList<NavigationAction>();
     NavigationSystem ns=null;
     Random r = new Random();
     int ActualInsertIndex=0;
     PriorityQueue<Integer> pendientesInsert=new PriorityQueue<Integer>();
     for (Integer integer : colIni.getResources())
    	 pendientesInsert.add(integer);
     
     while (!pendientesInsert.isEmpty()) {
    	 
//    	 //Borrar un 10
    	 if (col.getResources().getCardinality()!=0)
    	 {
    		 long nactionDel = 10;
    		 while (nactionDel>0)
    		 {
    		 Integer Borrar=RAND.nextInt(RecursosInsertados.size());
    		 
    		 Integer Recurso = RecursosInsertados.get(Borrar);
    		 RecursosInsertados.remove(Recurso);
    		 actions.add(makeDeleteAction(Recurso));
    		 col.removeObject(Recurso);
    		 nactionDel--;
    		 pendientesInsert.add(Recurso);
    		 
    		 }
    	 }
    	 
    	 
    	 //Insercion de 100 Elementos
    	 
    	 for (int i = ActualInsertIndex; i < ActualInsertIndex+100 &&!pendientesInsert.isEmpty(); i++) {
    		 Integer Recurso = pendientesInsert.remove();
    		 actions.add(makeInsertAction(colIni.getTagsFor(Recurso),Recurso));
    		 col.addObject(Recurso, colIni.getTagsFor(Recurso));
    		 col.addTags(colIni.getTagsFor(Recurso));
    		 RecursosInsertados.add(Recurso);
		}
    	 
    	 //Veo cuantos tengo
    	 ActualInsertIndex=col.getResources().getCardinality();
    	 
    	 //Lo creo y que construya todo
    	 ns = new CachedNavigationSystem(col,false);
	     ns.init();
    	 
    	
    	 
    	 //Calculo acciones
    	 long naction = Math.round(ActualInsertIndex*ACTIONSMULTIPLICITY);
    	 
    	 while (naction>0)
    	 {
    		 NavigationAction Aplicar=null; 
    		 
	        if (ns.getActiveTags().getCardinality() > 0 && ns.getSelectableTags().getCardinality() > 0) {
	            if (RAND.nextInt(2)==0)
	            	Aplicar = makeAddAction(ns,activeTags);
	            else 
	            	Aplicar = makeRemoveAction(ns,activeTags);
	        }  
	        else if (ns.getActiveTags().getCardinality() == 0) {
	        	Aplicar = makeAddAction(ns,activeTags);            
	        }
	        else {
	        	Aplicar = makeRemoveAction(ns,activeTags);
	        }
	        ns.run(Aplicar);
	       naction--;
	       actions.add(Aplicar);
    	 }
    	 
    	
    	 
    	 
     }
     NavigationAction[] Salida=new NavigationAction[actions.size()];
     for (int i = 0; i < actions.size(); i++)
    	 Salida[i]=actions.get(i);

     return Salida;
 }   
 




private static void simulate(NavigationSystem ns,NavigationAction[] actions, boolean out) {
     long time=0;
     long begin = System.nanoTime();
     ns.init();
     long end = System.nanoTime();
     time += (end-begin);
     if (out) System.out.println(0+"\t"+time);
     for(int a=0; a < actions.length; a++) {
         begin = System.nanoTime();
         ns.run(actions[a]);
         end = System.nanoTime();
         time += (end-begin);
         if ((a+1)%STEP == 0 && out) System.out.println((a+1)+"\t"+time);
     }
 }
    
 public static void main(String[] args) throws IOException {
   DCollection col = new DCollection();
   col.load(new FileReader(args[0]));
   System.out.println("Collection loaded");
   NavigationAction[] actions = makeActions(col);
   
//   simulate(new BasicNavigationSystem(new DCollection(),true),actions,false);
//   simulate(new BasicCachedNavigationSystem(new DCollection(),true),actions,false);
//   simulate(new CachedNavigationSystem(new DCollection(),true),actions,false);
//   simulate(new CachedAdvancedNavigationSystem(new DCollection(),true),actions,false);
   System.gc();System.gc();System.gc();System.gc();
   System.out.println("BASIC");
   simulate(new BasicNavigationSystem(new DCollection(),true),actions,true);
   System.gc();System.gc();System.gc();System.gc();
   System.out.println("BASIC CACHED");
   simulate(new BasicCachedNavigationSystem(new DCollection(),true),actions,true);
   System.gc();System.gc();System.gc();System.gc();
   System.out.println("CACHED");
   simulate(new CachedNavigationSystem(new DCollection(),true),actions,true);
   System.gc();System.gc();System.gc();System.gc();
   System.out.println("ADVANCED CACHED");
   simulate(new CachedAdvancedNavigationSystem(new DCollection(),true),actions,true);
   
 }


}
