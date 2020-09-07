import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class I_want extends JFrame implements ActionListener{

	public I_want() {
		this.setTitle("I WANT");
		this.setLayout(null);
		this.setSize(380,150);
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension size=kit.getScreenSize();
		this.setLocation((size.width-380)/2,(size.height-150)/2);
		this.setResizable(false);
		
		JLabel label1=new JLabel("I want to create a world where I can explore the unknown,"+"\n"+
				"without experience, without hierarchy, "+"\n"+
				"only to find my real purpose in constant exploration");
		label1.setLocation(20, 20);
		label1.setSize(320,20);
		this.add(label1);
		
		JButton button=new JButton("OK");
		button.setBounds(280,80,80,30);
		button.addActionListener(this);
		add(button);
		
		
		setVisible(true);
	}

	
	
	public static void main(String[] args) {
		I_want i=new I_want();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

}





















