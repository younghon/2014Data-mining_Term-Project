package c45;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c45.Classification;

public class Treenode {
	public Integer[] id;
	//public Integer[] candidate_attr;
	public HashMap<Integer,Integer> candidate_attr;
	public String attrinNode;
	public boolean visited = false;
	public double entropy = 0.0;
	public int a_best;					//在此node中最佳分類屬性
	//public Integer[] nextnode_candidate_attr;
	public HashMap<Integer,Integer> nextnode_candidate_attr;
	public ArrayList<Treenode> child= new ArrayList<Treenode>();
	
	Treenode(Integer[] id,HashMap<Integer,Integer> candidate_attr,String attrinNode){
		this.id = id;
		this.candidate_attr = candidate_attr;
		this.attrinNode = attrinNode;
	}
	
	public void dosomething(){
		if(this.growthornot()){		//判斷是否需要繼續分類
			this.setEntropy();		//算Entropy
			this.set_attr();		//找出此層最佳分類屬性
			this.growthTree();	    //長出child node
			for(int i=0;i<child.size();i++){
				child.get(i).dosomething();  //繼續對所有child node做處理
			}
		}
	}
	
	//將testing data丟入建好的decision tree做分類，推測其target_class
	public static void passTree(Treenode n, String[] test_data){
		Treenode node = n;
		if(node.child.size()!=0){
			//System.out.println("Attribute " + node.a_best + ": " + test_data[node.a_best]);
			boolean flag = false;  //flag set true if the testing data find its attr(a_best in that level) in child 
			for(int i=0;i<node.child.size();i++){
				String attr = Classification.data.get(node.child.get(i).id[0])[node.a_best];
				if(attr.contains("\"")){
					attr = attr.substring(1, attr.length()-1);
				}
				if(test_data[node.a_best].equals(attr)){
					node = node.child.get(i);
					flag = true;
					break;
				}
			}
			if(flag){		
				//System.out.println("pass");
				passTree(node, test_data);
			}else{
				System.out.print("The attribute\""+test_data[node.a_best]);
				System.out.println("\" can't pass at this level");		
			}
		}else {
			System.out.print("finish passing. Predict this data is in target_class\"");
			System.out.println(Treenode.leafClass(node) + "\"");
			
		}
	}
	
	//傳入leafnode 判斷此leaf屬於哪一個class (1.全部customer屬於同class 2.多數決)
	public static String leafClass(Treenode leafnode) {
		HashMap<String,Integer> classes = new HashMap<String,Integer>();
		for(int i=0;i<leafnode.id.length;i++){
			String id_class = Classification.data.get(leafnode.id[i])[Classification.target_class_Index];
			if (classes.containsKey(id_class)) {
				classes.put(id_class, classes.get(id_class)+1);
			}else{
				classes.put(id_class, 1);	
			}
		}
		
		if(classes.size()==1)
			return Classification.data.get(leafnode.id[0])[Classification.target_class_Index];
		else{
			List<Map.Entry<String, Integer>> list_Data =
		            new ArrayList<Map.Entry<String, Integer>>(classes.entrySet());
			Collections.sort(list_Data, new Comparator<Map.Entry<String, Integer>>(){
	            public int compare(Map.Entry<String, Integer> entry1,
	                               Map.Entry<String, Integer> entry2){
	                return (entry2.getValue() - entry1.getValue());
	            }
	        });
			return list_Data.get(0).getKey();
		}
	}
	
	public static void showTree(Treenode root){
		if(root == null) return;

		if(root.a_best!=-1)
			System.out.print(root.attrinNode + "_" + root.id.length + "_" + Classification.attributes[root.a_best]);
		else 
			System.out.print(root.attrinNode + "_" + root.id.length);
		root.visited = true;

        if(root.child.size()==0)
        	return;
        
        //for every child
        System.out.print("(");
        for(int i = 0; i < root.child.size(); i++){
        	if(i!=0)
        		System.out.print(",");
            //if childs state is not visited then recurse
            if(root.child.get(i).visited == false){
                showTree(root.child.get(i));
            }
        }
        System.out.print(")");
        
	}
	
	public void setEntropy(){
		/*印出此層的所有candidate attribute
		System.out.print("candidate_attr: ");
		for (int i = 0; i < candidate_attr.length; i++) {
			System.out.print(candidate_attr[i]+" ");
		}
		System.out.println();
		*/
		
		HashMap<String,Integer> classes = new HashMap<String,Integer>();
		for(int i=0;i<id.length;i++){
			if (classes.containsKey(Classification.data.get(id[i])[Classification.target_class_Index])) {
				classes.put(Classification.data.get(id[i])[Classification.target_class_Index],classes.get(Classification.data.get(id[i])[Classification.target_class_Index])+1);
			}else{
				classes.put(Classification.data.get(id[i])[Classification.target_class_Index],1);	
			}
		}
		for(String classN:classes.keySet()){
			//System.out.println("class "+classN+": "+classes.get(classN));
			entropy += (-1 * (classes.get(classN)/(double)id.length)) * (Math.log((classes.get(classN)/(double)id.length)) / Math.log(2));
		}
		//System.out.println("total: "+ id.length);
		//System.out.println("Entropy = "+entropy);
	}
	
	public void set_attr(){
		HashMap<Integer,Double> candidate_attr_gain= new HashMap<Integer,Double>();
		for(Integer i:candidate_attr.keySet()){
			if(candidate_attr.get(i)==1){
				candidate_attr_gain.put(i, categorical_gain_ratio(i));	//算出以各candidate attribute做分類的Gain ratio
			}else if(candidate_attr.get(i)==0){
				
			}
		}
		
		int highest_gain_attr = 0;				
		double Max = 0.0;						//挑選Gain ratio最大者為最佳分類attribute
		for(Integer i:candidate_attr_gain.keySet()){
			//System.out.println(candidate_attr[i]+": "+candidate_attr_gain[i]);
			if(candidate_attr_gain.get(i) > Max){
				highest_gain_attr = i;
				Max = candidate_attr_gain.get(i);
			}
		}
		a_best = highest_gain_attr;
		//System.out.println("highest_gain_index: " + highest_gain_index);
		//System.out.println("highest_gain_attr: " + candidate_attr[highest_gain_index]);
		/*ArrayList<Integer> nextcandidate = new ArrayList<Integer>();
		for(int i=0;i<candidate_attr.length;i++){
			if(candidate_attr[i] != a_best){
				nextcandidate.add(candidate_attr[i]);	
			}
		}*/
		//刪除此層分類最佳分類的attribute傳至child node
		nextnode_candidate_attr = new HashMap<Integer, Integer>(candidate_attr);
		nextnode_candidate_attr.remove(a_best);
	}
	
	public double categorical_gain_ratio(int attr_index){
		HashMap<String,HashMap<String, Integer>> tmp = new HashMap<String,HashMap<String, Integer>>();
		for(int i=0;i<id.length;i++){
			String id_attr = Classification.data.get(id[i])[attr_index];
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			if(tmp.containsKey(id_attr)){
				if (tmp.get(id_attr).containsKey(id_class)) {
					tmp.get(id_attr).put(id_class, tmp.get(id_attr).get(id_class)+1);
				}else {
					tmp.get(id_attr).put(id_class, 1);
				}
			}else{
				HashMap<String, Integer> addin = new HashMap<String, Integer>();
				addin.put(id_class, 1);
				tmp.put(id_attr, addin);
			}
		}
		
		ArrayList<Values> values = new ArrayList<Values>();
		for(String s: tmp.keySet()){
			int classesCount = 0;
			for(String ss: tmp.get(s).keySet()){
				classesCount += tmp.get(s).get(ss);						
			}

			double I = 0.0;
			for(String ss : tmp.get(s).keySet()){
				I += (-1 * (tmp.get(s).get(ss)/(double)classesCount)) * (Math.log((tmp.get(s).get(ss)/(double)classesCount)) / Math.log(2));
			}
			
			values.add(new Values(s, I, classesCount));					
		}
		
		int totalClasses = id.length;
		double E = 0.0;
		double IV = 0.0;		//Information Value
		for(Values v : values){
			IV += (-1 * (v.classesCount/(double)totalClasses) * (Math.log((v.classesCount/(double)totalClasses)) / Math.log(2)));
			E += (v.classesCount/(double)totalClasses) * v.I;
		}
		//System.out.println("IV =" + IV);
		//System.out.println("E =" + E);
		
		return (entropy - E) / IV;
	}
	public boolean growthornot(){
		boolean growth_or_not = true;
		Set<String> tmp = new HashSet<String>();
		for(int i=0;i<id.length;i++){
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			tmp.add(id_class);
		}
		if (tmp.size()==1) {			//在node中的所有customer都屬於
			growth_or_not = false;
			attrinNode = tmp.toArray(new String[tmp.size()])[0];
			a_best = -1;
		}
		if(candidate_attr.size()==0){	//已經沒有attribute可以用來分類了
			growth_or_not = false;
		}
		return growth_or_not;
	}
	
	public void growthTree(){
		HashMap<String,Integer> tmp = new HashMap<String,Integer>();
		for(int i=0;i<id.length;i++){
			String id_attr = Classification.data.get(id[i])[a_best];
			if(tmp.containsKey(id_attr)){				//依照此node中的最佳分類attribute分出幾類
				tmp.put(id_attr, tmp.get(id_attr)+1);
			}else{
				tmp.put(id_attr, 1);
			}
		}
		
		for (String s: tmp.keySet()) {
			ArrayList<Integer> addintonextId = new ArrayList<Integer>();
			for(int i=0;i<id.length;i++){				//相同attribute的customer記錄起來傳至child node
				if(Classification.data.get(id[i])[a_best].equals(s)){
					addintonextId.add(id[i]);
				}
			}
			Integer[] nextId = addintonextId.toArray(new Integer[addintonextId.size()]);
			Treenode node = new Treenode(nextId,nextnode_candidate_attr, s);
			child.add(node);
		}
	}
	
	
}
