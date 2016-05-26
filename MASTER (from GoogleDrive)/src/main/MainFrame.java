package main;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import entities.Course;
import graphics.ConstructorPanel;
import graphics.GameOptionPanel;
import graphics.GamePanel;
import graphics.GenericPanel;
import graphics.MainMenuPanel;
import graphics.MasterGamePanel;
import graphics.SettingsPanel;

public class MainFrame extends JFrame {
	
	public static final int FPS = 120;
	
	private JPanel panel = new JPanel(new CardLayout());
	private CardLayout cl;
	private MasterGamePanel gP; 
	
	public MainFrame(){
		setSize(new Dimension(1000,600));
		//add(new GamePanel());
		setTitle("MMMinigolf");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
        add(panel);
        panel.add(new GameOptionPanel(this), "GameOption");
        gP = new MasterGamePanel(this);
        panel.add(gP, "Game");
        panel.add(new ConstructorPanel(this), "Designer");
        panel.add(new MainMenuPanel(this), "MainMenu");
        panel.add(new SettingsPanel(this), "Settings");
        cl = (CardLayout)(panel.getLayout());
        cl.show(panel,"MainMenu");
        setVisible(true);
        
        Timer t = new Timer(1000/MainFrame.FPS, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
        });
        t.start();
        
        
	}
	
	public void openGame(Course course){
		gP.setCourse(course);
		cl.show(panel,"Game");
		repaint();
	}
	
	public void openGameOption(){
		cl.show(panel,"GameOption");
		repaint();
	}
	
	public void openDesigner(){
		cl.show(panel,"Designer");
		repaint();
	}
	
	public void openMainMenu(){
		cl.show(panel,"MainMenu");
		repaint();
	}
	public void openSettings() {
		cl.show(panel,"Settings");
		repaint();
	}
	
	public static void main(String[] args){
		MainFrame frame = new MainFrame();
	}
}
