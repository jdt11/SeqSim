package jdt11;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author Jessica Turner
 * This is the window in the application which allows you to input sequences step by step,
 * followed by execution. You can also generate and execute "random" sequences, random in 
 * the sense that this allows you to randomly pick a next step. You can keep a list of these
 * sequences, load sequences from a saved file or save the sequences you have to a txt file. 
 *
 */

public class SequenceInput extends JFrame {

	private JPanel contentPane;
	private FSA machine;
	private SequenceExecuter parent;
	//This stores the options available to the user for input
	private JList options_list;
	//This stores the list of sequences that you can execute
	private JList sequence_list;
	//This stores the history of the sequence you are inputting
	private JList sequence_history_list;
	//This stores internally the sequences which we can execute
	private ArrayList<ArrayList<Triple>> sequences;

	/**
	 * Create the frame.
	 */
	public SequenceInput(SequenceExecuter p) {
		//Set the parent frame, in this case the Sequence Executer
		parent = p;
		sequences = new ArrayList<ArrayList<Triple>>();
		setBounds(700, 200, 700, 287);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		setTitle("Sequence Generator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//Ensure that if this window is closed/hidden the executer is refreshed
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				parent.refreshAll();
			}
		});
		
		options_list = new JList(new DefaultListModel());
		options_list.setBackground(Color.WHITE);
		options_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		options_list.setSelectionBackground(new Color(237, 248, 255));
		options_list.setSelectionForeground(Color.BLACK);
		//This adds a mouse click listener to update the options available to the user, that is
		//the next steps in the sequence
		options_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				//Get the selected action
				String selectedValue = (String) ((JList) e.getSource()).getSelectedValue();
				//If we have a selected action
				if(selectedValue != null){
					//update the history list to add the selection
					updateSequenceHistoryList(selectedValue);
					//Get the to state from the selected triple
					String [] split = ((String) selectedValue.subSequence(1, selectedValue.length()-1)).split(",");
					//update the options list with the next set of actions based on the to state
					updateOptionsList(split[2]);
				}
			}
		});
		JScrollPane options_list_scroller = new JScrollPane(options_list);
		options_list_scroller.setBounds(10, 6, 220, 223);
		contentPane.add(options_list_scroller);
		
		sequence_list = new JList(new DefaultListModel());
		sequence_list.setBackground(Color.WHITE);
		sequence_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sequence_list.setSelectionBackground(new Color(237, 248, 255));
		sequence_list.setSelectionForeground(Color.BLACK);
		//Here if we click on a sequence it is executed
		sequence_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				//Refresh the sequence executer for the b and sequence models
				parent.refreshB();
				parent.refreshSeq();
				//Get the selected value index
				int index = ((JList) e.getSource()).getSelectedIndex();
				if(index >= 0){
					//execute the sequence we have selected
					executeSequence(sequences.get(index));
				}
			}
		});
		JScrollPane sequence_list_scroller = new JScrollPane(sequence_list);
		sequence_list_scroller.setBounds(470, 6, 220, 223);
		contentPane.add(sequence_list_scroller);
		
		sequence_history_list = new JList(new DefaultListModel());
		sequence_history_list.setBackground(Color.WHITE);
		sequence_history_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sequence_history_list.setSelectionBackground(new Color(237, 248, 255));
		sequence_history_list.setSelectionForeground(Color.BLACK);
		JScrollPane sequence_history_list_scroller = new JScrollPane(sequence_history_list);
		sequence_history_list_scroller.setBounds(240, 6, 220, 223);
		contentPane.add(sequence_history_list_scroller);
		
		//this allows us to run the sequence we have just inputted
		JButton btnRunSequence = new JButton("Run");
		btnRunSequence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runSequence();
			}
		});
		btnRunSequence.setBounds(10, 232, 117, 29);
		contentPane.add(btnRunSequence);
		
		//this allows us to generate a random sequence to be executed
		JButton btnGenerateRandom = new JButton("Generate Random");
		btnGenerateRandom.setBounds(121, 232, 141, 29);
		btnGenerateRandom.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				generateRandomSeq();
			}
		});
		contentPane.add(btnGenerateRandom);
		
	}

	/**
	 * In this method we first refresh the display and get a random sequence from the machine. 
	 * We then loop till we find a unique sequence. Once we have a unique sequence we can 
	 * execute the sequence and add it to the appropriate lists. 
	 */
	public void generateRandomSeq(){
		refresh();
		ArrayList<Triple> random_seq = parent.getMachine().getRandom();
		int index = 0; 
		while(hasSequence(random_seq)==true&&index < 5){
			System.out.println(hasSequence(random_seq));
			random_seq = parent.getMachine().getRandom();
			index++;
		}
		executeSequence(random_seq);
		sequences.add(random_seq);
		((DefaultListModel) sequence_list.getModel()).addElement(random_seq);
	}
	
	/**
	 * This method simply checks if we currently have this a specified sequence already stored
	 * in the list.
	 * @param seq: the sequence which we are going to check. 
	 * @return true if sequence exists in list, or false if it doesn't. 
	 */
	public boolean hasSequence(ArrayList<Triple> seq){
		for(ArrayList<Triple> s: sequences){
			if(seq.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method updates the sequence history list to show new addition.
	 * @param selectedValue: the action selected. 
	 */
	protected void updateSequenceHistoryList(String selectedValue) {
		DefaultListModel model = (DefaultListModel) sequence_history_list.getModel();
		model.addElement(selectedValue);
	}

	/**
	 * This allows us to update the next set of actions displayed to the user based on a given
	 * state. 
	 * @param state
	 */
	private void updateOptionsList(String state) {
		if(machine!=null){
			ArrayList<Triple> nextActions = machine.getActions(state);
			DefaultListModel model = (DefaultListModel) options_list.getModel();
			model.clear();
			for(Triple t: nextActions){
				model.addElement(t.toString());
			}
		}
	}

	/**
	 * This is the method for the run button. We retrieve the user-inputed sequence from the
	 * sequence history list, execute the sequence, then proceed to add this sequence to the 
	 * sequence list. 
	 */
	protected void runSequence() {
		ArrayList<Triple> sequence = retrieveSequence();
		executeSequence(sequence);
		DefaultListModel model = (DefaultListModel) sequence_list.getModel();
		model.addElement(Arrays.toString(sequence.toArray()));
		sequences.add(sequence);
		refresh();
		
	}
	
	/**
	 * @return the list of sequences we have stored internally. 
	 */
	public JList getSequenceList(){
		return sequence_list;
	}
	
	/**
	 * This method refreshes the sequence inputer display.
	 */
	private void refresh(){
		DefaultListModel model = (DefaultListModel) sequence_history_list.getModel();
		model.clear();
		model = (DefaultListModel) options_list.getModel();
		model.clear();
		ArrayList<String> temp = machine.getStarts();
		if(temp.size() > 0 && machine.hasString(machine.INITIAL, temp)){
			updateOptionsList(machine.INITIAL);
		} else if(temp.size() > 0) {
			updateOptionsList(temp.get(0));
		}
		
	}
	
	/**
	 * This method retrieves the sequence from the sequence history list so that it can be
	 * added to our sequence list for execution. 
	 * @return the retrieved sequence.
	 */
	private ArrayList<Triple> retrieveSequence(){
		ArrayList<Triple> sequence = new ArrayList<Triple>();
		DefaultListModel model = (DefaultListModel) sequence_history_list.getModel();
		for(int i = 0; i < model.size(); i++){
			String item = (String) model.getElementAt(i);
			String [] split = ((String) item.subSequence(1, item.length()-1)).split(",");
			Triple t = new Triple(split[0],split[1],split[2]);
			sequence.add(t);
		}
		return sequence;
	}
	
	/**
	 * This ensures that when the window visibility is changed that the machine is updated,
	 * the sequence executer is refreshed, and the options list is reset to the start state.
	 */
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		machine = parent.getMachine();
		parent.refreshAll();
		ArrayList<String> temp = machine.getStarts();
		if(temp.size() > 0 && machine.hasString(machine.INITIAL, temp)){
			updateOptionsList(machine.INITIAL);
		} else if(temp.size() > 0) {
			updateOptionsList(temp.get(0));
		}
	}
	
	/**
	 * A wrapper method to execute the sequence using the sequence executer, depending
	 * on the sequence and state provided. 
	 * @param seq: the sequence to be executed. 
	 */
	public void executeSequence(ArrayList<Triple> seq){
		parent.refreshAll();
		for(Triple t: seq){
			if(t.from_state.equals(machine.INITIAL)||(machine.getStarts().get(0).equals(t.from_state))){
				parent.performInitialTrace(t.toArray());
			} else {
				System.out.println("Performing trace for : " + t.toString());
				parent.performTrace(t.toArray());
			}
			//System.out.println(t.toString() + ": finished");
		}
	}
	
	/**
	 * Ensure that the sequence inputer has some sequences stored internally. 
	 * @return boolean true or false. 
	 */
	public boolean hasSequences(){
		if(sequences.size()>0){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * This method allows us to load sequences into the internal sequence list. 
	 */
	public void loadSequences(){
		DefaultListModel model = (DefaultListModel) sequence_list.getModel();
		ArrayList<Triple> seq = new ArrayList<Triple>();
		//Get the index of where to begin adding loaded sequences
		int index = (sequences.size()>0) ? sequences.size() : 0;
		//Loop through the model of the sequence list
		for(int i = index; i < model.size(); i++){
			//System.out.println(model.get(i));
			//Retrieve the current item
			String item = model.get(i).toString();
			seq = new ArrayList<Triple>();
			//if the item has a length greater than 2 it means it is a non-empty sequence
			if(item.length()>2){
				//remove starting characters
				item = item.substring(2, item.length()-2);
				//split sequence into triples
				String [] split = item.split("\\), \\(");
				//for each triple
				for(String s: split){
					//split the triple
					String [] triple_split = s.split(",");
					//create new triple
					Triple t = new Triple(triple_split[0],triple_split[1],triple_split[2]);
					//add the new triple to the current sequence
					seq.add(t);
				}
			}
			//add the sequence to the internal memory list
			sequences.add(seq);
		}
	}
}
