import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageUtil_0 {
	
	
	
	public static final int BLOCKW=34;//图片大小常量
	public static final int BLOCK_MAP=17;//地图块大小常量
	public static final int GRASS=0;
	
	private int imgX;
	private int imgY;
	
	private static ImageUtil_0 instance_0=null; 
	private Image img =null;
	
	//坦克类数组的构建：
	private int tankX[][][]=new int[52][4][2];
	private int tankY[]=new int[52];
	
	
	//地图资源块//工厂
	private int mapBlockXY[][]= {{136,238,170,272}};
	
	//草块//土块//石块//水块//精灵块
	private int blockGrass[]= {136,238,153,255};
	private int blockBrick[]= {612,170,629,187};
	private int blockStone[]= {0,204,17,221};
	private int blockWater[]= {0,238,34,272};
	private int blockSpirit[]={340,238,374,272};
	
	//动画图片
	private int cartoonXY[][][]= {{{544,136},{578,136},{612,136},{646,136}},
						     	 {{680,136},{714,136},{748,136},{-34,-34}},
						     	 {{442,238},{510,238},{476,238},{-34,-34}}};
	
	
	
	private ImageUtil_0() {
		// TODO 构造统一图片类
		File f =new File("robots_sprite_0.png");//建立文件对象
		try {
			//错误处理
			img=ImageIO.read(f);//读取文件中图像
		}catch(IOException e ) {
			e.printStackTrace();
		}
		
		//为各类型坦克赋予初始X坐标值
		for(int category=0;category<52;category++) {
			for(int diraction=0;diraction<4;diraction++) {
				for(int frameState=0;frameState<2;frameState++) {
					tankX[category][diraction][frameState]=((category%4)*8+diraction*2+frameState)*BLOCKW;
					tankY[category]=category/4*BLOCKW;
				}
			}
		}
	}
	
	
	public static ImageUtil_0 getInstance() {
		if(instance_0==null) {
			instance_0=new ImageUtil_0();
		}
		return instance_0;
	}
	
	//绘制spirittankTank函数的构建
	public void drawSpirittank(Graphics g,SpiritTank spirittank) {
		imgX=tankX[spirittank.getCategory()][spirittank.getDiraction()][spirittank.getFrameState()];
		imgY=tankY[spirittank.getCategory()];
		g.drawImage(img, spirittank.getX()-BLOCKW/2, spirittank.getY()-BLOCKW/2, spirittank.getX()+BLOCKW/2, spirittank.getY()+BLOCKW/2,
				imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
	}
	
		
		
	//绘制playerTank函数的构建
	public void drawPlayerTank(Graphics g,PlayerTank playertank) {
		switch(playertank.getPlayerTankModel()) {
		case 0:
			imgX=tankX[playertank.getCategory()][playertank.getDiraction()][playertank.getFrameState()];
			imgY=tankY[playertank.getCategory()];
			g.drawImage(img, playertank.getX()-BLOCKW/2, playertank.getY()-BLOCKW/2, playertank.getX()+BLOCKW/2, playertank.getY()+BLOCKW/2,
					imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
			break;
		case 1:
			if(playertank.getJN()==0) {//技能关闭
				imgX=tankX[playertank.getCategory()][playertank.getDiraction()][playertank.getFrameState()];
				imgY=tankY[playertank.getCategory()];
				g.drawImage(img, playertank.getX()-BLOCKW/2, playertank.getY()-BLOCKW/2, playertank.getX()+BLOCKW/2, playertank.getY()+BLOCKW/2,
						imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
			}else {//技能开启
				imgX=tankX[playertank.getCategory()][playertank.getDiraction()][playertank.getFrameState()];
				imgY=tankY[playertank.getCategory()]+11*BLOCKW;
				g.drawImage(img, playertank.getX()-BLOCKW/2, playertank.getY()-BLOCKW/2, playertank.getX()+BLOCKW/2, playertank.getY()+BLOCKW/2,
						imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
			}
			break;
		case 2:
			if(playertank.getJN()==0) {//技能关闭
				imgX=tankX[playertank.getCategory()][playertank.getDiraction()][playertank.getFrameState()];
				imgY=tankY[playertank.getCategory()];
				g.drawImage(img, playertank.getX()-BLOCKW/2, playertank.getY()-BLOCKW/2, playertank.getX()+BLOCKW/2, playertank.getY()+BLOCKW/2,
						imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
			}else {//技能开启
				imgX=tankX[playertank.getCategory()][playertank.getDiraction()][playertank.getFrameState()];
				imgY=tankY[playertank.getCategory()]+11*BLOCKW;
				g.drawImage(img, playertank.getX()-BLOCKW/2, playertank.getY()-BLOCKW/2, playertank.getX()+BLOCKW/2, playertank.getY()+BLOCKW/2,
						imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
			}
			break;
		}
	}
	
	
	//绘制PlayerBullet函数的构建
	public void drawBullet(Graphics g,Bullet bullet) {
		if(bullet.getBulletType()==bullet.PLAYERBULLET) {
			switch(bullet.getDiraction()) {
			case Bullet.UP:
				g.fill3DRect(bullet.getX()-4, bullet.getY()-6, 8,12,true);
				g.fill3DRect(bullet.getX()-2, bullet.getY()-10, 4,4,true);
				break;
			case Bullet.DOWN:
				g.fill3DRect(bullet.getX()-4, bullet.getY()-6, 8,12,true);
				g.fill3DRect(bullet.getX()-2, bullet.getY()+6, 4,4,true);
				break;
			case Bullet.RIGHT:
				g.fill3DRect(bullet.getX()-6, bullet.getY()-4, 12,8,true);
				g.fill3DRect(bullet.getX()+6, bullet.getY()-2, 4,4,true);
			    break;
			case Bullet.LEFT:
				g.fill3DRect(bullet.getX()-6, bullet.getY()-4, 12,8,true);
				g.fill3DRect(bullet.getX()-10, bullet.getY()-2, 4,4,true);
			    break;
			}
		}else if(bullet.getBulletType()==bullet.SPIRITBULLET) {
			switch(bullet.getDiraction()) {
			case Bullet.UP:
				g.fill3DRect(bullet.getX()-4, bullet.getY()-6, 8,12,false);
				g.fill3DRect(bullet.getX()-2, bullet.getY()-10, 4,4,false);
				break;
			case Bullet.DOWN:
				g.fill3DRect(bullet.getX()-4, bullet.getY()-6, 8,12,false);
				g.fill3DRect(bullet.getX()-2, bullet.getY()+6, 4,4,false);
				break;
			case Bullet.RIGHT:
				g.fill3DRect(bullet.getX()-6, bullet.getY()-4, 12,8,false);
				g.fill3DRect(bullet.getX()+6, bullet.getY()-2, 4,4,false);
			    break;
			case Bullet.LEFT:
				g.fill3DRect(bullet.getX()-6, bullet.getY()-4, 12,8,false);
				g.fill3DRect(bullet.getX()-10, bullet.getY()-2, 4,4,false);
			    break;
			}
		}
		
		/*旧版炮弹
		g.drawImage(img, bullet.getX()-BLOCKW/2, bullet.getY()-BLOCKW/2, 
				bullet.getX()+BLOCKW/2, bullet.getY()+BLOCKW/2,
				imgX, imgY, imgX+BLOCKW, imgY+BLOCKW, null);
		*/
	}
	
	
	
	//绘制地图快
	//中心坐标绘制
	public void drawMapBlock(Graphics g,int x,int y,int blockIndex) {
		g.drawImage(img, x-BLOCKW/2, y-BLOCKW/2, x+BLOCKW/2,y+BLOCKW/2,
				mapBlockXY[blockIndex][0],mapBlockXY[blockIndex][1],
				mapBlockXY[blockIndex][2],mapBlockXY[blockIndex][3],null);
	}
	
	
	
	//绘制动画
	public void drawCartoon(Graphics g,Cartoon obj) {
		int [][]pos=new int [4][2];
		pos[0][0]=obj.getX()-BLOCKW/2;
		pos[0][1]=obj.getY()-BLOCKW/2;
		pos[1][0]=obj.getX()+BLOCKW/2;
		pos[1][1]=obj.getY()+BLOCKW/2;
		pos[2][0]=cartoonXY[obj.getStyle()][obj.getFrameNumber()][0];
		pos[2][1]=cartoonXY[obj.getStyle()][obj.getFrameNumber()][1];
		pos[3][0]=pos[2][0]+BLOCKW;
		pos[3][1]=pos[2][1]+BLOCKW;
		
		g.drawImage(img, pos[0][0], pos[0][1], pos[1][0], pos[1][1],
				pos[2][0], pos[2][1], pos[3][0], pos[3][1], null);
	}
	//绘制精灵块
	public void drawSpirit(Graphics g,int x,int y) {
		g.drawImage(img, x-BLOCK_MAP, y-BLOCK_MAP, x+BLOCK_MAP, y+BLOCK_MAP,
				blockSpirit[0], blockSpirit[1], blockSpirit[2], blockSpirit[3], null);
	}
	//绘制草块
	public void drawGrass(Graphics g,int x,int y) {
		g.drawImage(img, x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
				blockGrass[0], blockGrass[1], blockGrass[2], blockGrass[3], null);
	}
	
	//绘制砖块
	public void drawBrick(Graphics g,int x,int y,BlockBrick blockbrick) {
		int b_0=blockbrick.getState_0();
		int b_1=blockbrick.getState_1();
		int b_2=blockbrick.getState_2();
		int b_3=blockbrick.getState_3();
		
		if(b_0==1&&b_1==1&&b_2==1&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0], blockBrick[1], blockBrick[2], blockBrick[3], null);
		}
		if(b_0==1&&b_1==1&&b_2==1&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*4, blockBrick[1], blockBrick[2]-34*4, blockBrick[3], null);
		}
		if(b_0==1&&b_1==1&&b_2==0&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*8, blockBrick[1], blockBrick[2]-34*8, blockBrick[3], null);
		}
		if(b_0==1&&b_1==0&&b_2==1&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*2, blockBrick[1], blockBrick[2]-34*2, blockBrick[3], null);
		}
		if(b_0==0&&b_1==1&&b_2==1&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34, blockBrick[1], blockBrick[2]-34, blockBrick[3], null);
		}
		if(b_0==1&&b_1==1&&b_2==0&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*12, blockBrick[1], blockBrick[2]-34*12, blockBrick[3], null);
		}
		if(b_0==1&&b_1==0&&b_2==1&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*6, blockBrick[1], blockBrick[2]-34*6, blockBrick[3], null);
		}
		if(b_0==0&&b_1==1&&b_2==1&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*5, blockBrick[1], blockBrick[2]-34*5, blockBrick[3], null);
		}
		if(b_0==0&&b_1==1&&b_2==0&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*9, blockBrick[1], blockBrick[2]-34*9, blockBrick[3], null);
		}
		if(b_0==1&&b_1==0&&b_2==0&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*10, blockBrick[1], blockBrick[2]-34*10, blockBrick[3], null);
		}
		if(b_0==0&&b_1==0&&b_2==1&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*3, blockBrick[1], blockBrick[2]-34*3, blockBrick[3], null);
		}
		if(b_0==1&&b_1==0&&b_2==0&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*14, blockBrick[1], blockBrick[2]-34*14, blockBrick[3], null);
		}
		if(b_0==0&&b_1==1&&b_2==0&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*13, blockBrick[1], blockBrick[2]-34*13, blockBrick[3], null);
		}
		if(b_0==0&&b_1==0&&b_2==1&&b_3==0) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*7, blockBrick[1], blockBrick[2]-34*7, blockBrick[3], null);
		}
		if(b_0==0&&b_1==0&&b_2==0&&b_3==1) {
			g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
					blockBrick[0]-34*11, blockBrick[1], blockBrick[2]-34*11, blockBrick[3], null);
		}
		/*原版
		g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
				blockBrick[0]-34, blockBrick[1], blockBrick[2]-34, blockBrick[3], null);
				*/
	}
	
	//绘制石块
	public void drawStone(Graphics g,int x,int y) {
		g.drawImage(img,x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2,
				blockStone[0], blockStone[1], blockStone[2], blockStone[3], null);
	}
	
	//绘制水块
	public void drawWater(Graphics g,int x,int y,int state) {

		g.drawImage(img, x-BLOCK_MAP/2, y-BLOCK_MAP/2, x+BLOCK_MAP/2, y+BLOCK_MAP/2, 
				blockWater[0]+state*BLOCKW, blockWater[1], blockWater[2]+state*BLOCKW, blockWater[3], null);
	}
	
	
	//imageUtil end//
}














