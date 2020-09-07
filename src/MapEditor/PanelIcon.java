package MapEditor;

import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class PanelIcon extends JPanel implements MouseListener{

	private FrameMain frameMain;
	private ArrayList<Element> elements=new ArrayList<Element>();
	private Element eltSelected;
	
	public PanelIcon(FrameMain frameMain) {
		this.frameMain=frameMain;
		eltSelected=null;
		
		elements.add(new EltGrass(10,50));
		elements.add(new EltStone(50,50));
		elements.add(new EltBrick(10,100));
		elements.add(new EltWater(50,100));
		elements.add(new EltSpirittank(10,150));
		elements.add(new kong(50,150));
		this.addMouseListener(this);
	}
	
	public void initPanel() {
		setBorder(new EtchedBorder());				//设置边框
		Label label=new Label("   地图元素坐标   ");
		add(label);
	}
	
	public void paint(Graphics g) {
		for(int i=0;i<elements.size();i++) {
			elements.get(i).draw(g);
		}
	}

	public Element getEltSelected() {
		return eltSelected;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Element elt=null;
		for(int i=0;i<elements.size();i++) {
			Element tempElt;
			tempElt=elements.get(i).click(e.getX(), e.getY());
			if(tempElt!=null) {
				elt=tempElt;
			}
		}
		Operation.getInstance().setElment(elt);
		frameMain.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

















