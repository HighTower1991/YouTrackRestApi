package youtrack;

public enum State {
	Undefined (""),
	Submitted ("Soumis"),
	Open ("Ouvert"),
	InProgress ("En cours"),
	ToBeDiscussed ("À discuter"),
	Reopened ("Réouvert"),
	CanTReproduce ("Reproduction impossible"),
	Duplicate ("Doublon"),
	Fixed ("Corrigé"),
	WonTFix ("Ne sera pas corrigé"),
	Incomplete ("Incomplet"),
	Obsolete ("Obsolète"),
	Verified ("Vérifié"),
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
