package graphics;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import entities.Course;
import main.MainFrame;

public class GameOptionPanel extends GenericPanel{
	
	private MainFrame frame;
	private Course currentSelection;
	
	public GameOptionPanel(MainFrame frame){
		this.frame = frame;
		setLayout(new BorderLayout());
		
		JPanel wPanel = new JPanel(new GridLayout(4,1));
		JLabel bArr = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().createImage("res\\Textures\\back_arr.png")));
		bArr.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				frame.openMainMenu();
			}
		});
		JRadioButton singlePlayer = new JRadioButton("Single Player", true);
		JRadioButton multiPlayer = new JRadioButton("Two Players", false);
		ButtonGroup group = new ButtonGroup();
		group.add(singlePlayer);
		group.add(multiPlayer);
		
		JComboBox<String> courseSelection = new JComboBox<String>();
		courseSelection.addItem("Course 1");
		courseSelection.addItem("Course 2");
		courseSelection.addItem("Course 3");
		courseSelection.addItem("Course 4");
		
		wPanel.add(bArr);
		wPanel.add(singlePlayer);
		wPanel.add(multiPlayer);
		wPanel.add(courseSelection);
		
		
		JPanel ePanel = new JPanel();
		JLabel label = new JLabel("Course Visualizer");
		ePanel.add(label);
		
		
		JPanel sPanel = new JPanel();
		JButton playButton = new JButton("PLAY");
		playButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.openGame(Course.createDefaultCourse());
			}
		});
		sPanel.add(playButton);
		
		add(wPanel, BorderLayout.WEST);
		add(ePanel, BorderLayout.EAST);
		add(sPanel, BorderLayout.SOUTH);
			
		
	}
}
