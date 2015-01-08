package UI;
import java.util.Map;


public class ID3 {

	static String run(String trainingFile, String testFile, String targetAttr, Map <String, Boolean> isContinous){
		System.out.println(trainingFile);
		System.out.println(testFile);
		System.out.println(targetAttr);
		return "Test file path : C:\\\\test.txt\r\n\r\n----------------------------\r\nTotal time : 100000ms\r\nRecall : 101%\r\nPrecision : 101%\r\nAccuracy : 101%\r\nTP rate : 101%\r\nFP rate : 101%";
	}

}
