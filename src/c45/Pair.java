package c45;

public class Pair {
	double id_attr;
	String id_class;
	
	Pair(double id_attr,String id_class){
		this.id_attr = id_attr;
		this.id_class = id_class;	
	}
	
	public double getId_attr() {
		return id_attr;
	}

	public String getId_class() {
		return id_class;
	}
	
}
