package MapEditor;

import Addons.EntitiesGroup;
import Base.FileManager;
import TestAI.TestAI;
import TestEngine.TestWin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener, ComponentListener
{
	private EntitiesGroup entitiesGroup;
	
	private MainContainer mainContainer;
	private boolean isLooping;
	private JMenuBar menu;
	private JMenu file, edit, create;
	private JMenuItem save, saveAs, load,
		copy, paste, undo, redo,
		path;
	private JButton testAIB, testEngigne, advEntityEditorB, entityEditorB;
	private Container buttonsBox;
	private GridBagLayout gbl;
	private GridBagConstraints gbc;
	private TestWin testWin;
	private TestAI testAI;
	
	public Game()
	{
		this.setResizable(false);
		isLooping = true;
		setTitle("Map editor");
		setSize(1600, 900);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			//e.printStackTrace();
		}
		entitiesGroup = (EntitiesGroup) FileManager.getInstance().getObjectInside(EntitiesGroup.class.getName() + ".data");
		if(entitiesGroup == null)
			entitiesGroup = new EntitiesGroup();
		entitiesGroup.loadTexture();
		
		menu = new JMenuBar();
		
		//create Panels
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		setLayout(gbl);
		
		//create Menu options
		//file
		file = new JMenu("File"); 
		save = new JMenuItem("Save"); save.addActionListener(this); save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
		saveAs = new JMenuItem("Save as"); saveAs.addActionListener(this); saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK));
		load = new JMenuItem("Load"); load.addActionListener(this); load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
		file.add(save); file.add(saveAs); file.add(load);
		//edit
		edit = new JMenu("Edit");
		copy = new JMenuItem("Copy"); copy.addActionListener(this); copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
		paste = new JMenuItem("Paste"); paste.addActionListener(this); paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
		undo = new JMenuItem("Undo"); undo.addActionListener(this); undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
		redo = new JMenuItem("Redo"); redo.addActionListener(this); redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,InputEvent.CTRL_MASK));
		edit.add(copy); edit.add(paste); edit.add(undo); edit.add(redo);
		//create
		create = new JMenu("Create");
		path = new JMenuItem("Path"); path.addActionListener(this);
		create.add(path);
		
		//create Menu buttons
		buttonsBox = new Container();
		buttonsBox.setLayout(new FlowLayout(FlowLayout.RIGHT));
		testAIB = new JButton("Test AI"); buttonsBox.add(testAIB); testAIB.addActionListener(this);
		testEngigne = new JButton("Test Engine"); buttonsBox.add(testEngigne); testEngigne.addActionListener(this);
		advEntityEditorB = new JButton("AdvEntity Editor"); buttonsBox.add(advEntityEditorB); advEntityEditorB.addActionListener(this);
		entityEditorB = new JButton("Entity Editor"); buttonsBox.add(entityEditorB); entityEditorB.addActionListener(this);
		
		menu.add(file);
		menu.add(edit);
		menu.add(create);
		menu.add(buttonsBox);
		
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.gridwidth = 100; gbc.gridheight = 1;
		gbc.weightx = 33;  gbc.weighty = 0;
		add(menu, gbc);
		
		mainContainer = new MainContainer(this, entitiesGroup);
		gbc.gridx = 0; gbc.gridy = 1;
		gbc.gridwidth = 100; gbc.gridheight = 97;
		gbc.weightx = 33;  gbc.weighty = 100;
		add(mainContainer, gbc);
		
		testWin = new TestWin(this);
		testAI = new TestAI(this);
		
		this.addComponentListener(this);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		//file
		if(source == save)
		{
			entitiesGroup.clearTexture();
			FileManager.getInstance().saveObject(entitiesGroup);
			entitiesGroup.loadTexture();
		}
		if(source == saveAs)
		{
			System.out.println("Save As MotherFucker");
		}
		if(source == load)
		{
			
		}
		
		//edit
		if(source == copy)
		{
			
		}
		if(source == paste)
		{
			
		}
		if(source == undo)
		{
			
		}
		if(source == redo)
		{
			
		}
		
		//create
		if(source == path)
		{
			
		}
		
		//buttons
		if(source == testAIB)
		{
			testAI.start();
		}
		if(source == testEngigne)
		{
			//testWin.start(entitiesGroup.getEntities(0));
			testWin.start1();
		}
		if(source == advEntityEditorB)
		{
			mainContainer.setAdvEntityEditorVisible(true);
		}
		if(source == entityEditorB)
		{
			mainContainer.setEntityEditorVisible(true);
			
			//mainContainer.addEntity();
		}
	}
	public boolean getIsLooping() {return isLooping;}
	public void setIsLooping(boolean b) {this.isLooping = b;}
	public void paint(Graphics g) 
	{
		super.paint(g);
	}
	public void update()
	{
		
		
	}
	
	@Override
	public void componentHidden(ComponentEvent e) 
	{
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
		mainContainer.render();
	}
}

