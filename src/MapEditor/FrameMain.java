package MapEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

public class FrameMain extends JFrame {

	private PanelMain panelMain;
	
	public FrameMain() {
		this.setTitle("map editor");
		
		//��Ӳ˵�
		JMenuBar menuBar=new JMenuBar();		//�½��˵���
		setJMenuBar(menuBar);					//�˵��������
		addFileMenu(menuBar);					//����ļ��˵���
		
		setLayout(new BorderLayout());
		
		PanelIcon panelIcon=new PanelIcon(this);
		panelIcon.initPanel();
		add(panelIcon,BorderLayout.WEST);
		
		panelMain=new PanelMain(this);
		panelMain.setBorder(new LineBorder(Color.BLUE));
		panelMain.setPreferredSize(new Dimension(800,600));
		
		JScrollPane panel=new JScrollPane(panelMain,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(panel);
		
		this.setSize(800,600);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension dm=kit.getScreenSize();
		this.setLocation((dm.width-800)/2, (dm.height-600)/2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	
	private void addFileMenu(JMenuBar  menuBar) {
		JMenu fileMenu=new JMenu("File");			//�½��ļ��˵�
		menuBar.add(fileMenu);
		
		JMenuItem newItem=new JMenuItem("new");		//new �˵���
		newItem.addActionListener(new NewItemAct());
		fileMenu.add(newItem);
		
		JMenuItem openItem=new JMenuItem("open");	//open �˵���
		openItem.addActionListener(new OpenItemAct());
		fileMenu.add(openItem);
		
		JMenuItem saveItem=new JMenuItem("save");	//save �˵���
		saveItem.addActionListener(new SaveItemAct());
		fileMenu.add(saveItem);
		
		
	}
	
	private class SaveItemAct implements ActionListener{

		JFileChooser fileDialog=new JFileChooser();
		@Override
		public void actionPerformed(ActionEvent e) {
			int state=fileDialog.showSaveDialog(null);
			if(state==JFileChooser.APPROVE_OPTION) {
				panelMain.saveMap(fileDialog.getSelectedFile().getAbsoluteFile());
			}
		}
	}
	
	private class OpenItemAct implements ActionListener{

		JFileChooser fileDialog=new JFileChooser();
		@Override
		public void actionPerformed(ActionEvent e) {
			int state=fileDialog.showOpenDialog(null);
			if(state==JFileChooser.APPROVE_OPTION) {
				try {
					panelMain.openMap(fileDialog.getSelectedFile().getAbsoluteFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	private class NewItemAct implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			panelMain.newMap();
			
		}
	}
	
	
	
}























