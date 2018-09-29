package EntityEditor;

import javax.swing.* ;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Addons.Animation;
import BaseWindows.ListPerso;

@SuppressWarnings("serial")
public class AnimList extends ListPerso implements ActionListener, FocusListener, ListSelectionListener
{
	private Animation anim;
	private AnimDialog dial;
	private JLabel delayLabel, massLabel;
	private JTextField delayField, massField;
	private Select select;
	private FramesList framesList;
	private boolean isInitialized;
	
	public AnimList(JDialog frame, String labelName, int width, int height) 
	{
		super(frame, labelName, width, height);
		isInitialized = false;
		//ajout listener
		jlist.addListSelectionListener(this);
		
		delayLabel = new JLabel("Delay ms: ");
		massLabel = new JLabel("Inv mass: ");
		delayField = new JTextField("30", 4);
		massField = new JTextField("0", 4);
			
		layout.putConstraint(SpringLayout.WEST, delayLabel, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, delayLabel, 12, SpringLayout.SOUTH, listScroller);
		this.add(delayLabel);
		
		layout.putConstraint(SpringLayout.WEST, delayField, 5, SpringLayout.EAST, delayLabel);
		layout.putConstraint(SpringLayout.NORTH, delayField, 5, SpringLayout.SOUTH, listScroller);
		delayField.addFocusListener(this);
		this.add(delayField);
		
		layout.putConstraint(SpringLayout.WEST, massLabel, 5, SpringLayout.EAST, delayLabel);
		layout.putConstraint(SpringLayout.NORTH, massLabel, 12, SpringLayout.SOUTH, listScroller);
		this.add(massLabel);
		
		layout.putConstraint(SpringLayout.WEST, massField, 5, SpringLayout.EAST, massLabel);
		layout.putConstraint(SpringLayout.NORTH, massField, 5, SpringLayout.SOUTH, listScroller);
		this.add(massField);
		
		
		dial = new AnimDialog(frame);
		dial.setLocationRelativeTo(null);
		
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		buttonSup.setEnabled(false);
	}
	public void initialize(Animation anim, FramesList framesList, Select select)
	{
		this.anim = anim;
		this.framesList = framesList;
		this.select = select;
		
		if(select.isInitialized())
			isInitialized = true;
		//test
		/*
		framesList.setFile(select.getFile());
		AnimInfos l_infos = new AnimInfos();
		addElements(l_infos.name);
		anim.add(l_infos.frame, Integer.parseInt(delayField.getText()));*/
	}
	
	public void clear()
	{
		if(!isInitialized)
			return;
		anim.setCurrentAnim(-1);
		buttonSup.setEnabled(false);
		delayField.setText("30");
		massField.setText("0");
		super.clear();
	}
	@Override
	public void deselect()
	{
		super.deselect();
	}
	@Override
	public void setSelected(int selected)
	{
		super.setSelected(selected);
	}
	@Override
	public void removeSelected()
	{
		super.removeSelected();
	}
	@Override
	public void addElements(String item)
	{
		super.addElements(item);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) 
	{
		int selected = jlist.getSelectedIndex();
		if(listModel.size() != 0)
			select.enableCombo(false);
		if(jlist.isSelectionEmpty())
		{
			anim.setCurrentAnim(-1);
			buttonSup.setEnabled(false);
			delayField.setText("30");
			
			framesList.reload();
		}
		else
		{
			buttonSup.setEnabled(true);
			anim.setCurrentFrame(-1);
			if(anim.getCurrentAnim() != -1)
				anim.setDelay(Float.parseFloat(delayField.getText()));
			anim.setCurrentAnim(selected);
			delayField.setText((int)(anim.getDelay()) + "");
			
			framesList.reload();
			if(framesList.getListModel().size() != 0)
				framesList.setSelected(0);
		}
		
	}
	@Override
	public void focusGained(FocusEvent e) 
	{	
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		if(!isInitialized)
			return;
		if(!jlist.isSelectionEmpty())
			anim.setDelay(Float.parseFloat(delayField.getText()));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		int selected = jlist.getSelectedIndex();
		if(!isInitialized)
			return;
		Object source = e.getSource();
		if(source == buttonAdd)
		{
			framesList.setFile(select.getFile());
			AnimInfos l_infos = new AnimInfos();
			l_infos.frame.x = select.getSelectedIndex();
			l_infos.frame.y = select.getSelected2Index();
			
			if(dial.launchDial(l_infos))
			{
				addElements(l_infos.name);
				anim.add(l_infos.name, l_infos.frame, Integer.parseInt(delayField.getText()));
				jlist.setSelectedIndex(listModel.size() - 1);
			}
		}
		else if(source == buttonSup)
		{
			int newSelected = selected - 1;
			anim.remove(selected);
			removeSelected();
			if(!listModel.isEmpty())
				setSelected(newSelected);
		}
		
		if(anim.size() == 0)
			select.enableCombo(true);
		else
			select.enableCombo(false);
	}
	public void updateList()
	{
		listModel.removeAllElements();
		if(anim == null)
			return;
		for(int i=0; i<anim.getNames().size(); i++)
		{
			listModel.addElement(anim.getName(i));
		}
	}
	
	public float getInvMass()
	{
		return Float.parseFloat(massField.getText());
	}
	public void setInvMass(float invMass)
	{
		massField.setText(invMass + "");
	}
}
