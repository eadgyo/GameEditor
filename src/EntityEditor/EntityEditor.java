package EntityEditor;

import javax.swing.* ;

import Addons.Animation;
import Addons.EntitiesGroup;
import Addons.Entity;
import Base.Image;
import BaseWindows.AddEntityDialog;
import BaseWindows.GridContainer;
import MapEditor.ComboItem;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EntityEditor extends JDialog implements ActionListener, ComponentListener
{
	private JComboBox<ComboItem> comboType;
	
	private JButton saveB, addB, cancelB;
	private EntitiesGroup entitiesGroup;
	
	private BufferedImage surfEntity;
	private JScrollPane surfScroller;
	private JLabel surfLabel;
	
	private AFormDef aFormsDef;
	private AnimList animList; 
	private CollisionsList collisionsList;
	private FramesList framesList;
	
	private Select select;
	private Graphics surfaceG;
	
	private Animation anim;
	private Entity savedEntity;
	private GridContainer grid;
	private Modifying modif;
	
	private AddEntityDialog addEntityDialog;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EntityEditor(EntitiesGroup entitiesGroup, JFrame parent)
	{
		super(parent, "Entity editor", true);
		this.setResizable(false);
		setSize(1600, 900);
		this.entitiesGroup = entitiesGroup;
		
		savedEntity = null;
		
		//creation de l'image buffer
		surfEntity = new BufferedImage(1200, 650, BufferedImage.TYPE_INT_RGB);
		surfLabel = new JLabel(new ImageIcon(surfEntity));
		surfScroller = new JScrollPane(surfLabel);
		surfScroller.setPreferredSize(new Dimension(1210, 660));
		addEntityDialog = new AddEntityDialog(this, entitiesGroup);
		
		grid = new GridContainer(surfEntity);
		anim = new Animation();
		modif = new Modifying();
		
		//create JButton
		saveB = new JButton("Save");
		saveB.setEnabled(false);
		addB = new JButton("Add");
		cancelB = new JButton("Cancel");
		
		saveB.addActionListener(this);
		addB.addActionListener(this);
		cancelB.addActionListener(this);
		
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		
		select = new Select(300, 800);
		
		//creation lists
		collisionsList = new CollisionsList(this,"Collisions", 200, 75);
		aFormsDef = new AFormDef(modif);
		framesList = new FramesList(this,"Frames", 200, 75);
		animList = new AnimList(this,"Animations", 200, 75);
		
		surfaceG = surfEntity.getGraphics();
		surfaceG.setColor(Color.LIGHT_GRAY);
		surfaceG.fillRect(0, 0, surfEntity.getWidth(), surfEntity.getHeight());
		
		//initilization des objets
		collisionsList.initialize(anim, surfEntity, surfScroller, animList,
				framesList, grid.getGrid(), aFormsDef, modif);
		animList.initialize(anim, framesList, select);
		framesList.initialize(anim, surfEntity, surfScroller, select, collisionsList);
		aFormsDef.initialize(anim, framesList, collisionsList, surfEntity);
		grid.initialize(framesList);
		
		
		//container pour les options en haut de l'écran
		Box containerOptions = Box.createHorizontalBox();
		containerOptions.add(grid);
		containerOptions.add(Box.createHorizontalStrut(770));
		containerOptions.add(saveB);
		containerOptions.add(addB);
		containerOptions.add(cancelB);
		
		
		//rangement listes
		Box containerLists = Box.createHorizontalBox();
		containerLists.setPreferredSize(new Dimension(1230,200));
		containerLists.add(animList);
		containerLists.add(framesList);
		containerLists.add(collisionsList);
		containerLists.add(aFormsDef);
		
		layout.putConstraint(SpringLayout.WEST, containerOptions, 10, SpringLayout.EAST, select);
		layout.putConstraint(SpringLayout.NORTH, containerOptions, 5, SpringLayout.NORTH, this);
		this.add(containerOptions);
		
		layout.putConstraint(SpringLayout.WEST, select, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, select, 5, SpringLayout.NORTH, this);
		this.add(select);
		
		layout.putConstraint(SpringLayout.WEST, surfScroller, 10, SpringLayout.EAST, select);
		layout.putConstraint(SpringLayout.NORTH, surfScroller, 5, SpringLayout.SOUTH, containerOptions);
		this.add(surfScroller);
		
		layout.putConstraint(SpringLayout.WEST, containerLists, 5, SpringLayout.EAST, select);
		layout.putConstraint(SpringLayout.NORTH, containerLists, 5, SpringLayout.SOUTH, surfScroller);
		this.add(containerLists);
		
		//Key Listener
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));
        
        this.addComponentListener(this);
	}
	public void launchFrame()
	{
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if(source == saveB)
		{
			if(framesList.getImages().size() != 0)
			{
				Entity l_entity = new Entity(framesList.getImages().get(0).clone());
				l_entity.setAnim(anim);
				l_entity.setName(savedEntity.getName());
				l_entity.setInverseMass(animList.getInvMass());
				l_entity.setGraphics(null);
				l_entity.ready();
				savedEntity.set(l_entity);
				savedEntity = null;
			}
			clear();
			setVisible(false);
		}
		else if(source == addB)
		{
			if(framesList.getImages().size() != 0)
			{
				Entity l_entity = new Entity(framesList.getImages().get(0).clone());
				l_entity.setInverseMass(animList.getInvMass());
				l_entity.setAnim(anim.clone());
				l_entity.ready();
				if(addEntityDialog.launchDial(l_entity, anim.getName(0)))
				{
					clear();
					setVisible(false);
				}	
			}
			else
				setVisible(false);
		}
		else if(source == cancelB)
		{
			clear();
			setVisible(false);
		}
	}
	public void clear()
	{
		aFormsDef.reset();
		select.reset();
		animList.clear();
		collisionsList.clear();
		framesList.clear();
		anim.clear();
		surfaceG.setColor(Color.WHITE);
		surfaceG.fillRect(0,0,surfEntity.getWidth(), surfEntity.getHeight());
		this.repaint();
	};
    private class MyDispatcher implements KeyEventDispatcher 
    {
    	JDialog parent;
    	public MyDispatcher(JDialog parent)
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
	        			if(e.getKeyCode() == KeyEvent.VK_C)
	        			{
	        				collisionsList.copy();
	        			}
	        			else if(e.getKeyCode() == KeyEvent.VK_V)
	        			{
	        				collisionsList.paste();
	        			}
	        		}
	            }
        	}
            return false;
        }
    }
    public void paint(Graphics g)
	{
    	super.paint(g);
	}

    public boolean loadEntity(Entity entity)
    {
    	entity.getAnim().setCurrentAnim(0);
    	if(select.setTexture(entity.getTextureName()))
    	{
	    	savedEntity = entity;
	    	anim.set(entity.getAnim());
	    	anim.setCurrentAnim(0);
	    	saveB.setEnabled(true);
	    	return true;
    	}
    	return false;
    }
    
	@Override
	public void componentHidden(ComponentEvent e) 
	{
		saveB.setEnabled(false);
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
		select.loadTexture();
		select.updateList();
		select.renderZoom();
		
		//On a besoin de synchro le framesList avec la texture séléctionnée
		framesList.setFile(select.getCurrentTexture());
		
		animList.updateList();
		if(savedEntity != null)
			animList.setInvMass(savedEntity.getInverseMass());
		else
			animList.setInvMass(0f);
		if(animList.getListModel().size() != 0)
			animList.setSelected(0);
		
		this.repaint();
	}
}
