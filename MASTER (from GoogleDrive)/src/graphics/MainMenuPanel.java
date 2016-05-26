package graphics;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;

import main.MainFrame;

public class MainMenuPanel extends GenericPanel {
	
	private Image backgroundImage;
	private MainFrame frame;
	
	public MainMenuPanel(MainFrame frame){
		this.frame = frame;
		backgroundImage = Toolkit.getDefaultToolkit().createImage("res\\Textures\\grass_texture6.png");
		 JLabel title = new JLabel("Mark's Marvelous Minigolf");
		 JLabel game = new JLabel("PLAY");
		 JLabel designer = new JLabel("DESIGNER");
		 JLabel settings = new JLabel("SETTINGS");
		 
        //use the font
		Font customFont = addCustomFont("Digital_tech",30f);
		Font customFont1 = addCustomFont("Digital_tech",40f);
        title.setFont(customFont1);
        game.setFont(customFont);
        designer.setFont(customFont);
        settings.setFont(customFont);
        title.setHorizontalAlignment(JLabel.CENTER);
        game.setHorizontalAlignment(JLabel.CENTER);
        designer.setHorizontalAlignment(JLabel.CENTER);
        settings.setHorizontalAlignment(JLabel.CENTER);
        title.setForeground(Color.BLACK);
        game.setForeground(Color.BLACK);
        designer.setForeground(Color.BLACK);
        settings.setForeground(Color.BLACK);
        GridLayout layout = new GridLayout(4,1,0,0);
		setLayout(layout);
        this.add(title);
        this.add(game);
        this.add(designer);
        this.add(settings);
        addEnlighter(frame,game);
        addEnlighter(frame,designer);
        addEnlighter(frame,settings);
        
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this);
	}
	
}
