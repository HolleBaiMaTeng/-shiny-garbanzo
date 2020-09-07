import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class PlayerTank extends Tank {

	public int PLAYERTANK_V=3;					//����Ĭ���ٶ�
	private int playertankModel=1;				//0-ԭʼ̹�� 1-��ս̹�� 2-ʱ̹ͣ��
	private int jn=0;    						//0-���ܹر� 1-���ܿ���
	public int firepower=1;						//������С �������ӵ�������
	public String JNname="NULL!!!";				//̹�˼�������
	
	public static final int fire_0_0=4;	
	public static final int fire_0_1=4;
	public static final int fire_1_0=3;	
	public static final int fire_1_1=15;
	
	public PlayerTank(int x, int y,int playertankModel) {
		super(x, y);
		this.setVelocity(Spirit.UP);
		this.setCategory(4);
		this.setVelocity(PLAYERTANK_V);
		this.playertankModel=playertankModel;
		
		//��playertankmodel�йر����ĳ�ʼ��
		switch(this.playertankModel) {
		case 0:
			this.firepower=fire_0_0;
			this.PLAYERTANK_V=1;
			this.setCategory(5);
			break;
		case 1:
			this.firepower=fire_1_0;
			this.setCategory(6);
			break;
		case 2:
			this.firepower=1;
			this.setCategory(4);
			break;
		}
	}
	
	//���ʺ���
	public int getPlayerTankModel() {
		return this.playertankModel;
	}
	public int getJN() {
		return this.jn;
	}
	public void chageJN(int jn) {
		this.jn=jn;
	}
	
	//����ת��̹�˿����͵ĺ���
	public void changePlayerTankModel(int playerTankModel) {
		this.playertankModel=playerTankModel;
		switch(playertankModel) {
		case 0:
			this.firepower=fire_0_0;
			this.setCategory(5);
			this.JNname="NULL!!!";//ע���ַ����ȿ���
			break;
		case 1:
			this.firepower=fire_1_0;
			this.setCategory(6);
			this.JNname="MORE FIRE!!!";//ע���ַ����ȿ���
			break;
		case 2:
			this.firepower=1;
			this.setCategory(4);
			this.JNname="TIME STOP!!!";//ע���ַ����ȿ���
			break;
		}
	}
	
	//����ʱͣ����
	public int getTimeSpeed() {
		if(this.playertankModel==2&&jn==1) {
			return 3;//ʱ̹ͣ��ʱ�����3��
		}else {
			return 1;
		}
	}
	
	//��ͼ����
	public void draw(Graphics g) {
		ImageUtil.getInstance().drawPlayerTank(g, this);
	}
	public void draw_0(Graphics g) {
		ImageUtil_0.getInstance().drawPlayerTank(g, this);
	}
	
	//���尴������
	public void keyPressed(KeyEvent e) {
		int key=e.getKeyCode();
		//��������
		switch(key) {
		case KeyEvent.VK_W:
			setDiraction(Spirit.UP);
			break;
		case KeyEvent.VK_D:
			setDiraction(Spirit.RIGHT);
			break;
		case KeyEvent.VK_S:
			setDiraction(Spirit.DOWN);
			break;
		case KeyEvent.VK_A:
			setDiraction(Spirit.LEFT);
			break;
		case KeyEvent.VK_K:  						//���ܼ��ж�
			if(this.playertankModel==1) {				//��̹������Ϊ1
				setVelocity(PLAYERTANK_V/2);			//������׼״̬
				this.firepower=fire_1_1;				//���ӻ���
				this.jn=1;								//��������
			}else if(this.playertankModel==2) {			//��̹������Ϊ2
				this.jn=1;								//��������
			}
			break;
		case KeyEvent.VK_NUMPAD2:  						//���ܼ��ж�
			if(this.playertankModel==1) {				//��̹������Ϊ1
				setVelocity(PLAYERTANK_V/2);			//������׼״̬
				this.firepower=fire_1_1;				//���ӻ���
				this.jn=1;								//��������
			}else if(this.playertankModel==2) {			//��̹������Ϊ2
				this.jn=1;								//��������
			}
			break;
		}
	}
	
	//�����ɼ�����
	public void keyReleased(KeyEvent e) {
		int key=e.getKeyCode();
		if(key==KeyEvent.VK_K||key==KeyEvent.VK_NUMPAD2) {//���ܼ�
			if(this.playertankModel==1) {
				this.jn=0;									//�رռ���
				setVelocity(PLAYERTANK_V);					//�ָ��ٶ�
				this.firepower=fire_1_0;					//�ָ�����
			}else if(this.playertankModel==2) {
				this.jn=0;									//�رռ���
			}
		}
	}
}
