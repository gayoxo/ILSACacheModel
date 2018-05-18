package es.ucm.fdi.ilsa.navigationautomata.test;

import java.util.HashMap;
import java.util.Map;
import org.roaringbitmap.RoaringBitmap;

public class CachedAdvancedNavigationSystem extends BasicNavigationSystem {
    private final static boolean DEBUG=false;
    private Map<RoaringBitmap,RoaringBitmap> resourceSetsStore;
    private Map<RoaringBitmap,RoaringBitmap> selectableTagsStore;
    private Map<RoaringBitmap,RoaringBitmap> representativesStore;
    private Map<Integer,Long> snapshotTags;
    private Map<RoaringBitmap,Long> snapshotState;
    
    public CachedAdvancedNavigationSystem(DCollection col,boolean vacio) {
        super(col,vacio);
        resourceSetsStore = new HashMap<>();
        selectableTagsStore = new HashMap<>();
        representativesStore = new HashMap<>();
        snapshotTags = new HashMap<>();
        snapshotState= new HashMap<>();
    }
    

    @Override
    public void init() {
        super.init();
        resourceSetsStore.put(activeTags.clone(),filteredResources);
        selectableTagsStore.put(activeTags.clone(),selectableTags);
        for(int tag: collection.getTags()) {
            snapshotTags.put(tag,System.nanoTime());
        }
        snapshotState.put(activeTags,System.nanoTime());
    }

    @Override
    public void run(NavigationAction a) {
        if (a.isAdd()) {
           activeTags.add(a.getTag());
        }
        else if(a.isRemove()){
           activeTags.remove(a.getTag());
        }
        else if (a.isInsert()) {
        	collection.addObject(a.getResource(), a.getTagsFor());
    		collection.addTags(a.getTagsFor());
                RoaringBitmap tagsResource = collection.getTagsFor(a.getResource());
    		iindex.InsertResource(a.getResource(), tagsResource);
    		for (int tag : tagsResource) 
    			snapshotTags.put(tag, System.nanoTime());
        }	
        else if (a.isDelete()) {
        	
                RoaringBitmap tagsResource = collection.getTagsFor(a.getResource());
		iindex.DeleteResource(a.getResource(), tagsResource);
		collection.removeObject(a.getResource());
		for (int tag : tagsResource) 
		    snapshotTags.put(tag, System.nanoTime());
        }
        
        if (a.isAdd()||a.isRemove()) {
        
         Long updatingTime = snapshotState.get(activeTags);   
         
         if(updatingTime != null && updated(updatingTime,activeTags)) {
           if(DEBUG) {
             System.out.println("***** "+activeTags+" CACHED");  
           } 
           filteredResources = resourceSetsStore.get(activeTags);
           selectableTags = selectableTagsStore.get(activeTags); 
        }
        else {
           RoaringBitmap atags = activeTags.clone();
           if (a.isAdd()) {
             filteredResources = RoaringBitmap.and(filteredResources, iindex.resourcesFor(a.getTag()));
           }
           //a.isRemove()
           else {
             filteredResources = computeResourcesAfterRemove();
           }
           
           
           RoaringBitmap representative = representativesStore.get(filteredResources);
           
           if(representative != null && updated(snapshotState.get(representative), selectableTagsStore.get(representative))) { 
              selectableTags = selectableTagsStore.get(representative);    
           }
           else {
             if (a.isAdd()) {
               selectableTags = computeSelectableTagsAfterAdd(a.getTag());
             }
             else {
               selectableTags = computeSelectableTagsAfterRemove();
             }
             representativesStore.put(filteredResources,atags);
           }
           resourceSetsStore.put(atags,filteredResources);
           selectableTagsStore.put(atags,selectableTags);
           snapshotState.put(atags, System.nanoTime());
         }  
    }
    }     

    private boolean updated(Long timeSet, RoaringBitmap tagsFor) {
            for(int tag: tagsFor) {
                if(snapshotTags.get(tag) > timeSet) return false;
            }
            return true;
    }            
          
               
}
