package jdt11;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;

import de.prob.animator.domainobjects.AbstractEvalResult;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.scripting.Api;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;
import example.MyGuiceConfig;

public class SmallExample {
	
	private static Injector INJECTOR = Guice.createInjector(Stage.PRODUCTION, new MyGuiceConfig());
	private Api api;

	@Inject
	public SmallExample(Api api) {
		this.api = api;
	}
	
	public void buildExample() throws Exception {
		
		//Get the path to the B machine
		Path path = Paths.get(getClass().getResource("/InternalCounter.mch").toURI());
		//Load the state space
		StateSpace stateSpace = api.b_load(path.toAbsolutePath().toString());
		
		//Create a new trace to execute interaction sequence
		Trace t = new Trace(stateSpace);
		
		//Get the available transitions from this state
		Set<Transition> transitions = t.getNextTransitions();
		
		for(Transition tr: transitions){
			System.out.println(tr);
			//Execute a transition based on a name
			t = t.execute(tr.getName(), tr.getParameterPredicates());
		}
		
	/*	Any random event for 50 steps
	 * for(int i = 0; i < 50; i++){
			t = t.anyEvent(null);
		} */
		
		//Initialise the machine
	//	t = t.anyEvent(0);
		
		//Increment 10 times
		for(int i = 1; i <= 10; i++){
			t = t.anyEvent("Increment");
		}
		
		//Decrement 5 times
		for(int i = 0; i < 5; i++){
			t = t.anyEvent("Decrement");
		}
		
		//Get the current state of the machine
		State state = t.getCurrentState();
		
		 //Get each individual value
		 //Print out the current state, value should be 5
		 Set<Entry<IEvalElement, AbstractEvalResult>> entrySet = state.getValues().entrySet();
			for (Entry<IEvalElement, AbstractEvalResult> entry : entrySet) {
				System.out.println(entry.getKey() + " -> " + entry.getValue());
		}
		
		
		
		
	}
	
	public static void main(String[] args) throws Exception {

		SmallExample m = INJECTOR.getInstance(SmallExample.class);
		m.buildExample();
		System.exit(0);
	}

}
