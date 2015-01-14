package c45;

public class Feature {
	String name;
	int feature_index;
	boolean continuous;
	double split_value=-1;
	
	Feature(String name, boolean continuous){
		this.name = name;
		this.feature_index = Classification.Header.indexOf(name);
		this.continuous = continuous;
	}
	
	public String getName() {
		return name;
	}
	public int getFeature_index() {
		return feature_index;
	}
	public boolean isContinuous() {
		return continuous;
	}
	public void setSplit_value(double split_value) {
		this.split_value = split_value;
	}
	public double getSplit_value() {
		return split_value;
	}
	
}
