package c45;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import c45.Classification;

public class Treenode {
	public Integer[] id;
	public String attrinNode;
	public boolean visited = false;
	public double entropy = 0.0;
	public int a_best;					//在此node中最佳分類屬性同一個class
	public int best_list_index;
	List<Feature> candidate_feature = new ArrayList<Feature>();
	List<Feature> nextnode_candidate_feature = new ArrayList<Feature>();
	public ArrayList<Treenode> child= new ArrayList<Treenode>();
	
	Treenode(Integer[] id,List<Feature> candidate_feature,String attrinNode){
		this.id = id;
		this.candidate_feature = candidate_feature;
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
			System.out.print(root.attrinNode + "_" + root.id.length + "_" + Classification.Header.get(root.a_best));
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
		for(int i=0; i<candidate_feature.size(); i++){
			if(!candidate_feature.get(i).isContinuous()){
				//算出以各candidate attribute做分類的Gain ratio
				candidate_attr_gain.put(candidate_feature.get(i).getFeature_index(), categorical_gain_ratio(candidate_feature.get(i)));
			}else if(candidate_feature.get(i).isContinuous()){
				candidate_attr_gain.put(candidate_feature.get(i).getFeature_index(), continuous_gain_ratio(candidate_feature.get(i)));
			}
		}
					
		double Max = 0.0;						//挑選Gain ratio最大者為最佳分類attribute
		for(Integer i:candidate_attr_gain.keySet()){
			//System.out.println("candidate_attr"+i+ " gain ratio: "+candidate_attr_gain.get(i));
			if(candidate_attr_gain.get(i) > Max){
				a_best = i;
				Max = candidate_attr_gain.get(i);
			}
		}
		//System.out.println("highest_gain_index: " + highest_gain_index);
		//System.out.println("highest_gain_attr: " + candidate_attr[highest_gain_index]);
		/*ArrayList<Integer> nextcandidate = new ArrayList<Integer>();
		for(int i=0;i<candidate_attr.length;i++){
			if(candidate_attr[i] != a_best){
				nextcandidate.add(candidate_attr[i]);	
			}
		}*/
		//刪除此層分類最佳分類的attribute傳至child node
		nextnode_candidate_feature = new ArrayList<Feature>(candidate_feature);
		for(int i=0;i<candidate_feature.size();i++){
			if(candidate_feature.get(i).getFeature_index()==a_best){
				best_list_index=i;
				nextnode_candidate_feature.remove(i);
				break;
			}
		}
	}
	
	public double continuous_gain_ratio(Feature feature){
		int attr_index = feature.getFeature_index();
		List<Pair> tmp = new ArrayList<Pair>();
		for(int i=0;i<id.length;i++){
			Integer id_attr = Integer.parseInt(Classification.data.get(id[i])[attr_index]);
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			tmp.add(new Pair(id_attr, id_class));
		}
		
		Collections.sort(tmp, new Comparator<Pair>() {
		            public int compare(Pair o1, Pair o2) {
		                return o1.getId_attr()-o2.getId_attr();
		            }
		});
		
		double max_I=0.0;
		int compare = tmp.get(0).getId_attr();
		for(int i=0;i<tmp.size();i++){
			if(compare<tmp.get(i).getId_attr()){
				double e1=0.0;
				double e2=0.0;
				double e=0.0;
				double IV=0.0;
				HashMap<String,Integer> count1 = new HashMap<String,Integer>();
				HashMap<String,Integer> count2 = new HashMap<String,Integer>();
				int count1_total=0;
				int count2_total=0;
				for(int j=0;j<i;j++){
					count1_total++;
					if(count1.containsKey(tmp.get(j).getId_class())){
						count1.put(tmp.get(j).getId_class(), count1.get(tmp.get(j).getId_class())+1);
					}else{
						count1.put(tmp.get(j).getId_class(),1);
					}
				}
				for(int j=i;j<tmp.size();j++){
					count2_total++;
					if(count2.containsKey(tmp.get(j).getId_class())){
						count2.put(tmp.get(j).getId_class(), count2.get(tmp.get(j).getId_class())+1);
					}else{
						count2.put(tmp.get(j).getId_class(),1);
					}
				}
				for(String x:count1.keySet()){
					e1 += (-1 * (count1.get(x)/(double)count1_total)) * (Math.log((count1.get(x)/(double)count1_total)) / Math.log(2));
				}
				for(String x:count2.keySet()){
					e2 += (-1 * (count2.get(x)/(double)count2_total)) * (Math.log((count2.get(x)/(double)count2_total)) / Math.log(2));
				}
				e=(count1_total/(double)(count1_total+count2_total))*e1+(count2_total/(double)(count1_total+count2_total))*e2;
				IV = (-1 * (count1_total/(double)(count1_total+count2_total)) * (Math.log((count1_total/(double)(count1_total+count2_total))) / Math.log(2)))
						+(-1 * (count2_total/(double)(count1_total+count2_total)) * (Math.log((count2_total/(double)(count1_total+count2_total))) / Math.log(2)));
				
				if(max_I < (entropy-e)/IV){
					max_I = (entropy-e)/IV;
					feature.setSplit_value(compare);
				}
				compare = tmp.get(i).getId_attr();
			}
		}	
		return max_I;
	}
	
	public double categorical_gain_ratio(Feature feature){
		int attr_index = feature.getFeature_index();
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
		HashMap<String,Integer> tmp = new HashMap<String,Integer>();
		for(int i=0;i<id.length;i++){
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			if(tmp.containsKey(id_class)){
				tmp.put(id_class, tmp.get(id_class)+1);
			}else{
				tmp.put(id_class,1);
			}
		}
		if (tmp.size()==1) {			//在該node中的所有customer都屬於
			attrinNode = "target_class: " + Classification.data.get(id[0])[Classification.target_class_Index];
			a_best = -1;
			return false;
		}
		if(candidate_feature.size()==0){	//已經沒有attribute可以用來分類了(取數量最多的當作預測class)
			int maxV = -1;
			Iterator keys = tmp.keySet().iterator();
			while(keys.hasNext()){
				   Object key = keys.next();
				   Integer value = tmp.get(key);
				   if(value > maxV){
					   maxV=value;
					   attrinNode = "target_class: "+ key.toString() +" (guess)";
				   }
			}
			a_best = -1;
			return false;
		}
		return true;
	}
	
	public void growthTree(){
		//System.out.println("a_best: "+ a_best);
		if(!candidate_feature.get(best_list_index).isContinuous()){
			System.out.println("Use categorical attribute "+ candidate_feature.get(best_list_index).getName() +" to growth");
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
				Treenode node = new Treenode(nextId,nextnode_candidate_feature, s);
				//System.out.println(candidate_feature.get(best_list_index).getName()+" "+s+": "+nextId.length);
				child.add(node);
			}
		}else if(candidate_feature.get(best_list_index).isContinuous()){
			System.out.println("Use continuous attribute "+ candidate_feature.get(best_list_index).getName() +" to growth");
			if(candidate_feature.get(best_list_index).getSplit_value()==-1){
				System.out.println("Wrong: Use continuous attribute but splilt_index is not set.");
			}else{
				ArrayList<Integer> addinto1Id = new ArrayList<Integer>();
				ArrayList<Integer> addinto2Id = new ArrayList<Integer>();
				for(int i=0;i<id.length;i++){				//<=split_value一類; >split_value一類
					if(Integer.parseInt(Classification.data.get(id[i])[a_best])<=candidate_feature.get(best_list_index).getSplit_value()){
						addinto1Id.add(id[i]);
					}else{
						addinto2Id.add(id[i]);
					}
				}
				Integer[] next1Id = addinto1Id.toArray(new Integer[addinto1Id.size()]);
				Integer[] next2Id = addinto2Id.toArray(new Integer[addinto2Id.size()]);
				Treenode node1 = new Treenode(next1Id,nextnode_candidate_feature, candidate_feature.get(best_list_index).getName()+"<="+candidate_feature.get(best_list_index).getSplit_value());
				Treenode node2 = new Treenode(next1Id,nextnode_candidate_feature, candidate_feature.get(best_list_index).getName()+">"+candidate_feature.get(best_list_index).getSplit_value());
				System.out.println(candidate_feature.get(best_list_index).getName()+"<="+candidate_feature.get(best_list_index).getSplit_value()+": "+next1Id.length);
				System.out.println(candidate_feature.get(best_list_index).getName()+">"+candidate_feature.get(best_list_index).getSplit_value()+": "+next2Id.length);
				child.add(node1);
				child.add(node2);
			}
		}
		
		
	}
	
	
}
