import java.util.Random;

public class SpiritTank001 extends SpiritTank {

	private Map map;
	public int move_state=0;		//0-����1-�н�
	private int[] mb= {0,0};
	public static final int SIPIRITTANK_V=2;
	private int fangxiang=0;		//�����н��ķ���任����
	
	
	public SpiritTank001(int x, int y,Map map) {
		super(x, y);
		this.map=map;
		mb[0]=x/17;//��ʼ��Ŀ���
		mb[1]=y/17;//��ʼ��Ŀ���
		this.setVelocity(SIPIRITTANK_V);
		this.setCategory(44);
	}

	
		//���㺯��
		public void caculateDate001(int[][] map) {
			//֡������
			frameState++;
			if(frameState==aliveFrameCount) {
				frameState=0;
			}
			Random random=new Random();
			int x=this.getX();
			int y=this.getY();
			int i1=x/17;
			int j1=y/17;
			if(x%17>3) {
				i1++;
			}
			if(y%17>3) {
				j1++;
			}
			
			if(mol(mb[0]*17,x)>34||mol(mb[1]*17,y)>34) {
				System.out.println("rrrrrr");
				System.out.println(mb[0]*17+"  "+mb[1]*17);
				System.out.println(x+"  "+y);
				System.out.println("Diraction"+this.getDiraction());
			}
			
			
			if(map[mb[0]][mb[1]]==0) {
				this.setVelocity(0);  							//ԭ�ؾ�ֹģʽ�����򲻱����״̬
			}else{
				this.setVelocity(SIPIRITTANK_V);  				//�ָ�����
				if(move_state==1) { 	//ǰ��״̬(�ж��Ƿ񵽴�Ŀ���)(ͬʱ������Ŀ���ĳ���)
					if(x<=mb[0]*17+2&&x>=mb[0]*17-2&&y<=mb[1]*17+2&&y>=mb[1]*17-2) {
						move_state=0;	//����״̬
					}
				}
				if(move_state==0) {		//����״̬������ѡȡ��һ��Ŀ���,ֱ�Ӷ�Ŀ�����в�����
					//����ѡȡĿ���
					int lingpai01=0;							
					int lingpai02=0;
					//�ݴ�ԭĿ���
					int[] o1=new int[2];
					o1[0]=mb[0];							//Ŀ��㴢�治��
					o1[1]=mb[1];							//Ŀ��㴢�治��
					int mb_mol=1;
					if(mb[1]-1>=0) {
						if(map[o1[0]][o1[1]]>map[o1[0]][o1[1]-1]) {
							mb[0]=o1[0];					//����Ŀ���
							mb[1]=o1[1];					//����Ŀ���
							mb[1]--;						//�޸�Ŀ���
							mb_mol=map[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
							lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
							move_state=1;					//����ǰ��ģʽ
							this.setDiraction(UP); 			//��������
						}
					}
					if(mb[1]+1<=36) {
						if(map[o1[0]][o1[1]]>map[o1[0]][o1[1]+1]) {
							if(map[o1[0]][o1[1]+1]<mb_mol) {
								mb[0]=o1[0];					//����Ŀ���
								mb[1]=o1[1];					//����Ŀ���
								mb[1]++;						//�޸�Ŀ���
								mb_mol=map[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
								lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
								move_state=1;					//����ǰ��ģʽ
								this.setDiraction(DOWN); 		//��������
							}
						}
					}
					if(mb[0]-1>=0) {
						if(map[o1[0]][o1[1]]>map[o1[0]-1][o1[1]]) {
							if(map[o1[0]-1][o1[1]]<mb_mol) {
								mb[0]=o1[0];					//����Ŀ���
								mb[1]=o1[1];					//����Ŀ���
								mb[0]--;						//�޸�Ŀ���
								mb_mol=map[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
								lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
								move_state=1;					//����ǰ��ģʽ
								this.setDiraction(LEFT); 		//��������
							}
						}
					}
					if(mb[0]+1<=47) {
						if(map[o1[0]][o1[1]]>map[o1[0]+1][o1[1]]) {
							if(map[o1[0]+1][o1[1]]<mb_mol) {
								mb[0]=o1[0];					//����Ŀ���
								mb[1]=o1[1];					//����Ŀ���
								mb[0]++;						//�޸�Ŀ���
								mb_mol=map[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
								lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
								move_state=1;					//����ǰ��ģʽ
								this.setDiraction(RIGHT); 		//��������
							}
						}
					}
					
					if(lingpai02==0) { 							//δ���в����ж�
						this.setVelocity(0); 					//��δ���в�������������ͣ
						move_state=0; 							//�ж�״̬Ϊ����
						mb[0]=o1[0];							//��ԭĿ���
						mb[1]=o1[1];
					}
				}
			}
			/*��������ʾ����*
			System.out.println("move_state"+move_state);
			System.out.println(mb[0]+" "+mb[1]);
			System.out.println(map[mb[0]][mb[1]]);
			/**/
		}

	public int mol(int a,int b) {
		int result=0;
		if(a>=b) {
			result= a-b;
		}
		if(a<b) {
			result= b-a;
		}
		return result;
	}
	
	public int min(int a,int b) {
		if(a>b) {
			return b;
		}else {
			return a;
		}
	}
	
	public int max(int a,int b) {
		if(a>b) {
			return a;
		}else {
			return b;
		}
	}
	
	
}
