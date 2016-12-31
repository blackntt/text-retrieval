package text_retrieval;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
		lblNewLabel.setBounds(10, 283, 46, 14);
		frmTextRetrieval.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_Status = new JLabel(".....");
		lblNewLabel_Status.setBounds(66, 283, 387, 14);
		frmTextRetrieval.getContentPane().add(lblNewLabel_Status);
		
		JButton btnNewButton_1 = new JButton("Count Freq for Docs");
		btnNewButton_1.setBounds(10, 11, 161, 23);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				lblNewLabel_Status.setText("Processing....................");
				
				String inputDir = "news_dataset_utf8";
				String outputDir = "news_output_key_value";
				
				
				TextProcessor.tokenize_and_count_frequent_in_docs();
				
				lblNewLabel_Status.setText(">>>>>>Done<<<<<<");
				
			}
		});
		frmTextRetrieval.getContentPane().setLayout(null);
		frmTextRetrieval.getContentPane().add(btnNewButton_1);
		
		JButton btnCountFreqAll = new JButton("Count Freq All Docs");
		btnCountFreqAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TextProcessor.countFreqInAllDocs("news_output_key_value", "words_count.txt",0.02);
				
				lblNewLabel_Status.setText("countFreqInAllDocs..................Done");
			}
		});
		btnCountFreqAll.setBounds(177, 11, 133, 23);
		frmTextRetrieval.getContentPane().add(btnCountFreqAll);
		
		JButton btnNewButton = new JButton("Create vector");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TextProcessor.createVector();
				lblNewLabel_Status.setText("Create vector>>>>>>>>>>>>>Done");
			}
		});
		btnNewButton.setBounds(320, 11, 133, 23);
		frmTextRetrieval.getContentPane().add(btnNewButton);
		
		textField.setBounds(10, 79, 161, 31);
		frmTextRetrieval.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton_2 = new JButton("process query");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TextProcessor.processQuery(textField.getText());
			}
		});
		btnNewButton_2.setBounds(10, 45, 161, 23);
		frmTextRetrieval.getContentPane().add(btnNewButton_2);

				
	}
}
