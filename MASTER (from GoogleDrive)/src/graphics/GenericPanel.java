package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.MainFrame;

public class GenericPanel extends JPanel {
	MainFrame frame;
	public static final double PX_SCALE = 1;
	public static final int TILE_SIZE = 140;

	public static final int SIZE = 50;

	public Font addCustomFont(String name, float size){
		Font customFont = null;
		 try {
           //create the font to use. Specify the size!
           customFont = Font.createFont(Font.TRUETYPE_FONT, new File("res\\Fonts\\" + name + ".otf")).deriveFont(size);
           GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
           //register the font
           ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res\\Fonts\\" + name + ".otf")));
       } catch (IOException e) {
      	 e.printStackTrace();
           customFont = new Font(Font.SANS_SERIF, Font.PLAIN, 1);
       }
       catch (FontFormatException e)
       {
           e.printStackTrace();
           customFont = new Font(Font.SANS_SERIF, Font.PLAIN, 1);
       }
		 return customFont;
	}

	public static void addEnlighter(MainFrame frame, JLabel label){
		label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                label.setForeground(Color.YELLOW);
            }
            @Override
            public void mouseExited(MouseEvent evt) {
            	label.setForeground(Color.BLACK);
            }
            @Override
            public void mousePressed(MouseEvent evt){
            	if(label.getText().equals("PLAY")){
            		frame.openGameOption();
            	}
            	if(label.getText().equals("DESIGNER")){
            		frame.openDesigner();
            	}
            	if(label.getText().equals("SETTINGS")){
            		frame.openSettings();
            	}
            }
        });
	}
}
