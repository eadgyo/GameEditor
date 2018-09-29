package AdvEntityEditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import Addons.AdvEntity;
import MapEditor.ComboItem;
import Maths.Vector2D;
import Maths.Vector2D;

public class DisplayManagerDialog extends JDialog implements ActionListener, MouseMotionListener, MouseWheelListener, MouseListener
{
	private JList<ComboItem> list1, list2;
	private DefaultListModel<ComboItem> listModel1, listModel2;
	private JButton rightB, leftB, upB, downB, okB;
	private JScrollPane listScroll1, listScroll2;
	private SpringLayout layout;
	private AdvEntity advEntity;
	private ArrayList<AdvEntity> advEntities;
	
	private Vector2D translate;
	private Vector2D savedPoint;
	private float scale;
	
	private BufferedImage zoomSurface;
	private Graphics zoomG;
	private JLabel zoomImageLabel;
	
	public DisplayManagerDialog(JDialog parent)
	{
		super(parent, "Display Manager", true);
		this.setResizable(false);
		setSize(800, 600);
		this.setLocationRelativeTo(null);

		advEntities = new ArrayList<AdvEntity>();
		
		zoomSurface = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
		zoomG = zoomSurface.getGraphics();
		zoomImageLabel = new JLabel(new ImageIcon(zoomSurface));
		zoomImageLabel.setPreferredSize(new Dimension(350,350));
		
		translate = new Vector2D(zoomSurface.getWidth()/2, zoomSurface.getHeight()/2);
		savedPoint = new Vector2D();
		scale = 0;
		
		listModel1 = new DefaultListModel<ComboItem>();
		listModel2 = new DefaultListModel<ComboItem>();
		
		list1 = new JList<ComboItem>(listModel1);
		list2 = new JList<ComboItem>(listModel2);
		
		list1.setSelectionMode(2);
		list2.setSelectionMode(2);
		
		listScroll1 = new JScrollPane(list1);
		listScroll2 = new JScrollPane(list2);
		listScroll1.setPreferredSize(new Dimension(350,175));
		listScroll2.setPreferredSize(new Dimension(350,530));
		
		rightB = new JButton(">");
		leftB = new JButton("<");
		upB = new JButton("    UP   ");
		downB = new JButton("DOWN");
		okB = new JButton("OK");
		
		advEntity = null;
		
		layout = new SpringLayout();
		
		layout.putConstraint(SpringLayout.WEST, listScroll1, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, listScroll1, 5, SpringLayout.NORTH, this);
		add(listScroll1);
		
		layout.putConstraint(SpringLayout.WEST, zoomImageLabel, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, zoomImageLabel, 5, SpringLayout.SOUTH, listScroll1);
		add(zoomImageLabel);
		
		layout.putConstraint(SpringLayout.WEST, rightB, 5, SpringLayout.EAST, listScroll1);
		layout.putConstraint(SpringLayout.NORTH, rightB, this.getHeight()/2 - 20, SpringLayout.NORTH, this);
		add(rightB);
		
		layout.putConstraint(SpringLayout.WEST, leftB, 5, SpringLayout.EAST, listScroll1);
		layout.putConstraint(SpringLayout.NORTH, leftB, this.getHeight()/2 + 20, SpringLayout.NORTH, this);
		add(leftB);
		
		layout.putConstraint(SpringLayout.WEST, listScroll2, 5, SpringLayout.EAST, rightB);
		layout.putConstraint(SpringLayout.NORTH, listScroll2, 5, SpringLayout.NORTH, this);
		add(listScroll2);
		
		layout.putConstraint(SpringLayout.WEST, upB, 5, SpringLayout.EAST, listScroll2);
		layout.putConstraint(SpringLayout.NORTH, upB, this.getHeight()/2 - 20, SpringLayout.NORTH, this);
		add(upB);
		
		layout.putConstraint(SpringLayout.WEST, downB, 5, SpringLayout.EAST, listScroll2);
		layout.putConstraint(SpringLayout.NORTH, downB, this.getHeight()/2 + 20, SpringLayout.NORTH, this);
		add(downB);
		
		layout.putConstraint(SpringLayout.WEST, okB, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, okB, 5, SpringLayout.SOUTH, zoomImageLabel);
		add(okB);
		
		rightB.addActionListener(this);
		leftB.addActionListener(this);
		upB.addActionListener(this);
		downB.addActionListener(this);
		okB.addActionListener(this);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.setLayout(layout);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		int[] selected1 = list1.getSelectedIndices();
		int[] selected2 = list2.getSelectedIndices();
		if(source == rightB)
		{
			if(list1.isSelectionEmpty())
				return;
		
			for(int i=selected1.length-1; i>-1; i--)
			{
				//On ajoute l'objet dans la disp list
				advEntity.getDisplayAdvEntities().add(advEntities.get((int) listModel1.get(selected1[i]).getValue()));
				listModel2.addElement(listModel1.get(selected1[i]));
				listModel1.remove(selected1[i]);
				
				list2.setSelectedIndex(listModel2.size() - 1);
				if(listModel1.size() != 0)
				{
					if(selected1[i] - 1 < 0)
						list1.setSelectedIndex(selected1[i]);
					else
						list1.setSelectedIndex(selected1[i] - 1);
				}
			}
		}
		else if(source == leftB)
		{
			if(list2.isSelectionEmpty())
				return;
			for(int i=selected2.length-1; i>-1; i--)
			{
				//On ajoute l'objet dans la disp list
				advEntity.getDisplayAdvEntities().remove(selected2[i]);
				listModel1.addElement(listModel2.get(selected2[i]));
				listModel2.remove(selected2[i]);
				
				list1.setSelectedIndex(listModel1.size() - 1);
				if(listModel2.size() != 0)
				{
					if(selected2[i] - 1 < 0)
						list2.setSelectedIndex(selected2[i]);
					else
						list2.setSelectedIndex(selected2[i] - 1);
				}
			}
		}
		else if(source == upB)
		{
			if(list2.isSelectionEmpty())
				return;
			for(int i=0; i<1; i++)
			{
				if(selected2[i] - 1 > -1)
				{
					advEntity.getDisplayAdvEntities().add(selected2[i] - 1, advEntity.getDisplayAdvEntities().get(selected2[i]));
					advEntity.getDisplayAdvEntities().remove(selected2[i] + 1);
					listModel2.add(selected2[i] - 1, listModel2.get(selected2[i]));
					listModel2.remove(selected2[i] + 1);
					list2.setSelectedIndex(selected2[i] - 1);
				}
			}
		}
		else if(source == downB)
		{
			if(list2.isSelectionEmpty())
				return;
			for(int i=0; i<1; i++)
			{
				if(selected2[i] + 1 < listModel2.size())
				{
					advEntity.getDisplayAdvEntities().add(selected2[i] + 2, advEntity.getDisplayAdvEntities().get(selected2[i]));
					advEntity.getDisplayAdvEntities().remove(selected2[i]);
					listModel2.add(selected2[i] + 2, listModel2.get(selected2[i]));
					listModel2.remove(selected2[i]);
					list2.setSelectedIndex(selected2[i] + 1);
				}
			}
		}
		else if(source == okB)
		{
			this.setVisible(false);
		}
		update();
		render();
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		int scale = e.getScrollAmount();
		if(e.getWheelRotation() > 0)
			scale = - scale;
		
		this.scale = scale + this.scale;
		if(this.scale < 2)
			this.scale = 2;
		
		update();
		render();
	}
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		translate.translate(new Vector2D(savedPoint, new Vector2D(e.getX(), e.getY())));
		savedPoint.set(e.getX(), e.getY());
		render();
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
	}
	@Override
	public void mouseClicked(MouseEvent e) 
	{
	}
	@Override
	public void mouseEntered(MouseEvent e) 
	{
	}
	@Override
	public void mouseExited(MouseEvent e) 
	{
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{
		savedPoint.set(e.getX(), e.getY());
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
	}
	
	public void launchDial(AdvEntity advEntity)
	{
		translate.set(zoomSurface.getWidth()/2, zoomSurface.getHeight()/2);
		scale = 8;
		
		this.advEntity = advEntity;
		updateList();
		if(listModel1.getSize() != 0)
			list1.setSelectedIndex(0);
		if(listModel2.getSize() != 0)
			list2.setSelectedIndex(0);
		update();
		render();
		
		this.setVisible(true);
		this.advEntity = null;
		this.advEntities.clear();
	}
	public void updateList()
	{
		if(advEntity == null)
			return;
		this.advEntities.clear();
		
		advEntity.getAllAdvEntities(advEntities);
		
		ArrayList<AdvEntity> l_dispAdvEntities = advEntity.getDisplayAdvEntitiesClone();
		
		listModel1.removeAllElements();
		listModel2.removeAllElements();
		
		boolean isAlreadyDisp;
		for(int i=0; i<advEntities.size(); i++)
		{
			isAlreadyDisp = false;
			for(int j=0; j<l_dispAdvEntities.size(); j++)
			{
				if(advEntities.get(i) == l_dispAdvEntities.get(j))
				{
					isAlreadyDisp = true;
					listModel2.addElement(new ComboItem(advEntities.get(i).getName(), i));
					l_dispAdvEntities.remove(j);
					break;
				}
			}
			if(!isAlreadyDisp)
				listModel1.addElement(new ComboItem(advEntities.get(i).getName(), i));
		}
	}
	public void update()
	{
		advEntity.reset();
		advEntity.resetAdvAnim();
		advEntity.update(0, new Vector2D(1,0), new Vector2D(), 100);
		advEntity.setScale((advEntity.getScale() + scale) * 0.2f);	
	}
	public void render()
	{
		if(advEntity == null)
			return;
		zoomG.setColor(Color.white);
		zoomG.fillRect(0,0,zoomSurface.getWidth(), zoomSurface.getHeight());
		advEntity.draw(zoomG, translate);
		zoomImageLabel.repaint();
	}

}
