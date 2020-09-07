package MapEditor;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {

	public static final int BLOCKW=34;
	
	private static ImageUtil instance=null;
	private Image img;
	
	private int blockGrass[]= {136,238,170,272};
	private int blockBrick[]= {612,170,629,187};
	private int blockStone[]= {0,204,17,221};
	private int blockWater[]= {0,238,34,272};
	private int blockSpirittank[][]= {{340,238,374,272},{0,0,34,34}};
	
	private ImageUtil() {
		File f=new File("robots_sprite.png");		//建立图片对象
		try {
			img=ImageIO.read(f);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static ImageUtil getInstance() {
		if(instance==null) {
			instance=new ImageUtil();
		}
		return instance;
	}
	
	//绘制草块
	public void drawGrass(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockGrass[0], blockGrass[1], blockGrass[2], blockGrass[3], null);
	}
	public void drawGrass01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				136,238, 153, 255, null);
	}
	//绘制水块
	public void drawWater(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockWater[0], blockWater[1], blockWater[2], blockWater[3], null);
	}
	public void drawWater01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				0,238,17,255, null);
	}
	
	//绘制石块
	public void drawStone(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
		g.drawImage(img, x+BLOCKW/2, y, x+BLOCKW, y+BLOCKW/2,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
		g.drawImage(img, x, y+BLOCKW/2, x+BLOCKW/2, y+BLOCKW,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
		g.drawImage(img, x+BLOCKW/2, y+BLOCKW/2, x+BLOCKW, y+BLOCKW,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
	}
	public void drawStone01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
	}
	
	//绘制砖块
	public void drawBrick(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
		g.drawImage(img, x+BLOCKW/2, y, x+BLOCKW, y+BLOCKW/2,
				blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
		g.drawImage(img, x, y+BLOCKW/2, x+BLOCKW/2, y+BLOCKW,
				blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
		g.drawImage(img, x+BLOCKW/2, y+BLOCKW/2, x+BLOCKW, y+BLOCKW,
				blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
	}
	public void drawBrick01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
	}
	//绘制精灵坦克工厂
	public void drawSpirittank(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockSpirittank[0][0], blockSpirittank[0][1], blockSpirittank[0][2], blockSpirittank[0][3], null);
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockSpirittank[1][0], blockSpirittank[1][1], blockSpirittank[1][2], blockSpirittank[1][3], null);
	}
	public void drawSpirittank01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockSpirittank[0][0], blockSpirittank[0][1], blockSpirittank[0][2], blockSpirittank[0][3], null);
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				blockSpirittank[1][0], blockSpirittank[1][1], blockSpirittank[1][2], blockSpirittank[1][3], null);
	}
	
	//绘制空块
	public void drawKong(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW, y+BLOCKW,
				8*34,6*34,9*34,7*34, null);
	}
	public void drawKong01(Graphics g,int x,int y) {//左上角坐标x，y
		g.drawImage(img, x, y, x+BLOCKW/2, y+BLOCKW/2,
				8*34+8,6*34+8,8*34+25,6*34+25, null);
	}
	
	
	
	
}







































