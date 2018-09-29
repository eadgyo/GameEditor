package BaseWindows;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import Addons.AdvEntity;
import Addons.EntitiesGroup;
import Addons.Entity;

public class AddEntityDialog extends JDialog implements ActionListener, KeyListener
{
	JButton okButton, cancelButton, plusButton;
	JTextField fieldName;
	JComboBox<String> comboGroup;
	boolean ok;
	
	EntitiesGroup entitiesGroup;
	
	public AddEntityDialog(JDialog frame, EntitiesGroup entitiesGroup) 
	{
		super(frame, "Create Anim", true);
		this.setResizable(false);
		this.entitiesGroup = entitiesGroup;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setLocationRelativeTo(null);
		
		ok = false;
		setSize(160, 140);
		
		okButton = new JButton("Ok");
		cancelButton = new JButton("Cancel");
		plusButton = new JButton(" + ");
		
		comboGroup = new JComboBox<String>();
		comboGroup.setPreferredSize(new Dimension(120,30));
		fieldName = new JTextField("Name", 10);
		
		this.add(fieldName);
		this.add(comboGroup);
		this.add(plusButton);
		this.add(okButton);
		this.add(cancelButton);
		
		plusButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		comboGroup.addKeyListener(this);
		fieldName.addKeyListener(this);
		this.addKeyListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if(source == okButton)
		{
			ok = true;
			setVisible(false);
		}
		else if(source == cancelButton)
		{
			setVisible(false);
		}
		else if(source == plusButton)
		{
			String txt = JOptionPane.showInputDialog(this, "Nom du groupe");
			entitiesGroup.addGroup(txt);
			updateCombo();
			comboGroup.setSelectedIndex(comboGroup.getItemCount()-1);
		}
	}
	public void updateCombo()
	{
		comboGroup.removeAllItems();
		for(int i=0; i<entitiesGroup.size(); i++)
		{
			comboGroup.addItem(entitiesGroup.getNameGroup(i));
		}
	}
	
	public boolean launchDial(Entity entity, String defaultName)
	{
		ok = false;
		updateCombo();
		fieldName.setText(defaultName);
		this.setVisible(true);
		
		if(ok)
		{
			entity.setName(fieldName.getText());
			entity.getAnim().setEntity(entity);
			entitiesGroup.addEntity(comboGroup.getSelectedIndex(), entity);
			return true;
		}
		return false;
	}
	public boolean launchDial(AdvEntity advEntity, String defaultName)
	{
		ok = false;
		updateCombo();
		fieldName.setText(defaultName);
		this.setVisible(true);
		
		if(ok)
		{
			advEntity.setName(fieldName.getText());
			entitiesGroup.addAdvEntity(comboGroup.getSelectedIndex(), advEntity);
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
