package AdvEntityEditor;

import javax.swing.* ;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;

import BaseWindows.AddEntityDialog;
import Maths.PointInt;

public class KeyBoolContainer extends KeyContainer
{
	public JCheckBox valueCheck;
	public SpringLayout layout;
	
	protected static Font myFont = new Font("Arial", Font.BOLD, 14);
	
	public KeyBoolContainer(String name, PointInt length)
	{
		layout = new SpringLayout();
		this.setLayout(layout);
		
		label = new JLabel(name);
		label.setFont(myFont);
		label.setPreferredSize(new Dimension(102,15));
		
		valueCheck = new JCheckBox();
		buttonAdd = new JButton("+");
		buttonSupp = new JButton("-");
		buttonApply = new JButton("o");
		
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, this);
		this.add(label);
		
		layout.putConstraint(SpringLayout.WEST, valueCheck, 5, SpringLayout.EAST, label);
		layout.putConstraint(SpringLayout.NORTH, valueCheck, 5, SpringLayout.NORTH, this);
		this.add(valueCheck);
		
		layout.putConstraint(SpringLayout.WEST, buttonAdd, 5, SpringLayout.EAST, valueCheck);
		layout.putConstraint(SpringLayout.NORTH, buttonAdd, 5, SpringLayout.NORTH, this);
		this.add(buttonAdd);
		
		layout.putConstraint(SpringLayout.WEST, buttonSupp, 0, SpringLayout.EAST, buttonAdd);
		layout.putConstraint(SpringLayout.NORTH, buttonSupp, 5, SpringLayout.NORTH, this);
		this.add(buttonSupp);
		
		layout.putConstraint(SpringLayout.WEST, buttonApply, 0, SpringLayout.EAST, buttonSupp);
		layout.putConstraint(SpringLayout.NORTH, buttonApply, 5, SpringLayout.NORTH, this);
		this.add(buttonApply);
		
		
		this.setPreferredSize(new Dimension(length.x, length.y));
		
	}
	public boolean isSelected()
	{
		return valueCheck.isSelected();
	}
	public void setSelected(boolean selected)
	{
		valueCheck.setSelected(selected);
	}
}
