package jdt11;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Console extends JFrame {

	private JPanel contentPane;
	private JTextArea textArea;

	/**
	 * Create the frame.
	 */
	public Console() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(750, 50, 700, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setBounds(0, 0, 700, 378);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.LIGHT_GRAY);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));	
		textArea.setEditable(false);
		textArea.setAutoscrolls(true);
		JScrollPane textArea_scroller = new JScrollPane(textArea);
		textArea_scroller.setBounds(0,0,700,378);
		textArea_scroller.setBackground(Color.black);
		contentPane.add(textArea_scroller);
	}
	
	public void init(){
		//this.pack();
		this.setVisible(true);
	}
	
	public void write(String line){
		String text = textArea.getText() + line + "\n";
		textArea.setText(text);
	}
	
	public void refresh(){
		textArea.setText("");
	}
}
