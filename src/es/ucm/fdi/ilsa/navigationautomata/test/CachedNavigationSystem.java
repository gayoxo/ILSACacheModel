package es.ucm.fdi.ilsa.navigationautomata.test;

import java.util.HashMap;
import java.util.Map;
import org.roaringbitmap.RoaringBitmap;

public class CachedNavigationSystem extends BasicNavigationSystem {
    private final static boolean DEBUG=false;
    private Map<RoaringBitmap,RoaringBitmap> resourceSetsStore;
    private Map<RoaringBitmap,RoaringBitmap> selectableTagsStore;
    private Map<RoaringBitmap,RoaringBitmap> representativesStore;
    public CachedNavigationSystem(DCollection col,boolean vacio) {
        super(col,vacio);
        resourceSetsStore = new HashMap<>();
        selectableTagsStore = new HashMap<>();
        representativesStore = new HashMap<>();
    }
    

    @Override
    public void init() {
        super.init();
        resourceSetsStore.put(activeTags.clone(),filteredResources);
        selectableTagsStore.put(activeTags.clone(),selectableTags);
    }

    @Override
    public void run(NavigationAction a) {
        if (a.isAdd()) {
           activeTags.add(a.getTag());
        }
        else {
           activeTags.remove(a.getTag());
        }
        if (a.isAdd()||a.isRemove())
        {
        RoaringBitmap fResources = resourceSetsStore.get(activeTags);
        if (fResources != null) {
           if(DEBUG) {
             System.out.println("***** "+activeTags+" CACHED");  
           } 
           filteredResources = fResources;
           selectableTags = selectableTagsStore.get(activeTags); 
        }
        else {
           RoaringBitmap atags = activeTags.clone();
           if (a.isAdd()) {
             filteredResources = RoaringBitmap.and(filteredResources, iindex.resourcesFor(a.getTag()));
           }
           else {
             filteredResources = computeResourcesAfterRemove();
           }
           RoaringBitmap representative = representativesStore.get(filteredResources);
           if (representative != null) {
               if(DEBUG) {
                System.out.println("***** "+activeTags+" EQUIVALENT TO "+representative);  
              } 
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
        }
        }else
        	if (a.isInsert())
        	{
        		collection.addObject(a.getResource(), a.getTagsFor());
        		collection.addTags(a.getTagsFor());
        		iindex.InsertResource(a.getResource(), collection.getTagsFor(a.getResource()));
        		resourceSetsStore.clear();
        		selectableTagsStore.clear();
        		representativesStore.clear();
        	}else
        		if (a.isDelete())
        		{
        			iindex.DeleteResource(a.getResource(), collection.getTagsFor(a.getResource()),collection);
        			collection.removeObject(a.getResource(), collection.getTagsFor(a.getResource()));
           		        resourceSetsStore.clear();
        		        selectableTagsStore.clear();
            		        representativesStore.clear();
        		}
    }   
               
}
