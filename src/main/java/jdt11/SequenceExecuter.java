package jdt11;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

public class SequenceExecuter {

	private JFrame frame;
	private JList values_list;
	private JList action_list; 
	private JList trace_list;
	private JList sequence_trace_list;
	private JList sequence_history_list;
	private static Injector INJECTOR = Guice.createInjector(Stage.PRODUCTION, new MyGuiceConfig());
	private Trace trace;
	private PresentationModel pm;
	private FSA machine;
	private String state;
	private StateSpace stateSpace;
	private PresentationRelationModel pmr;
	private PresentationInteractionModel pim;
	private SequenceInput siFrame;
	private MachineBuilder mbuilder;
	private SequenceBuilder sbuilder;
	private SelfContainedMachineViewer scmview;
	private static SequenceExecuter my_seqex;
	private PIMViewer pimViewer;
	private Console console;
	private static Class myClass;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SequenceExecuter window = new SequenceExecuter();
					my_seqex = window;
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SequenceExecuter() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		state = "INITIALISATION";
		
		frame = new JFrame();
		frame.setTitle("Sequence Simulator");
		frame.setResizable(false);
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		siFrame = new SequenceInput(this);
		mbuilder = new MachineBuilder(this);
		scmview = new SelfContainedMachineViewer(this);
		sbuilder = new SequenceBuilder(this);
		pimViewer = new PIMViewer(this);
		console = new Console();
		console.init();
		
		//Set up the file menu
		JMenuBar menu = new JMenuBar();
		JMenu file_menu = new JMenu("File");
		menu.add(file_menu);
		
		//Open file menu item
		JMenuItem open_file = new JMenuItem("Open Models");
		KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		open_file.setAccelerator(ctrlO);
		open_file.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		
		//Close file menu item
		JMenuItem close_app = new JMenuItem("Quit");
		KeyStroke ctrlQ = KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		close_app.setAccelerator(ctrlQ);
		close_app.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		JMenu edit_menu = new JMenu("Edit");
		menu.add(edit_menu);
		
		JMenuItem refresh = new JMenuItem("Refresh All");
		KeyStroke ctrlR = KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		refresh.setAccelerator(ctrlR);
		refresh.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshAll();
			}
		});
		
		JMenuItem refresh_seq = new JMenuItem("Refresh Sequence Model");
		refresh_seq.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSeq();
			}
		});
		
		JMenuItem refresh_b = new JMenuItem("Refresh B Model");
		refresh_b.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshB();
			}
		});
		
		JMenuItem reset_item = new JMenuItem("Reset automaton");
		reset_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				resetMachine();
			}
		});
		
		JMenu sequence_menu = new JMenu("Sequences");
		menu.add(sequence_menu);
		
		JMenuItem machine_from_seq_item = new JMenuItem("Build Automaton From Sequence");
		machine_from_seq_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showMachineFromSeqBuilder();
			}
		});
		
		JMenuItem machine_builder_item = new JMenuItem("Build New Automaton");
		machine_builder_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showMachineBuilder();
			}
		});
		
		JMenuItem seq_input_item = new JMenuItem("Input Sequences");
		seq_input_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(pim!=null&&machine!=null&&pmr!=null&&pm!=null){
					siFrame.setVisible(true);
					action_list.setEnabled(false);
					sequence_trace_list.setEnabled(false);
				}
			}
		});
		
		JMenuItem seq_save_item = new JMenuItem("Save Sequences");
		seq_save_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(pim!=null&&machine!=null&&pmr!=null&&pm!=null){
					siFrame.setVisible(true);
					action_list.setEnabled(false);
					sequence_trace_list.setEnabled(false);
					if(siFrame.hasSequences()==true){
						saveAllSequences();
					}
				}
			}
		});
		
		JMenuItem seq_load_item = new JMenuItem("Load Sequences");
		seq_load_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(pim!=null&&machine!=null&&pmr!=null&&pm!=null){
					siFrame.setVisible(true);
					action_list.setEnabled(false);
					sequence_trace_list.setEnabled(false);
					loadSequences((DefaultListModel) siFrame.getSequenceList().getModel(),true);
				}
			}
		});
		
		JMenuItem scm_viewer_item = new JMenuItem("Self-contained Automaton Viewer");
		scm_viewer_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(machine!=null){
					scmview.setVisible(true);
				}
			}
		});
		
		JMenuItem test_item = new JMenuItem("Test Builder");
		test_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				if(trace!=null){
					TestBuilder testview = new TestBuilder(my_seqex,trace,pim);
					testview.setVisible(true);
				}
			}
		});
		
		JMenu view_menu = new JMenu("View");
		menu.add(view_menu);
		
		final JCheckBoxMenuItem show_extras = new JCheckBoxMenuItem("View extra windows");
		show_extras.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				showExtras(show_extras.isSelected());
			}
		});
		
		JMenuItem pim_view_item = new JMenuItem("View the PIM");
		pim_view_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				pimViewer.setVisible(true);
			}
		});
		
		JMenuItem PM4Latex_item = new JMenuItem("Dump PM for Latex");
		PM4Latex_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				PMToModel();
			}
		});
		
		JMenuItem PIM4Latex_item = new JMenuItem("Dump PIM for Latex");
		PIM4Latex_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				PIMToModel();
			}
		});
		
		JMenuItem PMR4Latex_item = new JMenuItem("Dump PMR for Latex");
		PMR4Latex_item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				PMRToModel();
			}
		});
		
		file_menu.add(open_file);
		file_menu.add(close_app);
		
		edit_menu.add(refresh);
		edit_menu.add(refresh_b);
		edit_menu.add(refresh_seq);
		edit_menu.addSeparator();
		edit_menu.add(reset_item);
		
		sequence_menu.add(machine_from_seq_item);
		sequence_menu.add(machine_builder_item);
		sequence_menu.addSeparator();
		sequence_menu.add(seq_input_item);
		sequence_menu.add(seq_save_item);
		sequence_menu.add(seq_load_item);
		sequence_menu.addSeparator();
		sequence_menu.add(scm_viewer_item);
		sequence_menu.addSeparator();
		sequence_menu.add(test_item);
		
		view_menu.add(pim_view_item);
		view_menu.add(show_extras);
		view_menu.addSeparator();
		view_menu.add(PM4Latex_item);
		view_menu.add(PIM4Latex_item);
		view_menu.add(PMR4Latex_item);
		
		frame.setJMenuBar(menu);
		
		//The list to display the current value set
		values_list = new JList(new DefaultListModel());
		values_list.setBackground(Color.WHITE);
		values_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		values_list.setSelectionBackground(new Color(237, 248, 255));
		values_list.setSelectionForeground(Color.BLACK);
		JScrollPane values_list_scroller = new JScrollPane(values_list);
		values_list_scroller.setBounds(10, 235, 220, 215);
		frame.getContentPane().add(values_list_scroller);
		
		//The list to display the next available actions
		action_list = new JList(new DefaultListModel());
		action_list.setBackground(Color.WHITE);
		action_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		action_list.setSelectionBackground(new Color(237, 248, 255));
		action_list.setSelectionForeground(Color.BLACK);
		JScrollPane action_list_scroller = new JScrollPane(action_list);
		action_list_scroller.setBounds(240, 235, 220, 215);
		frame.getContentPane().add(action_list_scroller);
		
		//The list to display the current interaction sequence
		trace_list = new JList(new DefaultListModel());
		trace_list.setBackground(Color.WHITE);
		trace_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		trace_list.setSelectionBackground(new Color(237, 248, 255));
		trace_list.setSelectionForeground(Color.BLACK);
		JScrollPane trace_list_scroller = new JScrollPane(trace_list);
		trace_list_scroller.setBounds(470, 235, 220, 215);
		frame.getContentPane().add(trace_list_scroller);
		
		sequence_trace_list = new JList(new DefaultListModel());
		sequence_trace_list.setBackground(Color.WHITE);
		sequence_trace_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sequence_trace_list.setSelectionBackground(new Color(237, 248, 255));
		sequence_trace_list.setSelectionForeground(Color.BLACK);
		JScrollPane sequence_trace_list_scroller = new JScrollPane(sequence_trace_list);
		sequence_trace_list_scroller.setBounds(240,6,220,223);
		frame.getContentPane().add(sequence_trace_list_scroller);
		
		sequence_history_list = new JList(new DefaultListModel());
		sequence_history_list.setBackground(Color.WHITE);
		sequence_history_list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		sequence_history_list.setSelectionBackground(new Color(237, 248, 255));
		sequence_history_list.setSelectionForeground(Color.BLACK);
		JScrollPane sequence_history_list_scroller = new JScrollPane(sequence_history_list);
		sequence_history_list_scroller.setBounds(470,6,220,223);
		frame.getContentPane().add(sequence_history_list_scroller);
	
	}
	
	protected void PMRToModel() {
		String line = "\nPMR\n";
		if(pm!=null&&pim!=null&&pmr!=null){
			for(Relation r: pmr.getRelations()){
				line += r.getBehaviour().replaceAll("\\_", "\\\\_") + " $\\rightarrow$ " + r.getOperation()
				+ "\\\\\n";
			}
		}
		System.out.println(line);
	}

	protected void PIMToModel() {
		String line = "\nPIM\n";
		if(pm!=null&&pim!=null&&pmr!=null){
			for(jdt11.Transition t: pim.getTransitions()){
				line += t.getStart() + " $\\rightarrow$ "+ t.getIBeh().replaceAll("\\_", "\\\\_") + " $\\rightarrow$ " + t.getEnd()
				+ "\\\\\n";
			}
		}
		System.out.println(line);
	}

	protected void PMToModel() {
		String line = "";
		if(pm!=null&&pim!=null&&pmr!=null){
			line += "PModel ";
			for(CPModel cp: pm.cpmodels){
				line += cp.name + " ";
			}
			line += "\\\\\\\\\nWidgetName ";
			for(Widget w: pm.getWidgets()){
				line += w.name + " ";
			}
			line +="\\\\\\\\\nCategory ";
			ArrayList<String> categories = new ArrayList<String>();
			for(Widget w: pm.getWidgets()){
				for(String c: w.categories){
					if(categories.contains(c)==false){
						categories.add(c);
					}
				}
			}
			for(String c: categories){
				line += c + " ";
			}
			line +="\\\\\\\\\nBehaviour ";
			ArrayList<String> behaviours = new ArrayList<String>();
			for(Widget w: pm.getWidgets()){
				for(String b: w.behaviours){
					if(behaviours.contains(b)==false){
						behaviours.add(b);
					}
				}
			}
			for(String b: behaviours){
				line += b.replaceAll("\\_", "\\\\_") + " ";
			}
			line +="\\\\\\\\\n" + pm.name + " is ";
			for(CPModel cp: pm.cpmodels){
				line += cp.name + " : ";
			}
			line += "\\\\\\\\\n";
			for(CPModel cp: pm.cpmodels){
				line += cp.name + " is \\\\\n";
				for(Widget w: cp.widgets){
					line += w.toString().replaceAll("\\_", "\\\\_") + "\\\\\n";
				}
				line += "\\\\\n";
			}
			System.out.println(line);
		}
		
	}

	private void resetMachine(){
		FSA orig = scmview.getOriginal();
		if(orig!=null){
			setMachine(orig);
			refreshAll();
		}
	}
	
	private void showMachineFromSeqBuilder(){
		sbuilder.setVisible(true);
		sbuilder.refresh();
	}
	
	private void showMachineBuilder() {
		mbuilder.setVisible(true);
		mbuilder.refresh();
	}

	private void saveAllSequences() {
		try {
			String path = System.getProperty("user.dir") + "/saved";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Save sequences");
			
			int returnVal = fc.showSaveDialog(this.frame);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File save_file = fc.getSelectedFile();
				System.out.println("Save as file: " + save_file);
				BufferedWriter bw = new BufferedWriter(new FileWriter(save_file));
				DefaultListModel model = (DefaultListModel) siFrame.getSequenceList().getModel();
				for(int i = 0; i < model.size(); i++){
					bw.write(model.get(i) + "\n");
				}
				bw.close();
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(this.frame,ex.getMessage(),"Error Saving Sequences",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
		
	}
	
	private void loadSequences(DefaultListModel model, boolean siload) {
		try {
			String path = System.getProperty("user.dir") + "/saved";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Load sequences");
			
			int returnVal = fc.showOpenDialog(this.frame);
			
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = br.readLine())!= null){
					model.addElement(line);
				}
				br.close();
				if(siload){
					siFrame.loadSequences();
				}
				
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(this.frame,ex.getMessage(),"Error Loading Sequences",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
		
	}

	public FSA getMachine(){
		return machine;
	}
	
	public PresentationInteractionModel getPIM(){
		return pim;
	}
	
	public void setMachine(FSA m){
		if(m!=null){
			machine = m;
		}
	}
	
	public void refreshAll(){
		refreshSeq();
		refreshB();
		action_list.setEnabled(true);
		sequence_trace_list.setEnabled(true);
		console.refresh();
	}

	private void openFile() {
		try {
			ZModel m = INJECTOR.getInstance(ZModel.class);
			String path = System.getProperty("user.dir") + "/src/main/resources";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Open B Model");
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileNameExtensionFilter("B Models","mch"));
			int returnVal = fc.showOpenDialog(this.frame);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File openedFile = fc.getSelectedFile();
				System.out.println("File: " + openedFile.getName());
				stateSpace = m.getApi().b_load(openedFile.getAbsolutePath().toString());
				init();
			}
			openSequenceModel(this.frame);
			openPIM();
			sequenceActionClick();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error Opening File",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	public void refreshSeq(){
		if(machine!=null&&pim!=null){
			updateSequenceTraceList(machine.getStarts());
			((DefaultListModel) sequence_history_list.getModel()).clear();
			state = pim.getStart();
		}
	}
	
	public void refreshFromBuild(){
		if(machine!=null&&pim!=null){
			refreshSeq();
		}
		else if(machine!=null){
			updateSequenceTraceList(machine.getStarts());
		}
	}
	
	public void refreshB(){
		if(stateSpace != null){
			init();
		}
		if(trace_list != null){
			((DefaultListModel) trace_list.getModel()).clear();
		}
	}
	
	private void init(){
		trace = new Trace(stateSpace);
		update();
	}
	
	public void update(){
		updateValues();
		updateActions();
		updateTrace();
		actionClick();
		this.frame.invalidate();
	}
	
	public void showExtras(boolean show){
		if(show == true&&pim!=null&&pmr!=null&&machine!=null){
			frame.setSize(1160, 500);
			String [][] data = pim.get2DArray();
			JScrollPane table_one = loadTable(data,new String[]{"Start","I Behaviour","End"},930,235,220,215);
			data = pmr.get2DArray();
			JScrollPane table_two = loadTable(data,new String[]{"Behavior","Operation"},930,6,220,223);
			data = machine.get2DArrayTransitions();
			JScrollPane table_three = loadTable(data,new String[]{"From State","Symbol","To State"},700,6,220,223);
			JTextArea text_area = new JTextArea(pm.toString());
			JScrollPane list_four = new JScrollPane(text_area);
			list_four.setHorizontalScrollBar(null);
			list_four.setBounds(700, 235, 220, 215);
			frame.getContentPane().add(list_four);
		} else {
			frame.setSize(700, 500);
		}
	}
	
	public void updateValues(){
		//initialise
		State trace_state = trace.getCurrentState();
		DefaultListModel listModel = (DefaultListModel) values_list.getModel();
		listModel.clear();
		//Get each individual value
		Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = trace_state.getValues().entrySet();
		for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
			listModel.addElement(entry.getKey() + ": " + entry.getValue());
		}
		if(state!=null){
			listModel.addElement("state: " + state);
		}
		sortList();
	}
	
	public State getCurrentTraceState(){
		trace.anyEvent(null);
		return trace.getCurrentState();
	}
	
	private void sortList(){
		DefaultListModel model = (DefaultListModel) values_list.getModel();
		int length = model.getSize();
		String [] data = new String[length];
		for(int i = 0; i < model.getSize(); i++){
			String item = (String) model.getElementAt(i);
			data[i] = item;
		}
		Arrays.sort(data);
		model.clear();
		for(String d: data){
			model.addElement(d);
		}
	}
	
	public void updateActions(){
		DefaultListModel listModel = (DefaultListModel) action_list.getModel();
		listModel.clear();
		Set<Transition> transitions = trace.getNextTransitions();
		for(Transition tr: transitions){
			listModel.addElement(tr.getPrettyRep().replaceAll("\\(", "").replaceAll("\\)", ""));
		}
		this.frame.invalidate();
	}
	
	public void updateTrace(){
		DefaultListModel listModel = (DefaultListModel) trace_list.getModel();
	//	listModel.clear();
		Transition t = trace.getCurrentTransition();
		if(t!=null){
			State state = trace.getCurrentState();
			Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = state.getValues().entrySet();
			String display = t.getPrettyRep().replaceAll("\\(", "").replaceAll("\\)", "") + "(";
			for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
				display += entry.getValue() + ",";
			}
			display = display.substring(0,display.length()-1);
			display += ")";
		//	listModel.addElement(display);
			listModel.add(0, display);
		}
	}
	
	private void actionClick(){
		MouseListener mouseListener = new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				String selectedValue = (String) ((JList) e.getSource()).getSelectedValue();
				if(selectedValue != null){
					if(selectedValue.equals("INITIALISATION")){
						trace = trace.anyEvent(null);
					} else {
						trace = trace.anyEvent(selectedValue);
					}
					updateTrace();
					updateValues();
					updateActions();
				}
			}
		};
		action_list.addMouseListener(mouseListener);
	}
	
	//Written with assistance from https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
	private void openPIM(){
		try {
			String path = System.getProperty("user.dir") + "/src/main/resources";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Open PIMS");
			int returnVal = fc.showOpenDialog(this.frame);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				loadPresentationRelationModel(doc);
				loadPresentationInteractionModel(doc);
				loadPresentationModel(doc);
				/*if(machine.mapping.size()<0){
					loadWidgets();
				}*/
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error Opening PIM File",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	private void loadPresentationInteractionModel(Document doc){
		NodeList pims = doc.getElementsByTagName("PIM");
		pim = new PresentationInteractionModel();
		for(int i = 0; i < pims.getLength(); i++){
			Node pim_node = pims.item(i);
			if(pim_node.getNodeType()==Node.ELEMENT_NODE&&pim_node.hasChildNodes()){
				NodeList transitions = pim_node.getChildNodes();
				for(int j = 0; j < transitions.getLength(); j++){
					Node transition = transitions.item(j);
					if(transition.getNodeType()==Node.ELEMENT_NODE&&transition.hasChildNodes()){
						NodeList information = transition.getChildNodes();
						String start = "";
						String end = "";
						String ibeh = "";
						for(int k = 0; k < information.getLength(); k++){
							Node info = information.item(k);
							if(info.getNodeType()==Node.ELEMENT_NODE){
								if(info.getNodeName().equals("start")){
									start = info.getTextContent();
								} else if(info.getNodeName().equals("end")){
									end = info.getTextContent();
								} else if(info.getNodeName().equals("ibeh")){
									ibeh = info.getTextContent();
								}
							}
						}
						pim.addTransition(start, end, ibeh);
					}
				}
			}
		}
	}
	
	private void loadPresentationRelationModel(Document doc){
		NodeList pmrs = doc.getElementsByTagName("PMR");
		pmr = new PresentationRelationModel();
		for(int i = 0; i < pmrs.getLength(); i++){
			Node pmr_node = pmrs.item(i);
			if(pmr_node.getNodeType()==Node.ELEMENT_NODE&&pmr_node.hasChildNodes()){
				NodeList relations = pmr_node.getChildNodes();
				for(int j = 0; j < relations.getLength(); j++){
					Node relation = relations.item(j);
					if(relation.getNodeType()==Node.ELEMENT_NODE&&relation.hasChildNodes()){
						NodeList relation_info = relation.getChildNodes();
						String behaviour = "";
						String operation = "";
						for(int k = 0; k < relation_info.getLength(); k++){
							Node info = relation_info.item(k);
							if(info.getNodeType()==Node.ELEMENT_NODE){
								if(info.getNodeName().equals("sbeh")){
									behaviour = info.getTextContent();
								} else if(info.getNodeName().equals("sop")){
									operation = info.getTextContent();
								}
							}
						}
						pmr.addRelation(behaviour, operation);
					}
				}
			}
		}
	}
	
	private void loadPresentationModel(Document doc){
		NodeList pmodels = doc.getElementsByTagName("PresentationModel");
		for(int i = 0; i < pmodels.getLength(); i++){
			Node node = pmodels.item(i);
			if(node.hasChildNodes()==true){
				NodeList cpmodels = node.getChildNodes();
				for(int j = 0; j < cpmodels.getLength(); j++){
					Node cpnode = cpmodels.item(j);
					if(cpnode.getNodeType() == Node.ELEMENT_NODE){
						if(cpnode.getNodeName().equals("PModel")){
							Element element = (Element) cpnode;
							pm = new PresentationModel(element.getTextContent());
						} else if(cpnode.getNodeName().equals("cpmodel")&&pm!=null){
							NodeList widgets = cpnode.getChildNodes();
							for(int k = 0; k < widgets.getLength(); k++){
								Node widget = widgets.item(k);
								if (widget.getNodeType() == Node.ELEMENT_NODE){
									if(widget.getNodeName().equals("cpname")){
										CPModel cpmodel = new CPModel(widget.getTextContent());
										pm.cpmodels.add(cpmodel);
									}
									else if(widget.getNodeName().equals("widget")&&pm.cpmodels.size()>0){
										CPModel cpmodel = pm.cpmodels.get(pm.cpmodels.size()-1);
										Element widget_element = (Element) widget;
										NodeList names = widget_element.getElementsByTagName("name");
										Widget wid = new Widget(names.item(0).getTextContent().replaceAll("#", ""));
										NodeList categories = widget_element.getElementsByTagName("cat");
										for(int l = 0; l < categories.getLength(); l++){
											wid.categories.add(categories.item(l).getTextContent());
										}
										NodeList behaviours = widget_element.getElementsByTagName("beh");
										for(int l = 0; l < behaviours.getLength(); l++){
											wid.behaviours.add(behaviours.item(l).getTextContent());
										}
										cpmodel.widgets.add(wid);
										pm.addWidget(wid);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private JScrollPane loadTable(String [][] dummy, String [] header, int x, int y, int width, int height){
		JTable table = new JTable(dummy, header){
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		table.setBounds(470, 6, 220, 223);
		JTableHeader table_header = table.getTableHeader();
		table_header.setBackground(Color.yellow);
		table.setSelectionBackground(new Color(237, 248, 255));
		table.setSelectionForeground(Color.black);
		JScrollPane table_scroller = new JScrollPane(table);
		table_scroller.setBounds(x,y,width,height);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		frame.getContentPane().add(table_scroller);
		return table_scroller;
	}
	
	private boolean validate(Component [] comps){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for(Component c: comps){
			if(c.getClass().equals(JTextField.class)){
				JTextField textBox = (JTextField) c;
				String text = textBox.getText();
				if(text.isEmpty()){
					JOptionPane.showMessageDialog(null, "Numbers must be non-empty.");
					return false;
				} else if(hasNumber(numbers, text)==true){
					JOptionPane.showMessageDialog(null, "Numbers must be unique.");
					return false;
				} else if(hasNumber(numbers, text)==false){
					numbers.add(Integer.parseInt(text));
				}
			}
		}
		return true;
	}
	
	private boolean hasNumber(ArrayList<Integer> numbers, String text){
		int number = Integer.parseInt(text);
		for(int n: numbers){
			if(n==number){
				return true;
			}
		}
		return false;
	}
	
	public void openSequenceModel(JFrame parent){
		try {
			String path = System.getProperty("user.dir") + "/src/main/resources";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Open Sequence Model");
			int returnVal = fc.showOpenDialog(parent);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				doc.getDocumentElement().normalize();
				NodeList fsa = doc.getElementsByTagName("FSA");
				for(int i = 0; i < fsa.getLength(); i++){
					Node node = fsa.item(i);
					machine = new FSA();
					if(node.hasChildNodes()==true){
						NodeList fsa_def = node.getChildNodes();
						for(int j = 0; j < fsa_def.getLength(); j++){
							Node node2 = fsa_def.item(j);
							if(node2.getNodeType() == Node.ELEMENT_NODE){
								if(node2.getNodeName().equals("state")){
									machine.addState(node2.getTextContent());
								} else if(node2.getNodeName().equals("startstate")){
									machine.addStartState(node2.getTextContent());
								} else if(node2.getNodeName().equals("finalstate")){
									machine.addFinalState(node2.getTextContent());
								} else if(node2.getNodeName().equals("transition")&&node2.hasChildNodes()){
									NodeList triple = node2.getChildNodes();
									String fromstate = "";
									String symbol = "";
									String tostate = "";
									for(int k = 0; k < triple.getLength(); k++){
										Node node3 = triple.item(k);
										if(node3.getNodeType() == Node.ELEMENT_NODE){
											if(node3.getNodeName().equals("fromstate")){
												fromstate = node3.getTextContent();
											} else if (node3.getNodeName().equals("action")){
												symbol = node3.getTextContent();
											} else if(node3.getNodeName().equals("tostate")){
												tostate = node3.getTextContent();
											}
										}
									}
									machine.addTriple(fromstate, symbol, tostate);
								}
							}
						}
					}
					//System.out.println(machine.toString());
					updateSequenceTraceList(machine.getStarts());
					//machine.setSelfContained();
				}
				//loadMap(file);
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(frame,ex.getMessage(),"Error Opening Sequence Model File",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	public void updateSequenceTraceList(ArrayList<String> states){
		ArrayList<Triple> actions = new ArrayList<Triple>();
		for(String s: states){
			System.out.println("State s = " + s);
			actions.addAll(machine.getActions(s));
		}
		DefaultListModel model = (DefaultListModel) sequence_trace_list.getModel();
		model.clear();
		for(Triple t: actions){
			model.addElement(t.toString());
		}
	}
	
	private void sequenceActionClick(){
		if(pm!=null&&pim!=null&&pmr!=null){
			showSelectStateWindow();
			MouseListener mouseListener = new MouseAdapter(){
				@Override 
				public void mouseClicked(MouseEvent e){
					String selectedValue = (String) ((JList) e.getSource()).getSelectedValue();
					if(selectedValue!=null){
						String [] split = ((String) selectedValue.subSequence(1, selectedValue.length()-1)).split(",");
						if(split[0].equals(machine.INITIAL)||(machine.getStarts().get(0).equals(split[0]))){
							performInitialTrace(split);
						} else {
							performTrace(split);
						}
						ArrayList<String> new_state = new ArrayList<String>();
						new_state.add(split[2]);
						updateSequenceTraceList(new_state);
					}
				}
			};
			sequence_trace_list.addMouseListener(mouseListener);
		} else {
			JOptionPane.showMessageDialog(null, "Please load PIMS.");
		}
	}
	
	public void performInitialTrace(String [] split){
		//System.out.println("performInitialTrace");
		trace = trace.anyEvent(null);
		updateTrace();
		updateValues();
		updateActions();
		performTrace(split);
	}
	
	public void performTrace(String [] split){
		System.out.println("performTrace(" + Arrays.toString(split) + ")");
		//Get the widget and the action
		String widget_name = split[2];
		String action = split[1];
		String rep = "";
		console.write(action + " " + widget_name);
		if(widget_name.toCharArray()[0]==machine.ABSTRACT_CHAR){
			trace = trace.anyEvent("Skip");
			updateTrace();
			updateValues();
			updateActions();
		//} else if(!action.equals("observe")){
		} else {
			//Get the model for our current state
			CPModel cpmodel = pm.getCPModel(state);
			console.write(cpmodel.name);
			//Get the widget behaviour in current state
			if(cpmodel!=null){
				Widget the_widget = cpmodel.getWidget(widget_name);
				if(the_widget!=null){
					console.write(the_widget.toString().replaceAll("\\n", ""));
				} else {
					console.write(widget_name + " is null in " + cpmodel.name);
				}
				if(the_widget!=null&&!(the_widget.categories.get(0).equals("Responder"))){
					for(String beh: the_widget.behaviours){
						console.write("Executing behaviour " + beh + "...");
						//Get the function from PMR or PIM
						if(beh.charAt(0)=='S'){
							Relation r = pmr.getRelation(beh);
							console.write("Relation is " + r.toString());
							if(r!=null){
								trace = trace.anyEvent(r.getOperation());
								updateTrace();
								updateValues();
								updateActions();
							} else {
								System.out.println("Relation for behaviour " + beh + " not found.");
							}
						} else if(beh.charAt(0)=='I') {
							jdt11.Transition t = pim.getNextState(state, beh);
							if(t!=null){
								state = t.getEnd();
								updateValues();
								ArrayList<String> temp = new ArrayList<String>();
								temp.add(widget_name);
								updateSequenceTraceList(temp);
								rep += widget_name + ": " + t.getIBeh();
								console.write("Relation is " + rep);
								((DefaultListModel) sequence_history_list.getModel()).insertElementAt(rep, 0);
							} else {
								System.out.println("Transition for behaviour " + beh + " not found.");
							}
						}
					}
				}
				console.write("----------------------------------------------------------------");
			//} else {
			//	System.out.println("Could not find cpmodel: " + state);
			}
		}
	}
	
	public void showSelectStateWindow(){
		state = pim.getStart();
		//if(state == null){
			final JFrame select_start = new JFrame("Select PIM Start State");
			select_start.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			select_start.setBounds(frame.getX(),frame.getY(),300,150);
			select_start.getContentPane().setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(3,3,3,3);
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridx = 0;
			gbc.gridy = 0;
			JLabel label = new JLabel("Select start state:");
			label.setBorder(new EmptyBorder(10,10,10,10));
			select_start.add(label,gbc);
			gbc.gridy++;
			ArrayList<String> names = pim.getTransitionNames();
			String [] choices = new String [names.size()];
			for(int i = 0; i < names.size(); i++){
				choices[i] = names.get(i);
			}
			final JComboBox transitions_list = new JComboBox(choices);
			transitions_list.setBorder(new EmptyBorder(10,10,10,10));
			select_start.add(transitions_list,gbc);
			gbc.gridy++;
			JButton button_save = new JButton("Save");
			button_save.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					state = transitions_list.getSelectedItem().toString();
					pim.start = state;
					select_start.dispose();
				}
			});
			select_start.add(button_save,gbc);
			select_start.setVisible(true);
		//}
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public String getWidgetBehaviours(String widget_name){
		CPModel cpmodel = pm.getCPModel(state);
		//Get the widget behaviour in current state
		if(cpmodel!=null){
			Widget the_widget = cpmodel.getWidget(widget_name);
			if(the_widget!=null&&the_widget.behaviours!=null){
				return Arrays.toString(the_widget.behaviours.toArray()).replaceAll("\\[", "\\(").replaceAll("\\]", "\\)");
			}
		}
		return null;
	}
	
	
	
	
}