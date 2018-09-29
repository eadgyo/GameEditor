package MapEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import Base.Image;

public class EntityListRenderer extends DefaultListCellRenderer 
{
	private ArrayList<BufferedImage> buffs;
	public static int SIZE = 32;
    private Font font = new Font("helvitica", Font.BOLD, 12);
    private BufferedImage buff= new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
    
    public EntityListRenderer()
    {
    	super();
    	buffs = new ArrayList<BufferedImage>();
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
    {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        assert(index < buffs.size());
        label.setIcon(new ImageIcon(buffs.get(index)));
        label.setHorizontalTextPosition(JLabel.RIGHT);
        label.setFont(font);
        return label;
    }
    public void render(int n, Image image)
    {
    	while(n>=buffs.size())
    		buffs.add(new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB));
    	
    	Graphics g = buffs.get(n).getGraphics();
    	g.setColor(Color.black);
    	g.fillRect(0,0,buffs.get(n).getWidth(),buffs.get(n).getHeight());
    	image.setGraphics(g);
    	image.draw();
    }
}