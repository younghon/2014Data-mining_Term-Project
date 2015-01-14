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
	static long startTime;
	static long endTime;
	static String buildTime;
	static String textTime;
	static Counting c;
	
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
			
			int transactionNo=0;
			while ((nextLine = reader.readNext()) != null) {
				transactionNo++;
				data.put(transactionNo,nextLine);
				//data.put(Integer.parseInt(nextLine[0]),nextLine);		    	
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
		
		//��l��root���e: id, candidate_feature
		Integer[] id = data.keySet().toArray(new Integer[data.size()]);	
		List<Feature> candidate_feature = new ArrayList<Feature>();
		for(String featureName: selectedFeature.keySet()){
			candidate_feature.add(new Feature(featureName, selectedFeature.get(featureName)));
		}
		
		//�إ�Decision tree model
		root = new Treenode(id,candidate_feature,"");
		startTime = System.currentTimeMillis();
		System.out.println("Starting growth decision tree...");
		root.dosomething();
		endTime = System.currentTimeMillis();
		buildTime = "Build decision tree takes "+(endTime - startTime)/1000.0+" secs.\n";
		System.out.println("Build decision tree takes "+(endTime - startTime)/1000.0+" secs.");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                DecisionTree.createAndShowGUI(root);
            }
		});
		
		
		
		//testing
		try {
			CSVReader reader = new CSVReader(new FileReader(finPath_testing));
		    String[] nextLine = reader.readNext();
		    
		    //��testing file���C����ư������ùw��,�p��έp���G
		    startTime = System.currentTimeMillis();
		    c = new Counting(finPath_testing,target_class);
			while ((nextLine = reader.readNext()) != null) {
				c.isCorrect(nextLine);
		    }
			endTime =System.currentTimeMillis();
			textTime = "Testing takes "+ (endTime - startTime)/1000.0 +" secs.\n";
			System.out.println("Testing takes "+ (endTime - startTime)/1000.0 +" secs.");
			c.getResult();
		} catch(IOException e) {
			e.printStackTrace();
		}	
		
		
	}
	
	public String getResult(){
		return buildTime + c.getResult()+ "\n" +textTime;
	}
	
	public Treenode getRoot(){
		return root;
	}
	
}
