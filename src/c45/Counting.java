package c45;

import java.util.HashMap;

public class Counting {
	public static HashMap<String, Integer> actualCount = new HashMap<String, Integer>();
	public static HashMap<String, Integer> predictCount = new HashMap<String, Integer>(); 
	
	
	
	public static boolean isCorrect(String[] s){
		String predictClass = passTree(Classification.root, s);
		if(actualCount.containsKey(s[Classification.target_class_Index])){
			actualCount.put(s[Classification.target_class_Index], actualCount.get(s[Classification.target_class_Index])+1);
		}else{
			actualCount.put(s[Classification.target_class_Index], 1);
		}
		if(predictCount.containsKey(predictClass)){
			predictCount.put(predictClass, predictCount.get(predictClass)+1);
		}else{
			predictCount.put(predictClass, 1);
		}
		
		System.out.println(predictClass);
		if(s[Classification.target_class_Index].equals(predictClass)){

			return true;
		}else{
			return false;
		}
	}
	
	//將testing data丟入建好的decision tree做分類，推測其target_class
	public static String passTree(Treenode node, String[] test_data){
		String returnString;
		if(node.child.size()!=0){
			boolean flag = false;  //flag set true if the testing data find its attr(a_best in that level) in child 
			//類別型pass
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
			//數值型pass
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
				return node.guess();  //在該層無法繼續parse,直接取其node的多數決class作為預測
			}
		}else {
			/*for(int i=0;i<test_data.length;i++){
				System.out.print(test_data[i]+" ");
			}
			System.out.println();*/
			//System.out.println("Finish passing. Predict target_class: "+ node.leafnode_class);
			//return "Predict: "+node.leafnode_class;
			return node.leafnode_class; //有parse完成的預測結果
		}
		return returnString;
	}
}
