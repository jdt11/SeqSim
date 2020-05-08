package jdt11;

/***
 * @author Jessica Turner
 * This creates the ZModel api so that we can execute the B Model. 
 */

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Stage;

import de.prob.scripting.Api;

public class ZModel {
	
	private Api api;

	@Inject
	public ZModel(Api api) {
		this.api = api;
	}
	
	public Api getApi(){
		return api;
	}

}
