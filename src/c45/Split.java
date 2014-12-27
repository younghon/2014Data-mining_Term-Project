package c45;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Split {
	static String finPath = "CUSTOMER.txt";
	static BufferedWriter bw_training;
	static BufferedWriter bw_testing;
	static int customer_count = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Scanner inputScanner = new Scanner(new File(finPath));
			String headerLine = inputScanner.nextLine();
			
			while(inputScanner.hasNextLine()){
					customer_count++;
					String line = inputScanner.nextLine();
			}
			inputScanner.close();
		} catch(IOException e) {
				e.printStackTrace();
		}
		
		System.out.println("finish counting.");
		
		Set<Integer> set = new HashSet<Integer>();
		while(set.size()<(int)customer_count*0.3){
			set.add((int)(Math.random()*customer_count)+1);
		}
		
		System.out.println("finish sampling.");
		
		try {
			Scanner inputScanner = new Scanner(new File(finPath));
			String headerLine = inputScanner.nextLine();
			bw_training=new BufferedWriter(new FileWriter("training.txt"));
			bw_testing=new BufferedWriter(new FileWriter("testing.txt"));
			int count = 1;
			
			while(inputScanner.hasNextLine()){
					String line = inputScanner.nextLine();
					if(!set.contains(count)){
						bw_training.write(line); 
						bw_training.newLine();
					}else{
						bw_testing.write(line); 
						bw_testing.newLine();
					}
					count++;
			}
			inputScanner.close();
			bw_testing.close();
			bw_training.close();
			System.out.println("finish spliting.");
		} catch(IOException e) {
				e.printStackTrace();
		}
		
	}

}
