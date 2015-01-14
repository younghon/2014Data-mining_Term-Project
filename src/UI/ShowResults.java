package UI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.Font;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTree;
import javax.swing.JTextArea;

import c45.Treenode;
import javax.swing.ScrollPaneConstants;


public class ShowResults extends JFrame {

	private JPanel contentPane;
	private DefaultListModel leftListModel;
	private DefaultListModel rightListModel;
	static DefaultMutableTreeNode Top;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowResults frame = new ShowResults("", null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ShowResults(String info, Treenode root) {
		setTitle("DMFinal");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 610, 560);
	
	/*最上層File 選單*/	
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		//File
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		//Open File
		JMenuItem mntmNewMenuItem = new JMenuItem("Open File");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "123");
			}
		});
		mnFile.add(mntmNewMenuItem);
		
		//Exit
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Exit");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmNewMenuItem_1);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Top = new DefaultMutableTreeNode("root");
		createNodes(Top, root);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(45, 51, 505, 249);
		contentPane.add(scrollPane_1);
		JTree Show_DecisionTree = new JTree(Top);
		scrollPane_1.setViewportView(Show_DecisionTree);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(45, 343, 505, 157);
		contentPane.add(scrollPane);
		
		JTextArea txtrTotalTime = new JTextArea();
		scrollPane.setViewportView(txtrTotalTime);
		txtrTotalTime.setText(info);
		
		JLabel leftLabel = new JLabel("Decision Tree");
		leftLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftLabel.setBounds(10, 20, 162, 31);
		contentPane.add(leftLabel);
		
		JLabel rightLabel = new JLabel("Information");
		rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		rightLabel.setBounds(31, 316, 116, 31);
		contentPane.add(rightLabel);
		
		leftListModel = new DefaultListModel();
		
		rightListModel = new DefaultListModel();


		
	}
	
	private static void createNodes(DefaultMutableTreeNode top, Treenode root) {
        DefaultMutableTreeNode newTop = null;
        
        for(int i=0;i<root.child.size();i++){
        	if(root.child.get(i).a_best == -1){	
        		newTop = new DefaultMutableTreeNode(root.candidate_feature.get(root.best_list_index).getName() + "   (" + root.child.get(i).attrinNode + "): " + root.child.get(i).leafnode_class);
        	}else{
        		newTop = new DefaultMutableTreeNode(root.candidate_feature.get(root.best_list_index).getName() + "   (" + root.child.get(i).attrinNode + ")");
        	}
        	top.add(newTop);
        	createNodes(newTop, root.child.get(i));
        }
    }
}
