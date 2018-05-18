package es.ucm.fdi.ilsa.navigationautomata.test;

import org.roaringbitmap.RoaringBitmap;

public interface NavigationSystem {   
    public void init();
    public void run(NavigationAction a); 
    public RoaringBitmap getFilteredResources();
    public RoaringBitmap getActiveTags();
    public RoaringBitmap getSelectableTags();
}
