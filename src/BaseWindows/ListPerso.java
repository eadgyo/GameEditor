package BaseWindows;
import javax.swing.* ;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class ListPerso extends Container// implements ListSelectionListener
{
	//variables
	protected JList<String> jlist;
	protected JLabel label;
	protected JButton buttonAdd, buttonSup;
	protected JScrollPane listScroller;
	
	protected DefaultListModel<String> listModel;
	protected static Font myFont = new Font("Arial", Font.BOLD, 18);
	protected SpringLayout layout;
	protected JDialog frame;
	protected boolean isUsed;
	
	//functions
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ListPerso(JDialog frame, String labelName, int width, int height)
	{	
		this.frame = frame;
		layout = new SpringLayout();
		//create Text
		listModel = new DefaultListModel();
		isUsed = false;
		
		//create list
		jlist = new JList(listModel);
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setLayoutOrientation(JList.VERTICAL);
		listScroller = new JScrollPane(jlist);
		
		listScroller.setPreferredSize(new Dimension(width, height));
		//jlist.addListSelectionListener(this);
		
		//set label
		label = new JLabel(labelName);
		label.setFont(myFont);
		
		//buttons
		buttonAdd = new JButton("+");
		buttonSup = new JButton("-");
		buttonAdd.setPreferredSize(new Dimension(20, (int)(height*0.6f)));
		buttonSup.setPreferredSize(new Dimension(20, (int)(height*0.3f)));
		
		
		//container
		this.setLayout(layout);
		layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, this);
		add(label);
		
		layout.putConstraint(SpringLayout.WEST, listScroller, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, listScroller, 5, SpringLayout.SOUTH, label);
		add(listScroller);
		
		layout.putConstraint(SpringLayout.WEST, buttonAdd, 5, SpringLayout.EAST, listScroller);
		layout.putConstraint(SpringLayout.NORTH, buttonAdd, 5, SpringLayout.SOUTH, label);
		add(buttonAdd);
		
		layout.putConstraint(SpringLayout.WEST, buttonSup, 5, SpringLayout.EAST, listScroller);
		layout.putConstraint(SpringLayout.NORTH, buttonSup, 5, SpringLayout.SOUTH, buttonAdd);
		add(buttonSup);
		this.setPreferredSize(new Dimension(width + 30 , height + myFont.getSize()*3));
	} 
	public void deselect()
	{
		jlist.clearSelection();
	}
	public int getSelected()
	{
		return jlist.getSelectedIndex();
	}
	public void setSelected(int selected)
	{
		jlist.setSelectedIndex(selected);
	}
	public void removeSelected()
	{
		if(!jlist.isSelectionEmpty())
			listModel.remove(jlist.getSelectedIndex());
	}
	public DefaultListModel<String> getListModel()
	{
		return listModel;
	}
	
	public void clear()
	{
		listScroller.getVerticalScrollBar().setValue((int) 0);
		listModel.removeAllElements();
	}
	public void addElements(String item)
	{
		listModel.addElement(item);
	}
	public void addAll(int size)
	{
		for(int i=0; i<size; i++)
		{
			listModel.addElement(i + "");
		}
	}
	public void addAll(int start, int end)
	{
		for(int i=start; i<end + 1; i++)
		{
			listModel.addElement(i + "");
		}
	}
	public void isUsed()
	{
		/*
		while(isUsed)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Used");
			
		}
		
		isUsed = true;*/
	}
	public void endUsed()
	{
		//isUsed = false;
	}
}
