package c45;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import c45.*;

public class Treenode {
	public Integer[] id;
	public String attrinNode;
	List<Feature> candidate_feature = new ArrayList<Feature>();
	
	public double entropy = 0.0;
	public int a_best=-1;					//在此node中最佳分類屬性同一個class
	public int best_list_index=-1;
	public String leafnode_class=null;
	
	List<Feature> nextnode_candidate_feature = new ArrayList<Feature>();
	public ArrayList<Treenode> child= new ArrayList<Treenode>();

	
	Treenode(Integer[] id,List<Feature> candidate_feature,String attrinNode){
		this.id = id;
		this.candidate_feature = candidate_feature;
		this.attrinNode = attrinNode;
	}
	
	public void dosomething(){
		if(this.growthornot()){		//判斷是否需要繼續分類
			this.setEntropy();		//算target class亂度
			if(this.set_attr()){		//找出此層最佳分類屬性
				this.growthTree();	    //長出child node
				for(int i=0;i<child.size();i++){
					child.get(i).dosomething();  //繼續對所有child node做處理
				}
			}
		}
	}
	
	public boolean growthornot(){
		HashMap<String,Integer> targetclassName = new HashMap<String,Integer>();
		//記錄每筆資料共構成幾類target class
		for(int i=0;i<id.length;i++){
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			if(targetclassName.containsKey(id_class)){
				targetclassName.put(id_class, targetclassName.get(id_class)+1);
			}else{
				targetclassName.put(id_class,1);
			}
		}
		if (targetclassName.size()==1) {			//在該node中的所有customer都屬於同一個class
			leafnode_class = Classification.data.get(id[0])[Classification.target_class_Index];
			a_best = -1;
			return false;
		}
		
		if(candidate_feature.size()==0){	//已經沒有attribute可以用來分類了(取數量最多的當作預測class)
			int maxV = -1;
			Iterator<String> keys = targetclassName.keySet().iterator();
			while(keys.hasNext()){
				   Object key = keys.next();
				   Integer value = targetclassName.get(key);
				   if(value > maxV){
					   maxV=value;
					   leafnode_class = key.toString();
				   }
			}
			a_best = -1;
			return false;
		}
		return true;
	}
	
	public void setEntropy(){
		HashMap<String,Integer> targetclassName = new HashMap<String,Integer>();
		for(int i=0;i<id.length;i++){
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			if (targetclassName.containsKey(id_class)){
				targetclassName.put(id_class,targetclassName.get(id_class)+1);
			}else{
				targetclassName.put(id_class,1);	
			}
		}
		for(String classN:targetclassName.keySet()){
			//System.out.println("class "+classN+": "+classes.get(classN));
			entropy += (-1 * (targetclassName.get(classN)/(double)id.length)) * (Math.log((targetclassName.get(classN)/(double)id.length)) / Math.log(2));
		}
		//System.out.println("total: "+ id.length);
		//System.out.println("Entropy = "+entropy);
	}
	
	public boolean set_attr(){
		HashMap<Integer,Double> candidate_attr_gain= new HashMap<Integer,Double>();
		for(int i=0; i<candidate_feature.size(); i++){
			//算出以各candidate attribute做分類的Gain ratio
			if(!candidate_feature.get(i).isContinuous()){
				candidate_attr_gain.put(candidate_feature.get(i).getFeature_index(), categorical_gain_ratio(candidate_feature.get(i)));
			}else if(candidate_feature.get(i).isContinuous()){
				candidate_attr_gain.put(candidate_feature.get(i).getFeature_index(), continuous_gain_ratio(candidate_feature.get(i)));
			}
		}
		
		//挑選Gain ratio最大者為最佳分類attribute			
		double Max = -1;						
		for(Integer i:candidate_attr_gain.keySet()){
			System.out.println("candidate_attr"+i+ " gain ratio: "+candidate_attr_gain.get(i));
			if(candidate_attr_gain.get(i) > Max){
				a_best = i;
				Max = candidate_attr_gain.get(i);
			}
		}
		
		if(a_best==-1){			//所有attribute的gain ratio = NaN; 不繼續長樹
			leafnode_class = guess();
			return false;
		}

		//刪除此層分類最佳分類的attribute傳至child node
		nextnode_candidate_feature = new ArrayList<Feature>();
		for(int i=0;i<candidate_feature.size();i++){
			if(candidate_feature.get(i).getFeature_index() == a_best){
				best_list_index=i;
			}else 
				nextnode_candidate_feature.add(new Feature(candidate_feature.get(i).getName(), candidate_feature.get(i).isContinuous()));
		}
		
		
		//debugging 印出candidate_feature,此node最佳分類a_best,以及nextnode_candidate_feature
		/*System.out.print("candidate_feature: ");
		for(int i=0;i<candidate_feature.size();i++){
			System.out.print(candidate_feature.get(i).getFeature_index()+" ");
		}
		System.out.println("\na_best: "+a_best);
		System.out.print("nextnode_candidate_feature: ");
		for(int i=0;i<nextnode_candidate_feature.size();i++){
			System.out.print(nextnode_candidate_feature.get(i).getFeature_index()+" ");
		}
		System.out.println();*/
		
		return true;
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
		
		double max_Gain=-1;
		int compare = tmp.get(0).getId_attr();
		for(int i=0;i<tmp.size();i++){
			if(tmp.get(i).getId_attr()==tmp.get(tmp.size()-1).getId_attr()){
				break;
			}
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
				
				if(max_Gain < (entropy-e)/IV){
					max_Gain = (entropy-e)/IV;
					feature.setSplit_value(compare);
				}
				compare = tmp.get(i).getId_attr();		
			}
		}	
		return max_Gain;
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
				HashMap<String, Integer> addIn = new HashMap<String, Integer>();
				addIn.put(id_class, 1);
				tmp.put(id_attr, addIn);
			}
		}
		
		ArrayList<AttrKind> kinds = new ArrayList<AttrKind>();
		for(String attrKind: tmp.keySet()){
			int classesCount = 0;
			for(String targetclassName: tmp.get(attrKind).keySet()){
				classesCount += tmp.get(attrKind).get(targetclassName);						
			}

			double e = 0.0;
			for(String targetclassName : tmp.get(attrKind).keySet()){
				e += (-1 * (tmp.get(attrKind).get(targetclassName)/(double)classesCount)) * (Math.log((tmp.get(attrKind).get(targetclassName)/(double)classesCount)) / Math.log(2));
			}
			
			kinds.add(new AttrKind(attrKind, e, classesCount));					
		}
		
		int totalClasses = id.length;
		double E = 0.0;
		double IV = 0.0;		//Information Value
		for(AttrKind kind : kinds){
			IV += (-1 * (kind.classesCount/(double)totalClasses) * (Math.log((kind.classesCount/(double)totalClasses)) / Math.log(2)));
			E += (kind.classesCount/(double)totalClasses) * kind.value_entropy;
		}
		//System.out.println("IV =" + IV);
		//System.out.println("E =" + E);
		
		return (entropy - E) / IV;
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
			for (String attrKind: tmp.keySet()) {
				ArrayList<Integer> addintonextId = new ArrayList<Integer>();
				for(int i=0;i<id.length;i++){				//相同attribute的customer記錄起來傳至child node
					if(Classification.data.get(id[i])[a_best].equals(attrKind)){
						addintonextId.add(id[i]);
					}
				}
				Integer[] nextId = addintonextId.toArray(new Integer[addintonextId.size()]);
				Treenode node = new Treenode(nextId,nextnode_candidate_feature, attrKind);
				//System.out.println(candidate_feature.get(best_list_index).getName()+" "+attrKind+": "+nextId.length);
				child.add(node);
			}
		}else if(candidate_feature.get(best_list_index).isContinuous()){
			System.out.println("Use continuous attribute "+ candidate_feature.get(best_list_index).getName() +" to growth");
			if(candidate_feature.get(best_list_index).getSplit_value()==-1){
				System.out.println("Wrong: Use continuous attribute but split_index has not been set.");
			}else{
				ArrayList<Integer> addinto1Id = new ArrayList<Integer>();
				ArrayList<Integer> addinto2Id = new ArrayList<Integer>();
				int compareValue = candidate_feature.get(best_list_index).getSplit_value();
				for(int i=0;i<id.length;i++){				//<=split_value一類; >split_value一類
					String id_attr = Classification.data.get(id[i])[a_best];
					if(Integer.parseInt(id_attr) <= compareValue){
						addinto1Id.add(id[i]);
					}else if(Integer.parseInt(id_attr)>compareValue){
						addinto2Id.add(id[i]);
					}
				}
				Integer[] next1Id = addinto1Id.toArray(new Integer[addinto1Id.size()]);
				Integer[] next2Id = addinto2Id.toArray(new Integer[addinto2Id.size()]);
				Treenode node1 = new Treenode(next1Id, nextnode_candidate_feature, candidate_feature.get(best_list_index).getName()+"<="+compareValue);
				Treenode node2 = new Treenode(next2Id, nextnode_candidate_feature, candidate_feature.get(best_list_index).getName()+">"+compareValue);
				//System.out.println(candidate_feature.get(best_list_index).getName()+"<="+candidate_feature.get(best_list_index).getSplit_value()+": "+next1Id.length);
				//System.out.println(candidate_feature.get(best_list_index).getName()+">"+candidate_feature.get(best_list_index).getSplit_value()+": "+next2Id.length);
				child.add(node1);
				child.add(node2);
			}
		}	
	}
	
	//回傳該node最多的target class Name 
	public String guess(){
		String Guess=null;
		HashMap<String,Integer> targetclassCount = new HashMap<String,Integer>();
		for(int i=0;i<id.length;i++){
			String id_class = Classification.data.get(id[i])[Classification.target_class_Index];
			if(targetclassCount.containsKey(id_class)){
				targetclassCount.put(id_class, targetclassCount.get(id_class)+1);
			}else{
				targetclassCount.put(id_class,1);
			}
		}
		
		int maxV = -1;
		Iterator<String> keys = targetclassCount.keySet().iterator();
		while(keys.hasNext()){
			   Object key = keys.next();
			   Integer value = targetclassCount.get(key);
			   if(value > maxV){
				   maxV = value;
				   Guess = key.toString();
			   }
		}		
		return Guess;
	}
	
}
