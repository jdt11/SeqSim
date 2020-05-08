package jdt11;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.LensTranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class SelfContainedMachineViewer extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private FSA machine;
	private FSA orig_machine;
	private HashMap<Integer,FSA> sc_machines;
	private JTextArea orig_text;
	private JTextArea abstract_text;
	private JTextArea sc_text;
	private JList select_list;
	private boolean first;
	private JScrollPane graphScrollPane;
	private VisualizationViewer<String, StateLink> graph_view;

	/**
	 * Create the frame.
	 */
	public SelfContainedMachineViewer(SequenceExecuter p) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(700, 200, 770, 603);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				parent.showSelectStateWindow();
			}
		});
		
		
		JLabel lblSelectMachine = new JLabel("Select Automata:");
		lblSelectMachine.setBounds(6, 373, 126, 16);
		contentPane.add(lblSelectMachine);
		
		JLabel lblOriginalMachine = new JLabel("Original Automaton:");
		lblOriginalMachine.setBounds(198, 373, 157, 16);
		contentPane.add(lblOriginalMachine);
		
		JLabel lblAbstractMachine = new JLabel("Abstract Automaton:");
		lblAbstractMachine.setBounds(390, 373, 162, 16);
		contentPane.add(lblAbstractMachine);
		
		JLabel lblSelfcontainedMachine = new JLabel("Self-contained automata:");
		lblSelfcontainedMachine.setBounds(582, 373, 180, 16);
		contentPane.add(lblSelfcontainedMachine);
		
		orig_text = new JTextArea();
		orig_text.setBackground(Color.WHITE);
		orig_text.setEditable(false);
		orig_text.setLineWrap(true);
		JScrollPane origScrollPane = new JScrollPane(orig_text);
		origScrollPane.setBounds(198, 395, 180, 180);
		contentPane.add(origScrollPane);
		
		abstract_text = new JTextArea();
		abstract_text.setBackground(Color.WHITE);
		abstract_text.setEditable(false);
		abstract_text.setLineWrap(true);
		JScrollPane abstractScrollPane = new JScrollPane(abstract_text);
		abstractScrollPane.setBounds(390, 395, 180, 180);
		contentPane.add(abstractScrollPane);
		
		sc_text = new JTextArea();
		sc_text.setBackground(Color.WHITE);
		sc_text.setEditable(false);
		sc_text.setLineWrap(true);
		JScrollPane scScrollPane = new JScrollPane(sc_text);
		scScrollPane.setBounds(582, 395, 180, 180);
		contentPane.add(scScrollPane);
		
		select_list = new JList(new DefaultListModel());
		select_list.setBackground(Color.WHITE);
		select_list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		select_list.setSelectionBackground(new Color(237, 248, 255));
		select_list.setSelectionForeground(Color.BLACK);
		select_list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				selectSelfContainedMachine();
			}
		});
		JScrollPane selectScrollPane = new JScrollPane(select_list);
		selectScrollPane.setBounds(6, 395, 180, 180);
		contentPane.add(selectScrollPane);
		
		setTitle("Abstract Sequence Model");
		parent = p;
		first = true;
		updateMachine();
		
		addComponentListener(new ComponentAdapter(){
			@Override
			public void componentShown(ComponentEvent e){
				updateMachine();
			}
		});
		
		Layout<String, StateLink> layout = new CircleLayout(updateGraph());
		layout.setSize(new Dimension(708,300));
		graph_view = new VisualizationViewer<String, StateLink>(layout);
		graph_view.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		graph_view.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		graph_view.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		graph_view.setBounds(6, 6, 758, 360);
		PluggableGraphMouse gm = new PluggableGraphMouse();
		gm.add(new LensTranslatingGraphMousePlugin());
		gm.add(new PickingGraphMousePlugin<String, StateLink>(){
			@Override
			public void mouseClicked(MouseEvent e){
				System.out.println("Mouse clicked!");
				this.setLocked(true);
				ArrayList<String> states = new ArrayList(graph_view.getPickedVertexState().getPicked());
				System.out.println("states = " + Arrays.toString(states.toArray()));
				if(states.size()==1){
					String selected_state = states.get(0);
					if(selected_state.toCharArray()[0]==machine.ABSTRACT_CHAR){
						System.out.println("Abstract State selected");
						expandMachine(selected_state);
					}
					System.out.println("selected_state = " + selected_state);
				} else if(states.size()>1){
					JOptionPane.showMessageDialog(contentPane, "Mulitple states selected.","Error",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		graph_view.setGraphMouse(gm);
		final FontMetrics fm = graph_view.getFontMetrics(graph_view.getFont());
		Transformer<String, Shape> vertexShape = new Transformer<String, Shape>(){	
			@Override
			public Shape transform(String s){
				double width = fm.stringWidth(s)+5;
				double height = fm.getHeight()+5;
				double x = (width/2) * -1;
				double y = (height/2) * -1;
				return new Ellipse2D.Double(x, y, width, height);
			}
		};
		Transformer<String,Paint> vertexPaint = new Transformer<String,Paint>(){
			@Override
			public Paint transform(String s){
				if(machine.hasString(s, machine.getStarts())){
					return Color.GREEN;
				} else if(machine.hasString(s, machine.getFinals())){
					return Color.RED;
				}
				return Color.WHITE;
			}
		};
		graph_view.getRenderContext().setVertexShapeTransformer(vertexShape);
		graph_view.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		graph_view.setBackground(Color.WHITE);
		graph_view.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		contentPane.add(graph_view);

	}
	
	private void updateMachine(){
		if(parent.getMachine()==null){
			parent.setMachine(new FSA());
		}
		machine = parent.getMachine();
		if(graph_view!=null){
			graph_view.setGraphLayout(new CircleLayout(updateGraph()));
		}
	}
	
	private void populateMachineList(){
		DefaultListModel model = (DefaultListModel) select_list.getModel();
		model.clear();
		for(Map.Entry<Integer, FSA> entry: sc_machines.entrySet()){
			FSA temp = entry.getValue();
			if(temp!=null){
				String display = Arrays.toString(temp.getStates().toArray());
				model.addElement(display);
			}
		}
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		parent.refreshAll();
		FSA temp = parent.getMachine();
		if(first&&!temp.getStates().isEmpty()&&!temp.getStarts().isEmpty()&&
				!temp.getFinals().isEmpty()&&!temp.getAlphabet().isEmpty()&&
				!temp.getTransitions().isEmpty()){
			first = false;
			parent.getMachine().setSelfContained();
			orig_machine = parent.getMachine();
			sc_machines = orig_machine.selfContained;
			System.out.println("ORIG_MACHINE: \n" + orig_machine.toString());
			graph_view.setGraphLayout(new CircleLayout(updateGraph()));
		}
		graph_view.setGraphLayout(new CircleLayout(updateGraph()));
		updateMachine();		
		populateMachineList();
		updateOrigMachine();
		updateSCMachine();
	}
	
	public FSA getOriginal(){
		return orig_machine;
	}
	
	private void updateOrigMachine(){
		orig_text.setText(orig_machine.toString());
	}
	
	private boolean updateSCMachine(){
		int [] index = select_list.getSelectedIndices();
		FSA [] machines = new FSA[index.length];
		
		for(int i = 0; i < index.length; i++){
			machines[i] = sc_machines.get(index[i]);
			
		}
		boolean check = isSelectionOk(machines);
		if(check==false){
			String display = "";
			for(FSA m: machines){
				display += m.toString() + "\n--------------------";
			}
			sc_text.setText(display);
		} else if(check&&select_list.getSelectedIndex()>=0){
			sc_text.setText("Error: selected self-contained machines must have different states. Please try new selection.");
			select_list.setSelectedIndex(-1);
		}
		return check;
	}
	
	private boolean isSelectionOk(FSA [] machines){
		for(int i = 0; i < machines.length; i++){
			ArrayList<String> states_one = machines[i].getStates();
			for(int j = i+1; j < machines.length; j++){
				ArrayList<String> states_two = machines[j].getStates();
				if(compareStates(states_one,states_two)==true){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean compareStates(ArrayList<String> states_one, ArrayList<String> states_two){
		for(String so: states_one){
			for(String st: states_two){
				if(so.equals(st)){
					return true;
				}
			}
		}
		return false; 
	}
	
	private void selectSelfContainedMachine(){
		boolean check = updateSCMachine();
		if(check==false){
			FSA machine_a = orig_machine.getAbstractMachine(select_list.getSelectedIndices());
			System.out.println("Abstract Machine = \n" + machine_a.toString());
			abstract_text.setText(machine_a.toString());
			parent.setMachine(machine_a);
			parent.refreshAll();
			updateMachine();
			graph_view.setGraphLayout(new CircleLayout(updateGraph()));
		}
	}
	
	private void expandMachine(String selected_state){
		int index = Integer.parseInt(selected_state.substring(1));
		System.out.println("INDEX IS = " + index);
		FSA machine_n = machine.getExpandedMachine(index,orig_machine.selfContained);
		System.out.println("Machine_n is \n" + machine_n.toString());
		abstract_text.setText(machine_n.toString());
		parent.setMachine(machine_n);
		parent.refreshAll();
		updateMachine();
		graph_view.setGraphLayout(new CircleLayout(updateGraph()));
		ArrayList<Integer> indices = new ArrayList<Integer>();
		for(String s: machine.getStates()){
			if(s.toCharArray()[0]==machine.ABSTRACT_CHAR){
				indices.add(Integer.parseInt(s.substring(1)));
			}
		}
		select_list.setSelectedIndices(listToIntArray(indices));
		updateSCMachine();
	}
	
	private int [] listToIntArray(ArrayList<Integer> list){
		int [] temp = new int[list.size()];
		for(int i = 0; i < list.size(); i++){
			temp[i] = list.get(i);
		}
		return temp;
	}
	
	private Graph<String, StateLink> updateGraph(){
		Graph<String, StateLink> g = new DirectedSparseMultigraph<String, StateLink>();
		ArrayList<Triple> tr = machine.getTransitions();
		for(int i = 0; i < tr.size(); i++){
			Triple t = tr.get(i);
			g.addEdge(new StateLink(t.symbol,i), t.from_state, t.to_state, EdgeType.DIRECTED);
		}
		for(String state: machine.getStates()){
			g.addVertex(state);
		}
		return g;
	}
}

class StateLink {
	private String action;
	private int id; 
	
	public StateLink(String a, int i){
		action = a;
		id = i;
	}
	
	public String toString() {
		return action;
	}
}
