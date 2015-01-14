package UI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import c45.Classification;


public class ChooseConFrame extends JFrame {

	private JPanel contentPane;
	private DefaultListModel leftListModel = new DefaultListModel();
	private DefaultListModel rightListModel = new DefaultListModel();
	private JList leftList = new JList(leftListModel);
	private JList rightList = new JList(rightListModel);
	private JTextField targetText;
	
	// argument
	private String trainingFile = "";
	private String testingFile = "";
	private List<String> selectedAttribute = new ArrayList<String>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChooseConFrame frame = new ChooseConFrame(null, null, new ArrayList<String>());
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
	public ChooseConFrame(String trainingFile, String testingFile, List<String> selectedAttribute) {
		
		// argument passing
		this.trainingFile = trainingFile;
		this.testingFile = testingFile;
		this.selectedAttribute = selectedAttribute;
		
		setResizable(false);
		setTitle("Choose Continous Attributes");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 610, 560);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu chooseConFrameFileMenu = new JMenu("File");
		menuBar.add(chooseConFrameFileMenu);
		
		JMenuItem chooseConFrameExitMenuItem = new JMenuItem("Exit");
		chooseConFrameFileMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		chooseConFrameFileMenu.add(chooseConFrameExitMenuItem);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane leftListScrollPane = new JScrollPane();
		leftListScrollPane.setBounds(50, 55, 200, 250);
		contentPane.add(leftListScrollPane);
		
		leftListScrollPane.setViewportView(leftList);
		
		JScrollPane rightListScrollPane = new JScrollPane();
		rightListScrollPane.setBounds(350, 55, 200, 250);
		contentPane.add(rightListScrollPane);
		
		rightListScrollPane.setViewportView(rightList);
		
		JButton moveAllButton = new JButton(">>");
		moveAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveAllItem();
			}
		});
		moveAllButton.setFont(new Font("新細明體", Font.BOLD, 12));
		moveAllButton.setBounds(275, 59, 50, 50);
		contentPane.add(moveAllButton);
		
		JButton moveOneButton = new JButton(">");
		moveOneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveOneItem();
			}
		});
		moveOneButton.setFont(new Font("新細明體", Font.BOLD, 12));
		moveOneButton.setBounds(275, 127, 50, 50);
		contentPane.add(moveOneButton);
		
		JButton removeOneButton = new JButton("<");
		removeOneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeOneItem();
			}
		});
		removeOneButton.setFont(new Font("新細明體", Font.BOLD, 12));
		removeOneButton.setBounds(275, 195, 50, 50);
		contentPane.add(removeOneButton);
		
		JButton removeAllButton = new JButton("<<");
		removeAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAllItem();
			}
		});
		removeAllButton.setFont(new Font("新細明體", Font.BOLD, 12));
		removeAllButton.setBounds(275, 255, 50, 50);
		contentPane.add(removeAllButton);
		
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Boolean> isAttributeContinous = new HashMap<String, Boolean>();
				
				
				while(!leftListModel.isEmpty()){
					isAttributeContinous.put((String)leftListModel.get(0), true);
					leftListModel.remove(0);
				}
				while(!rightListModel.isEmpty()){
					isAttributeContinous.put((String)rightListModel.get(0), false);
					rightListModel.remove(0);
				}
				
				/* call ID3 or C4.5 */
				c45.Classification c45 = new Classification(ChooseConFrame.this.trainingFile, ChooseConFrame.this.testingFile, targetText.getText().trim(), isAttributeContinous);
				c45.main(null);
				final String result = c45.getResult();
				final c45.Treenode c45Root = c45.getRoot();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ShowResults frame = new ShowResults(result,c45Root);
							frame.setVisible(true);
							setVisible(false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
			}
		});
		doneButton.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		doneButton.setBounds(409, 431, 141, 57);
		contentPane.add(doneButton);
		
		JLabel leftLabel = new JLabel("Continous Attributes");
		leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		leftLabel.setBounds(50, 15, 200, 31);
		contentPane.add(leftLabel);
		
		JLabel rightLabel = new JLabel("Not Continous Attributes");
		rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		rightLabel.setBounds(350, 15, 200, 31);
		contentPane.add(rightLabel);
		
		targetText = new JTextField();
		targetText.setText("member_card");
		targetText.setBounds(192, 359, 135, 35);
		contentPane.add(targetText);
		targetText.setColumns(10);
		
		JLabel targetLabel = new JLabel("Target Attribute :");
		targetLabel.setHorizontalAlignment(SwingConstants.CENTER);
		targetLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		targetLabel.setBounds(50, 358, 157, 32);
		contentPane.add(targetLabel);

		for(int i=0;i<selectedAttribute.size();i++){
			leftListModel.addElement(selectedAttribute.get(i));
		}
		
	}
	
	private void moveOneItem(){
		if(leftListModel.isEmpty()){
			return;
		}
		int selectedIndex = leftList.getSelectedIndex();
		String selectedItem = (String)leftList.getSelectedValue();
		leftListModel.remove(selectedIndex);
		rightListModel.addElement(selectedItem);
	}
	
	private void removeOneItem(){
		if(rightListModel.isEmpty()){
			return;
		}
		int selectedIndex = rightList.getSelectedIndex();
		String selectedItem = (String)rightList.getSelectedValue();
		rightListModel.remove(selectedIndex);
		leftListModel.addElement(selectedItem);
	}
	
	private void moveAllItem(){
		while(!leftListModel.isEmpty()){
		rightListModel.addElement(leftListModel.get(0));
		leftListModel.remove(0);
		}
	}
	
	private void removeAllItem(){
		while(!rightListModel.isEmpty()){
			leftListModel.addElement(rightListModel.get(0));
			rightListModel.remove(0);
			}
	}
}
