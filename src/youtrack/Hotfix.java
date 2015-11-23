package youtrack;

public enum Hotfix {
	Undefined (""),
	Deliver ("À livrer"),
	Delivered ("Livré"),
	NotDeliver ("Non");
	
	private final String name;       

    private Hotfix(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }
    
    @Override
    public String toString() {
       return this.name;
    }
    
    
    public static Hotfix getEnum(String name){
    	Hotfix[] v = values();
    	for (Hotfix deliver : v) {
			if(deliver.toString().equals(name))
				return deliver;
		}
    	return Undefined;
    }
}
