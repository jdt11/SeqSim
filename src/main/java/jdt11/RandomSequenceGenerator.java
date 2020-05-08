package jdt11;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class RandomSequenceGenerator extends JFrame {

	private JPanel contentPane;
	private SequenceExecuter parent;
	private ArrayList<ArrayList<Triple>> sequences;
	private JList list;
	/**
	 * Create the frame.
	 */
	public RandomSequenceGenerator(SequenceExecuter p) {
		parent = p;
		sequences = new ArrayList<ArrayList<Triple>>();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(700, 200, 500, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("Random Sequence Generator");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				parent.refreshAll();
			}
		});
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.setBounds(377, 243, 117, 29);
		btnGenerate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				generateRandomSeq();
			}
		});
		contentPane.add(btnGenerate);
		
		list = new JList(new DefaultListModel());
		list.setBounds(6, 6, 488, 236);
		JScrollPane list_scroller = new JScrollPane(list);
		list_scroller.setBounds(6, 6, 488, 236);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		MouseListener ml = new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				parent.refreshB();
				parent.refreshSeq();
				int selectedIndex = ((JList) e.getSource()).getSelectedIndex();
				ArrayList<Triple> sequence = sequences.get(selectedIndex);
				executeSequence(sequence);
			}
		};
		list.addMouseListener(ml);
		contentPane.add(list_scroller);
	}
	
	public void executeSequence(ArrayList<Triple> seq){
		for(Triple t: seq){
			if(t.from_state.equals("0")){
				parent.performInitialTrace(t.toArray());
			} else {
				parent.performTrace(t.toArray());
			}
			System.out.println(t.toString() + ": finished");
		}
	}
	
	public void generateRandomSeq(){
		ArrayList<Triple> random_seq = parent.getMachine().getRandom();
		while(hasSequence(random_seq)==true){
			System.out.println(hasSequence(random_seq));
			random_seq = parent.getMachine().getRandom();
		}
		sequences.add(random_seq);
		((DefaultListModel) list.getModel()).addElement(random_seq);
	}
	
	public boolean hasSequence(ArrayList<Triple> seq){
		for(ArrayList<Triple> s: sequences){
			if(seq.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setVisible(boolean b){
		super.setVisible(b);
		parent.refreshAll();
	}

}