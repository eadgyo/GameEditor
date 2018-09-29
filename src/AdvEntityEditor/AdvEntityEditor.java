package AdvEntityEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import Addons.AdvAnimation;
import Addons.AdvEntity;
import Addons.EntitiesGroup;
import Addons.Entity;
import BaseWindows.AddEntityDialog;
import BaseWindows.EntitiesDisplay;
import BaseWindows.GridContainer;
import BaseWindows.ListPerso;
import EntityEditor.CollisionsList;
import EntityEditor.Modifying;
import Maths.Vector2D;
import Maths.PointInt;
import Maths.Vector2D;


public class AdvEntityEditor extends JDialog implements ActionListener, ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private PointInt dimension = new PointInt(300, 500);
	
	private JButton saveB, addB, cancelB;
	protected SpringLayout layout;
	private EntitiesGroup entitiesGroup;
	
	private BufferedImage surfEntity;
	private JScrollPane surfScroller;
	private JLabel surfLabel;
	private Graphics surfaceG;
	
	private EntitiesDisplay entitiesDisplay;
	
	private SlidePanel slidePanel;
	
	private GridContainer gridContainer;
	private Modifying modif;
	
	private Vector2D savedPoint;
	
	private ObjectTree objectsTree;
	private AdvAnimList advAnimList;
	
	private AdvEntity treeAdvEntity, savedAdvEntity;

	private AddEntityDialog addEntityDialog;
	private int copying;
	
	public AdvEntityEditor(EntitiesGroup entitiesGroup, JFrame parent)
	{
		super(parent, "Adv Entity Editor", true);
		this.setResizable(false);
		setSize(1600, 900);
		
		savedPoint = new Vector2D();
		
		addEntityDialog = new AddEntityDialog(this, entitiesGroup);
		
		this.setLocationRelativeTo(null);
		this.entitiesGroup = entitiesGroup;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		surfEntity = new BufferedImage(1200, 600, BufferedImage.TYPE_INT_RGB);
		surfaceG = surfEntity.getGraphics();
		surfaceG.setColor(Color.LIGHT_GRAY);
		surfaceG.fillRect(0, 0, surfEntity.getWidth(), surfEntity.getHeight());
		
		surfLabel = new JLabel(new ImageIcon(surfEntity));
		surfScroller = new JScrollPane(surfLabel);
		surfScroller.setPreferredSize(new Dimension(1210, 610));
		
		entitiesDisplay = new EntitiesDisplay(entitiesGroup, dimension);
		
		//gridContainer
		gridContainer = new GridContainer(surfEntity);
		modif = new Modifying();
		
		//create JButton
		saveB = new JButton("Save");
		saveB.setEnabled(false);
		addB = new JButton("Add");
		cancelB = new JButton("Cancel");
		addB.addActionListener(this);
		saveB.addActionListener(this);
		cancelB.addActionListener(this);
		
		//container pour les options en haut de l'écran
		Box containerOptions = Box.createHorizontalBox();
		containerOptions.add(gridContainer);
		containerOptions.add(Box.createHorizontalStrut(770));
		containerOptions.add(saveB);
		containerOptions.add(addB);
		containerOptions.add(cancelB);
		
		layout = new SpringLayout();
		Container container = this.getContentPane();
		container.setLayout(layout);
		
		objectsTree = new ObjectTree(null, "Objects", dimension.x - 35, 135);
		advAnimList = new AdvAnimList(null, "Adv Anim", dimension.x - 35, 105);
		slidePanel = new SlidePanel(surfEntity.getWidth(), 190, this);
		
		treeAdvEntity = new AdvEntity();
		treeAdvEntity.setGraphics(surfaceG);
		savedAdvEntity = null;
		
		//initiliasiation des lists
		objectsTree.initialize(entitiesGroup, treeAdvEntity, entitiesDisplay, slidePanel, advAnimList, surfEntity, gridContainer.getGrid());
		advAnimList.initialize(treeAdvEntity, slidePanel, objectsTree);
		slidePanel.initialize(treeAdvEntity, objectsTree, advAnimList, surfEntity, gridContainer.getGrid());
		gridContainer.initialize(slidePanel);
		
		//ajout des Objets dans la fenetre
		layout.putConstraint(SpringLayout.WEST, entitiesDisplay, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, entitiesDisplay, 5, SpringLayout.NORTH, this);
		container.add(entitiesDisplay);
		
		layout.putConstraint(SpringLayout.WEST, containerOptions, 5, SpringLayout.EAST, entitiesDisplay);
		layout.putConstraint(SpringLayout.NORTH, containerOptions, 5, SpringLayout.NORTH, this);
		container.add(containerOptions);
		
		layout.putConstraint(SpringLayout.WEST, surfScroller, 5, SpringLayout.EAST, entitiesDisplay);
		layout.putConstraint(SpringLayout.NORTH, surfScroller, 5, SpringLayout.SOUTH, containerOptions);
		container.add(surfScroller);
		
		layout.putConstraint(SpringLayout.WEST, objectsTree, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, objectsTree, 5, SpringLayout.SOUTH, entitiesDisplay);
		container.add(objectsTree);
		
		layout.putConstraint(SpringLayout.WEST, advAnimList, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, advAnimList, 5, SpringLayout.SOUTH, objectsTree);
		container.add(advAnimList);
		
		layout.putConstraint(SpringLayout.WEST, slidePanel, 15, SpringLayout.EAST, objectsTree);
		layout.putConstraint(SpringLayout.NORTH, slidePanel, 5, SpringLayout.SOUTH, surfScroller);
		container.add(slidePanel);
		
		copying = -1;
		
		//Key Listener
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher(this));
		
		this.addComponentListener(this);
		surfLabel.addMouseListener(this);
		surfLabel.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}
	public void launchFrame()
	{
		setVisible(true);
	}
	
	@Override
	public void componentHidden(ComponentEvent e) 
	{
		if(slidePanel.getIsPlaying())
			slidePanel.managedPlay();
		surfaceG.setColor(Color.WHITE);
		surfaceG.fillRect(0,0,surfEntity.getWidth(), surfEntity.getHeight());
	}
	@Override
	public void componentMoved(ComponentEvent e) 
	{
		
	}
	@Override
	public void componentResized(ComponentEvent e) 
	{
		
	}
	@Override
	public void componentShown(ComponentEvent e) 
	{
		entitiesDisplay.updateList();
		objectsTree.redrawTree();
		this.repaint();
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
		slidePanel.closeRunner();
		savedPoint.set(e.getX(), e.getY());
		objectsTree.modifyingCenter(e);
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
	}
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if(advAnimList.getSelected() != -1)
		{
			slidePanel.closeRunner();
			slidePanel.getTranslate().translate(new Vector2D(savedPoint, new Vector2D(e.getX(), e.getY())));
			savedPoint.set(e.getX(), e.getY());
			slidePanel.render();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if(source == saveB)
		{
			savedAdvEntity.set(treeAdvEntity);
			savedAdvEntity.setGraphics(null);
			clear();
			setVisible(false);
		}
		else if(source == addB)
		{
			if(treeAdvEntity.getAdvEntities().size() != 0 && treeAdvEntity.getAdvEntities().get(0).isInitialized() && addEntityDialog.launchDial(treeAdvEntity.clone(), treeAdvEntity.getAdvEntities().get(0).getName()))
			{
				clear();
				setVisible(false);
			}	
		}
		else if(source == cancelB)
		{
			clear();
			setVisible(false);
		}
	}
	private class MyDispatcher implements KeyEventDispatcher 
    {
		JDialog parent;
		MyDispatcher(JDialog parent)
		{
			super();
			this.parent = parent;
		}
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) 
        {
        	if(parent.isActive())
        	{
	            if (e.getID() == KeyEvent.KEY_PRESSED) 
	            {
	        		if(e.isControlDown())
	        		{
	        			if(e.getKeyCode() == KeyEvent.VK_A)
	        			{
	        				copying = 0;
	        				advAnimList.clearCopy();
	        				objectsTree.copy();
	        			}
	        			if(e.getKeyCode() == KeyEvent.VK_C)
	        			{
	        				copying = 1;
	        				objectsTree.clearCopy();
	        				advAnimList.copy();
	        			}
	        			else if(e.getKeyCode() == KeyEvent.VK_V)
	        			{
	        				if(copying == 0)
	        				{
	        					objectsTree.paste();
	        				}
	        				else if(copying == 1)
	        				{
	        					advAnimList.paste();
	        				}
	        			}
	        		}
	            }
        	}
            return false;
        }
    }
	
	public void loadAdvEntity(AdvEntity advEntity)
	{
		savedAdvEntity = advEntity;
		treeAdvEntity.set(advEntity);
		treeAdvEntity.setIsDisplayingRec(true);
		treeAdvEntity.setGraphics(surfaceG);
		
		objectsTree.loadAdvEntity();
		
		saveB.setEnabled(true);
	}
	public void clear()
	{
		treeAdvEntity.clear();
		
		objectsTree.clear();
		advAnimList.updateList();
		
		slidePanel.getTranslate().set(surfEntity.getWidth()*0.5f, surfEntity.getHeight()*0.5f);
		slidePanel.setScale(0);
		slidePanel.update();
		slidePanel.render();
		
		saveB.setEnabled(false);
		
		savedAdvEntity = null;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) 
	{
		float scale = e.getScrollAmount()/2f;
		if(e.getWheelRotation() > 0)
			scale = - scale;
		
		slidePanel.closeRunner();
		slidePanel.setScale(slidePanel.getScale() + scale);
		slidePanel.update();
		slidePanel.render();
	}
}