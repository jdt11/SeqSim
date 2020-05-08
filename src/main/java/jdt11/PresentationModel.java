package jdt11;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Another data class to store the presentation model. 
 * @author Jessica Turner
 *
 */
public class PresentationModel {
	
	//The name of the presentation model
	String name;
	//List of the component presentation models
	ArrayList<CPModel> cpmodels;
	//List of all the widgets in the component presentation models
	private ArrayList<Widget> all_widgets;
	
	//Initialise the presentation model
	public PresentationModel(String n){
		name = n;
		cpmodels = new ArrayList<CPModel>();
		all_widgets = new ArrayList<Widget>();
		
	}
	
	public String toString(){
		String the_string = name + "\n";
		for(CPModel cp: cpmodels){
			the_string += cp.toString();
		}
		return the_string;
	}
	
	public ArrayList<Widget> getWidgets(){
		return all_widgets;
	}
	
	//Add widget only if it is unique
	public void addWidget(Widget w){
		boolean key = false;
		for(Widget aw: all_widgets){
			if(w.name.equals(aw.name)){
				key = true;
				break;
			}
		}
		if(key==false){
			all_widgets.add(w);
		}
	}
	
	public void removeWidget(Widget w){
		all_widgets.remove(w);
	}
	
	public String widgetsToString(){
		String the_string = "(";
		if(all_widgets!=null && all_widgets.size() > 0){
			for(int i = 0; i < all_widgets.size()-1; i++){
				the_string += all_widgets.get(i).name + ",";
			}
			the_string += all_widgets.get(all_widgets.size()-1).name + ")";
		} else {
			the_string += ")";
		}
		return the_string;
	}
	
	//Get a component presentation model based on a state
	public CPModel getCPModel(String state){
		for(CPModel cp: cpmodels){
			if(cp.name.equals(state)){
				return cp;
			}
		}
		return null;
	}
	
	@Override
	public boolean equals(Object o){
		PresentationModel pm = (PresentationModel) o;
		if(pm.name.equals(name)&&pm.cpmodels.equals(cpmodels)&&pm.all_widgets.equals(all_widgets)){
			return true;
		}
		return false;
	}
}

/**
 * Component presentation model to store the component models.
 * @author Jessica Turner
 *
 */
class CPModel {
	
	String name;
	ArrayList<Widget> widgets;
	
	//Initialise the component presentation model, based on the name, and initialise the widgets.
	public CPModel(String n){
		name = n;
		widgets = new ArrayList<Widget>();
	}
	
	public String toString(){
		String the_string = "";
		the_string += name + "\n";
		for(Widget widget: widgets){
			the_string += widget;
		}
		return the_string;
	}
	
	@Override
	public boolean equals(Object o){
		CPModel cp = (CPModel) o;
		if(cp.name.equals(name)&&cp.widgets.equals(widgets)){
			return true;
		}
		return false;
	}
	
	//Get a widget based on it's name.
	public Widget getWidget(String n){
		for(Widget w: widgets){
			if(w.name.equals(n)){
				return w;
			}
		}
		return null;
	}
}

/**
 * Widget class to store the information about the models. 
 * @author Jessica Turner
 *
 */
class Widget {
	
	//Name of the widget
	String name;
	//List of the widget categories that belong to this widget
	ArrayList<String> categories;
	//List of the behaviours that belong to this widget
	ArrayList<String> behaviours;
	
	//Initialise the widget based on the name
	public Widget(String n){
		name = n;
		categories = new ArrayList<String>();
		behaviours = new ArrayList<String>();
	}
	
	public String toString(){
		String the_string = "(";
		the_string += name + ", ";
		for(String cat: categories){
			the_string += cat + ", ";
		}
		the_string += "(";
		for(int i = 0; i < behaviours.size()-1; i++){
			the_string += behaviours.get(i) + ", ";
		}
		if(behaviours.size() > 0){
			the_string += behaviours.get(behaviours.size()-1) + "))";
		} else {
			the_string += "))";
		}
		return the_string;
	}
	
	@Override
	public boolean equals(Object o){
		Widget w = (Widget) o;
		if(w.name.equals(name)&&w.behaviours.equals(behaviours)&&w.categories.equals(categories)){
			return true;
		}
		return false;
	}
	
}
