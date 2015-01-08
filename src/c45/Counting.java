package c45;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.opencsv.CSVReader;

public class Counting {
	public int target_Index=-1;
	public HashMap<String, ConfusionMatrix> countingMap = new HashMap<String, ConfusionMatrix>();

	//Initialize target_Index and countingMap
	Counting(String testingFile, String targetClass) {
		try {
			CSVReader reader = new CSVReader(new FileReader(testingFile));
		    String[] nextLine = reader.readNext();
		    for(int i=0;i<nextLine.length;i++){
		    	if(nextLine[i].equals(targetClass)){
		    		this.target_Index=i;
		    	}
		    }
			Set<String> targetSet = new HashSet<String>();
		    //���@��testing file�ݬ�target class�`�@���X��
			while ((nextLine = reader.readNext()) != null) {
				targetSet.add(nextLine[target_Index]);
		    }
			
			for(String className:targetSet){
				countingMap.put(className, new ConfusionMatrix());
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean isCorrect(String[] test_data){
		String predictClass = passTree(Classification.root, test_data);
		//System.out.println(predictClass);
		
		if(test_data[target_Index].equals(predictClass)){				//�q��: ���T����TP++,��L��TN++
			this.countingMap.get(test_data[target_Index]).addTP();
			for(String className: countingMap.keySet()){
				if(!className.equals(test_data[target_Index])){
					this.countingMap.get(className).addTN();
				}
			}
			return true;
		}else{													//�q��: �����FN++,������FP++,��L��TN++
			this.countingMap.get(test_data[target_Index]).addFN();
			this.countingMap.get(predictClass).addFP();
			for(String className: countingMap.keySet()){
				if(!className.equals(test_data[target_Index]) && !className.equals(predictClass)){
					this.countingMap.get(className).addTN();
				}
			}
			return false;
		}
	}
	
	public void getResult(){
		double totalAccuracy=0.0;
		double totalPrecision=0.0;
		double totalRecall=0.0;
		for(String className: countingMap.keySet()){
			totalAccuracy += countingMap.get(className).getAccuracy();
			totalPrecision += countingMap.get(className).getPrecision();
			totalRecall += countingMap.get(className).getRecall();
		}
		totalAccuracy=totalAccuracy/(double)countingMap.size();
		totalPrecision=totalPrecision/(double)countingMap.size();
		totalRecall=totalRecall/(double)countingMap.size();
		
		System.out.println("Total accuracy: "+totalAccuracy);
		System.out.println("Total precision: "+totalPrecision);
		System.out.println("Total recall: "+totalRecall);
	}
	
	//�Ntesting data��J�ئn��decision tree�������A������target_class
	public String passTree(Treenode node, String[] test_data){
		String returnString;
		if(node.child.size()!=0){
			boolean flag = false;  //flag set true if the testing data find its attr(a_best in that level) in child 
			//���O��pass
			if(!node.candidate_feature.get(node.best_list_index).isContinuous()){
				for(int i=0;i<node.child.size();i++){
					String attr = Classification.data.get(node.child.get(i).id[0])[node.a_best];
					if(test_data[node.a_best].equals(attr)){
						node = node.child.get(i);
						flag = true;
						break;
					}
				}
			}else if(node.candidate_feature.get(node.best_list_index).isContinuous()){
			//�ƭȫ�pass
				if(Integer.parseInt(test_data[node.a_best])<=Integer.parseInt(node.child.get(0).attrinNode.split("<=")[1])){
					node = node.child.get(0);
				}else{
					node = node.child.get(1);
				}
				flag=true;		
			}
			if(flag){
				returnString = passTree(node, test_data);
			}else{
				/*for(int i=0;i<test_data.length;i++){
					System.out.print(test_data[i]+" ");
				}
				System.out.println();
				System.out.print("The attribute\""+test_data[node.a_best]);
				System.out.println("\" can't pass at this level.   Guess: " + node.guess());
				*/
				//return "Guess: "+node.guess();
				return node.guess();  //�b�Ӽh�L�k�~��parse,��������node���h�ƨMclass�@���w��
			}
		}else {
			/*for(int i=0;i<test_data.length;i++){
				System.out.print(test_data[i]+" ");
			}
			System.out.println();*/
			//System.out.println("Finish passing. Predict target_class: "+ node.leafnode_class);
			//return "Predict: "+node.leafnode_class;
			return node.leafnode_class; //��parse�������w�����G
		}
		return returnString;
	}
}
