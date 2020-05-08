package jdt11;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class PIMViewer extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private VisualizationViewer<String, StateLink> graph_view;
	private PresentationInteractionModel pim;

	/**
	 * Create the frame.
	 */
	public PIMViewer(SequenceExecuter p) {
		parent = p; 
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setTitle("PIM Viewer");
	}
	
	public void generateGraph(){
		pim = parent.getPIM();
		Graph<String, StateLink> g = new DirectedSparseMultigraph<String, StateLink>();
		if(pim!=null){
			ArrayList<Transition> transitions = pim.getTransitions();
			for(int i = 0; i < transitions.size(); i++){
				Transition t = transitions.get(i);
				System.out.println(i + ": " + t.toString());
				g.addEdge(new StateLink(t.getIBeh(), i), t.getStart(), t.getEnd(), EdgeType.DIRECTED);
				if(g.containsVertex(t.getStart())==false){
					g.addVertex(t.getStart());
				}
				if(g.containsVertex(t.getEnd())==false){
					g.addVertex(t.getEnd());
				}
			}
		}
		System.out.println(g.toString());
		Layout<String, StateLink> layout = new CircleLayout(g);
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
		graph_view.getRenderContext().setVertexShapeTransformer(vertexShape);
		//graph_view.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		graph_view.setBackground(Color.WHITE);
		graph_view.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		graph_view.setGraphMouse(gm);
		graph_view.setGraphLayout(new ISOMLayout(g));
		contentPane.add(graph_view);
		
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
			generateGraph();
		}
	}

}
