package c45;
import c45.DecisionTree;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVReader;


public class Classification {
	static String finPath_training;
	static String finPath_testing;
	static String target_class;
	static Map<String, Boolean> selectedFeature;
	//static String finPath_training = "customer_card-0.7-Training.txt";
	//static String finPath_testing = "customer_card-Testing.txt";
	//static String finPath_training = "STULONG-Death-data-0.7-Training.txt";
	//static String finPath_testing = "STULONG-Death-data-0.7-Testing.txt";

	static int target_class_Index=-1;
	static ArrayList<String> Header = new ArrayList<String>();
	static HashMap<Integer,String[]> data = new HashMap<Integer,String[]>();
	static Treenode root;
	
	public Classification(String trainingFile,String testingFile, String targetAttr, Map<String, Boolean> isAttributeContinous) {
		finPath_training=trainingFile;
		finPath_testing=testingFile;
		target_class=targetAttr;
		selectedFeature=isAttributeContinous;
	}
	
	public static void main(String[] args) {
		//System.out.println("Target class: "+target_class);
		try {
			CSVReader reader = new CSVReader(new FileReader(finPath_training));
		    String[] nextLine;
		    nextLine = reader.readNext();
			for(int i = 0; i < nextLine.length; i++){
				Header.add(nextLine[i]);
				if(nextLine[i].equals(target_class)){
					target_class_Index=i;
				}
			}
			
			while ((nextLine = reader.readNext()) != null) {
		    	data.put(Integer.parseInt(nextLine[0]),nextLine);
		    }	    
		} catch(IOException e) {
				e.printStackTrace();
		}
		/* debugging
		for(Integer s:data.keySet()){
		    	for(int i=0;i<data.get(s).length;i++)
		    		System.out.print(data.get(s)[i]+" ");
		    	System.out.println();
		    }
		}
		*/
		
		//初始化root內容: id, candidate_feature
		Integer[] id = data.keySet().toArray(new Integer[data.size()]);	
		List<Feature> candidate_feature = new ArrayList<Feature>();
		for(String featureName: selectedFeature.keySet()){
			candidate_feature.add(new Feature(featureName, selectedFeature.get(featureName)));
		}
		
		//建立Decision tree model
		root = new Treenode(id,candidate_feature,"");
		root.dosomething();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DecisionTree.createAndShowGUI(root);
            }
		});
		
		
		
		//testing
		try {
			CSVReader reader = new CSVReader(new FileReader(finPath_testing));
		    String[] nextLine = reader.readNext();
		    
		    //對testing file的每筆資料做分類並預測,計算統計結果
		    Counting c = new Counting(finPath_testing,target_class);
			while ((nextLine = reader.readNext()) != null) {
				c.isCorrect(nextLine);
		    }	
			c.getResult();
		} catch(IOException e) {
			e.printStackTrace();
		}		
	}
	
}
