package youtrack;

public enum State {
	Undefined (""),
	Submitted ("Soumis"),
	Open ("Ouvert"),
	InProgress ("En cours"),
	ToBeDiscussed ("� discuter"),
	Reopened ("R�ouvert"),
	CanTReproduce ("Reproduction impossible"),
	Duplicate ("Doublon"),
	Fixed ("Corrig�"),
	WonTFix ("Ne sera pas corrig�"),
	Incomplete ("Incomplet"),
	Obsolete ("Obsol�te"),
	Verified ("V�rifi�"),
	New ("Nouveau");
	
	
	private final String name;       

    private State(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }
    
    @Override
    public String toString() {
       return this.name;
    }
    
    
    public static State getEnum(String name){
    	State[] v = values();
    	for (State state : v) {
			if(state.toString().equals(name))
				return state;
		}
    	return Undefined;
    }
}
