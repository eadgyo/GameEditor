package AdvEntityEditor;

import javax.swing.* ;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;

import BaseWindows.AddEntityDialog;
import Maths.PointInt;

public class KeyFloatContainer extends KeyContainer
{
	
	public JTextField valueField;
	public static Font myFont = new Font("Arial", Font.BOLD, 14);
	
	
	public KeyFloatContainer(String name, PointInt length)
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		label = new JLabel(name);
		label.setFont(myFont);
		label.setPreferredSize(new Dimension(60,15));
		valueField = new JTextField("0.0",5);
		valueField.setSize(new Dimension(70, 20));
		buttonAdd = new JButton("+");
		buttonSupp = new JButton("-");
		buttonApply = new JButton("o");
		
		Box box = Box.createHorizontalBox();
		box.add(label);
		box.add(Box.createHorizontalStrut(5));
		box.add(valueField);
		box.add(buttonAdd);
		box.add(buttonSupp);
		box.add(buttonApply);
		this.add(box);
		
		this.setPreferredSize(new Dimension(length.x, length.y));
	}
	public String getText() 
	{
		return valueField.getText();
	}
	public void setText(String text)
	{
		valueField.setText(text);
	}
}
