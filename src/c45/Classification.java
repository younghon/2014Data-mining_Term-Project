package c45;
import c45.DecisionTree;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.opencsv.CSVReader;


public class Classification {
	static String finPath_training = "customer_card-0.7-Training.txt";
	static String finPath_testing = "customer_card-Testing.txt";
	static BufferedWriter bw;
	static int target_class_Index = 16;
	static ArrayList<String> Header = new ArrayList<String>();
	static HashMap<Integer,String[]> data = new HashMap<Integer,String[]>();
	static Treenode root;
	//static int customer_count = 0;
	
	
	public static void main(String[] args) {
		try {
			CSVReader reader = new CSVReader(new FileReader(finPath_training));
		    String[] nextLine;
		    nextLine = reader.readNext();
			for(int i = 0; i < nextLine.length; i++){
				Header.add(nextLine[i]);
			}
			
			while ((nextLine = reader.readNext()) != null) {
		    	//customer_count++;
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
		
		
		Integer[] id = data.keySet().toArray(new Integer[data.size()]);
		//Integer[] candidate_attr = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		HashMap<String, Boolean> passFeature = new HashMap<String, Boolean>();
		//passFeature.put("lname", false);
		//passFeature.put("fname", false);
		passFeature.put("city", false);
		passFeature.put("state_province", false);
		//passFeature.put("postal_code", false);
		passFeature.put("country", false);
		//passFeature.put("customer_region_id", true);
		//passFeature.put("phone", false);
		passFeature.put("marital_status", false);
		passFeature.put("gender", false);
		passFeature.put("total_children", true);
		passFeature.put("num_children_at_home", true);
		passFeature.put("education", false);
		passFeature.put("age", true);
		passFeature.put("year_income", true);		
		
		List<Feature> candidate_feature = new ArrayList<Feature>();
		for(String candidateName: passFeature.keySet()){
			candidate_feature.add(new Feature(candidateName, passFeature.get(candidateName)));
		}
		
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
			while ((nextLine = reader.readNext()) != null) {
				Treenode.passTree(root, nextLine);
		    }	
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
