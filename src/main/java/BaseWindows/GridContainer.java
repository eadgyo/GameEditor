package BaseWindows;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

import javax.swing.* ;

import AdvEntityEditor.SlidePanel;
import EntityEditor.FramesList;
public class GridContainer extends Container implements ActionListener, FocusListener
{
	private JLabel gridLab, gridXLab, gridYLab, gridLengthLab;
	private JCheckBox gridCheck;
	private JTextField gridXField, gridYField, gridLengthField;
	private Grid grid;
	private Object object;
	
	public GridContainer(BufferedImage surfEntity)
	{
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.grid = new Grid(surfEntity);
		
		gridLab = new JLabel("Grid");
		gridXLab = new JLabel("X:");
		gridYLab = new JLabel("Y:");
		gridLengthLab = new JLabel("Length:");
		
		gridXField = new JTextField("0", 3);
		gridXField.addFocusListener(this);
		gridYField = new JTextField("0", 3);
		gridYField.addFocusListener(this);
		gridLengthField = new JTextField("64", 5);
		gridLengthField.addFocusListener(this);
		
		gridCheck = new JCheckBox();
		gridCheck.addActionListener(this);
		
		this.add(gridLab);
		this.add(gridCheck);
		
		
		this.add(gridXLab);
		this.add(gridXField);
		
		this.add(gridYLab);
		this.add(gridYField);
		
		this.add(gridLengthLab);
		this.add(gridLengthField);
	}
	public void initialize(Object object)
	{
		this.object = object;
	}
	public Grid getGrid()
	{
		return grid;
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		
		grid.isActivated = gridCheck.isSelected();
		
		if(object instanceof FramesList)
	    {
			((FramesList) object).render();
	    }
		else if(object instanceof SlidePanel)
		{
			((SlidePanel) object).render();
		}
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		grid.move.set(Math.abs(Integer.parseInt(gridXField.getText())), Math.abs(Integer.parseInt(gridYField.getText())));
		grid.length = Integer.parseInt(gridLengthField.getText());
		
		if(object instanceof FramesList)
	    {
			((FramesList) object).render();
	    }
		else if(object instanceof SlidePanel)
		{
			((SlidePanel) object).render();
		}
	}

}
