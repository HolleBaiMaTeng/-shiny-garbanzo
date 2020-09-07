package MapEditor;

import java.awt.Graphics;

public class Operation {


	private Element element=null;
	private static Operation instance=null;
	
	private Operation() {
	}
	
	public static Operation getInstance() {
		if(instance==null) {
			instance=new Operation();
		}
		return instance;
	}
	
	
	
	public void setElment(Element elt) {
		element=elt;
	}
	
	public int getModel() {
		if(element==null) {
			return PanelMain.KONG;
		}else {
			return this.element.model;		
		}
	}
	
}























