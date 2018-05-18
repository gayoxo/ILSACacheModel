package es.ucm.fdi.ilsa.navigationautomata.test;

import org.roaringbitmap.RoaringBitmap;

public class RemoveNA extends NavigationAction {
    private int tag;
    public RemoveNA(int t) {
      this.tag = t;  
    }
    public int getTag() {
       return tag;
    }
    public boolean isRemove() {return true;}
    public String toString() {return "[+ "+tag+"]";}
    
	@Override
	public RoaringBitmap getTagsFor() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getResource() {
		// TODO Auto-generated method stub
		return 0;
	}
}
