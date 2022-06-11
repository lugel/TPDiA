package cmap;

public class StringPair {
	public String stringA;
	public String stringB;
	public StringPair(String a, String b) {
		this.stringA = a;
		this.stringB = b;
	}
	
	@Override
	public boolean equals(Object a) {
		if ((((StringPair)a).stringA.equals(this.stringA)) && (((StringPair)a).stringB.equals(this.stringB))) {
			return true;
		}
		return false;
	}
}
