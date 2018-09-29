package BaseWindows;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Addons.AdvEntity;
import Addons.EntitiesGroup;
import Addons.Entity;
import Base.FileManager;
import Base.Image;
import MapEditor.EntityListRenderer;
import MapEditor.MainContainer;
import Maths.Vector2D;
import Maths.PointInt;

public class EntitiesDisplay extends Container implements ActionListener, ListSelectionListener, MouseListener
{
	private EntitiesGroup entitiesGroup;
	private SpringLayout layout;
	private JList jType, entityList;
	
	private String[] type = {"Entity", "AdvEntity"};
	
	protected DefaultListModel<String> listModel;
	protected JScrollPane listScroller;
	
	private EntityListRenderer entityListRenderer;
	private JComboBox<String> comboGroup;
	
	private MainContainer mainContainer;
	
	private JButton deleteGroup;
	
	private JPopupMenu popupMenu;
	private JMenuItem renommer, supprimer;
	
	public EntitiesDisplay(EntitiesGroup entitiesGroup, PointInt dim)
	{			
		this.setPreferredSize(new Dimension(dim.x,dim.y));
		this.entitiesGroup = entitiesGroup;
		
		popupMenu = new JPopupMenu();
		
		renommer = new JMenuItem("renommer");
		supprimer = new JMenuItem("supprimer");
		
		popupMenu.add(renommer);
		popupMenu.add(supprimer);
		
		renommer.addActionListener(this);
		supprimer.addActionListener(this);
		
		mainContainer = null;
		
		listModel = new DefaultListModel();
		layout = new SpringLayout();
		
		jType = new JList(type);
		jType.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jType.setVisibleRowCount(1);//display only one object per column
		jType.setSelectedIndex(0);
		
		entityList = new JList(listModel);
		entityListRenderer = new EntityListRenderer();
		entityList.setCellRenderer(entityListRenderer);
		entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entityList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		entityList.setVisibleRowCount(-1);
		
		deleteGroup = new JButton("-");
	
		listScroller = new JScrollPane(entityList);
		listScroller.setPreferredSize(new Dimension(dim.x - 10, dim.y - 70));
		comboGroup = new JComboBox<String>();
		comboGroup.setPreferredSize(new Dimension(dim.x - 30, 30));
		
		//layout
		layout.putConstraint(SpringLayout.WEST, jType, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, jType, 5, SpringLayout.NORTH, this);
		add(jType);
		
		layout.putConstraint(SpringLayout.WEST, listScroller, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, listScroller, 0, SpringLayout.SOUTH, jType);
		add(listScroller);
		
		layout.putConstraint(SpringLayout.WEST, deleteGroup, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, deleteGroup, 5, SpringLayout.SOUTH, listScroller);
		add(deleteGroup);
		
		layout.putConstraint(SpringLayout.WEST, comboGroup, 5, SpringLayout.EAST, deleteGroup);
		layout.putConstraint(SpringLayout.NORTH, comboGroup, 5, SpringLayout.SOUTH, listScroller);
		add(comboGroup);
		
		comboGroup.addActionListener(this);
		jType.addListSelectionListener(this);
		entityList.addMouseListener(this);
		
		mainContainer = null;
		
		deleteGroup.addActionListener(this);
		this.setLayout(layout);
		this.updateList();
	}
	public void initialize(MainContainer mainContainer)
	{
		this.mainContainer = mainContainer;
	}
	
	public void reset()
	{
		jType.setSelectedIndex(0);
	}
	
	public void updateList()
	{
		if(entitiesGroup.size() == 0)
			return;
		
		comboGroup.removeAllItems();
		for(int i=0; i<entitiesGroup.size(); i++)
		{
			comboGroup.addItem(entitiesGroup.getNameGroup(i));
		}
		
		updateImage();
		
		if(listModel.size() != 0)
			entityList.setSelectedIndex(0);
		
		if(entitiesGroup.getNameGroup(comboGroup.getSelectedIndex()) == "Defaut" || entitiesGroup.size() < 2)
			deleteGroup.setEnabled(false);
		else
			deleteGroup.setEnabled(true);
	}
	public void updateImage()
	{
		if(jType.getSelectedIndex() == 0)
			updateImageEntity();
		else
			updateImageAdvEntity();
	
		if(listModel.size() != 0)
			entityList.setSelectedIndex(0);
	}
	public void updateImageEntity()
	{
		listModel.clear();
		
		for(int i=0; i<entitiesGroup.sizeEntities(comboGroup.getSelectedIndex()); i++)
		{
			Image l_image = entitiesGroup.getEntity(comboGroup.getSelectedIndex(),i).clone();
			
			int getFirstFrame = entitiesGroup.getEntity(comboGroup.getSelectedIndex(), i).getAnim().getX();
			l_image.reset();
			l_image.setSize(EntityListRenderer.SIZE);
			l_image.setCurrentFrame(getFirstFrame);
			l_image.setLeftPos(new Vector2D(0,0));
			
			entityListRenderer.render(i, l_image);
			listModel.addElement(entitiesGroup.getEntity(comboGroup.getSelectedIndex(), i).getName());
		}
		
	}
	public void updateImageAdvEntity()
	{
		listModel.clear();
		for(int i=0; i<entitiesGroup.sizeAdvEntities(comboGroup.getSelectedIndex()); i++)
		{
			Image l_image = entitiesGroup.getAdvEntity(comboGroup.getSelectedIndex(),i).clone();
			
			int getFirstFrame = entitiesGroup.getAdvEntity(comboGroup.getSelectedIndex(),i).getAdvEntities().get(0).getAnim().getX();
			l_image.reset();
			l_image.setSize(EntityListRenderer.SIZE);
			l_image.setCurrentFrame(getFirstFrame);
			l_image.setLeftPos(new Vector2D(0,0));
			
			entityListRenderer.render(i, l_image);
			listModel.addElement(entitiesGroup.getAdvEntity(comboGroup.getSelectedIndex(),i).getName());
		}
	}
	
	public int getSelectedType()
	{
		return jType.getSelectedIndex();
	}
	public int getSelectedGroup()
	{
		return comboGroup.getSelectedIndex();
	}
	public int getSelectedObject()
	{
		return entityList.getSelectedIndex();
	}
		
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(mainContainer == null)
			return;
		int selectedType = getSelectedType();
		int selectedGroup = getSelectedGroup();
		int selected = getSelectedObject();
		if(selected == -1)
			return;
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
		{
			if(selectedType == 0)
			{
				mainContainer.launchEditor(entitiesGroup.getEntity(selectedGroup, selected));
			}
			else if(selectedType == 1)
			{
				mainContainer.launchAdvEditor(entitiesGroup.getAdvEntity(selectedGroup, selected));
			}
		}
		else if(e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3)
		{
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
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
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int selectedType = getSelectedType();
		int selectedGroup = getSelectedGroup();
		int selected = getSelectedObject();
		if(selectedType == -1 || selectedGroup == -1)
			return;

		Object source = e.getSource();
		
		if(source == deleteGroup)
		{	
			if(selectedGroup != -1)
			{
				if(JOptionPane.showConfirmDialog(null, "Supprimer ce groupe?", "Suppression", JOptionPane.YES_NO_OPTION) == 0)
				{
					entitiesGroup.removeGroup(selectedGroup);
					updateList();
					int index = comboGroup.getSelectedIndex() - 1;
					if(index < 0)
						index++;
					comboGroup.setSelectedIndex(index);
				}
			}
		}
		else if(source == supprimer)
		{
			if(selectedType != -1 && selectedGroup != -1 && selected != -1)
			{
				if(JOptionPane.showConfirmDialog(null, "Supprimer cet élément?", "Suppression", JOptionPane.YES_NO_OPTION) == 0)
				{
					if(selectedType == 0)
						entitiesGroup.removeEntity(selectedGroup, selected);
					else
						entitiesGroup.removeAdvEntity(selectedGroup, selected);
				}
			}
		}
		else if(source == renommer)
		{
			if(selectedType != -1 && selectedGroup != -1 && selected != -1)
			{
				Entity entity = entitiesGroup.getObject(selectedType, selectedGroup, selected);
				String name = JOptionPane.showInputDialog(null, "Nom du calque", entity.getName());
				if(name != null && name != "")
				{
					entity.setName(name);
				}
			}
		}
		updateImage();
		
		if(entitiesGroup.getNameGroup(comboGroup.getSelectedIndex()) == "Defaut" || entitiesGroup.size() < 2)
			deleteGroup.setEnabled(false);
		else
			deleteGroup.setEnabled(true);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		if(comboGroup.getSelectedIndex() == -1 || jType.getSelectedIndex() == -1)
			return;

		updateImage();
		
		if(entitiesGroup.getNameGroup(comboGroup.getSelectedIndex()) == "Defaut" || entitiesGroup.size() < 2)
			deleteGroup.setEnabled(false);
		else
			deleteGroup.setEnabled(true);
	}

	public <T extends Entity> T getObject()
	{
		int selectedType = getSelectedType();
		int selectedGroup = getSelectedGroup();
		int selectedObject = getSelectedObject();
		if(selectedType == -1 || selectedGroup == -1 || selectedObject == -1)
			return null;
		
		
		if(selectedType == 0)
		{
			return (T) entitiesGroup.getEntity(selectedGroup, selectedObject);
		}
		else
		{
			return (T) entitiesGroup.getAdvEntity(selectedGroup, selectedObject);
		}
	}
}
