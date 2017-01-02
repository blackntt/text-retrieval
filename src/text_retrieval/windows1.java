package text_retrieval;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.plaf.basic.BasicTreeUI.SelectionModelPropertyChangeHandler;
import javax.swing.tree.DefaultMutableTreeNode;

import vn.hus.nlp.tokenizer.TokenizerOptions;
import vn.hus.nlp.utils.FileIterator;
import vn.hus.nlp.utils.MUTF8FileUtility;
import vn.hus.nlp.utils.TextFileFilter;

import javax.swing.JList;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JSplitPane;

public class windows1 {

	private JFrame frmTextRetrieval;
	private final JTextField textField = new JTextField();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					windows1 window = new windows1();
					window.frmTextRetrieval.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public windows1() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmTextRetrieval = new JFrame();
		frmTextRetrieval.setTitle("Text Retrieval");
		frmTextRetrieval.setBounds(100, 100, 885, 559);
		frmTextRetrieval.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 849, 39);
		frmTextRetrieval.getContentPane().add(panel);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton btnNewButton_1 = new JButton("Count Freq for Docs");
		panel.add(btnNewButton_1);
		
		JButton btnCountFreqAll = new JButton("Count Freq All Docs");
		panel.add(btnCountFreqAll);
		
		JLabel lblNewLabel_Status = new JLabel(".....");
		lblNewLabel_Status.setBounds(127, 62, 231, 14);
		
		JButton btnNewButton = new JButton("Create vector");
		panel.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					TextProcessor.createVector();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lblNewLabel_Status.setText("Create vector>>>>>>>>>>>>>Done");
			}
		});
		btnCountFreqAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TextProcessor.countFreqInAllDocs(0.02);
				
				lblNewLabel_Status.setText("countFreqInAllDocs..................Done");
			}
		});
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				lblNewLabel_Status.setText("Processing....................");
								
				try {
					TextProcessor.tokenize_and_count_frequent_in_docs();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				lblNewLabel_Status.setText(TextProcessor.THREAD_COUNT+">>>>>>Done<<<<<<");
				
			}
		});
		frmTextRetrieval.getContentPane().setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 53, 849, 100);
		frmTextRetrieval.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnNewButton_2 = new JButton("Search");
		btnNewButton_2.setBounds(10, 11, 107, 40);
		panel_1.add(btnNewButton_2);
		textField.setBounds(127, 11, 409, 40);
		panel_1.add(textField);
		textField.setToolTipText("Query");
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Status v1.0:");
		lblNewLabel.setBounds(20, 62, 85, 14);
		panel_1.add(lblNewLabel);
		

		panel_1.add(lblNewLabel_Status);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(10, 164, 849, 345);
		frmTextRetrieval.getContentPane().add(panel_2);
				panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
				
				JSplitPane splitPane = new JSplitPane();
				panel_2.add(splitPane);
				
				JScrollPane scrollPane = new JScrollPane();
				splitPane.setLeftComponent(scrollPane);
				
				JList list = new JList();
				
						scrollPane.setViewportView(list);
						list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
						
						JScrollPane scrollPane_1 = new JScrollPane();
						splitPane.setRightComponent(scrollPane_1);
						
						JTextPane textPane = new JTextPane();
						scrollPane_1.setViewportView(textPane);
				
				list.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent arg0) {
						int idx = list.getSelectedIndex();
						if (idx != -1){
				    	   textPane.setText(TextProcessor.getDocumentContent(list.getSelectedValue().toString()));
				    	  }
				          
					}
				});
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					MUTF8FileUtility mutf8 = new MUTF8FileUtility();
					List<String> filenames = TextProcessor.processQuery(textField.getText());
					DefaultListModel listmodel = new DefaultListModel();
					TextFileFilter fileFilter = new TextFileFilter(TokenizerOptions.TEXT_FILE_EXTENSION);
					File inputDirFile = new File("results");
					File[] inputFiles = FileIterator.listFiles(inputDirFile, fileFilter);
					mutf8.createWriter("results"+File.separator+(inputFiles.length+1) + ".txt");
					mutf8.write(textField.getText()+"\n");
					for (String string : filenames) {
						listmodel.addElement(string);	
						mutf8.write(string);
						mutf8.write("\n");
					}
					mutf8.closeWriter();
					list.setModel(listmodel);
								
				} catch (InterruptedException e) {
	
					e.printStackTrace();
				}
			}
		});
		

				
	}
}
