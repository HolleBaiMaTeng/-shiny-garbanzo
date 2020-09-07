package MapEditor;

import java.awt.Graphics;

public class EltSpirittank extends Element {

	public EltSpirittank(int x, int y) {
		super(x, y);
		this.model=PanelMain.SPIRIT1;
		
	}

	public void draw(Graphics g) {
		ImageUtil.getInstance().drawSpirittank(g, x, y);
		super.draw(g);
	}
}
