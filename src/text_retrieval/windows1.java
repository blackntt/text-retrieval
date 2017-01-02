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
		frmTextRetrieval.setBounds(100, 100, 503, 347);
		frmTextRetrieval.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel lblNewLabel = new JLabel("Status:");
		lblNewLabel.setBounds(177, 49, 46, 14);
		frmTextRetrieval.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_Status = new JLabel(".....");
		lblNewLabel_Status.setBounds(222, 49, 231, 14);
		frmTextRetrieval.getContentPane().add(lblNewLabel_Status);
		
		JButton btnNewButton_1 = new JButton("Count Freq for Docs");
		btnNewButton_1.setBounds(10, 11, 161, 23);
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
		frmTextRetrieval.getContentPane().add(btnNewButton_1);
		
		JButton btnCountFreqAll = new JButton("Count Freq All Docs");
		btnCountFreqAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TextProcessor.countFreqInAllDocs(/*"news_output_key_value", "words_count.txt",*/0.02);
				
				lblNewLabel_Status.setText("countFreqInAllDocs..................Done");
			}
		});
		btnCountFreqAll.setBounds(177, 11, 133, 23);
		frmTextRetrieval.getContentPane().add(btnCountFreqAll);
		
		JButton btnNewButton = new JButton("Create vector");
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
		btnNewButton.setBounds(320, 11, 133, 23);
		frmTextRetrieval.getContentPane().add(btnNewButton);
		
		textField.setBounds(10, 79, 161, 31);
		frmTextRetrieval.getContentPane().add(textField);
		textField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 121, 200, 187);
		frmTextRetrieval.getContentPane().add(scrollPane);
						
		JList list = new JList();

		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		JButton btnNewButton_2 = new JButton("process query");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton_2.setBounds(10, 45, 161, 23);
		frmTextRetrieval.getContentPane().add(btnNewButton_2);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(222, 74, 265, 234);
		frmTextRetrieval.getContentPane().add(scrollPane_1);
		
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
		

				
	}
}
