import java.util.Random;

public class SpiritTank002 extends SpiritTank {

	private Map map;
	public static final int SIPIRITTANK_V=2;
	
	private int fangxiang=0;		//�����н��ķ���任����
	public int mubiaoState00=0;		//����Ŀ��״̬��ͼѡ�� 2-9���Ͻ���ʼ��˳ʱ����ת
	//public int mubiaoState01=0;	//�����ƶ���ͼ���л� 0-���л� 1����
	private int[][] map00;			//�ж���ͼ���ݴ�
	public int move_state=0;		//0-����1-�н�2-�ڻ�ģʽ
	public int move_state01=0;		//0-����1-�н�2-�ڻ�ģʽ(����)
	private int move_fx;			//�ݴ�ԭ��ʻ����
	private int[] mb= {0,0};		//Ŀ��㴢��
	
	
	
	//̹����ֵ���ã�
	public  int FIRE_TIME=3;				//����̹�˿�����ʱ��
	private int firetime=0;					//��¼̹�˿���ʱ��
	public  int FIRE_SLEEP_TIME=0;			//��ֱʱ���趨
	private int fire_sleep_time=0;			//��¼��ֱʱ��
	public int distance=4;					//̹����ʻ�뾶
	private int jingdu=12;					//̹�˿��𾫶�
	
	public SpiritTank002(int x, int y,Map map,int fire_time,int fire_sleep_time) {
		super(x, y);
		this.map=map;
		mb[0]=x/17;				//��ʼ��Ŀ���
		mb[1]=y/17;				//��ʼ��Ŀ���
		this.setVelocity(SIPIRITTANK_V);
		this.setCategory(15);
		map00=new int[48][37];	//�ж���ͼ��ʼ��
		
		Random random=new Random();
		mubiaoState00=random.nextInt(8)+2;
		
		//��ʼ��Ŀ��㣺
		for(int p=0;p<8;p++) {
			mubiaoState00=(mubiaoState00-1)%8+2;		//˳ʱ��ѡȡ��һ��
			//�任�������Ƿ����
			if(mubiaoState00==2) {
				map00=map.mapmap_move_point002;
			}else if(mubiaoState00==3) {
				map00=map.mapmap_move_point003;
			}else if(mubiaoState00==4) {
				map00=map.mapmap_move_point004;
			}else if(mubiaoState00==5) {
				map00=map.mapmap_move_point005;
			}else if(mubiaoState00==6) {
				map00=map.mapmap_move_point006;
			}else if(mubiaoState00==7) {
				map00=map.mapmap_move_point007;
			}else if(mubiaoState00==8) {
				map00=map.mapmap_move_point008;
			}else if(mubiaoState00==9){
				map00=map.mapmap_move_point009;
			}
			if(map00[mb[0]][mb[1]]==0) {
				continue;
			}else {
				break;
			}
		}
		
		
		//���ڼ���ʱ�丳ֵ
		this.FIRE_TIME=fire_time;
		//���ڽ�ֱʱ�丳ֵ
		this.FIRE_SLEEP_TIME=fire_sleep_time;
	}

	
		//���㺯��
		public void caculateDate001(int[][] map002,int[][] map003, int[][] map004,int[][] map005,
				int[][] map006,int[][] map007, int[][] map008,int[][] map009,
				int playertank_x,int playertank_y) {
			
			if(fire_sleep_time!=0) {
				fire_sleep_time--;						//�����ڽ�ֱ�����趨���������ж�
			}else {
				if(move_state==2) {
					move_state=move_state01;			//�ڻ�ģʽ��ԭ
					this.setDiraction(move_fx);
				}
				
				if(firetime!=0) {
					firetime--;
				}
				
				if(mubiaoState00==2) {
					map00=map.mapmap_move_point002;
				}else if(mubiaoState00==3) {
					map00=map.mapmap_move_point003;
				}else if(mubiaoState00==4) {
					map00=map.mapmap_move_point004;
				}else if(mubiaoState00==5) {
					map00=map.mapmap_move_point005;
				}else if(mubiaoState00==6) {
					map00=map.mapmap_move_point006;
				}else if(mubiaoState00==7) {
					map00=map.mapmap_move_point007;
				}else if(mubiaoState00==8) {
					map00=map.mapmap_move_point008;
				}else if(mubiaoState00==9){
					map00=map.mapmap_move_point009;
				}
				
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
				if(x%17>=3) {
					i1++;
				}
				if(y%17>=3) {
					j1++;
				}
				
				//�ж����Ƿ�Ϊ�ڻ�ģʽ
				
				if((this.mol(this.getX(),playertank_x)<=this.jingdu||this.mol(this.getY(),playertank_y)<=this.jingdu)
						&&firetime==0
						&&map00[i1][j1]<-GamePanel.spirittank_miss) {			//���ﾫ��ֵ�����ڴ��ڼ�Ъ״̬��û�б��÷���ʱ�������ڻ�ģʽ
					move_state01=this.move_state;								//�ݴ�����ģʽ
					move_state=2;												//����Ϊ�ڻ�ģʽ
					firetime=this.FIRE_TIME;									//���뿪����
					this.setVelocity(0);										//��ֹ����
					move_fx=this.getDiraction();								//���淽��
					fire_sleep_time=FIRE_SLEEP_TIME;							//���뽩ֱ
				}
				
				
				if(mol(mb[0]*17,x)>34||mol(mb[1]*17,y)>34) {
					System.out.println("rrrrrr");
					System.out.println(mb[0]*17+"  "+mb[1]*17);
					System.out.println(x+"  "+y);
					System.out.println("Diraction"+this.getDiraction());
				}
				
				if(move_state==2) {
					//�ж�����
					if(this.mol(this.getX(),playertank_x)<=15) {
						if(this.getY()>=playertank_y) {
							this.setDiraction(UP);
						}else {
							this.setDiraction(DOWN);
						}
					}else {
						if(this.getX()>=playertank_x) {
							this.setDiraction(LEFT);
						}else {
							this.setDiraction(RIGHT);
						}
					}
					//��������
				}else {
					this.setVelocity(SIPIRITTANK_V);  							//�ָ�����
					if(move_state==1) { 										//ǰ��״̬(�ж��Ƿ񵽴�Ŀ���)(ͬʱ������Ŀ���ĳ���)
						if(x<=mb[0]*17+2&&x>=mb[0]*17-2&&y<=mb[1]*17+2&&y>=mb[1]*17-2) {
							move_state=0;										//����״̬
							
							if(map00[mb[0]][mb[1]]==-GamePanel.spirittank_see) {//���𵽴Ｋֵ��ʱ��ת��
								for(int p=0;p<8;p++) {
									mubiaoState00=(mubiaoState00-1)%8+2;		//˳ʱ��ѡȡ��һ��
									//�任�������Ƿ����
									if(mubiaoState00==2) {
										map00=map.mapmap_move_point002;
									}else if(mubiaoState00==3) {
										map00=map.mapmap_move_point003;
									}else if(mubiaoState00==4) {
										map00=map.mapmap_move_point004;
									}else if(mubiaoState00==5) {
										map00=map.mapmap_move_point005;
									}else if(mubiaoState00==6) {
										map00=map.mapmap_move_point006;
									}else if(mubiaoState00==7) {
										map00=map.mapmap_move_point007;
									}else if(mubiaoState00==8) {
										map00=map.mapmap_move_point008;
									}else if(mubiaoState00==9){
										map00=map.mapmap_move_point009;
									}
									if(map00[mb[0]][mb[1]]==0) {
										continue;
									}else {
										break;
									}
								}
								this.setVelocity(0);  							//��ʱ��ֹ
							}else if(map00[mb[0]][mb[1]]==0) {					//������������״̬ʱ��ת��
								for(int p=0;p<8;p++) {
									mubiaoState00=(mubiaoState00-1)%8+2;		//˳ʱ��ѡȡ��һ��
									//�任�������Ƿ����
									if(mubiaoState00==2) {
										map00=map.mapmap_move_point002;
									}else if(mubiaoState00==3) {
										map00=map.mapmap_move_point003;
									}else if(mubiaoState00==4) {
										map00=map.mapmap_move_point004;
									}else if(mubiaoState00==5) {
										map00=map.mapmap_move_point005;
									}else if(mubiaoState00==6) {
										map00=map.mapmap_move_point006;
									}else if(mubiaoState00==7) {
										map00=map.mapmap_move_point007;
									}else if(mubiaoState00==8) {
										map00=map.mapmap_move_point008;
									}else if(mubiaoState00==9){
										map00=map.mapmap_move_point009;
									}
									if(map00[mb[0]][mb[1]]==0) {
										continue;
									}else {
										break;
									}
								}
								this.setVelocity(0);  							//��ʱ��ֹ
							}
						}
					}
					if(move_state==0) {											//����״̬������ѡȡ��һ��Ŀ���,ֱ�Ӷ�Ŀ�����в�����
							
						//����ѡȡĿ���
						int lingpai01=0;							
						int lingpai02=0;
						//�ݴ�ԭĿ���
						int[] o1=new int[2];
						o1[0]=mb[0];							//Ŀ��㴢�治��
						o1[1]=mb[1];							//Ŀ��㴢�治��
						int mb_mol=1;
						if(mb[1]-1>=0) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]][o1[1]-1]&&map00[o1[0]][o1[1]-1]!=1) {
								mb[0]=o1[0];					//����Ŀ���
								mb[1]=o1[1];					//����Ŀ���
								mb[1]--;						//�޸�Ŀ���
								mb_mol=map00[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
								lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
								move_state=1;					//����ǰ��ģʽ
								this.setDiraction(UP); 			//��������
							}
						}
						if(mb[1]+1<=36) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]][o1[1]+1]&&map00[o1[0]][o1[1]+1]!=1) {
								if(map00[o1[0]][o1[1]+1]<mb_mol) {
									mb[0]=o1[0];					//����Ŀ���
									mb[1]=o1[1];					//����Ŀ���
									mb[1]++;						//�޸�Ŀ���
									mb_mol=map00[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
									lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
									move_state=1;					//����ǰ��ģʽ
									this.setDiraction(DOWN); 		//��������
								}
							}
						}
						if(mb[0]-1>=0) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]-1][o1[1]]&&map00[o1[0]-1][o1[1]]!=1) {
								if(map00[o1[0]-1][o1[1]]<mb_mol) {
									mb[0]=o1[0];					//����Ŀ���
									mb[1]=o1[1];					//����Ŀ���
									mb[0]--;						//�޸�Ŀ���
									mb_mol=map00[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
									lingpai02=1;					//lingpai02��֤�����ظ��ƶ�Ŀ���
									move_state=1;					//����ǰ��ģʽ
									this.setDiraction(LEFT); 		//��������
								}
							}
						}
						if(mb[0]+1<=47) {
							if(map00[o1[0]][o1[1]]>map00[o1[0]+1][o1[1]]&&map00[o1[0]+1][o1[1]]!=1) {
								if(map00[o1[0]+1][o1[1]]<mb_mol) {
									mb[0]=o1[0];					//����Ŀ���
									mb[1]=o1[1];					//����Ŀ���
									mb[0]++;						//�޸�Ŀ���
									mb_mol=map00[mb[0]][mb[1]];		//�ݴ�Ŀ���ֵ
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
				System.out.println(map00[mb[0]][mb[1]]);
				/**/
			}
		}

		//��д������
		public Bullet fire() {
			Bullet bullet=null;
			int bulletX;
			int bulletY;
			bulletX=getX();
			bulletY=getY();
			switch(getDiraction()) {
			case Spirit.UP:
				bulletY=bulletY-getWidth()/2;
				break;
			case Spirit.RIGHT:
				bulletX=bulletX+getWidth()/2;
				break;
			case Spirit.DOWN:
				bulletY=bulletY+getWidth()/2;
				break;
			case Spirit.LEFT:
				bulletX=bulletX-getWidth()/2;
				break;
			}
			bullet=new Bullet(bulletX,bulletY,getDiraction());
			if(this.move_state==2&&this.fire_sleep_time==0) {
				return bullet;
			}else {
				return null;
			}
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
