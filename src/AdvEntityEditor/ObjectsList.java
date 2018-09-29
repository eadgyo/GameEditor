package AdvEntityEditor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Addons.AdvEntity;
import Addons.AdvAnimation;
import Addons.EntitiesGroup;
import BaseWindows.EntitiesDisplay;
import BaseWindows.Grid;
import BaseWindows.ListPerso;
import Maths.Vector2D;

public class ObjectsList extends ListPerso implements ActionListener, MouseListener, ListSelectionListener, FocusListener
{
	private boolean isInitialized;
	private JButton dispB, centerB, resetB;
	
	private AdvEntity treeAdvEntity;
	private AdvEntity copyAdvEntity;
	private Stack<AdvEntity> actualTree;
	private EntitiesDisplay entitiesDisplay;
	private SlidePanel slidePanel;
	private AdvAnimList advAnimList;
	private EntitiesGroup entitiesGroup;
	private DisplayManagerDialog displayManagerDialog;
	
	private JTextField posX, posY;
	
	private BufferedImage surfEntity;
	private Graphics surfG;
	private boolean isModifyingCenter;
	private Grid grid;
	
	public ObjectsList(JDialog frame, String labelName, int width, int height)
	{
		super(frame, labelName, width, height);
		copyAdvEntity = new AdvEntity();
		
		isInitialized = false;
		dispB = new JButton("Display");
		centerB = new JButton("Center");
		resetB = new JButton("Reset");
		posX = new JTextField("0.0", 4);
		posY = new JTextField("0.0", 4);
		isModifyingCenter = false;
		
		displayManagerDialog = new DisplayManagerDialog(frame);
		
		layout.putConstraint(SpringLayout.WEST, dispB, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, dispB, 5, SpringLayout.SOUTH, listScroller);
		this.add(dispB);
		
		layout.putConstraint(SpringLayout.WEST, centerB, 5, SpringLayout.EAST, dispB);
		layout.putConstraint(SpringLayout.NORTH, centerB, 5, SpringLayout.SOUTH, listScroller);
		this.add(centerB);
		
		layout.putConstraint(SpringLayout.WEST, resetB, 5, SpringLayout.EAST, centerB);
		layout.putConstraint(SpringLayout.NORTH, resetB, 5, SpringLayout.SOUTH, listScroller);
		this.add(resetB);
		
		layout.putConstraint(SpringLayout.WEST, posX, 5, SpringLayout.EAST, resetB);
		layout.putConstraint(SpringLayout.NORTH, posX, 5, SpringLayout.SOUTH, listScroller);
		this.add(posX);
		
		layout.putConstraint(SpringLayout.WEST, posY, 5, SpringLayout.EAST, posX);
		layout.putConstraint(SpringLayout.NORTH, posY, 5, SpringLayout.SOUTH, listScroller);
		this.add(posY);
		
		posX.addFocusListener(this);
		posY.addFocusListener(this);
		
		dispB.addActionListener(this);
		centerB.addActionListener(this);
		resetB.addActionListener(this);
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		buttonSup.setEnabled(false);
		
		this.dispB.setEnabled(false);
		this.resetB.setEnabled(false);
		this.centerB.setEnabled(false);
		this.posX.setEnabled(false);
		this.posY.setEnabled(false);
		
		actualTree = null;
		treeAdvEntity = null;
		jlist.addMouseListener(this);
		jlist.addListSelectionListener(this);
		this.setPreferredSize(new Dimension(width + 30 , height + myFont.getSize()*3 + 15));
	}
	public void initialize(Stack<AdvEntity> actualTree, EntitiesGroup entitiesGroup, AdvEntity treeAdvEntity,
			EntitiesDisplay entitiesDisplay, SlidePanel slidePanel, AdvAnimList advAnimList, BufferedImage surfEntity,
			Grid grid)
	{
		this.actualTree = actualTree;
		this.entitiesGroup = entitiesGroup;
		this.treeAdvEntity = treeAdvEntity;
		this.entitiesDisplay = entitiesDisplay;
		this.slidePanel = slidePanel;
		this.advAnimList = advAnimList;
		this.surfEntity = surfEntity;
		this.surfG = surfEntity.getGraphics();
		this.grid = grid;
		//actualTree.push(this.treeAdvEntity);
		isInitialized = true;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int selected = jlist.getSelectedIndex();
		if(selected == -1)
			return;
		isUsed();
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
		{
			
			if(actualTree.size() == 0)
			{
				slidePanel.closeRunner();
				actualTree.push(treeAdvEntity);
				endUsed();
				updateList();
				advAnimList.updateList();
			}
			else if(selected < actualTree.lastElement().getAdvEntities().size())
			{
				slidePanel.closeRunner();
				actualTree.push(actualTree.lastElement().getAdvEntities().get(selected));
				endUsed();
				updateList();
				advAnimList.updateList();
			}
		}
		endUsed();
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
		isUsed();
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			if(actualTree.size() != 0)
			{
				slidePanel.closeRunner();
				actualTree.pop();
				endUsed();
				updateList();
			}
		}
		endUsed();
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		advAnimList.updateList();
		if(advAnimList.getListModel().size() != 0)
			advAnimList.setSelected(0);
		slidePanel.load();
		updateAdvCenter();
		isModifyingCenter = false;
		centerB.setText("Center");
		
		if(jlist.isSelectionEmpty() || actualTree.size() == 0)
			this.buttonSup.setEnabled(false);
		else
			this.buttonSup.setEnabled(true);
		
		if(jlist.isSelectionEmpty())
		{
			this.dispB.setEnabled(false);
			this.resetB.setEnabled(false);
			this.centerB.setEnabled(false);
			this.posX.setEnabled(false);
			this.posY.setEnabled(false);
		}
		else
		{
			if(actualTree.size() == 0)
				this.dispB.setEnabled(true);
			this.resetB.setEnabled(true);
			this.centerB.setEnabled(true);
			this.posX.setEnabled(true);
			this.posY.setEnabled(true);
		}
		slidePanel.render();
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(!isInitialized)
			return;
		int selected = jlist.getSelectedIndex();
		int selectedEntity = entitiesDisplay.getSelectedObject();
		int type = entitiesDisplay.getSelectedType();
		int group = entitiesDisplay.getSelectedGroup();
		if(type == -1)
		{
			entitiesDisplay.reset();
			return;
		}
		isUsed();
		Object source = e.getSource();
		if(source == centerB)
		{
			if(selected != -1)
				isModifyingCenter = !isModifyingCenter;
			if(isModifyingCenter)
				centerB.setText("   Set   ");
			else
				centerB.setText("Center");
		}
		else if(source == dispB)
		{
			displayManagerDialog.launchDial(getActualAdvEntity());
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == buttonAdd)
		{
			if(selectedEntity == -1 || type == -1 || group == -1)
			{
				endUsed();
				return;
			}
			if(type == 0)//Entity
			{
				AdvEntity currentAdvEntity = this.getActualAdvEntity();
				boolean addingIn = false;
				if(currentAdvEntity == null)
				{
					addingIn = true;
					currentAdvEntity = actualTree.lastElement();
				}
				if(!currentAdvEntity.isInitialized())
				{
					AdvEntity l_advEntity = new AdvEntity(entitiesGroup.getEntity(group, selectedEntity));
					l_advEntity.reset();
					l_advEntity.setAdvCenter(l_advEntity.getPos());
					l_advEntity.setIsDisplayingRec(true);
					l_advEntity.setGraphics(surfG);
					l_advEntity.setAdvAnimation(new AdvAnimation());
					currentAdvEntity.set(l_advEntity);
				}
				else
				{
					AdvEntity l_advEntity = new AdvEntity(entitiesGroup.getEntity(group, selectedEntity));
					l_advEntity.reset();
					l_advEntity.setAdvCenter(l_advEntity.getPos());
					l_advEntity.setIsDisplayingRec(true);
					l_advEntity.setGraphics(surfG);
					l_advEntity.setAdvAnimation(new AdvAnimation());
					currentAdvEntity.addAdvEntity(l_advEntity);
				}
				endUsed();
				
				if(addingIn)
					updateList();
				else if(actualTree.size() == 0 )
				{
					updateList();
					if(listModel.size() != 0 && selected == -1)
						jlist.setSelectedIndex(listModel.size() - 1);
				}
				
				advAnimList.updateList();
			}
			else if(type == 1)
			{
				AdvEntity currentAdvEntity = this.getActualAdvEntity();
				boolean addingIn = false;
				if(currentAdvEntity == null)
				{
					addingIn = true;
					currentAdvEntity = actualTree.lastElement();
				}
				if(!currentAdvEntity.isInitialized())
				{
					AdvEntity l_advEntity = entitiesGroup.getAdvEntity(group, selectedEntity).clone();
					l_advEntity.setIsDisplayingRec(true);
					l_advEntity.setGraphics(surfG);
					currentAdvEntity.set(l_advEntity);
				}
				else
				{
					AdvEntity l_advEntity = entitiesGroup.getAdvEntity(group, selectedEntity).clone();
					l_advEntity.setIsDisplayingRec(true);
					l_advEntity.setGraphics(surfG);
					currentAdvEntity.addAdvEntity(l_advEntity);
				}
				endUsed();
				updateList();
				if(addingIn)
					updateList();
				else if(actualTree.size() == 0)
				{
					updateList();
					if(listModel.size() != 0 && selected == -1)
						jlist.setSelectedIndex(listModel.size() - 1);
				}
				
				advAnimList.updateList();
				if(advAnimList.getListModel().size() != 0)
					advAnimList.setSelected(0);
			}
		}
		else if(source == resetB)
		{
			if(actualTree.size() == 0)
			{
				treeAdvEntity.resetAdvCenter();
			}
			else
			{
				actualTree.lastElement().getAdvEntity(selected).resetAdvCenter();
			}
			updateAdvCenter();
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == buttonSup)
		{
			actualTree.lastElement().clear(selected);
			endUsed();
			
			updateList();
			if(listModel.size() != 0)
			{
				if(selected - 1 < 0)
					jlist.setSelectedIndex(0);
				else
					jlist.setSelectedIndex(selected - 1);
			}
			advAnimList.updateList();
			
			slidePanel.update();
			slidePanel.render();
		}
		endUsed();
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		if(jlist.isSelectionEmpty())
			return;
		Vector2D pos = new Vector2D(Float.parseFloat(posX.getText()), Float.parseFloat(posY.getText()));
		AdvEntity advEntity = null;
		if(actualTree.size() == 0)
		{
			advEntity = treeAdvEntity;
		}
		else
		{
			advEntity = actualTree.lastElement().getAdvEntity(jlist.getSelectedIndex());
		}
		advEntity.setAdvCenter(pos);
		
		updateAdvCenter();
		slidePanel.update();
		slidePanel.render();
	}
	
	public void updateList()
	{
		isUsed();
		this.clear();
		
		if(!treeAdvEntity.isInitialized())
		{
			endUsed();
			return;
		}
		
		if(actualTree.size() == 0)
		{
			listModel.addElement(treeAdvEntity.getName());
		}
		else
		{
			for(int i=0; i<actualTree.lastElement().getAdvEntities().size(); i++)
			{
				listModel.addElement(actualTree.lastElement().getAdvEntities().get(i).getName());
			}
		}
		endUsed();
	}
	public void modifyingCenter(MouseEvent e)
	{
		if(jlist.isSelectionEmpty())
			return;
		if(isModifyingCenter)
		{
			isModifyingCenter = false;
			centerB.setText("Center");
			if(jlist.isSelectionEmpty())
			{
				return;
			}
			//position - le vec translation 0.5*length
			Vector2D pos = new Vector2D(e.getX(), e.getY());
 			Vector2D vec = new Vector2D(slidePanel.getTranslate());
			pos.set(grid.gridRound(pos, vec));
			pos.translate(vec.multiply(-1));
			AdvEntity advEntity = null;
			if(actualTree.size() == 0)
			{
				advEntity = treeAdvEntity;
			}
			else
			{
				advEntity = actualTree.lastElement().getAdvEntity(jlist.getSelectedIndex());
			}
			//calcule de advCenter sans les transformations
			pos.translate(advEntity.getPos().multiply(-1));
			pos.scale(advEntity.getSpriteDataRect().getWidth()/advEntity.getRectangle().getWidth(), new Vector2D(0,0));
			if(advEntity.getFlipH())
				pos.flipH(new Vector2D(0,0));
			if(advEntity.getFlipV())
				pos.flipV(new Vector2D(0,0));
			pos.rotateRadians(-advEntity.getRadians(), new Vector2D(0,0));
			
			//change la position du pivot
			advEntity.setAdvCenter(pos);
			updateAdvCenter();
			slidePanel.update();
			slidePanel.render();
		}
	}
	
	public AdvEntity getActualAdvEntity()
	{
		int selected = jlist.getSelectedIndex();
		if(actualTree.size() == 0)
			return treeAdvEntity;
		else
		{
			if(selected == -1)
				return null;
			else
				return actualTree.lastElement().getAdvEntity(selected);
		}
	}
	public void updateAdvCenter()
	{
		if(jlist.isSelectionEmpty())
			return;
		AdvEntity advEntity = null;
		if(actualTree.size() == 0)
		{
			advEntity = treeAdvEntity;
		}
		else
		{
			advEntity = actualTree.lastElement().getAdvEntity(jlist.getSelectedIndex());
		}
		posX.setText((((int) (advEntity.getAdvCenter().x*10))/10.0f) + "");
		posY.setText((((int) (advEntity.getAdvCenter().y*10))/10.0f) + "");
	}

	
	public void addAdvEntity(AdvEntity advEntity)
	{
		int selected = jlist.getSelectedIndex();
		AdvEntity currentAdvEntity = this.getActualAdvEntity();
		boolean addingIn = false;
		if(currentAdvEntity == null)
		{
			addingIn = true;
			currentAdvEntity = actualTree.lastElement();
		}
		if(!currentAdvEntity.isInitialized())
		{
			advEntity.setIsDisplayingRec(true);
			advEntity.setGraphics(surfG);
			currentAdvEntity.set(advEntity);
		}
		else
		{
			advEntity.setIsDisplayingRec(true);
			advEntity.setGraphics(surfG);
			currentAdvEntity.addAdvEntity(advEntity);
		}
		endUsed();
		updateList();
		if(addingIn)
			updateList();
		else if(actualTree.size() == 0)
		{
			updateList();
			if(listModel.size() != 0 && selected == -1)
				jlist.setSelectedIndex(listModel.size() - 1);
		}
		
		advAnimList.updateList();
		if(advAnimList.getListModel().size() != 0)
			advAnimList.setSelected(0);
	}
	
	public void copy()
	{
		AdvEntity advEntity = this.getActualAdvEntity();
		if(advEntity != null)
		{
			copyAdvEntity.set(advEntity);
		}
	}
	public void paste()
	{
		if(copyAdvEntity.isInitialized())
			this.addAdvEntity(copyAdvEntity.clone());
	}
	public void clearCopy()
	{
		copyAdvEntity.clear();
	}
}
