package jdt11;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.State;
import de.prob.statespace.Trace;

public class BoundaryTests extends JFrame {

	private JPanel contentPane;
	private TestBuilder parent;
	
	/*private JComboBox stepComboBox;
	private JTextField numberBox;
	private JComboBox observationBox;
	private JTextField lowerBox;
	private JTextField upperBox;*/
	
	private JTextField obsLowerBox;
	private JTextField obsUpperBox;
	private JComboBox obSeqBox;
	private JList sequenceBox;
	
	private JTextArea textArea;
	
	/**
	 * Create the frame.
	 */
	public BoundaryTests(TestBuilder p) {
		parent = p; 
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				addBoundaryTests();
			}
		});
		setBounds(100, 100, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(6, 6, 488, 207);
		contentPane.add(tabbedPane);
		
		/*JPanel panel = new JPanel();
		tabbedPane.addTab("Loop Boundary Test", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblSelectStep = new JLabel("Select Step:");
		lblSelectStep.setBounds(214, 10, 78, 16);
		panel.add(lblSelectStep);
		
		stepComboBox = new JComboBox();
		stepComboBox.setBounds(286, 6, 175, 27);
		panel.add(stepComboBox);
		
		JLabel lblNumber = new JLabel("Number:");
		lblNumber.setBounds(270, 43, 61, 16);
		panel.add(lblNumber);
		
		numberBox = new JTextField();
		numberBox.setBounds(331, 38, 130, 26);
		panel.add(numberBox);
		numberBox.setColumns(10);
		
		JLabel lblObservation = new JLabel("Select Observation:");
		lblObservation.setBounds(163, 75, 121, 16);
		panel.add(lblObservation);
		
		observationBox = new JComboBox();
		observationBox.setBounds(286, 71, 175, 27);
		panel.add(observationBox);
		
		JLabel lblLowerBoundary = new JLabel("Lower Boundary:");
		lblLowerBoundary.setBounds(228, 105, 103, 16);
		panel.add(lblLowerBoundary);
		
		lowerBox = new JTextField();
		lowerBox.setColumns(10);
		lowerBox.setBounds(331, 100, 130, 26);
		panel.add(lowerBox);
		
		JLabel lblUpperBoundary = new JLabel("Upper Boundary:");
		lblUpperBoundary .setBounds(228, 133, 103, 16);
		panel.add(lblUpperBoundary);
		
		upperBox = new JTextField();
		upperBox.setColumns(10);
		upperBox.setBounds(331, 128, 130, 26);
		panel.add(upperBox);
		
		JButton btnAddTestCase = new JButton("Add Test Case");
		btnAddTestCase.setBounds(6, 128, 117, 29);
		btnAddTestCase.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				createLoopTest();
			}
		});
		panel.add(btnAddTestCase);
		
		JLabel lblMode = new JLabel("Mode:");
		lblMode.setBounds(6, 10, 44, 16);
		panel.add(lblMode);*/
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Sequence Boundary Test", null, panel_1, null);
		panel_1.setLayout(null);
		
		sequenceBox = new JList(new DefaultListModel());
		sequenceBox.setBackground(Color.WHITE);
		sequenceBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		sequenceBox.setSelectionBackground(new Color(237, 248, 255));
		sequenceBox.setSelectionForeground(Color.BLACK);
		
		JScrollPane stepScroller = new JScrollPane(sequenceBox);
		stepScroller.setBounds(6, 23, 156, 132);
		panel_1.add(stepScroller);
		
		JLabel lblSelectSteps = new JLabel("Select Steps:");
		lblSelectSteps.setBounds(6, 6, 84, 16);
		panel_1.add(lblSelectSteps);
		
		obSeqBox = new JComboBox();
		obSeqBox.setBounds(286, 23, 175, 27);
		panel_1.add(obSeqBox);
		
		JLabel label = new JLabel("Select Observation:");
		label.setBounds(166, 27, 121, 16);
		panel_1.add(label);
		
		JLabel label_1 = new JLabel("Lower Boundary:");
		label_1.setBounds(228, 57, 103, 16);
		panel_1.add(label_1);
		
		obsLowerBox = new JTextField();
		obsLowerBox.setColumns(10);
		obsLowerBox.setBounds(331, 52, 130, 26);
		panel_1.add(obsLowerBox);
		
		JLabel label_2 = new JLabel("Upper Boundary:");
		label_2.setBounds(228, 85, 103, 16);
		panel_1.add(label_2);
		
		obsUpperBox = new JTextField();
		obsUpperBox.setColumns(10);
		obsUpperBox.setBounds(331, 80, 130, 26);
		panel_1.add(obsUpperBox);
		
		JButton obsAddTest = new JButton("Add Test Case");
		obsAddTest.setBounds(344, 126, 117, 29);
		obsAddTest.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				createSequenceTest();
			}
		});
		panel_1.add(obsAddTest);
		setTitle("Add Boundary Tests");
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setText("");
		textArea.setAutoscrolls(true);
		//contentPane.add(textArea);
		JScrollPane scroll_pane = new JScrollPane(textArea);
		scroll_pane.setBounds(6, 206, 488, 166);
		contentPane.add(scroll_pane);
	
	}
	
	private void loadSequence(){
		String sequence = parent.getSelectedSequence().replaceAll("\\[", "").replaceAll("\\]", "");
		System.out.println("BoundaryTests has sequence: " + sequence);
		//ArrayList<String> loop_steps = new ArrayList<String>();
		String [] split = sequence.split(", ");
		DefaultListModel model = (DefaultListModel) sequenceBox.getModel();
		for(String step: split){
			String [] step_split = step.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
			/*if(step_split[0].equals(step_split[2])&&loop_steps.contains(step)==false){
				//stepComboBox.addItem(step);
				loop_steps.add(step);
			}*/
			model.addElement(step);
		}
		
	}
	
	private void loadObservations(){
		Trace trace = parent.getTrace();
		State state = trace.getCurrentState();
		Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = state.getValues().entrySet();
		for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
			//System.out.println("MATCHES = " + (entry.getValue().toString().matches("\\d+")) + " VALUE = " + entry.getValue().toString());
			if(entry.getValue().toString().matches("\\d+")){
				//observationBox.addItem(entry.getKey());
				obSeqBox.addItem(entry.getKey());
			}
		}
	}
	
	/*private JTextField obsLowerBox;
	private JTextField obsUpperBox;
	private JComboBox obSeqBox;
	private JList sequenceBox;*/
	private void createSequenceTest(){
		String lower = obsLowerBox.getText();
		String upper = obsUpperBox.getText();
		String observation = obSeqBox.getSelectedItem().toString();
		ArrayList<String> selectedSteps = (ArrayList<String>) sequenceBox.getSelectedValuesList();
		int [] selectedIndices = sequenceBox.getSelectedIndices();
		if(selectedIndices[0]!=0){
			JOptionPane.showMessageDialog(this, "Selected subsequence must begin with first step!");
			return;
		}
		String test = "";
		if(checkConsecutive(selectedIndices)&&lower!=null&&upper!=null&&observation!=null&&selectedSteps!=null
				&&lower.isEmpty()==false&&upper.isEmpty()==false&&observation.isEmpty()==false&&selectedSteps.isEmpty()==false){
			test += "onSequence(";
			for(String step: selectedSteps){
				test += step;
			}
			test += ") & (" + lower + "<=" + observation + "<=" + upper + ")";
		}
		addTest(test);
	}
	
	private boolean checkConsecutive(int [] numbers){
		if(numbers==null){
			return false;
		}
		Arrays.sort(numbers);
		for(int i = 0; i < numbers.length-1; i++){
			if(numbers[i] + 1 != numbers[i+1]){
				return false;
			}
		}
		return true;
	}
	
	/*private void createLoopTest(){
		String step = stepComboBox.getSelectedItem().toString();
		String number = numberBox.getText();
		String observation = observationBox.getSelectedItem().toString();
		String lower = lowerBox.getText();
		String upper = upperBox.getText();
		String test = "";
		if(step!=null&&number!=null&&lower!=null&&upper!=null&&
				step.isEmpty()==false&&number.isEmpty()==false&&observation.isEmpty()==false&&(lower.isEmpty()==false||upper.isEmpty()==false)){
			test += "onStep(" + step + ") & stepNo(" + number + ") & (" + lower + "<=" + observation + "<=" + upper + ")";
		}
		addTest(test);
	}*/
	
	private void addTest(String test){
		if(test!=null&&test.isEmpty()==false){
			String temp = textArea.getText() + test + "\n";
			textArea.setText(temp);
		}
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			loadSequence();
			loadObservations();
		}
	}
	
	private void addBoundaryTests(){
		String [] tests = textArea.getText().split("\\n");
		for(String t: tests){
			parent.addBoundaryTest(t);
		}
	}
}
