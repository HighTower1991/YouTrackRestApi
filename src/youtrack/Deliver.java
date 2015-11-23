package youtrack;

public enum Deliver {
	Undefined (""),
	Deliver ("À livrer"),
	Delivered ("Livré"),
	NotDeliver ("Non");
	
	private final String name;       

    private Deliver(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }
    
    @Override
    public String toString() {
       return this.name;
    }
    
    
    public static Deliver getEnum(String name){
    	Deliver[] v = values();
    	for (Deliver deliver : v) {
			if(deliver.toString().equals(name))
				return deliver;
		}
    	return Undefined;
    }
}
