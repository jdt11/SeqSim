package jdt11;

import java.util.ArrayList;

/**
 * Presentation interaction model data class. 
 * @author Jessica Turner
 *
 */
public class PresentationInteractionModel {
	
	//List to store the transitions related to this model.
	private ArrayList<Transition> transitions;
	//String to store the starting I behaviour.
	String start;
	
	//Initialise the presentation interaction model.
	public PresentationInteractionModel(){
		transitions = new ArrayList<Transition>();
		start = null;
	}
	
	public ArrayList<Transition> getTransitions(){
		return transitions;
	}
	
	public void addTransition(String start, String end, String i_beh){
		transitions.add(new Transition(start,end,i_beh));
	}
	
	public void removeTransition(Transition t){
		transitions.remove(t);
	}
	
	public String getStart(){
		return start;
	}
	
	//get the 2D array to display in a table
	public String[][] get2DArray(){
		String [][] temp = new String[transitions.size()][3];
		for(int i = 0; i < transitions.size(); i++){
			Transition t = transitions.get(i);
			temp[i][0] = t.getStart();
			temp[i][1] = t.getIBeh();
			temp[i][2] = t.getEnd();
		}
		return temp;
	}
	
	//get just the transition names
	public ArrayList<String> getTransitionNames(){
		ArrayList<String> temp = new ArrayList<String>();
		for(Transition t: transitions){
			if(hasString(t.getStart(),temp)==false){
				temp.add(t.getStart());
			}
		}
		return temp;
	}
	
	public boolean hasString(String tr, ArrayList<String> list){
		for(String s: list){
			if(tr.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	//Get the next state based on the current state and selected I behaviour.
	public Transition getNextState(String current_state, String ibeh){
		for(Transition t: transitions){
			if(t.getStart().equals(current_state)&&t.getIBeh().equals(ibeh)){
				return t;
			}
		}
		return null;
	}
	
	//Get the next transitions based on the current state and selected I behaviour.
	public ArrayList<Transition> getNextTransitions(String name){
		ArrayList<Transition> temp = new ArrayList<Transition>();
		for(Transition t: transitions){
			if(t.getStart().equals(name)){
				temp.add(t);
			}
		}
		return temp;
	}

}

/**
 * Transition class to store the transition data. 
 * @author Jessica Turner
 *
 */
class Transition{
	
	//The from state of transition
	private String start;
	//The to state of the transition
	private String end;
	//The I behaviour which executes this transition
	private String i_behaviour;
	
	public Transition(String s, String e, String i){
		start = s;
		end = e;
		i_behaviour = i;
		
	}
	
	public String getStart(){
		return start;
	}
	
	public String getEnd(){
		return end;
	}
	
	public String getIBeh(){
		return i_behaviour;
	}
	
	@Override
	public boolean equals(Object o){
		Transition t = (Transition) o;
		if(t.start.equals(start)&&t.end.equals(end)&&t.i_behaviour.equals(i_behaviour)){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return "(" + start + "," + i_behaviour + "," + end + ")";
	}
	
}
