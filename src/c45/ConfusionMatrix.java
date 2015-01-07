package c45;

public class ConfusionMatrix {
	public int TP;
	public int TN;
	public int FP;
	public int FN;
	
	ConfusionMatrix(int truePositive,int trueNegative,int falsePositive,int falseNegative){
		this.TP=truePositive;
		this.TN=trueNegative;
		this.FP=falsePositive;
		this.FN=falseNegative;
	}
	
	public double getAccuracy(){
		return TP/(double)TP+TN+FP+FN;
	}
	public double getPrecision(){
		return TP/(double)TP+FP;
	}
	public double getRecall(){
		return TP/(double)TP+FN;
	}
}
