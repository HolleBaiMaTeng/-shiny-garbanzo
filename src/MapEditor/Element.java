package MapEditor;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Element {
	
	protected int x;
	protected int y;
	public int model=0;				//反应类型
	private boolean isSelected;
	
	public Element(int x,int y) {
		this.x=x;
		this.y=y;
		isSelected=false;
	}
	
	public Element click(int x,int y) {
		if(x>=this.x&&x<=this.x+ImageUtil.BLOCKW&&
				y>=this.y&&y<=this.y+ImageUtil.BLOCKW) {
			isSelected=true;
			return this;
		}else {
			isSelected=false;
			return null;
		}
	}
	
	public void draw(Graphics g) {
		if(isSelected) {
			Graphics2D g2=(Graphics2D)g;
			Stroke stroke=new BasicStroke(2.0f);
			g2.setStroke(stroke);
			g2.drawRect(x, y, ImageUtil.BLOCKW, ImageUtil.BLOCKW);
		}
	}

	public void draw01(Graphics g) {
		if(isSelected) {
			Graphics2D g2=(Graphics2D)g;
			Stroke stroke=new BasicStroke(2.0f);
			g2.setStroke(stroke);
			g2.drawRect(x, y, ImageUtil.BLOCKW/2, ImageUtil.BLOCKW/2);
		}
	}
	
}
