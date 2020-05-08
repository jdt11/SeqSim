package jdt11;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.statespace.State;
import de.prob.statespace.Trace;

public class TestBuilder extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private Trace trace;
	private PresentationInteractionModel pim;
	private ArrayList<String> boxNames;
	private JPanel panel;
	private HashMap<String,Component> componentMap;
	private JComboBox comboBox;
	private TestBuilder builder;
	private BoundaryTests btests;
	private ArrayList<String> boundaryTests;
	/**
	 * Create the frame.
	 */
	public TestBuilder(SequenceExecuter p, Trace t, PresentationInteractionModel pi) {
		builder = this;
		parent = p;
		trace = t;
		pim = pi;
		btests = new BoundaryTests(this);
		
		boxNames = new ArrayList<String>();
		componentMap = new HashMap<String,Component>();
		boundaryTests = new ArrayList<String>();
			
		setTitle("Test Builder");
		setBounds(100, 100, 700, 700);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		panel = new JPanel();
		panel.setLayout(null);
		panel.setName("panel");
		panel.setAutoscrolls(true);
		trace = trace.anyEvent(null);
		int y = addValues(10, 10, panel, "start");
		y = addValues(10,y, panel, "end");
		panel.setPreferredSize(new Dimension(340,y + 20));
		JScrollPane scroll_pane = new JScrollPane(panel);
		scroll_pane.setBounds(10, 10, 340, 660);
		scroll_pane.setName("scroll_pane");
		contentPane.add(scroll_pane);
		
		initComponentMap();
		printAssumptions();
		
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSequences();
			}
		});
		
		loadButton.setBounds(636, 5, 58, 29);
		contentPane.add(loadButton);
		
		JLabel Sequence = new JLabel("Sequences:");
		Sequence.setBounds(362, 10, 104, 16);
		contentPane.add(Sequence);
		
		comboBox = new JComboBox();
		comboBox.setBounds(435, 6, 202, 27);
		contentPane.add(comboBox);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(360, 37, 330, 633);
		contentPane.add(panel_1);
		
		final JCheckBox chckbxActive = new JCheckBox("Available");
		panel_1.add(chckbxActive);
		
		final JCheckBox chckbxUseAssumptions = new JCheckBox("Assumptions");
		panel_1.add(chckbxUseAssumptions);
		
		final JCheckBox chckbxMapping = new JCheckBox("Mapping");
		panel_1.add(chckbxMapping);
		
		JButton btnGenerateTests = new JButton("Generate Tests");
		btnGenerateTests.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tests = generateTests(chckbxActive.isSelected(), chckbxUseAssumptions.isSelected(), chckbxMapping.isSelected());
				AbstractTests abst = new AbstractTests(builder, tests);
				abst.setVisible(true);
			}
		});
		
		
		panel_1.add(btnGenerateTests);
		
		JButton btnAddBoundaryTests = new JButton("Add Boundary Tests");
		btnAddBoundaryTests.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getItemCount()>0){
					btests.setVisible(true);
				}
			}
		});
		panel_1.add(btnAddBoundaryTests);
		
	}
	
	private String generateTests(boolean active, boolean assumptions, boolean mapping){
		String abstractTests = "ABSTRACT TESTS\n";
		String [] selected_seq = ((String) comboBox.getSelectedItem()).replaceAll("\\[", "").replaceAll("\\]", "").split(", ");
		for(String s: selected_seq){
			System.out.println(s);
		}
		if(active){
			abstractTests += "----------------------------------------\n";
			abstractTests += "Available Tests\n";
			abstractTests += "----------------------------------------\n";
			for(String s: selected_seq){
				String [] split = s.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
				abstractTests += "onStep(" + s + ") & isNextActionActive(" + split[1] + "," + split[2] + ")\n";
			}
		}
		if(assumptions){
			abstractTests += "----------------------------------------\n";
			abstractTests += "Assumption Tests\n";
			abstractTests += "----------------------------------------\n";
			String temp = "";
			for(String s: boxNames){
				if(s.equals("start")||s.equals("end")){
					temp = s;
					abstractTests += s.toUpperCase().toString() + "ING ASSUMPTIONS\n";
				} else {
					String value = s.replaceAll("start", "").replaceAll("end", "").replaceAll("_", "");
					Component c = getComponentByName(s);
					String assumption = "";
					if(c.getClass().equals(JTextField.class)){
						assumption = ((JTextField) c).getText();
					} else {
						assumption = ((JComboBox) c).getSelectedItem().toString();
					}
					if(temp.equals("start")){
						abstractTests += "beforeStep(" + selected_seq[0] + ") & " + value + "Is(" + assumption + ")\n";
					} else {
						abstractTests += "afterStep(" + selected_seq[selected_seq.length-1] + ") & " + value + "Is(" + assumption + ")\n";
					}
				}
			}
		}
		if(mapping){
			abstractTests += "----------------------------------------\n";
			abstractTests += "Mapping Tests\n";
			abstractTests += "----------------------------------------\n";
			for(String s: selected_seq){
				String [] split = s.replaceAll("\\(", "").replaceAll("\\)", "").split(",");
				if(split!=null&&parent!=null){
					String behaviours = parent.getWidgetBehaviours(split[2]);
					abstractTests += "onStep(" + s + ") & behaviourMap(" + split[2] + "," + behaviours + ")\n";
					parent.performTrace(split);
				}
			}
			parent.refreshAll();
		}
		if(boundaryTests.isEmpty()==false){
			abstractTests += "----------------------------------------\n";
			abstractTests += "Boundary Tests\n";
			abstractTests += "----------------------------------------\n";
			for(String b: boundaryTests){
				abstractTests += b + "\n";
			}
		}
		
		return abstractTests;
		
	}
	
	private int addValues(int x, int y, JPanel panel, String prefix){
		boxNames.add(prefix);
		State trace_state = trace.getCurrentState();
		int width = 0;
		int height = 0;
		JLabel title = new JLabel(prefix.toUpperCase().toString()+ "ING ASSUMPTIONS");
		width = title.getPreferredSize().width;
		height = title.getPreferredSize().height;
		title.setBounds(x, y, width, height);
		panel.add(title);
		y += height + 10;
		//Get each individual value
		Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = trace_state.getValues().entrySet();
		for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
			JLabel label = new JLabel(entry.getKey().toString() + ": ");
			width = label.getPreferredSize().width;
			height = label.getPreferredSize().height;
			label.setBounds(x, y, width, height);
			panel.add(label);
			x += width + 5;
			JTextField textbox = new JTextField(entry.getValue().toString());
			textbox.setBounds(x, (y - ((textbox.getPreferredSize().height) / 4)), 150, textbox.getPreferredSize().height);
			String name = prefix + "_" + entry.getKey().toString();
			textbox.setName(name);
			boxNames.add(name);
			panel.add(textbox);
			x = 10;
			y += height + 10;
		}
		JLabel label = new JLabel("state: ");
		width = label.getPreferredSize().width;
		height = label.getPreferredSize().height;
		label.setBounds(x, y, width, height);
		panel.add(label);
		x += width + 5;
		JComboBox combobox = new JComboBox(pim.getTransitionNames().toArray());
		String name = prefix + "_state";
		combobox.setName(name);
		boxNames.add(name); 
		combobox.setBounds(x, (y - (combobox.getPreferredSize().height) / 4), 150, combobox.getPreferredSize().height);
		panel.add(combobox);
		y += height + 10;
		return y;
	}
	
	public void printAssumptions(){
		for(String s: boxNames){
			if(s.equals("start")||s.equals("end")){
				System.out.println("----------------------------------------------------");
				System.out.println(s.toUpperCase().toString() + "ING ASSUMPTIONS");
				System.out.println("----------------------------------------------------");
			} else {
				Component selected = getComponentByName(s);
				if(selected.getClass().equals(JTextField.class)&&selected!=null){
					System.out.println(s + ": " + ((JTextField) selected).getText());
				} else {
					System.out.println(s + ": " + ((JComboBox) selected).getSelectedItem().toString());
				}
			}
			
		}
	}
	
	private void initComponentMap(){
		Component [] comp =  panel.getComponents();
		for(Component c: comp){
			if(c!=null&&c.getName()!=null){
				//System.out.println("Component name = " + c.getName());
				componentMap.put(c.getName(), c);
			}
		}
	}
	
	private Component getComponentByName(String name){
		return componentMap.get(name);
	}
	
	private void loadSequences(){
		try {
			String path = System.getProperty("user.dir") + "/saved";
			JFileChooser fc = new JFileChooser(path);
			fc.setDialogTitle("Load sequences");
			
			int returnVal = fc.showOpenDialog(this);
	
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = "";
				while((line = br.readLine())!= null){
					comboBox.addItem(line);
				}
				br.close();
				
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(this,ex.getMessage(),"Error Loading Sequences",JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
		}
	}
	
	
	private void refresh(){
		//TODO
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			refresh();
		}
	}
	
	public String getSelectedSequence(){
		if(comboBox.getSelectedItem()!=null){
			return comboBox.getSelectedItem().toString();
		} else {
			return "";
		}
	}
	
	public Trace getTrace(){
		return trace;
	}
	
	public void addBoundaryTest(String test){
		if(boundaryTests.contains(test)==false){
			boundaryTests.add(test);
		}
	}
}
