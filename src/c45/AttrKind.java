package c45;

public class AttrKind {
	public String kindName;
	public double value_entropy;
	public int classesCount;
	
	public AttrKind(String Name, double e, int classesCount){
		this.kindName = Name;
		this.value_entropy = e;
		this.classesCount = classesCount;
	}
}
