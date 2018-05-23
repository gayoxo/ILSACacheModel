package es.ucm.fdi.ilsa.navigationautomata.test;

import java.util.HashMap;
import java.util.Map;
import org.roaringbitmap.RoaringBitmap;

public class InvertedIndex {
    private Map<Integer,RoaringBitmap> index;
    
    
    public InvertedIndex() {
    	 index = new HashMap<>();
	}
    
    public InvertedIndex(DCollection col) {
        index = new HashMap<>();
        for(int o: col.getResources()) {
            for(int t: col.getTagsFor(o)) {
                RoaringBitmap os = index.get(t);
                if (os == null) {
                    os = new RoaringBitmap();
                    index.put(t,os);
                }
                os.add(o);
            }
        }
    }
    public RoaringBitmap resourcesFor(int t) {
        return index.get(t);
    }    
    
    public void InsertResource(Integer o,RoaringBitmap tagsFor)
    {
             for(int t: tagsFor) {
                 RoaringBitmap os = index.get(t);
                 if (os == null) {
                     os = new RoaringBitmap();
                     index.put(t,os);
                 }
                 os.add(o);
             }
    }

     public void DeleteResource(int o, RoaringBitmap tagsFor,DCollection col) {
         for(int t: tagsFor) {
            RoaringBitmap os = index.get(t);
            os.remove(o);
            if (os.isEmpty()) {
                index.remove(t);
                col.removeTag(t);
            }
        }
    }
}
