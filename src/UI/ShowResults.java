package UI;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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


public class ShowResults extends JFrame {

	private JPanel contentPane;
	private DefaultListModel leftListModel;
	private DefaultListModel rightListModel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ShowResults frame = new ShowResults("");
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
	public ShowResults(String info) {
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
		
		JTree tree = new JTree();
		tree.setBounds(50, 61, 200, 249);
		contentPane.add(tree);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(350, 62, 200, 248);
		contentPane.add(scrollPane);
		
		JTextArea txtrTotalTime = new JTextArea();
		scrollPane.setViewportView(txtrTotalTime);
		txtrTotalTime.setText("Test file path : C:\\\\test.txt\r\n\r\n----------------------------\r\nTotal time : 100000ms\r\nRecall : 101%\r\nPrecision : 101%\r\nAccuracy : 101%\r\nTP rate : 101%\r\nFP rate : 101%");
		
		JLabel leftLabel = new JLabel("Decision Tree");
		leftLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftLabel.setBounds(50, 20, 200, 31);
		contentPane.add(leftLabel);
		
		JLabel rightLabel = new JLabel("Information");
		rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rightLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		rightLabel.setBounds(378, 20, 134, 31);
		contentPane.add(rightLabel);
		
		JButton btnNewButton_1 = new JButton("Testing");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnNewButton_1.setFont(new Font("微軟正黑體", Font.BOLD, 18));
		btnNewButton_1.setBounds(228, 337, 141, 57);
		contentPane.add(btnNewButton_1);
		
		leftListModel = new DefaultListModel();
		
		rightListModel = new DefaultListModel();


		
	}
}
