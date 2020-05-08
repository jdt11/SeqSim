package jdt11;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MachineBuilder extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private JList state_list;
	private JList alphabet_list;
	private JList transition_list;
	private JLabel lblStart;
	private JLabel lblFinal;
	private boolean changesMade;
	private FSA machine;
	private JTextField stateNameBox;
	private JTextField letterBox;
	private JComboBox fromDropBox;
	private JComboBox letterDropBox;
	private JComboBox toDropBox;
	private JComboBox startDropBox;
	private JComboBox finalDropBox;
	private JComboBox actionDropBox;
	private JComboBox existingDropBox;
	private JTabbedPane tabbedPane;
	
	/**
	 * Create the frame.
	 */
	public MachineBuilder(SequenceExecuter p) {
		changesMade = false;
		parent = p;
		if(parent.getMachine()==null){
			parent.setMachine(new FSA());
		}
		machine = parent.getMachine().copy();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 669, 548);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblStates = new JLabel("States:");
		lblStates.setBounds(6, 282, 61, 16);
		contentPane.add(lblStates);
		
		state_list = new JList(new DefaultListModel());
		state_list.setBackground(Color.WHITE);
		state_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		state_list.setSelectionBackground(new Color(237, 248, 255));
		state_list.setSelectionForeground(Color.BLACK);
		state_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
					displayState();
			}
		});
		JScrollPane stateScrollPane = new JScrollPane(state_list);
		stateScrollPane.setBounds(6, 298, 200, 200);
		contentPane.add(stateScrollPane);
		
		alphabet_list = new JList(new DefaultListModel());
		alphabet_list.setBackground(Color.WHITE);
		alphabet_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		alphabet_list.setSelectionBackground(new Color(237, 248, 255));
		alphabet_list.setSelectionForeground(Color.BLACK);
		alphabet_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				displayLetter();
			}
		});
		JScrollPane alphabetScrollPane = new JScrollPane(alphabet_list);
		alphabetScrollPane.setBounds(218, 298, 100, 200);
		contentPane.add(alphabetScrollPane);
		
		transition_list = new JList(new DefaultListModel());
		transition_list.setBackground(Color.WHITE);
		transition_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		transition_list.setSelectionBackground(new Color(237, 248, 255));
		transition_list.setSelectionForeground(Color.BLACK);
		transition_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				displayTransition();
			}
		});
		JScrollPane transitionScrollPane = new JScrollPane(transition_list);
		transitionScrollPane.setBounds(330, 298, 200, 200);
		contentPane.add(transitionScrollPane);
		
		JLabel lblTransitions = new JLabel("Transitions:");
		lblTransitions.setBounds(330, 282, 100, 16);
		contentPane.add(lblTransitions);
		
		JLabel lblStartState = new JLabel("Start state:");
		lblStartState.setBounds(542, 298, 93, 16);
		contentPane.add(lblStartState);
		
		lblStart = new JLabel("");
		lblStart.setHorizontalAlignment(SwingConstants.LEFT);
		lblStart.setBackground(Color.WHITE);
		lblStart.setBounds(542, 316, 121, 16);
		contentPane.add(lblStart);
		
		JLabel lblFinalState = new JLabel("Final state:");
		lblFinalState.setBounds(542, 335, 93, 16);
		contentPane.add(lblFinalState);
		
		lblFinal = new JLabel("");
		lblFinal.setBounds(542, 353, 121, 16);
		contentPane.add(lblFinal);
		
		JLabel lblAlphabet = new JLabel("Alphabet:");
		lblAlphabet.setBounds(218, 282, 61, 16);
		contentPane.add(lblAlphabet);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if(index==1||index==2){
					populateBoxes();
				}
			}
		});
		tabbedPane.setBounds(6, 6, 657, 264);
		
		contentPane.add(tabbedPane);
		
		JPanel states_panel = new JPanel();
		states_panel.setBounds(0,0, 100,100);
		tabbedPane.addTab("Edit States & Alphabet", null, states_panel, null);
		
		JLabel lblStateName = new JLabel("State Name:");
		states_panel.add(lblStateName);
		lblStateName.setLabelFor(stateNameBox);
		
		stateNameBox = new JTextField();
		states_panel.add(stateNameBox);
		stateNameBox.setColumns(10);
		
		final JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addState();
			}
		});
		
		JLabel lblAddExistingWidget = new JLabel("Existing Widget:");
		states_panel.add(lblAddExistingWidget);
		
		existingDropBox = new JComboBox();
		existingDropBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.SELECTED){
					String item = existingDropBox.getSelectedItem().toString();
					if(!item.equals("")){
						stateNameBox.setText(item);
					}
				}
			}
		});
		states_panel.add(existingDropBox);
		
		states_panel.add(btnSave);
		
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!stateNameBox.getText().equals("")){
					String item = stateNameBox.getText();
					if(!item.equals(machine.INITIAL)){
						System.out.println("REMOVE STATE " + item);
						machine.removeState(item);
						refresh();
						updateDisplay();
						changesMade = true;
					}
				}
			}
		});
		states_panel.add(btnRemove);
		
		JLabel lblNewLetter = new JLabel(" New Letter:");
		states_panel.add(lblNewLetter);
		
		final JButton btnLetterSave = new JButton("Save");
		btnLetterSave.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addLetter();
			}
		});
		
		letterBox = new JTextField();
		states_panel.add(letterBox);
		letterBox.setColumns(10);
		letterBox.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					btnLetterSave.doClick();
					letterBox.grabFocus();
					letterBox.setText("");
				}
			}
		});
		
		states_panel.add(btnLetterSave);
		
		JButton btnRemoveLetter = new JButton("Remove");
		btnRemoveLetter.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!letterBox.equals("")&&alphabet_list.getSelectedIndex()>=0){
					String item = alphabet_list.getSelectedValue().toString();
					machine.removeLetter(item);
					refresh();
					updateDisplay();
					changesMade = true;
				}
			}
		});
		states_panel.add(btnRemoveLetter);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stateNameBox.setText("");
				letterBox.setText("");
				existingDropBox.setSelectedIndex(0);
				stateNameBox.requestFocus();
			}
		});
		states_panel.add(btnClear);
		
		JPanel transition_panel = new JPanel();
		tabbedPane.addTab("Edit Transitions", null, transition_panel, null);
		
		JLabel lblFromState = new JLabel("From State");
		transition_panel.add(lblFromState);
		
		fromDropBox = new JComboBox();
		transition_panel.add(fromDropBox);
		
		JLabel lblOn = new JLabel("on");
		transition_panel.add(lblOn);
		
		letterDropBox = new JComboBox();
		transition_panel.add(letterDropBox);
		
		JLabel lblToState = new JLabel("to state");
		transition_panel.add(lblToState);
		
		final JButton btnSaveTransition = new JButton("Save");
		btnSaveTransition.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addTransition();
			}
		});
		
		toDropBox = new JComboBox();
		toDropBox.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					btnSaveTransition.doClick();
					fromDropBox.grabFocus();
				}
			}
		});
		transition_panel.add(toDropBox);
		
		transition_panel.add(btnSaveTransition);
		
		JButton btnRemoveTransition = new JButton("Remove");
		btnRemoveTransition.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String from = fromDropBox.getSelectedItem().toString();
				String letter = letterDropBox.getSelectedItem().toString();
				String to = toDropBox.getSelectedItem().toString();
				if(!from.equals("")&&!letter.equals("")&&!to.equals("")){
					Triple t = new Triple(from,letter,to);
					if(machine.hasTriple(t)){
						machine.removeTriple(t);
						refresh();
						updateDisplay();
						changesMade = true;
					}
				}
			}
		});
		transition_panel.add(btnRemoveTransition);
		
		JPanel sf_panel = new JPanel();
		tabbedPane.addTab("Edit Starts & Finals", null, sf_panel, null);
		
		JLabel lblStartState_1 = new JLabel("Start State:");
		sf_panel.add(lblStartState_1);
		
		final JButton btnSaveSF = new JButton("Save");
		btnSaveSF.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addSF();
			}
		});
		
		startDropBox = new JComboBox();
		sf_panel.add(startDropBox);
		
		JLabel lblStartAction = new JLabel("Start Action:");
		sf_panel.add(lblStartAction);
		
		actionDropBox = new JComboBox();
		actionDropBox.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					btnSaveSF.doClick();
				}
			}
		});
		sf_panel.add(actionDropBox);
		
		JLabel lblFinalState_1 = new JLabel("Final State:");
		sf_panel.add(lblFinalState_1);
		
		finalDropBox = new JComboBox();
		finalDropBox.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					btnSaveSF.doClick();
				}
			}
		});
		sf_panel.add(finalDropBox);
		
		sf_panel.add(btnSaveSF);
		
		setTitle("Automaton Builder");
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				onHideOrClose();
			}
			
		});
		
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentShown(ComponentEvent e){
				if(parent.getMachine()==null){
					parent.setMachine(new FSA());
				}
				machine = parent.getMachine().copy();
				updateDisplay();
				stateNameBox.requestFocus();
			}
			
		});
		
		//Set up the file menu
		JMenuBar menu = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		menu.add(file_menu);
		
		JMenuItem new_item = new JMenuItem("New");
		new_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				refresh();
				machine = new FSA();
				parent.setMachine(machine);
			}
		});
		
		JMenuItem open_item = new JMenuItem("Open");
		open_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openFSA();
			}
		});
		
		JMenuItem save_item = new JMenuItem("Save");
		save_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFSA();
			}
		});
		
		JMenuItem close_item = new JMenuItem("Close");
		close_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				onHideOrClose();
			}
		});
		
		file_menu.add(new_item);
		file_menu.add(open_item);
		file_menu.add(save_item);
		file_menu.add(close_item);
		
		setJMenuBar(menu);
	}
	
	private void populateBoxes(){
		fromDropBox.removeAllItems();
		letterDropBox.removeAllItems();
		toDropBox.removeAllItems();
		startDropBox.removeAllItems();
		finalDropBox.removeAllItems();
		actionDropBox.removeAllItems();
		existingDropBox.removeAllItems();
		ArrayList<String> states = machine.getStates();
		ArrayList<String> alphabet = machine.getAlphabet();
		existingDropBox.addItem("");
		if(states.size()>0&&alphabet.size()>0){
			for(String s: states){
				if(!s.equals(machine.INITIAL)){
					fromDropBox.addItem(s);
					toDropBox.addItem(s);
					startDropBox.addItem(s);
					finalDropBox.addItem(s);
					existingDropBox.addItem(s);
				}
			}
			for(String a: alphabet){
				letterDropBox.addItem(a);
				actionDropBox.addItem(a);
			}
			/*for(Map.Entry<Integer, String> entry : machine.mapping.entrySet()){
				int key = entry.getKey();
				String name = entry.getValue();
				existingDropBox.addItem(key + " : " + name);
			}*/
			ArrayList<Triple> t = machine.getActions(machine.INITIAL);
			ArrayList<String> finals = machine.getFinals();
			if(t.size()>0&&finals.size()>0){
				String orig_start = t.get(0).to_state;
				String orig_final = finals.get(0);
				for(int i = 0; i < startDropBox.getItemCount(); i++){
					if(orig_start.equals(startDropBox.getItemAt(i))){
						startDropBox.setSelectedIndex(i);
						break;
					}
				}
				for(int i = 0; i < finalDropBox.getItemCount(); i++){
					if(orig_final.equals(finalDropBox.getItemAt(i))){
						finalDropBox.setSelectedIndex(i);
						break;
					}
				}
			}
			stateNameBox.setText("");
			stateNameBox.requestFocus();
			
		} else {
			JOptionPane.showMessageDialog(this, "States or alphabet size too small.","Machine States and Alphabet Size Error",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void displayState(){
		DefaultListModel model = (DefaultListModel) state_list.getModel();
		int index = state_list.getSelectedIndex();
		String item = model.getElementAt(index).toString();
		if(!item.equals(machine.INITIAL)){
			if(item.toCharArray()[0]!=machine.ABSTRACT_CHAR){
				stateNameBox.setText(item);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Cannot modify initial state.","State Selection Error",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void displayLetter(){
		String item = alphabet_list.getSelectedValue().toString();
		letterBox.setText(item);
		letterBox.setEnabled(false);
	}
	
	private void displayTransition(){
		String item = transition_list.getSelectedValue().toString();
		item = item.substring(1, item.length()-1);
		String [] split = item.split(",");
		if(!split[0].equals(machine.INITIAL)){
			Triple t = new Triple(split[0],split[1],split[2]);
			if(machine.hasTriple(t)){
				changesMade = true;
				machine.removeTriple(t);
				refresh();
				updateDisplay();
				fromDropBox.setSelectedItem(split[0]);
				letterDropBox.setSelectedItem(split[1]);
				toDropBox.setSelectedItem(split[2]);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Cannot modify triples with state 0.","Triple Selection Error",JOptionPane.WARNING_MESSAGE);
		}
		
	}

	private void addState() {
		String name = stateNameBox.getText();
		if(!name.equals("")&&!machine.hasString(name, machine.getStates())){
			machine.addState(name);
			//machine.getMapping().put(Integer.parseInt(number), name);
			DefaultListModel model = (DefaultListModel) state_list.getModel();
			if(model.size()==0){
				model.addElement(machine.INITIAL);
			}
			model.addElement(name);
			changesMade = true;
			updateDisplay();
			isMachineConnected(name);
		} else if(!name.equals("")){
			int reply = JOptionPane.showConfirmDialog(this, "Would you like to over-write this state?", "Over-write State", JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.YES_OPTION){
				stateNameBox.setText("");
				stateNameBox.requestFocus();
				updateDisplay();
				isMachineConnected(name);
			}
		} else {
			JOptionPane.showMessageDialog(this,"State not valid.","State Input Error",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	
	private void addLetter(){
		String letter = letterBox.getText();
		if(!letter.equals("")&&!machine.hasString(letter, machine.getAlphabet())){
			machine.addLetter(letter);
			DefaultListModel model = (DefaultListModel) alphabet_list.getModel();
			model.addElement(letter);
			changesMade = true;
		} else {
			JOptionPane.showMessageDialog(this, "Duplicate letter.","Letter Input Error",JOptionPane.WARNING_MESSAGE);
			letterBox.setText("");
			letterBox.setEnabled(true);
		}
	}
	
	private void addTransition(){
		String fromState = fromDropBox.getSelectedItem().toString();
		String letter = letterDropBox.getSelectedItem().toString();
		String toState = toDropBox.getSelectedItem().toString();
		Triple t = new Triple(fromState,letter,toState);
		if(!machine.hasTriple(t)){
			machine.addTriple(fromState, letter, toState);
			DefaultListModel model = (DefaultListModel) transition_list.getModel();
			model.addElement(t.toString());
			changesMade = true;
		} else {
			JOptionPane.showMessageDialog(this, "Transition not valid.","Transition Input Error",JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void addSF(){
		String newStart = startDropBox.getSelectedItem().toString();
		String action = actionDropBox.getSelectedItem().toString();
		String newFinal = finalDropBox.getSelectedItem().toString();
		ArrayList<String> states = machine.getStates();
		if(machine.hasString(newStart, states)&&machine.hasString(newFinal, states)&&machine.hasString(action, machine.getAlphabet())){
			if(!machine.hasString(newStart, machine.getStarts())){
				//machine.switchStart(newStart);
				Triple t = new Triple(machine.INITIAL,action,newStart);
				machine.removeTriples(machine.INITIAL);
				machine.addTriple(machine.INITIAL, action, newStart);
				DefaultListModel model = (DefaultListModel) transition_list.getModel();
				model.clear();
				ArrayList<Triple> transitions = machine.getTransitions();
				for(Triple tr: transitions){
					model.addElement(tr);
				}
				lblStart.setText("[" + newStart + "]");
			}
			if(!machine.hasString(newFinal, machine.getFinals())){
				machine.switchFinal(newFinal);
				lblFinal.setText("[" + newFinal + "]");
			}
			changesMade = true;
		}
	}
	
	private void isMachineConnected(String state){
		boolean connected = machine.isConnected();
		System.out.println("Connected = " + connected);
		if(connected == false){
			JOptionPane.showMessageDialog(this, "Machine is now disconnected, please add transition for new state", "Error: Machine Disconnected",JOptionPane.WARNING_MESSAGE);
			tabbedPane.setSelectedIndex(1);
			
		}
	}

	private void saveFSA() {
		try {
			String path = System.getProperty("user.dir") + "/src/main/resources";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Save FSA");
			
			int returnVal = fc.showSaveDialog(this);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File save_file = fc.getSelectedFile();
				System.out.println("Save file: " + save_file.getName());
				BufferedWriter bw = new BufferedWriter(new FileWriter(save_file));
				bw.write("<SequenceModel>");
				bw.write("<FSA>");
				for(String s: machine.getStates()){
					bw.write("<state>" + s + "</state>");
				}
				for(String s: machine.getStarts()){
					bw.write("<startstate>" + s + "</startstate>");
				}
				for(String f: machine.getFinals()){
					bw.write("<finalstate>" + f + "</finalstate>");
				}
				for(Triple t: machine.getTransitions()){
					bw.write("<transition>");
					bw.write("<fromstate>" + t.from_state + "</fromstate>");
					bw.write("<action>" + t.symbol + "</action>");
					bw.write("<tostate>" + t.to_state + "</tostate>");
					bw.write("</transition>");
				}
				bw.write("</FSA>");
				/*bw.write("<WIDGETMAP>");
				for(String s: machine.getStates()){
					int key = Integer.parseInt(s);
					String name = machine.mapping.get(key);
					bw.write("<map>");
					bw.write("<name>" + name + "</name>");
					bw.write("<number>" + key + "</number>");
					bw.write("</map>");
				}
				bw.write("</WIDGETMAP>");*/
				bw.write("</SequenceModel>");
				bw.close();
			}
		} catch(Exception e){
			JOptionPane.showMessageDialog(this, "Error saving model as XML.", "Model Save Error", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
		
	}

	private void openFSA() {
		refresh();
		parent.openSequenceModel(this);
		machine = parent.getMachine().copy();
		updateDisplay();
	}
	
	private void updateDisplay(){
		ArrayList<String> temp_starts = machine.getStarts();
		ArrayList<Triple> starts = ((machine.hasString(machine.INITIAL, temp_starts) && temp_starts.size() > 0) ? machine.getActions(machine.INITIAL) : machine.getActions(temp_starts.get(0)));
		if(starts.size()>0){
			String start = ((machine.hasString(machine.INITIAL, temp_starts)) ? starts.get(0).to_state : temp_starts.get(0));
			lblStart.setText("[" + start + "]");
		} else {
			lblStart.setText("[]");
		}
		ArrayList<String> finals = machine.getFinals();
		lblFinal.setText(Arrays.toString(finals.toArray()));
		DefaultListModel model = (DefaultListModel) state_list.getModel();
		model.clear();
		ArrayList<String> states = machine.getStates();
		//HashMap<Integer,String> map = machine.getMapping();
		for(String s: states){
			if(s.toCharArray()[0]!=machine.ABSTRACT_CHAR){
				//String name = map.get(Integer.parseInt(s));
				model.addElement(s);
			} else {
				model.addElement(s);
			}
		}
		model = (DefaultListModel) alphabet_list.getModel();
		model.clear();
		ArrayList<String> alphabet = machine.getAlphabet();
		for(String a: alphabet){
			model.addElement(a);
		}
		model = (DefaultListModel) transition_list.getModel();
		model.clear();
		ArrayList<Triple> transitions = machine.getTransitions();
		for(Triple t: transitions){
			model.addElement(t.toString());
		}
		populateBoxes();
	}
	
	private void onHideOrClose(){
		System.out.println("STATES = " + Arrays.toString(machine.getStates().toArray()));
		if(machine.isConnected()){
			parent.setMachine(machine);
			changesOnHideOrClose();
			if(changesMade){
				changesMade = false;
				int reply = JOptionPane.showConfirmDialog(this, "Would you like to save your changes?", "Save FSA", JOptionPane.YES_NO_OPTION);
				if(reply == JOptionPane.YES_OPTION){
					saveFSA();
				}
			}
		} else {
			int reply = JOptionPane.showConfirmDialog(this, "Machine is disconnected, please add transitions, otherwise changes will not be saved.", "Error: Machine Disconnected", JOptionPane.OK_CANCEL_OPTION);
			if(reply == JOptionPane.OK_OPTION){
				tabbedPane.setSelectedIndex(1);
			} else if (reply == JOptionPane.CANCEL_OPTION){
				machine = parent.getMachine().copy();
				changesOnHideOrClose();
			}
		}
	}
	
	private void changesOnHideOrClose(){
		refresh();
		parent.refreshFromBuild();
		setVisible(false);
		updateDisplay();
	}
	
	private void updateList(JList list, ArrayList<?> data){
		System.out.println("updateList(" + list.getModel() + "," + Arrays.toString(data.toArray()) + ")");
		if(data.size()>0){
			DefaultListModel model = (DefaultListModel) list.getModel();
			for(Object s: data){
				model.addElement(s.toString());
			}
		}
	}

	public void refresh() {
		DefaultListModel model = (DefaultListModel) state_list.getModel();
		model.clear();
		model = (DefaultListModel) alphabet_list.getModel();
		model.clear();
		model = (DefaultListModel) transition_list.getModel();
		model.clear();
		lblStart.setText("");
		lblFinal.setText("");
		stateNameBox.setText("");
		stateNameBox.setEnabled(true);
		letterBox.setText("");
		letterBox.setEnabled(true);
		fromDropBox.removeAllItems();
		letterDropBox.removeAllItems();
		toDropBox.removeAllItems();
		startDropBox.removeAllItems();
		finalDropBox.removeAllItems();
		actionDropBox.removeAllItems();
	}
}
