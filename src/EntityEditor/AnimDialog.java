package EntityEditor;

import javax.swing.* ;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.* ;
import java.awt.event.* ;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class AnimDialog extends JDialog implements ActionListener, KeyListener
{
	JButton okButton, cancelButton;
	JLabel start, end;
	JTextField fieldName, x, y;
	boolean ok;
	
	public AnimDialog(JDialog frame)
	{
		super(frame, "Create Anim", true);
		this.setResizable(false);
		ok = false;
		setSize(220, 160);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		start = new JLabel("Start Frame");
		end = new JLabel("End Frame  ");
		
		fieldName = new JTextField("Name", 15);
		x = new JTextField("0", 10);
		y = new JTextField("0", 10);
		
		this.add(fieldName);
		this.add(start);
		this.add(x);
		this.add(end);
		this.add(y);
		this.add(okButton);
		this.add(cancelButton);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		this.addKeyListener(this);
		fieldName.addKeyListener(this);
		x.addKeyListener(this);
		y.addKeyListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(e.getSource() == okButton)
		{
			ok = true;
			setVisible(false);
		}
		if(e.getSource() == cancelButton)
		{
			setVisible(false);
		}
	}
	public boolean launchDial(AnimInfos infos)
	{
		x.setText(infos.frame.x + "");
		y.setText(infos.frame.y + "");
		fieldName.setText("Name");
		
		ok = false;
		this.setVisible(true);
		
		if(ok)
		{
			infos.frame.x = Integer.parseInt(x.getText());
			infos.frame.y = Integer.parseInt(y.getText());
			if(infos.frame.x > infos.frame.y)
			{
				return false;
			}
			if(infos.frame.x < 0 || infos.frame.y < 0)
			{
				return false;
			}
			infos.name = fieldName.getText();
			
			return true;
		}
		return false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			ok = true;
			this.setVisible(false);
		}
	}
	@Override
	public void keyReleased(KeyEvent e) 
	{
	}
	@Override
	public void keyTyped(KeyEvent e) 
	{
	}
}