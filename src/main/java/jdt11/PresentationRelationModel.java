package jdt11;

import java.util.ArrayList;

/**
 * This is essentially a data class to store the presentation model relations.
 * @author Jessica Turner
 *
 */
public class PresentationRelationModel {
	
	//List to store the relations
	private ArrayList<Relation> relations;
	
	//Initialise the relation model
	public PresentationRelationModel(){
		relations = new ArrayList<Relation>();
	}
	
	public ArrayList<Relation> getRelations(){
		return relations;
	}
	
	public void addRelation(String beh, String op){
		relations.add(new Relation(beh, op));
	}
	
	public void removeRelation(Relation r){
		relations.remove(r);
	}
	
	//Return the relation as a 2D array so that it can be stored as a table
	public String[][] get2DArray(){
		String [][] temp = new String[relations.size()][2];
		for(int i = 0; i < relations.size(); i++){
			Relation r = relations.get(i);
			temp[i][0] = r.getBehaviour();
			temp[i][1] = r.getOperation();
		}
		return temp;
	}
	
	public String toString(){
		String the_string = "{";
		for(int i = 0; i < relations.size()-1; i++){
			Relation r = relations.get(i);
			the_string += r.toString() + ",";
		}
		if(relations.size()>0){
			Relation r = relations.get(relations.size()-1);
			the_string += r.toString();
		}
		return the_string + "}";
	}

	//Get a relation based on the behaviour name
	public Relation getRelation(String beh){
		for(Relation r: relations){
			if(r.getBehaviour().equals(beh)){
				return r;
			}
		}
		return null;
	}

}

class Relation {
	
	private String behaviour;
	private String operation;
	
	public Relation(String b, String o){
		behaviour = b;
		operation = o;
	}
	
	public String getBehaviour(){
		return behaviour;
	}
	
	public String getOperation(){
		return operation;
	}
	
	public String toString(){
		return "(" + behaviour + "," + operation + ")";
	}
	
	@Override
	public boolean equals(Object o){
		Relation r = (Relation) o;
		if(r.behaviour.equals(behaviour)&&r.operation.equals(operation)){
			return true;
		}
		return false;
	}
}
