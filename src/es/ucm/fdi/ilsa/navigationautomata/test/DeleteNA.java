package es.ucm.fdi.ilsa.navigationautomata.test;

public class DeleteNA extends NavigationAction {
	
	    private int resource;
	    
	    public DeleteNA(int resource) {
	      this.resource=resource;
	    }

	    public boolean isDelete() {return true;}
	    public String toString() {return "Delete resource ->"+resource;}

		@Override
		public int getTag() {
			return 0;
		}
		
		
		public int getResource() {
			return resource;
		}
}
