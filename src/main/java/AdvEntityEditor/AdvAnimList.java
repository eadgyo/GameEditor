package AdvEntityEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Addons.AdvAnimKey;
import Addons.AdvAnimation;
import Addons.AdvEntity;
import BaseWindows.ListPerso;

public class AdvAnimList extends ListPerso implements ActionListener, ListSelectionListener
{
	private AdvAnimation copyingAdvAnim;
	
	private AdvEntity treeAdvEntity;
	private ObjectTree objectsTree;
	private SlidePanel slidePanel;
	private JCheckBox loopingCheck;
	private JLabel loopingLabel;
	private boolean isInitialized;
	
	public AdvAnimList(JDialog frame, String labelName, int width, int height)
	{
		super(frame, labelName, width, height);
		isInitialized = false;
		copyingAdvAnim = new AdvAnimation();
		
		loopingCheck = new JCheckBox();
		loopingCheck.addActionListener(this);
		loopingLabel = new JLabel("Is Looping");
		
		layout.putConstraint(SpringLayout.WEST, loopingCheck, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, loopingCheck, 2, SpringLayout.SOUTH, listScroller);
		this.add(loopingCheck);
		
		layout.putConstraint(SpringLayout.WEST, loopingLabel, 5, SpringLayout.EAST, loopingCheck);
		layout.putConstraint(SpringLayout.NORTH, loopingLabel, 5, SpringLayout.SOUTH, listScroller);
		this.add(loopingLabel);
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		buttonSup.setEnabled(false);
		loopingCheck.setEnabled(false);
		jlist.addListSelectionListener(this);
		this.setPreferredSize(new Dimension(width + 30 , height + myFont.getSize()*3 + 15));
	}
	public void initialize(AdvEntity treeAdvEntity, SlidePanel slidePanel, ObjectTree objectsTree)
	{
		
		this.treeAdvEntity = treeAdvEntity;
		this.slidePanel = slidePanel;
		this.objectsTree = objectsTree;
		isInitialized = true;
	}
	
	public AdvAnimation getCurrentAdvAnim()
	{
		AdvAnimation advAnim = null;

		AdvEntity advEntiy = objectsTree.getActualAdvEntity();
		if(advEntiy != null)
			advAnim = advEntiy.getAdvAnimation();
		else
			this.clear();
		return advAnim;
	}
	
	public void updateList()
	{
		AdvEntity advEntity = objectsTree.getActualAdvEntity();
		
		/*if(!treeAdvEntity.isInitialized())
		{
			this.clear();
			return;
		}
		if(advEntity != null && !advEntity.isInitialized())
		{
			this.clear();
			return;
		}*/
		isUsed();
		AdvAnimation advAnim = getCurrentAdvAnim();
		
		if(advAnim == null)
		{
			endUsed();
			this.clear();
			return;
		}
		
		ArrayList<AdvAnimKey> animKeys = advAnim.getAll();
		this.clear();
		for(int i=0; i<animKeys.size(); i++)
		{
			listModel.addElement(animKeys.get(i).name);
		}
		endUsed();
	}
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		if(slidePanel.getIsPlaying())
			slidePanel.managedPlay();
		int selected = jlist.getSelectedIndex();
		if(selected == -1)
		{
			slidePanel.renderBack();
			loopingCheck.setSelected(false);
			loopingCheck.setEnabled(false);
			this.buttonSup.setEnabled(false);
		}
		else
		{
			AdvEntity advEntity = objectsTree.getActualAdvEntity();
			if(advEntity.getAdvAnimation().getLooping(selected))
				loopingCheck.setSelected(true);
			else
				loopingCheck.setSelected(false);
			loopingCheck.setEnabled(true);
			slidePanel.load();
			slidePanel.render();
			this.buttonSup.setEnabled(true);
		}
	}
	public void actionPerformed(ActionEvent e) 
	{
		int selected = jlist.getSelectedIndex();
		isUsed();
		Object source = e.getSource();
		if(source == buttonAdd)
		{
			AdvAnimation advAnim = getCurrentAdvAnim();
			if(advAnim != null)
			{
				String l_name = JOptionPane.showInputDialog(this, "Animation name:");
				if(l_name != "" && l_name != null)
					advAnim.add(l_name, 10);
			}
			endUsed();
			
			updateList();
			jlist.setSelectedIndex(listModel.size() - 1);
			
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == buttonSup)
		{
			if(selected == -1)
				return;
			AdvAnimation advAnim = getCurrentAdvAnim();
			advAnim.remove(selected);
			
			endUsed();
			
			updateList();
			if(listModel.size() != 0)
			{
				if(selected - 1 < 0)
					jlist.setSelectedIndex(0);
				else
					jlist.setSelectedIndex(selected - 1);
			}
			
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == loopingCheck)
		{
			AdvAnimation advAnim = getCurrentAdvAnim();
			if(advAnim != null && selected != -1)
				advAnim.get(selected).isLooping = loopingCheck.isSelected();
		}
		endUsed();
	}
	
	public void copy()
	{
		int selected = jlist.getSelectedIndex();
		copyingAdvAnim.clear();
		if(selected != -1)
		{
			AdvAnimation advAnim = this.getCurrentAdvAnim();
			if(advAnim != null)
				copyingAdvAnim.add(advAnim, selected, -1);
		}
	}
	public void paste()
	{
		AdvAnimation advAnim = this.getCurrentAdvAnim();
		AdvEntity advEntity = objectsTree.getActualAdvEntity();
		
		if(advAnim != null && advEntity != null)
		{
			advAnim.add(copyingAdvAnim, advEntity.getAnim().size());
			this.updateList();
		}
	}
	public void clearCopy()
	{
		copyingAdvAnim.clear();
	}
}
