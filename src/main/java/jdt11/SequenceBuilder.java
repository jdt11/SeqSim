package jdt11;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class SequenceBuilder extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private JTextArea sequence_text;
	private JTextArea machine_text;
	private JButton button;
	private FSA machine;
	private VisualizationViewer<String, StateLink> graph_view;

	/**
	 * Create the frame.
	 */
	public SequenceBuilder(SequenceExecuter p) {
		parent = p;
		
		setTitle("Build Automaton from Sequence");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(10, 10, 700, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		final SequenceBuilder sb = this;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				int result = JOptionPane.showConfirmDialog(sb, "Would you like to load this automaton?","Change Machine",JOptionPane.YES_NO_OPTION);
				if(result==JOptionPane.YES_OPTION){
					parent.setMachine(machine);
					parent.refreshAll();
				}
				super.windowClosing(e);
			}
		});
		
		JLabel lblSequence = new JLabel("Sequence:");
		lblSequence.setBounds(6, 6, 80, 16);
		contentPane.add(lblSequence);
		
		sequence_text = new JTextArea();
		
		JScrollPane seqScrollPane = new JScrollPane(sequence_text);
		seqScrollPane.setBounds(6, 24, 280, 163);
		contentPane.add(seqScrollPane);
		
		machine_text = new JTextArea();
		machine_text.setLineWrap(true);
		
		JScrollPane mScrollPane = new JScrollPane(machine_text);
		mScrollPane.setBounds(320, 24, 274, 163);
		contentPane.add(mScrollPane);
		
		JLabel lblMachine = new JLabel("Automaton: ");
		lblMachine.setBounds(310, 6, 99, 16);
		contentPane.add(lblMachine);
		
		button = new JButton("->");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertToMachine(true);
			}
		});
		button.setBounds(281, 88, 43, 29);
		contentPane.add(button);
		
		Layout<String, StateLink> layout = new CircleLayout(updateGraph());
		//layout.setSize(new Dimension(1000, 1000));
		graph_view = new VisualizationViewer<String, StateLink>(layout);
		graph_view.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
		graph_view.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		graph_view.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		graph_view.setBounds(6, 192, 688, 480);
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
		//DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		//gm.setMode(ModalGraphMouse.Mode.PICKING);
		//PickingGraphMousePlugin pgmp = new PickingGraphMousePlugin(graph_view.getRenderContext());
		PluggableGraphMouse gm = new PluggableGraphMouse();
		gm.add(new PickingGraphMousePlugin());
		graph_view.setGraphMouse(gm);
		//graph_view.getRenderContext().setLabelOffset(20);
		
		//graph_view.setGraphMouse(gm);
		//graph_view.getPickedEdgeState().addItemListener(new EdgePickListener());
		contentPane.add(graph_view);
		
		JButton GoButton = new JButton("Go");
		GoButton.setBounds(281, 158, 43, 29);
		GoButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				convertToMachine(false);
			}
		});
		//contentPane.add(GoButton);
	}
	
	private void convertToMachine(boolean seq){
		FSA fsa_temp = new FSA();
		if(seq==true){
			//System.out.println("Sequence is somehow true");
			String [] info = sequence_text.getText().split("\n");
			System.out.println(Arrays.toString(info) + "!!!");
			for(int i = 0; i < info.length; i++){
				if(i+1 < info.length){
					System.out.println("HERE");
					String [] step_info1 = info[i].replaceAll("\\.", "").split(" ");
					String [] step_info2 = info[i+1].replaceAll("\\.", "").split(" ");
					if(step_info1.length!=3||step_info2.length!=3){
						JOptionPane.showMessageDialog(this.getContentPane(), "ERROR: Sequence is in the incorrect format:\n"
								+ info[i] + "; " + info[i+1]);
						return;
					}
					System.out.println(Arrays.toString(step_info1) + " : " + Arrays.toString(step_info2));
					if(i == 0&&seq){
						fsa_temp.addStartState(fsa_temp.INITIAL);
						fsa_temp.addTriple(fsa_temp.INITIAL, step_info1[0], step_info1[1]);
					}
					fsa_temp.addState(step_info1[1]);
					fsa_temp.addState(step_info2[1]);
					fsa_temp.addTriple(step_info1[1], step_info2[0], step_info2[1]);
					if(Integer.parseInt(step_info1[2]) > 1){
						fsa_temp.addTriple(step_info1[1], step_info1[0], step_info1[1]);
					}
					if(Integer.parseInt(step_info2[2]) > 1){
						fsa_temp.addTriple(step_info2[1], step_info2[0], step_info2[1]);
					}
					if((i+1) == (info.length-1)){
						fsa_temp.addFinalState(step_info2[1]);
						break;
					}
				} else if(i+1 == info.length) {
					System.out.println("HERE1");
					String [] step_info = info[i].replaceAll("\\.", "").split(" ");
					if(step_info.length!=3){
						JOptionPane.showMessageDialog(this.getContentPane(), "ERROR: Sequence is in the incorrect format:\n"
								+ info[i] + ";");
						return;
					}
					fsa_temp.addStartState(fsa_temp.INITIAL);
					fsa_temp.addTriple(fsa_temp.INITIAL, step_info[0], step_info[1]);
					fsa_temp.addState(step_info[1]);
					if(Integer.parseInt(step_info[2]) > 1){
						fsa_temp.addTriple(step_info[1], step_info[0], step_info[1]);
					}
					fsa_temp.addFinalState(step_info[1]);
				}
			}
		} else {
			System.out.println("Sequence is not true");
			String [] info = sequence_text.getText().split("\n");
			if(info.length>0){
				String [] states = info[0].split(",");
				String [] starts = info[1].split(",");
				String [] finals = info[2].split(",");
				for(String s: states){
					fsa_temp.addState(s);
				}
				for(String s: starts){
					fsa_temp.addStartState(s);
				}
				for(String f: finals){
					fsa_temp.addFinalState(f);
				}
				for(int i = 3; i < info.length; i++){
					String [] triple = info[i].replaceAll("\\(", "").replaceAll("\\)", "").split(",");
					System.out.println(Arrays.toString(triple));
					fsa_temp.addTriple(triple[0], triple[1], triple[2]);
				}
			}
		}
		fsa_temp.alphabetFunction();
		//fsa_temp.removeState(fsa_temp.INITIAL);
		machine = fsa_temp;
		machine_text.setText(machine.toString());
		graph_view.setGraphLayout(new ISOMLayout(updateGraph()));
	}
	
	public void refresh(){
		//TODO
		sequence_text.setText("");
		machine_text.setText("");
	}
	
	private Graph<String, StateLink> updateGraph(){
		Graph<String, StateLink> g = new DirectedSparseMultigraph<String, StateLink>();
		if(machine!=null){
			ArrayList<Triple> tr = machine.getTransitions();
			for(int i = 0; i < tr.size(); i++){
				Triple t = tr.get(i);
				if(t.from_state.equals(t.to_state)){
					g.addEdge(new StateLink(String.format("%25s", t.symbol),i), t.from_state, t.to_state, EdgeType.DIRECTED);
				} else {
					g.addEdge(new StateLink(t.symbol,i), t.from_state, t.to_state, EdgeType.DIRECTED);
				}
			}
			for(String state: machine.getStates()){
				g.addVertex(state);
			}
		}
		return g;
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			refresh();
		}
	}
}
