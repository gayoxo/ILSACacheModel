package es.ucm.fdi.ilsa.navigationautomata.test;

import org.roaringbitmap.RoaringBitmap;

public class InsertNA extends NavigationAction {
	
    private RoaringBitmap tagsFor;
    private int resource;
    
    public InsertNA(RoaringBitmap tagsFor, int resource) {
      this.tagsFor = tagsFor;  
      this.resource=resource;
    }

    public boolean isInsert() {return true;}
    public String toString() {return "insert resource ->"+resource;}

	@Override
	public int getTag() {
		return 0;
	}
	
	public RoaringBitmap getTagsFor() {
		return tagsFor;
	}
	
	public int getResource() {
		return resource;
	}
}
