package es.ucm.fdi.ilsa.navigationautomata.test;

import java.util.Iterator;
import org.roaringbitmap.RoaringBitmap;

public class BasicNavigationSystem implements NavigationSystem {
   protected DCollection collection;
   protected InvertedIndex iindex;
   protected RoaringBitmap activeTags;
   protected RoaringBitmap filteredResources;
   protected RoaringBitmap selectableTags;
   
   

   public BasicNavigationSystem(DCollection collection,boolean vacio) {
     this.collection = collection; 
     if (vacio)
    	 this.iindex = new InvertedIndex();
     else
    	 this.iindex = new InvertedIndex(collection);
   }
   
   
   protected RoaringBitmap computeSelectableTagsAfterAdd(int t) {
       RoaringBitmap stags = new RoaringBitmap();
       int nfilteredResources = filteredResources.getCardinality();
       for(int tag: selectableTags) {
         if (tag != t) {
           RoaringBitmap selectedRes = RoaringBitmap.and(filteredResources, iindex.resourcesFor(tag));
           int nSelectedRes = selectedRes.getCardinality();  
           if (nSelectedRes > 0 && nSelectedRes < nfilteredResources) {
              stags.add(tag); 
           }
          }   
       }
       return stags;
   }
   
   protected RoaringBitmap computeSelectableTagsAfterRemove() {
       RoaringBitmap stags = new RoaringBitmap();
       int nfilteredResources = filteredResources.getCardinality();
       for(int tag: collection.getTags()) {
         if (! activeTags.contains(tag)) {
           RoaringBitmap selectedRes = RoaringBitmap.and(filteredResources, iindex.resourcesFor(tag));
           int nSelectedRes = selectedRes.getCardinality();  
           if (nSelectedRes > 0 && nSelectedRes < nfilteredResources) {
              stags.add(tag); 
           }
          }   
       }
       return stags;
   }
   
   protected RoaringBitmap computeInitialSelectableTags() {
       RoaringBitmap stags = new RoaringBitmap();
       int nfilteredResources = filteredResources.getCardinality();
       for(int tag: collection.getTags()) {
           if(iindex.resourcesFor(tag).getCardinality() < nfilteredResources) {
              stags.add(tag); 
           }
       }
       return stags;
   }
   
   protected RoaringBitmap computeResourcesAfterRemove() {
       int nactivetags = activeTags.getCardinality();
       if (nactivetags == 0) return collection.getResources();
       else if (nactivetags == 1) return iindex.resourcesFor(activeTags.first());
       else {
           RoaringBitmap resul = new RoaringBitmap();
           Iterator<Integer> iatags = activeTags.iterator();
           resul.or(iindex.resourcesFor(iatags.next()));
           while (iatags.hasNext()) {
           resul.and(iindex.resourcesFor(iatags.next()));
           }
           return resul;
       }   
   }
   
   
       @Override
    public void init() {
        activeTags = new RoaringBitmap();
        filteredResources = collection.getResources();
        selectableTags = computeInitialSelectableTags();
    }

    @Override
    public void run(NavigationAction a) {
        if (a.isAdd()) {
           activeTags.add(a.getTag());
           filteredResources = RoaringBitmap.and(filteredResources, iindex.resourcesFor(a.getTag()));
           selectableTags = computeSelectableTagsAfterAdd(a.getTag());
        }
        else if (a.isRemove()){
           activeTags.remove(a.getTag());
           filteredResources = computeResourcesAfterRemove();
           if (activeTags.getCardinality() == 0)
               selectableTags = computeInitialSelectableTags();
           else 
               selectableTags = computeSelectableTagsAfterRemove();
        }else
        	if (a.isInsert())
        	{
        		collection.addObject(a.getResource(), a.getTagsFor());
        		collection.addTags(a.getTagsFor());
        		iindex.InsertResource(a.getResource(), a.getTagsFor());
        	}else
        		if (a.isDelete())
        		{
        			iindex.DeleteResource(a.getResource(), collection.getTagsFor(a.getResource()),collection);
        			collection.removeObject(a.getResource(), collection.getTagsFor(a.getResource()));
        		}
    }

    @Override
    public RoaringBitmap getFilteredResources() {
        return filteredResources;
    }

    @Override
    public RoaringBitmap getActiveTags() {
        return activeTags;
    }

    @Override
    public RoaringBitmap getSelectableTags() {
        return selectableTags;
    }

   
   
    
}
