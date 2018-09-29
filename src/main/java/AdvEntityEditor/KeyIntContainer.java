package AdvEntityEditor;

import javax.swing.* ;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;

import BaseWindows.AddEntityDialog;
import Maths.PointInt;

public class KeyIntContainer extends KeyContainer
{
	public JComboBox valueCombo;
	public static Font myFont = new Font("Arial", Font.BOLD, 14);
	
	public KeyIntContainer(String name, PointInt length)
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		label = new JLabel(name);
		label.setFont(myFont);
		label.setPreferredSize(new Dimension(42,20));
		valueCombo = new JComboBox();
		valueCombo.setPreferredSize(new Dimension(80,20));
		buttonAdd = new JButton("+");
		buttonSupp = new JButton("-");
		buttonApply = new JButton("o");
		
		Box box = Box.createHorizontalBox();
		box.add(label);
		box.add(Box.createHorizontalStrut(5));
		box.add(valueCombo);
		box.add(Box.createHorizontalStrut(5));
		box.add(buttonAdd);
		box.add(buttonSupp);
		box.add(buttonApply);
		this.add(box);
		
		this.setPreferredSize(new Dimension(length.x, length.y));
	}
	public void clearCombo()
	{
		valueCombo.removeAllItems();
	}
	public void addItem(String str)
	{
		valueCombo.addItem(str);
	}
	public int getSelected()
	{
		return valueCombo.getSelectedIndex();
	}
	public void setSelected(int selected)
	{
		if(selected >= valueCombo.getItemCount())
			return;
		
		valueCombo.setSelectedIndex(selected);
	}
}
