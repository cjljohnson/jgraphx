package petri;

public class Place {
	private int tokens;
	private int ind;
	
	public Place(int ind, int tokens) {
		this.tokens = tokens;
		this.ind = ind;
	}

	public int getTokens() {
		return tokens;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	public int getInd() {
		return ind;
	}

	public void setInd(int ind) {
		this.ind = ind;
	}

	@Override
	public String toString() {
		return "tokens=" + tokens;
	}
	
	

}
