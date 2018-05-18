package es.ucm.fdi.ilsa.navigationautomata.test;

import org.roaringbitmap.RoaringBitmap;

public abstract class NavigationAction {
    public abstract int getTag(); 
    public RoaringBitmap getTagsFor() {throw new UnsupportedOperationException();}
    public int getResource() {throw new UnsupportedOperationException();}
    public boolean isAdd(){return false;}
    public boolean isRemove() {return false;}
    public boolean isInsert(){return false;}
    public boolean isDelete(){return false;}
    
   
	
	
}
