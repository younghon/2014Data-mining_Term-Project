package c45;

public class ConfusionMatrix {
	private int TP;
	private int TN;
	private int FP;
	private int FN;
	
	ConfusionMatrix(){
		TP=0;
		TN=0;
		FP=0;
		FN=0;
	}
	
	ConfusionMatrix(int truePositive,int trueNegative,int falsePositive,int falseNegative){
		this.TP=truePositive;
		this.TN=trueNegative;
		this.FP=falsePositive;
		this.FN=falseNegative;
	}
	
	public void addTP(){
		TP++;
	}
	
	public void addTN(){
		TN++;
	}
	
	public void addFP(){
		FP++;
	}
	
	public void addFN(){
		FN++;
	}
	
	
	public double getAccuracy(){
		return (TP+TN)/(double)(TP+TN+FP+FN);
	}
	public double getPrecision(){
		return TP/(double)(TP+FP);
	}
	public double getRecall(){
		return TP/(double)(TP+FN);
	}
}
