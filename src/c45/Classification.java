package c45;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class Classification {
	static String finPath = "training.txt";
	static BufferedWriter bw;
	static int numAttributes;
	static int classIndex;
	static int customer_count = 0;
	static String[] attributes;
	static HashMap<Integer,String[]> data = new HashMap<Integer,String[]>();
	
	public static boolean correction(){
		return true;
	}
	
	public static void main(String[] args) {
		try {
			Scanner inputScanner = new Scanner(new File(finPath));
			String headerLine = inputScanner.nextLine();
			String[] headers = headerLine.split(",");
			
			attributes = new String[headers.length];
			for(int i = 0; i < headers.length; i++)
				attributes[i] = headers[i];
			
			numAttributes = headers.length - 1;
			classIndex = 18;
			
			while(inputScanner.hasNextLine()){
					customer_count++;
					String line = inputScanner.nextLine();
					String[] tokens = line.split(",");
					String[] attr_value = new String[tokens.length];
					for(int i=0; i<tokens.length; i++){
						if(tokens[i].contains("\"")){
							if(!tokens[i].endsWith("\"")){
								tokens[i] = tokens[i]+tokens[i+1];
								for(int j=i+1; j<tokens.length-1;j++)
									tokens[j] = tokens[j+1];
							}	
						}
						//System.out.print(tokens[i]+" ");
					}
					//System.out.println();
					for(int i=0;i<tokens.length;i++){
						attr_value[i] = tokens[i];
					}
					data.put(Integer.parseInt(tokens[0]),attr_value);
			}
			inputScanner.close();
		} catch(IOException e) {
				e.printStackTrace();
		}
		
		/* debugging
		for(int s:data.keySet()){
			for(int i=0;i<numAttributes+1;i++){
				//System.out.print(data.get(s)[i]+",");
			}
			//System.out.println();
		}
		//
		*/
		
		
		Integer[] id = data.keySet().toArray(new Integer[data.size()]);
		Integer[] candidate_attr = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
		treenode root = new treenode(id,candidate_attr,"");
		root.dosomething();		
		
		//testing
		try {
			Scanner inputScanner = new Scanner(new File(finPath));
			String line = inputScanner.nextLine();
			
			while(inputScanner.hasNextLine()){
				customer_count++;
				line = inputScanner.nextLine();
				line = line.replace("\"", "");
				String[] tokens = line.split(",");
				treenode.passTree(root, tokens);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		//treenode.showTree(root);
		
	}
	
}
