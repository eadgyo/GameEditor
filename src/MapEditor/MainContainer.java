package MapEditor;
import javax.swing.* ;

import Addons.AdvEntity;
import Addons.EntitiesGroup;
import Addons.Entity;
import AdvEntityEditor.AdvEntityEditor;
import Base.Image;
import BaseWindows.EntitiesDisplay;
import EntityEditor.EntityEditor;
import EntityEditor.Select;
import Map.Map;
import Map.Maps;
import Maths.PointInt;
import Maths.Vector2D;
import Maths.sRectangle;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class MainContainer extends Container implements ActionListener, ItemListener
{
	
	Maps maps;
	
	//variables
	private JPanel process;
	private EntityEditor entityEditor;
	private AdvEntityEditor advEntityEditor;
	private Box inform;
	private Container test, defEntity;
	
	//functions
	//bottom
	private JLabel labLayer, labmap, labpos;
	private JButton addMap, deleteMap;
	
	private JComboBox comboMaps;
	
	//defEntity
	private LayersTree layersTree;
	/*private JLabel labDefEntity, labPos2, labAngle, labSize, labFlipV, labFlipH, labImage, labAnimation, labSetAnim, labEvenemential;
	private JTextField fieldPosX, fieldPosY, fieldAngle, fieldSize;
	private JButton addAnimation, modifyAnimation, addEven, modifyEven, modifyImage;
	private JCheckBox checkFlipV, checkFlipH;
	private JComboBox comboAnim;*/

	private EntitiesGroup entitiesGroup;
	private EntitiesDisplay entitiesDisplay;
	
	//process
	private MapView mapView;
	
	private Graphics gProcess;
	private JLabel processImageLabel;
	private PointInt dimension = new PointInt(300, 350);
	private PointInt WindowDim = new PointInt(1600, 800);
	
	private JFrame parent;
	
	public MainContainer(JFrame parent, EntitiesGroup entitiesGroup)
	{
		this.parent = parent;
		
		maps = new Maps();
		maps.addMap(new Map("Defaut"));
		
		this.entitiesGroup = entitiesGroup;
		entityEditor = new EntityEditor(entitiesGroup, parent);
		advEntityEditor = new AdvEntityEditor(entitiesGroup, parent);
		
		sRectangle rec = new sRectangle();
		
		initializeBasic();
		initializeBottom();
		initializeDefEntity();
	}
	private void initializeBasic()
	{
		setLayout(new BorderLayout());
		test = new Container();
		test.setLayout(new BorderLayout());
		
		entitiesDisplay = new EntitiesDisplay(entitiesGroup, dimension);
		entitiesDisplay.initialize(this);
		
		process = new JPanel();	
		process.setPreferredSize(new Dimension(WindowDim.x - dimension.x - 1,WindowDim.y - 20));
		defEntity = new Container();
		defEntity.setPreferredSize(new Dimension(dimension.x,450));
		inform = Box.createHorizontalBox();
		inform.setPreferredSize(new Dimension(WindowDim.x,30));
		
		test.add(entitiesDisplay, BorderLayout.PAGE_START);
		test.add(defEntity, BorderLayout.PAGE_END);
		test.setPreferredSize(new Dimension(dimension.x, dimension.y));
		
		this.add(test, BorderLayout.LINE_START);
		add(process, BorderLayout.LINE_END);
		add(inform, BorderLayout.PAGE_END);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeDefEntity()
	{
		defEntity.setLayout(new FlowLayout());
		
		mapView = new MapView(WindowDim.x - dimension.x - 1, WindowDim.y - 20, BufferedImage.TYPE_INT_ARGB);
		mapView.setMap(maps.get(0));
		
		process.add(mapView);
		
		layersTree = new LayersTree(new PointInt(270, 450), entitiesDisplay, mapView);
		layersTree.loadMap(maps.get(0));
		defEntity.add(layersTree);
		
		mapView.initialize(layersTree);
		
		updateComboMap();
		
		/*labDefEntity = new JLabel("Entity");
		labDefEntity.setFont(new Font("Arial", Font.BOLD, 30));
		layout.putConstraint(SpringLayout.WEST, labDefEntity, 90, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labDefEntity, 5, SpringLayout.NORTH, defEntity);
		defEntity.add(labDefEntity);
		
		//Position
		labPos2 = new JLabel("Position: ");
		fieldPosX = new JTextField(5);
		fieldPosX.setText("0.0");
		fieldPosY = new JTextField(5);
		fieldPosY.setText("0.0");
		layout.putConstraint(SpringLayout.WEST, labPos2, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labPos2, 10, SpringLayout.SOUTH, labDefEntity);
		
		layout.putConstraint(SpringLayout.WEST, fieldPosX, 5, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, fieldPosX, 10, SpringLayout.SOUTH, labDefEntity);
		
		layout.putConstraint(SpringLayout.WEST, fieldPosY, 5, SpringLayout.EAST, fieldPosX);
		layout.putConstraint(SpringLayout.NORTH, fieldPosY, 10, SpringLayout.SOUTH, labDefEntity);
		defEntity.add(labPos2);
		defEntity.add(fieldPosX);
		defEntity.add(fieldPosY);
		fieldPosX.addActionListener(this);
		fieldPosY.addActionListener(this);
		
		//angle
		labAngle = new JLabel("Angle: ");
		fieldAngle = new JTextField(5);
		fieldAngle.setText("0.0");
		layout.putConstraint(SpringLayout.WEST, labAngle, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labAngle, 5, SpringLayout.SOUTH, labPos2);
		
		layout.putConstraint(SpringLayout.WEST, fieldAngle, 5, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, fieldAngle, 5, SpringLayout.SOUTH, labPos2);
		defEntity.add(labAngle);
		defEntity.add(fieldAngle);
		fieldAngle.addActionListener(this);
		
		//Size
		labSize = new JLabel("Size: ");
		fieldSize = new JTextField(5);
		fieldSize.setText("1.0");
		layout.putConstraint(SpringLayout.WEST, labSize, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labSize, 5, SpringLayout.SOUTH, labAngle);
		
		layout.putConstraint(SpringLayout.WEST, fieldSize, 5, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, fieldSize, 5, SpringLayout.SOUTH, labAngle);
		defEntity.add(labSize);
		defEntity.add(fieldSize);
		fieldSize.addActionListener(this);
		
		//Flip
		labFlipV = new JLabel("Flip V: ");
		checkFlipV = new JCheckBox();
		layout.putConstraint(SpringLayout.WEST, labFlipV, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labFlipV, 15, SpringLayout.SOUTH, labSize);
		
		layout.putConstraint(SpringLayout.WEST, checkFlipV, 1, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, checkFlipV, 12, SpringLayout.SOUTH, labSize);
		defEntity.add(labFlipV);
		defEntity.add(checkFlipV);
		checkFlipV.addActionListener(this);
		
		labFlipH = new JLabel("Flip H: ");
		checkFlipH = new JCheckBox();
		layout.putConstraint(SpringLayout.WEST, labFlipH, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labFlipH, 5, SpringLayout.SOUTH, labFlipV);
		
		layout.putConstraint(SpringLayout.WEST, checkFlipH, 1, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, checkFlipH, 1, SpringLayout.SOUTH, labFlipV);
		defEntity.add(labFlipH);
		defEntity.add(checkFlipH);
		checkFlipH.addActionListener(this);
		
		//Image
		labImage = new JLabel("Image: ");
		modifyImage = new JButton("Set");
		layout.putConstraint(SpringLayout.WEST, labImage, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labImage, 12, SpringLayout.SOUTH, labFlipH);
		
		layout.putConstraint(SpringLayout.WEST, modifyImage, 5, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, modifyImage, 7, SpringLayout.SOUTH, labFlipH);
		defEntity.add(labImage);
		defEntity.add(modifyImage);
		modifyImage.addActionListener(this);
		
		
		//////Animation
		Font l_titleFont = new Font("Arial", Font.BOLD, 17);
		labAnimation = new JLabel("Animation ");
		labAnimation.setFont(l_titleFont);
		addAnimation = new JButton("  Add   ");
		modifyAnimation = new JButton("Modify");
		labSetAnim = new JLabel("Current: ");
		comboAnim = new JComboBox();
		comboAnim.addItem(new ComboItem("-", 0));
		layout.putConstraint(SpringLayout.WEST, labAnimation, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labAnimation, 30, SpringLayout.SOUTH, labImage);
		
		layout.putConstraint(SpringLayout.WEST, addAnimation, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, addAnimation, 5, SpringLayout.SOUTH, labAnimation);
		
		layout.putConstraint(SpringLayout.WEST, modifyAnimation, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, modifyAnimation, 5, SpringLayout.SOUTH, addAnimation);
		
		layout.putConstraint(SpringLayout.WEST, labSetAnim, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labSetAnim, 12, SpringLayout.SOUTH, modifyAnimation);
		
		layout.putConstraint(SpringLayout.WEST, comboAnim, 5, SpringLayout.EAST, labPos2);
		layout.putConstraint(SpringLayout.NORTH, comboAnim, 7, SpringLayout.SOUTH, modifyAnimation);
		defEntity.add(labAnimation);
		defEntity.add(addAnimation);
		defEntity.add(modifyAnimation);
		defEntity.add(labSetAnim);
		defEntity.add(comboAnim);
		addAnimation.addActionListener(this);
		modifyAnimation.addActionListener(this);
		comboAnim.addActionListener(this);
		
		/////Evenemential
		labEvenemential = new JLabel("Evenemential ");
		labEvenemential.setFont(l_titleFont);
		addEven = new JButton("  Add   ");
		modifyEven = new JButton("Modify");
		layout.putConstraint(SpringLayout.WEST, labEvenemential, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, labEvenemential, 30, SpringLayout.SOUTH, labSetAnim);
		
		layout.putConstraint(SpringLayout.WEST, addEven, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, addEven, 5, SpringLayout.SOUTH, labEvenemential);
		
		layout.putConstraint(SpringLayout.WEST, modifyEven, 5, SpringLayout.WEST, defEntity);
		layout.putConstraint(SpringLayout.NORTH, modifyEven, 5, SpringLayout.SOUTH, addEven);
		defEntity.add(labEvenemential);
		defEntity.add(addEven);
		defEntity.add(modifyEven);
		addEven.addActionListener(this);
		addEven.addActionListener(this);
		*/
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initializeBottom()
	{
		//Layer
		inform.add(Box.createHorizontalStrut(10));

		//Map
		addMap = new JButton("+");
		deleteMap = new JButton("-");
		
		comboMaps = new JComboBox();
		/*for(int i=0; i<map12s.size(); i++)
			comboMaps.addItem(new ComboItem(map12s.get(i).getName(), i));*/
		labmap = new JLabel("Map: ");
		inform.add(labmap);
		inform.add(comboMaps);
		inform.add(Box.createHorizontalStrut(10));
		inform.add(addMap);
		inform.add(deleteMap);
		
		comboMaps.addItemListener(this);
		
		inform.add(Box.createHorizontalStrut(100));
		
		//PosfieldPosX.getText()
		labpos = new JLabel("Position: (0.0, 0.0)");
		inform.add(labpos);
		inform.add(Box.createHorizontalStrut(10));
		
		addMap.addActionListener(this);
		deleteMap.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int selectedMap = comboMaps.getSelectedIndex();
		Object source = e.getSource();
		if(source == addMap)
		{
			String name = JOptionPane.showInputDialog(null, "Nom de la Map", "map");
			if(name != null && name != "")
			{
				Map newMap = new Map(name);
				maps.addMap(newMap);
				comboMaps.addItem(new ComboItem(newMap.getName(), newMap));
			}
		}
		else if(source == deleteMap)
		{
			if(selectedMap != -1 && comboMaps.getItemCount() != 1)
			{
				int answer = JOptionPane.showConfirmDialog(null, "Voulez-vous supprimer cet map", "Suppression", JOptionPane.YES_NO_OPTION);
				if(answer == 0)
				{
					comboMaps.removeItemAt(selectedMap);
					maps.removeMap(selectedMap);
				}
			}
		}
	}
	public void paint(Graphics g)
	{
		super.paint(g);
		//process.repaint();
		
		
		//select.repaint();
		//inform.repaint();
		
	}
	public void addEntity()
	{
		/*
		l_image.setX(Float.parseFloat(fieldPosX.getText()));
		l_image.setY(Float.parseFloat(fieldPosY.getText()));
		l_image.setDegrees(Float.parseFloat(fieldAngle.getText()));
		l_image.setScale(Float.parseFloat(fieldSize.getText()));
		l_image.setFlipH(checkFlipH.isSelected());
		l_image.setFlipV(checkFlipV.isSelected());
		
		l_image.setGraphics(processSurface.getGraphics());
		
		System.out.println(l_image.getX());
		System.out.println(l_image.getY());
		l_image.draw(new Point2D());
		repaint();
		*/
	}
	
	public void setEntityEditorVisible(boolean b)
	{
		entityEditor.setVisible(b);
		entitiesDisplay.updateList();
	}
	public void setAdvEntityEditorVisible(boolean b)
	{
		advEntityEditor.setVisible(b);
		entitiesDisplay.updateList();
	}

	public void launchAdvEditor(AdvEntity advEntity)
	{
		advEntityEditor.loadAdvEntity(advEntity);
		advEntityEditor.setVisible(true);
		entitiesDisplay.updateList();
	}
	public void launchEditor(Entity entity)
	{
		if(entityEditor.loadEntity(entity))
		{
			entityEditor.setVisible(true);
			entitiesDisplay.updateList();
		}
		else
		{
			JOptionPane.showMessageDialog(parent, "We are looking for: " + entity.getTextureName() , "Error: File is missing", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void render()
	{
		mapView.render();
	}
	public void updateComboMap()
	{
		comboMaps.removeAllItems();
		for(int i=0; i<maps.size(); i++)
		{
			comboMaps.addItem(new ComboItem(maps.getName(i), maps.get(i)));
		}
	}
	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		ComboItem selectedItem = (ComboItem) (e.getItem());
		if(comboMaps.getSelectedIndex() != -1)
		{
			Map selectedMap = (Map) selectedItem.getValue();
			mapView.setMap(selectedMap);
			layersTree.loadMap(selectedMap);
		}
	}
}
