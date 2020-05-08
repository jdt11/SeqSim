package jdt11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import tla2tex.Symbol;

/**
 * Class which stores the information for the sequence model and allows us to manipulate
 * the sequence. 
 * @author Jessica Turner
 *
 */
public class FSA {
	
	private ArrayList<String> states;
	private ArrayList<String> starts;
	private ArrayList<String> finals;
	private ArrayList<Triple> transitions;
	private ArrayList<String> alphabet;
	//HashMap<Integer,String> mapping;
	HashMap<Integer,FSA> selfContained;
	public String INITIAL = "Initialise";
	public char ABSTRACT_CHAR = '\u03A9';
	
	/*public static void main(String [] args){
		FSA fsa = new FSA();
		fsa.addState("1");
		fsa.addState("2");
	//	fsa.addState("3");
	//	fsa.addState("4");
		fsa.addFinalState("2");
		fsa.addTriple("0", "a", "1");
		fsa.addTriple("1", "a", "2");
		fsa.addTriple("2", "a", "2");
		fsa.addTriple("0", "b", "2");
	/*	fsa.addTriple("2", "a", "3");
		fsa.addTriple("1", "a", "4");
		fsa.addTriple("4", "a", "4");
		fsa.addTriple("4", "a", "1");
		fsa.addTriple("3", "a", "3");
		System.out.println("Machine is connected: " + fsa.isConnected());
		System.out.println("---------------------------------------------------------");
		ArrayList<ArrayList<String>> powerSet = fsa.powerSet(fsa.states);
		System.out.println("PowerSet = " + Arrays.toString(powerSet.toArray()));
		System.out.println("---------------------------------------------------------");
		HashMap<Integer,FSA> selfContainedSet = fsa.selfContained();
		for(Map.Entry<Integer, FSA> entry: selfContainedSet.entrySet()){
			System.out.println(entry.getValue().toString());
		}
		//System.out.println("Self-contained Set = " + Arrays.toString(selfcontainedSet.toArray()));
		System.out.println("---------------------------------------------------------");
		SequenceExecuter window = new SequenceExecuter();
		window.openSequenceModel(window.getFrame());;
		FSA machine = window.getMachine();
		System.out.println("Selected fsa 1: " + machine.isConnected());
		System.out.println("---------------------------------------------------------");
		machine.addState("7");
		machine.addTriple("1", "click", "7");
		System.out.println("Selected fsa 2: " + machine.isConnected());
		System.out.println("---------------------------------------------------------");
		machine.addState("7");
		machine.addTriple("7", "click", "7");
		System.out.println("Selected fsa 3: " + machine.isConnected());
		System.out.println("---------------------------------------------------------");
		machine.addState("7");
		machine.addTriple("7", "click", "1");
		System.out.println("Selected fsa 4: " + machine.isConnected());
		System.out.println("---------------------------------------------------------");
		powerSet = machine.powerSet(machine.states);
		System.out.println("PowerSet 1 = " + Arrays.toString(powerSet.toArray()));
		System.out.println("---------------------------------------------------------");
		selfContainedSet = machine.selfContained();
		for(Map.Entry<Integer, FSA> entry: selfContainedSet.entrySet()){
			System.out.println(entry.getValue().toString());
		}
		//System.out.println("Self-contained Set 1 = " + Arrays.toString(selfcontainedSet.toArray()));
	}*/
	
	public FSA(){
		states = new ArrayList<String>();
		states.add(INITIAL);
		starts = new ArrayList<String>();
		starts.add(INITIAL);
		finals = new ArrayList<String>();
		transitions = new ArrayList<Triple>();
		alphabet = new ArrayList<String>();
		//mapping = new HashMap<Integer,String>();
		selfContained = new HashMap<Integer,FSA>();
	}
	
	public FSA(ArrayList<String> s, ArrayList<String> st, ArrayList<String> f, ArrayList<Triple> t){
		states = s;
		starts = st;
		finals = f;
		transitions = t;
		alphabet = new ArrayList<String>();
		alphabetFunction();
		//mapping = new HashMap<Integer,String>();
		selfContained = new HashMap<Integer,FSA>();
	}
	
	public ArrayList<String> getStates(){
		return states;
	}
	
	public ArrayList<String> getStarts(){
		return starts;
	}
	
	public ArrayList<String> getFinals(){
		return finals;
	}
	
	public ArrayList<Triple> getTransitions(){
		return transitions;
	}
	
	public ArrayList<String> getAlphabet(){
		return alphabet;
	}
	
	/*public HashMap<Integer,String> getMapping(){
		return mapping;
	}*/
	
	public void setSelfContained(){
		selfContained = selfContained();
	}
	
	public void addState(String s){
		if(hasString(s,states)==false){
			states.add(s);
		}
	}
	
	public void addLetter(String l){
		if(hasString(l,alphabet)==false){
			alphabet.add(l);
		}
	}
	
	public void addStartState(String s){
		if(hasString(s,starts)==false){
			starts.add(s);
			addState(s);
		}
	}
	
	public void addFinalState(String s){
		if(hasString(s,finals)==false){
			finals.add(s);
			addState(s);
		}
	}
	
	public void addTriple(String fs, String s, String ts){
		Triple t = new Triple(fs, s, ts);
		if(hasTriple(t)==false){
			transitions.add(t);
			alphabetFunction();
		}
	}
	
	public void removeTriples(String state){
		boolean [] keys = new boolean[transitions.size()];
		for(int i = 0; i < transitions.size(); i++){
			Triple tr = transitions.get(i);
			if(tr.from_state.equals(state)){
				keys[i] = true;
			}
		}
		for(int i = 0; i < keys.length; i++){
			if(keys[i]==true){
				transitions.remove(i);
			}
		}
	}
	public void alphabetFunction(){
		alphabet.clear();
		for(Triple t: transitions){
			if(hasString(t.symbol, alphabet)==false){
				alphabet.add(t.symbol);
			}
		}
	}
	
	public boolean hasString(String s, ArrayList<String> list){
		for(String l: list){
			if(l.equals(s)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasTriple(Triple t){
		for(Triple tr: transitions){
			if(t.from_state.equals(tr.from_state)&&
			   t.symbol.equals(tr.symbol)&&
			   t.to_state.equals(tr.to_state)){
				return true;
			}
		}
		return false;
	}

	public String toString(){
		String the_string = "Q = " + listAsString(states) + "\n\u03A3 = " + listAsString(alphabet) +
				"\nS = " + listAsString(starts) + "\nF = " + listAsString(finals) + "\n\u03B4 = " 
				+ transitionsAsString();
		return the_string;
	}
	
	public ArrayList<Triple> getActions(String state){
		ArrayList<Triple> actions = new ArrayList<Triple>();
		for(Triple t: transitions){
			if(t.from_state.equals(state)){
				actions.add(t);
			}
		}
		return actions;
	}
	
	private String listAsString(ArrayList<String> list){
		String the_string = "{";
		if(list.size()> 0){
			for(int i = 0; i < list.size()-1; i++){
				the_string += list.get(i) + ",";
			}
			the_string += list.get(list.size()-1);
		}
		return the_string + "}";
	}
	
	private String transitionsAsString(){
		String the_string = "{";
		if(transitions.size() > 0){
			for(int i = 0; i < transitions.size()-1; i++){
				the_string += transitions.get(i).toString() + ",";
			}
			the_string += transitions.get(transitions.size()-1);
		}
		return the_string + "}";
	}
	
	public String [][] get2DArrayTransitions(){
		String [][] dummy = new String [transitions.size()][3];
		for(int l = 0; l < transitions.size(); l++){
			Triple t = transitions.get(l);
			dummy[l][0] = t.from_state;
			dummy[l][1] = t.symbol;
			dummy[l][2] = t.to_state;
		}
		return dummy;
	}
	
	/*public String[][] hashMapTo2DArray(){
		int row = 0;
		String [][] dummy = new String [mapping.size()][2];
		for(Integer key: mapping.keySet()){
			dummy[row][0] = Integer.toString(key);
			dummy[row][1] = mapping.get(key);
			row++;
		}
		return dummy;
	}*/
	
	public ArrayList<Triple> getRandom(){
		ArrayList<Triple> list = new ArrayList<Triple>();
		Random rand = new Random();
		String state = starts.get(0);
		while(true){
			ArrayList<Triple> nextAction = getActions(state);
			System.out.println("state = " + state);
			System.out.println("nextAction.size() = " + nextAction.size());
			System.out.println("nextAction = " + nextAction.toString());
			if(nextAction.size()>0){
				int random = rand.nextInt(nextAction.size());
				Triple selected = nextAction.get(random);
				list.add(selected);
				state = selected.to_state;
				if(hasString(state,finals)==true){
					int cont = rand.nextInt(2);
					if(cont==0){
						break;
					}
				}
			} else {
				System.out.println("Break");
				break;
			}
		}
		return list;
	}
	
	public void switchFinal(String f){
		finals = new ArrayList<String>();
		finals.add(f);
	}
	
	/*public void updateState(String num, String name){
		int key = Integer.parseInt(num);
		mapping.remove(key);
		mapping.put(key, name);
	}*/
	
	public void removeState(String num){
		//mapping.remove(key);
		removeFromList(num, states);
		removeFromList(num, starts);
		removeFromList(num, finals);
		ArrayList<Triple> tr = getAllTransitionsWithState(num);
		transitions.removeAll(tr);
	}
	
	private void removeFromList(String s, ArrayList<String> list){
		int index = -1;
		for(int i = 0; i < list.size(); i++){
			String l = list.get(i);
			if(l.equals(s)){
				index = i;
				break;
			}
		}
		if(index >= 0){
			list.remove(index);
		}
	}
	
	private ArrayList<Triple> getAllTransitionsWithState(String num){
		ArrayList<Triple> temp = new ArrayList<Triple>();
		for(Triple t: transitions){
			if(t.from_state.equals(num)||t.to_state.equals(num)){
				temp.add(t);
			}
		}
		return temp;
	}
	
	public void removeLetter(String l){
		removeFromList(l,alphabet);
		ArrayList<Triple> tr = getAllTransitionsWithLetter(l);
		transitions.removeAll(tr);
	}
	
	private ArrayList<Triple> getAllTransitionsWithLetter(String l){
		ArrayList<Triple> temp = new ArrayList<Triple>();
		for(Triple t: transitions){
			if(t.symbol.equals(l)){
				temp.add(t);
			}
		}
		return temp;
	}
	
	public void removeTriple(Triple t){
		transitions.remove(t);
	}
	
	public boolean isConnected(){
		//System.out.println("isConnected()");
		FSA dual = this.dual();
	//	System.out.println("The transitions = " + Arrays.toString(this.transitions.toArray()));
	//	System.out.println("Dual transitions = " + Arrays.toString(dual.transitions.toArray()));
		if(dual.starts.size()>0){
			String start = dual.starts.get(0);
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(start);
			ArrayList<String> visited_states = visitedPaths(start, temp, dual);
	//		System.out.println("Visited states: " + Arrays.toString(visited_states.toArray()));
			if(checkListsEqual(states, visited_states)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	private ArrayList<String> visitedPaths(String state, ArrayList<String> temp, FSA dual){
		
		ArrayList<Triple> tr = dual.getActions(state);
	//	System.out.println("STATE: " + state + " TR: " + Arrays.toString(tr.toArray()));
		for(Triple t: tr){
	//		System.out.println("STATE: " + state + " TRIPLE: " + t.toString());
			if(hasString(t.to_state,temp)==false&&!t.to_state.equals(t.from_state)){
				temp.add(t.to_state);
				visitedPaths(t.to_state, temp,dual);
			}
		}
		return temp;
	}
	
	private boolean checkListsEqual(ArrayList<String> list_one, ArrayList<String> list_two){
		if(list_one.size()!=list_two.size()){
			return false;
		}
		boolean [] keys = new boolean[list_one.size()];
		for(int i = 0; i < list_one.size(); i++){
			String item = list_one.get(i);
			for(String l2: list_two){
				if(item.equals(l2)){
					keys[i] = true;
					break;
				}
			}
		}
		boolean key = true;
		for(boolean b: keys){
			key &= b;
		}
		return key;
	}
	
	public FSA copy(){
		FSA fsa_copy = new FSA();
		fsa_copy.starts = new ArrayList<String>();
		fsa_copy.states = new ArrayList<String>();
		for(String a: alphabet){
			fsa_copy.addLetter(a);
		}
		for(String f: finals){
			fsa_copy.addFinalState(f);
		}
		/*for(Map.Entry<Integer, String> entry: mapping.entrySet()){
			fsa_copy.mapping.put(entry.getKey(), entry.getValue());
		}*/
		for(String s: starts){
			fsa_copy.addStartState(s);
		}
		for(String s: states){
			fsa_copy.addState(s);
		}
		for(Triple t: transitions){
			fsa_copy.addTriple(t.from_state, t.symbol, t.to_state);
		}
		return fsa_copy;
	}
	
	private FSA dual(){
		FSA dual = this.copy();
		ArrayList<String> temp = dual.starts;
		dual.starts = dual.finals;
		dual.finals = temp;
		for(Triple t: dual.transitions){
			String s = t.from_state;
			t.from_state = t.to_state;
			t.to_state = s;
		}
		return dual;
	
	}
	
	public HashMap<Integer, FSA> selfContained(){
		ArrayList<ArrayList<String>> allsets = powerSet(states);
		//ArrayList<ArrayList<String>> sc = new ArrayList<ArrayList<String>>();
		int key = 0;
		HashMap<Integer,FSA> selfContainedMachines = new HashMap<Integer,FSA>();
		for(ArrayList<String> set: allsets){
			if(set.size() <= 1&&!set.isEmpty()){
				ArrayList<Triple> t = findTriples(set);
				FSA temp = new FSA(set,set,set,t);
				selfContainedMachines.put(key, temp);
				key++;
			} else if(listsAreEqual(set,states)){
				//System.out.println("set = " + Arrays.toString(set.toArray()) + "; states = " + Arrays.toString(states.toArray()) +
				//		"; listsAreEqual(set,states) = " + listsAreEqual(set,states));
				FSA temp = new FSA(set,set,set,findTriples(set));
				selfContainedMachines.put(key, temp);
				key++;
			} else if(set.size() > 1){
				//Find the triples, start, and finals for this set
				ArrayList<Triple> triples = findTriples(set);
				ArrayList<String> s = findStart(set);
				ArrayList<String> f = findFinal(set);
				//Check we have complete set, if yes then add this set to set of self-contained machines
				if(s!=null&&f!=null&&triples!=null&&
						s.size() > 0&&f.size() > 0 &&triples.size() > 0){
					//System.out.println("s = " + Arrays.toString(s.toArray()) + "; f = " + Arrays.toString(f.toArray()) + "; triples = " +
				//Arrays.toString(triples.toArray()));
					FSA temp = new FSA(set,s,f,triples);
					if(!selfContainedMachines.containsValue(temp)&&temp.isConnected()){
						selfContainedMachines.put(key, temp);
						key++;
					}
				}
			}
		}
		return selfContainedMachines;
		//return sc;
	}
	
	public ArrayList<Triple> findTriples(ArrayList<String> set){
		//Add all the transitions for this set
		ArrayList<Triple> triples = new ArrayList<Triple>();
		for(String s: set){
			ArrayList<Triple> deltas = getActions(s);
			for(Triple d: deltas){
				if(set.contains(d.from_state)&&set.contains(d.to_state)){
					triples.add(d);
				}
			}
		}
		//System.out.println("The set = " + Arrays.toString(set.toArray()) + "; The triples = " + Arrays.toString(triples.toArray()));
		return triples;
	}
	
	public ArrayList<String> findStart(ArrayList<String> set){
		ArrayList<String> setStarts = new ArrayList<String>();
		//if there's no reason to find a start, then return null
		if(set.size()<= 0){
			return null;
		}
		//Find the outter states
		ArrayList<String> not_in_Qs = (ArrayList<String>) states.clone();
		not_in_Qs.removeAll(set);
		//Check if the start state is in the set
		for(String s: set){
			for(String st: starts){
				if(!setStarts.contains(s)&&s.equals(st)){
					setStarts.add(s);
				}
			}
		}
		//Find a start state with incoming transitions from not in Qs
		for(String qdash: not_in_Qs){
			ArrayList<Triple> tr = getActions(qdash);
			for(Triple t: tr){
				for(String s: set){
					if(!setStarts.contains(s)&&t.to_state.equals(s)){
						setStarts.add(s);
					}
				}
			}
		}
		//If we've found at least one start state then return this set
		if(setStarts.size()==1){
			return setStarts;
		} else {
			return null;
		}
		
	}
	
	public ArrayList<String> findFinal(ArrayList<String> set){
		//if there's no reason to find a final, then return null
		if(set.size()<= 0){
			return null;
		}
		ArrayList<String> setFinals = new ArrayList<String>();
		ArrayList<String> not_in_Qs = (ArrayList<String>) states.clone();
		not_in_Qs.removeAll(set);
		//See if final state is included in set
		for(String s: set){
			for(String f: finals){
				if(!setFinals.contains(f)&&s.equals(f)){
					setFinals.add(s);
				}
			}
		}
		//Find a state with outgoing transitions in not in Qs, this will be a final state
		for(String s: set){
			ArrayList<Triple> temp = getActions(s);
			for(Triple t: temp){
				for(String qdash: not_in_Qs){
					if(!setFinals.contains(s)&&t.to_state.equals(qdash)){
						setFinals.add(s);
					}
				}
			}
		}
		//If we've found at least one final state then return this set
		if(setFinals.size()==1){
			return setFinals;
		} else {
			return null;
		}
	}
	
	private boolean listsAreEqual(ArrayList<String> one, ArrayList<String> two){
		boolean [] markers = new boolean[one.size()];
		if(one.size()!=two.size()||one.size()<=0||two.size()<=0){
			return false;
		} else {
			for(int i = 0; i < one.size(); i++){
				String first = one.get(i);
				for(int j = 0; j < two.size(); j++){
					String second = two.get(j);
					if(first.equals(two)){
						markers[i] = true;
						break;
					}
				}
			}
			boolean answer = true;
			for(boolean b: markers){
				answer &= b;
			}
			return answer;
		}
	}
	
	//https://rosettacode.org/wiki/Power_set#Java used as reference (Java iteration)
	private ArrayList<ArrayList<String>> powerSet(ArrayList<String> states){
		ArrayList<ArrayList<String>> ps = new ArrayList<ArrayList<String>>();
		ps.add(new ArrayList<String>());
		for(String s: states){
			ArrayList<ArrayList<String>> newPs = new ArrayList<ArrayList<String>>();
			for(ArrayList<String> subset: ps){
				newPs.add(subset);
			//	ArrayList<String> new_subset = new ArrayList<String>();
			//	Collections.copy(new_subset, subset);
			//	new_subset.addAll(subset);
				ArrayList<String> new_subset = new ArrayList<String>(subset);
				new_subset.add(s);
				newPs.add(new_subset);
			}
			ps = newPs;
		}
		return ps;
	}
	
	public FSA getAbstractMachine(int [] indices){
		//Get the selected self-contained machine
		//The abstract machine is a copy of the original machine
		FSA machine_a = this.copy();
		//For every self-contained machine
		for(int index: indices){
			FSA scm = selfContained.get(index);
			//We remove all the states of the self-contained machine from the abstract machine
			machine_a.getStates().removeAll(scm.getStates());
			//Then we add the new abstract state
			String abstract_state = String.valueOf(ABSTRACT_CHAR) + index;
			machine_a.addState(abstract_state);
			//If the self-contained machine has the start state of the original machine
			if(scm.hasString(starts.get(0), scm.getStarts())){
				//Then make the abstract state the start state
				machine_a.getStarts().clear();
				machine_a.addStartState(abstract_state);
			}
			//If the self-contained machine has the final state of the original machine
			if(scm.hasString(finals.get(0), scm.getFinals())){
				//Then make the abstract state the final state
				machine_a.getFinals().clear();
				machine_a.addFinalState(abstract_state);
			}
			//We remove all the transitions of the self-contained machine from the abstract machine
			machine_a.getTransitions().removeAll(scm.getTransitions());
			String start_state = scm.getStarts().get(0);
			String final_state = scm.getFinals().get(0);
			//If the to state is the start state of the self-contained machine then make it the abstract state, 
			//similarly for the final state.
			for(Triple t: machine_a.getTransitions()){
				if(t.to_state.equals(start_state)){
					t.to_state = abstract_state;
				}
				if(t.from_state.equals(final_state)){
					t.from_state = abstract_state;
				}
			}
		}
		//update the alphabet
		machine_a.alphabetFunction();
		return machine_a;
		
	}
	
	public FSA getExpandedMachine(int index, HashMap<Integer, FSA> orig_sc){
		//Make a copy of the abstract machine
		FSA machine_n = this.copy();
		//Get the selected self-contained machine for expansion
		FSA scm = orig_sc.get(index);
		String state_name = String.valueOf(ABSTRACT_CHAR) + index;
		//Remove the abstract state and add the self-contained states
		machine_n.states.remove(state_name);
		System.out.println("scm = " + scm);
		for(String state: scm.getStates()){
			machine_n.addState(state);
		}
		//Get start state of n (note should only have 1 start state)
		String start_state = machine_n.starts.get(0);
		//If the start state is the abstract state, set new machine to start state of self-contained machine
		if(start_state.equals(state_name)){
			machine_n.starts = scm.starts;
		}
		//Similarly for the final state
		String final_state = machine_n.finals.get(0);
		if(final_state.equals(state_name)){
			machine_n.finals = scm.finals;
		}
		String scm_start = scm.starts.get(0);
		String scm_final = scm.finals.get(0);
		//Overwrite ingoing transitions which went to abstract state with start state of scm,
		//and overwrite outgoing transitions which went from abstract state with final state of scm
		for(Triple t: machine_n.transitions){
			if(t.to_state.equals(state_name)){
				t.to_state = scm_start;
			}
			if(t.from_state.equals(state_name)){
				t.from_state = scm_final;
			}
		}
		//Add all transitions from self-contained machine
		for(Triple t: scm.transitions){
			machine_n.addTriple(t.from_state, t.symbol, t.to_state);
		}
		//update the alphabet
		machine_n.alphabetFunction();
		return machine_n;
	}
}

class Triple {
	
	String from_state;
	String symbol;
	String to_state;
	
	public Triple(String fs, String s, String ts){
		from_state = fs;
		symbol = s;
		to_state = ts;
	}
	
	public String toString(){
		return "(" + from_state + "," + symbol + "," + to_state + ")";
	}
	
	public String[] toArray(){
		return new String[]{from_state,symbol,to_state};
	}
	
	@Override 
	public boolean equals(Object o){
		Triple t = (Triple) o;
		if(from_state.equals(t.from_state)&&symbol.equals(t.symbol)&&to_state.equals(t.to_state)){
			return true;
		}
		return false;
	}
	
	public Triple copy(){
		return new Triple(from_state,symbol,to_state);
	}
}
