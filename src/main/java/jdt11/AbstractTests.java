package jdt11;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AbstractTests extends JFrame {

	private JPanel contentPane;
	private TestBuilder parent;

	/**
	 * Create the frame.
	 */
	public AbstractTests(TestBuilder p, String tests) {
		parent = p;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBounds(6, 6, 588, 566);
		textArea.setText(tests);
		textArea.setAutoscrolls(true);
		//contentPane.add(textArea);
		JScrollPane scroll_pane = new JScrollPane(textArea);
		scroll_pane.setBounds(6, 6, 588, 566);
		contentPane.add(scroll_pane);
	}
	
	public void refresh(){
		//TODO
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			refresh();
		}
	}
}
