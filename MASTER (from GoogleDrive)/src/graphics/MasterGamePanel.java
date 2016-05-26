package graphics;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import entities.Course;
import main.MainFrame;

public class MasterGamePanel extends GenericPanel {

	
	private MainFrame frame;
	private GamePanel panel1;
	private JLabel Score;
	public MasterGamePanel(MainFrame frame){
		this.frame = frame;
		this.setLayout(new BorderLayout());
		
		
		panel1= new GamePanel(this);
		
		JPanel panel2= getPlayerPanel();
		
		JPanel panel3= getOptionPanel();
		
		this.add(panel1,BorderLayout.CENTER);
		this.add(panel2,BorderLayout.EAST);
		this.add(panel3,BorderLayout.SOUTH);
	}
	public JPanel getPlayerPanel(){
		JPanel playerPanel= new JPanel();
		Score= new JLabel("Player shot = 0");
		playerPanel.add(Score);
		
		
		
		return playerPanel;
		
	}
	
	public  void UpdateScore(int score){
		Score.setText("player shot = " + score);
	}
	
	
	public JPanel getOptionPanel(){
		JPanel OptionPanel= new JPanel();
		JLabel test2= new JLabel("test2");
		OptionPanel.add(test2);
		return OptionPanel;
	} 
	
	public void setCourse(Course course) {
		panel1.setCourse(course);
	}
	
}
