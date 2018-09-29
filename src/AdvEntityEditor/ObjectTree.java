
package AdvEntityEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.synth.SynthTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Addons.AdvAnimation;
import Addons.AdvEntity;
import Addons.EntitiesGroup;
import Addons.Entity;
import BaseWindows.EntitiesDisplay;
import BaseWindows.Grid;
import Map.Layer;
import Maths.Vector2D;
import Maths.Vector2D;

public class ObjectTree extends Container implements TreeSelectionListener, ActionListener, MouseListener, MouseMotionListener, FocusListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	private static class MyTreeCellRenderer extends DefaultTreeCellRenderer
	{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
        {
        	JLabel label = (JLabel) (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, false);
        	//label.setBorder(border);
            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) 
            {	
            	
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            }
            return this;
        }
	}
	class NonRootEditor extends DefaultTreeCellEditor
	{
	    private final DefaultTreeCellRenderer renderer;
	 
	    public NonRootEditor(JTree tree, DefaultTreeCellRenderer renderer)
	    {
	        super(tree, renderer);
	        this.renderer = renderer;
	    }
	    
	    @Override
	    protected boolean canEditImmediately(EventObject event) 
	    {
	    	if(tree.getSelectionCount() != 1)
	    		return false;
	    	
	        return (false);
	    }
	    
	}
	class MyTreeModel extends DefaultTreeModel
	{
		public MyTreeModel(DefaultMutableTreeNode root) 
		{
			super(root);
			
		}
		@Override
		public void valueForPathChanged(TreePath path, Object newValue) 
	    {
			DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			((AdvEntity) aNode.getUserObject()).setName((String) newValue);
			redrawTree();
	    }
	}
	class MyTree extends JTree
	{
		public MyTree(MyTreeModel myTreeModel) 
		{
			super(myTreeModel);
			setCellRenderer(new MyTreeCellRenderer());
			setCellEditor(new NonRootEditor(this, (DefaultTreeCellRenderer) this.getCellRenderer()));
			setEditable(true);	
			setRowHeight(0);
		}
		@Override
		public boolean hasBeenExpanded(TreePath path) { return true; }
	}
	public class NiceTreeUI extends SynthTreeUI
	{
		public NiceTreeUI()
		{
			super();
		}
		/*
		Change Icon
		@Override
	    public Icon getCollapsedIcon() 
		{
	        return collapseIcon;
	    }

	    @Override
	    public Icon getExpandedIcon() 
	    {
	        return expandIcon;
	    }*/
		@Override
		protected boolean shouldPaintExpandControl(TreePath tree, int row, boolean isExpanded, boolean wasExpanded, boolean lead)
		{
			return true;
		}
	    @Override
	    protected void paintRow( Graphics g, Rectangle clipBounds, Insets insets, Rectangle bounds,
	        TreePath path, int row, boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf )
	    {
	        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
            g.setColor( Color.GREEN );
            g.fillRect( 0, 0, 100, 100 );
            
	            
	        
	        
	    }
	     
	}
	
	
	
	private JTree tree;
	private JScrollPane treeView;
	private DefaultMutableTreeNode root;
	private DefaultMutableTreeNode selectedNode, insertNode;
	
	private JPopupMenu popupMenu;
	protected JButton buttonAdd, buttonSup;
	
	private boolean isInitialized;
	private JButton dispB, centerB, resetB;
	
	private AdvEntity treeAdvEntity;
	private AdvEntity copyAdvEntity;
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
	private SpringLayout layout;
	
	boolean isDragging;
	
	public ObjectTree(JDialog frame, String labelName, int width, int height)
	{
		super();
		layout = new SpringLayout();
		this.setLayout(layout);
		copyAdvEntity = new AdvEntity();
		
		isInitialized = false;
		dispB = new JButton("Display");
		centerB = new JButton("Center");
		resetB = new JButton("Reset");
		posX = new JTextField("0.0", 4);
		posY = new JTextField("0.0", 4);
		isModifyingCenter = false;
		
		isDragging = false;
		this.entitiesDisplay = entitiesDisplay;
		
		root = new DefaultMutableTreeNode();
		tree = new MyTree(new MyTreeModel(root));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		tree.expandRow(0);
		tree.setRootVisible(false);
		
		tree.addMouseListener(this);
		tree.addMouseMotionListener(this);
		
		treeView = new JScrollPane(tree);
		treeView.setPreferredSize(new Dimension(width, height - 30));
		
		buttonAdd = new JButton("+");
		buttonSup = new JButton("-");
		
		displayManagerDialog = new DisplayManagerDialog(frame);
		
		layout.putConstraint(SpringLayout.WEST, dispB, 5, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, dispB, 5, SpringLayout.SOUTH, treeView);
		this.add(dispB);
		
		layout.putConstraint(SpringLayout.WEST, centerB, 5, SpringLayout.EAST, dispB);
		layout.putConstraint(SpringLayout.NORTH, centerB, 5, SpringLayout.SOUTH, treeView);
		this.add(centerB);
		
		layout.putConstraint(SpringLayout.WEST, resetB, 5, SpringLayout.EAST, centerB);
		layout.putConstraint(SpringLayout.NORTH, resetB, 5, SpringLayout.SOUTH, treeView);
		this.add(resetB);
		
		layout.putConstraint(SpringLayout.WEST, posX, 5, SpringLayout.EAST, resetB);
		layout.putConstraint(SpringLayout.NORTH, posX, 5, SpringLayout.SOUTH, treeView);
		this.add(posX);
		
		layout.putConstraint(SpringLayout.WEST, posY, 5, SpringLayout.EAST, posX);
		layout.putConstraint(SpringLayout.NORTH, posY, 5, SpringLayout.SOUTH, treeView);
		this.add(posY);
		
		posX.addFocusListener(this);
		posY.addFocusListener(this);
		
		dispB.addActionListener(this);
		centerB.addActionListener(this);
		resetB.addActionListener(this);
		
		buttonAdd.addActionListener(this);
		buttonSup.addActionListener(this);
		buttonSup.setEnabled(false);
		
		this.dispB.setEnabled(true);
		this.resetB.setEnabled(false);
		this.centerB.setEnabled(false);
		this.posX.setEnabled(false);
		this.posY.setEnabled(false);
		
		//JTree
		selectedNode = null;
		insertNode = null;
		popupMenu = new JPopupMenu();
		
		//Tree
		layout.putConstraint(SpringLayout.WEST, treeView, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, treeView, 5, SpringLayout.NORTH, this);
		add(treeView);
		
		layout.putConstraint(SpringLayout.WEST, buttonAdd, 5, SpringLayout.EAST, treeView);
		layout.putConstraint(SpringLayout.NORTH, buttonAdd, 5, SpringLayout.NORTH, this);
		add(buttonAdd);
		
		layout.putConstraint(SpringLayout.WEST, buttonSup, 5, SpringLayout.EAST, treeView);
		layout.putConstraint(SpringLayout.NORTH, buttonSup, 5, SpringLayout.SOUTH, buttonAdd);
		add(buttonSup);
		
		treeAdvEntity = null;
		
		posX.addKeyListener(this);
		posY.addKeyListener(this);
		this.setPreferredSize(new Dimension(width + 30 , height  + 15));
	}
	public void initialize(EntitiesGroup entitiesGroup, AdvEntity treeAdvEntity,
			EntitiesDisplay entitiesDisplay, SlidePanel slidePanel, AdvAnimList advAnimList, BufferedImage surfEntity,
			Grid grid)
	{
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
		
		root.setUserObject(treeAdvEntity);
	}
	
	public void clear()
	{
		root.removeAllChildren();
		tree.clearSelection();
		
		updateButtons();
		this.redrawTree();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			selectedNode = root;
		if(e.getClickCount() == 1 && e.getButton() == 3)
		{
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
		
		TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(pathFromPos);
		
		this.advAnimList.updateList();
		updateList();
		redrawTree();
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
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			selectedNode = root;
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		if(isDragging && insertNode != null) //insertion d'une node avant une node
		{
			if(insertNode != selectedNode && !insertNode.isNodeAncestor((selectedNode)))
    		{
				//((Layer) root.getUserObject()).reset();
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) insertNode.getParent();
				int index = parentNode.getIndex(insertNode);
				
				AdvEntity parent = (AdvEntity) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
				AdvEntity draggingAdv = (AdvEntity) selectedNode.getUserObject();
	    		parent.removeAdvEntity(draggingAdv);
	    		AdvEntity newParent = (AdvEntity) parentNode.getUserObject();
	    		
	    		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);		
	    		newParent.addAdvEntity(index, draggingAdv);
	    		
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(draggingAdv);
				recreateNodes(newNode, draggingAdv);
				((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, parentNode, index);
			
				//((Layer) root.getUserObject()).build();
				redrawTree();
				insertNode = null;
    		}
		}
		else if(isDragging && selectedNode != null && selectedNode.getParent() != null && selectedNode != root)
	    {
	    	TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
	    	DefaultMutableTreeNode selectedNodeFromPos = null; 
	    	if(pathFromPos == null)
	    		selectedNodeFromPos = root;
	    	else
	    		selectedNodeFromPos = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
    		if(selectedNodeFromPos != selectedNode && !selectedNodeFromPos.isNodeAncestor((selectedNode)))
    		{
    			//((Layer) root.getUserObject()).reset();
    			
	    		AdvEntity parent = (AdvEntity) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
	    		AdvEntity draggingAdv = (AdvEntity) selectedNode.getUserObject();
	    		parent.removeAdvEntity(draggingAdv);
	    		AdvEntity newParent = (AdvEntity) selectedNodeFromPos.getUserObject();
	    		
	    		((DefaultTreeModel) tree.getModel()).removeNodeFromParent(selectedNode);
	    		
	    		newParent.addAdvEntity(draggingAdv);
	    		
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(draggingAdv);
				recreateNodes(newNode, draggingAdv);
				((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNodeFromPos, selectedNodeFromPos.getChildCount());
				
				//((Layer) root.getUserObject()).build();
				redrawTree();
    		}
	    }
		if(this.getActualAdvEntity() != null)
		{
			buttonSup.setEnabled(true);
		}
		else
			buttonSup.setEnabled(false);
	}
	
	
	public void updateList() 
	{
		if(advAnimList.getListModel().size() != 0)
			advAnimList.setSelected(0);
		slidePanel.load();
		updateAdvCenter();
		isModifyingCenter = false;
		centerB.setText("Center");
		
		updateButtons();
		slidePanel.render();
	}
	public void updateButtons()
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
		if(selectedNode == null)
			this.buttonSup.setEnabled(false);
		else
			this.buttonSup.setEnabled(true);
		
		if(selectedNode == null)
		{
			this.resetB.setEnabled(false);
			this.centerB.setEnabled(false);
			this.posX.setEnabled(false);
			this.posY.setEnabled(false);
			this.dispB.setEnabled(true);
		}
		else
		{
			//Here actualTree
			this.resetB.setEnabled(true);
			this.centerB.setEnabled(true);
			this.posX.setEnabled(true);
			this.posY.setEnabled(true);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(!isInitialized)
			return;
		int selectedEntity = entitiesDisplay.getSelectedObject();
		int type = entitiesDisplay.getSelectedType();
		int group = entitiesDisplay.getSelectedGroup();
		if(type == -1)
		{
			entitiesDisplay.reset();
			return;
		}
		Object source = e.getSource();
		if(source == centerB)
		{
			if(selectedNode != null)
				isModifyingCenter = !isModifyingCenter;
			if(isModifyingCenter)
				centerB.setText("   Set   ");
			else
				centerB.setText("Center");
		}
		else if(source == dispB)
		{
			displayManagerDialog.launchDial(treeAdvEntity);
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == buttonAdd)
		{
			if(selectedEntity == -1 || type == -1 || group == -1)
			{
				return;
			}
			if(type == 0)//Entity
			{
				AdvEntity currentAdvEntity = this.getActualAdvEntity();
				if(currentAdvEntity == null)
				{
					currentAdvEntity = treeAdvEntity;
				}
				AdvEntity l_advEntity = new AdvEntity(entitiesGroup.getEntity(group, selectedEntity));
				l_advEntity.reset();
				l_advEntity.setAdvCenter(l_advEntity.getPos());
				l_advEntity.setIsDisplayingRec(true);
				l_advEntity.setGraphics(surfG);
				l_advEntity.setAdvAnimation(new AdvAnimation());
				this.addObject(l_advEntity);
	
				redrawTree();
			}
			else if(type == 1)
			{
				AdvEntity currentAdvEntity = this.getActualAdvEntity();
				if(currentAdvEntity == null)
				{
					currentAdvEntity = this.treeAdvEntity;
				}
				
				AdvEntity l_advEntity = entitiesGroup.getAdvEntity(group, selectedEntity).clone();
				l_advEntity.setIsDisplayingRec(true);
				l_advEntity.setGraphics(surfG);
				currentAdvEntity.addAdvEntity(l_advEntity);
				
				if(l_advEntity.getAdvAnimation() == null)
				{
					l_advEntity.setAdvAnimation(new AdvAnimation());
				}
				redrawTree();
				
				
			}
			
			if(selectedNode == null)
				selectedNode = root;
			tree.setSelectionPath(new TreePath(((DefaultMutableTreeNode) selectedNode.getLastChild()).getPath()));
			advAnimList.updateList();
			if(advAnimList.getListModel().size() != 0)
				advAnimList.setSelected(0);
			
			updateButtons();
		}
		else if(source == resetB)
		{
			AdvEntity actual = this.getActualAdvEntity();
			if(actual != null)
			{
				actual.resetAdvCenter();
			}
			updateAdvCenter();
			slidePanel.update();
			slidePanel.render();
		}
		else if(source == buttonSup)
		{
			AdvEntity actual = this.getActualAdvEntity();
			if(actual != null)
			{
				selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				AdvEntity parent = (AdvEntity) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
				
				DefaultMutableTreeNode parentNode = ((DefaultMutableTreeNode) selectedNode.getParent());
				int pos;
				for(pos = 0; pos < parentNode.getChildCount(); pos++)
				{
					if(selectedNode == parentNode.getChildAt(pos))
						break;
				}
				((DefaultMutableTreeNode) selectedNode.getParent()).remove(pos);
				parent.removeAdvEntity(actual);
				
				if(pos == 0)
				{
					if(parentNode.getChildCount() == 0)
					{
						if(parentNode != root)
						{
							tree.setSelectionPath(new TreePath(parentNode.getPath()));
						}
						else
						{
							tree.clearSelection();
						}
					}
					else
					{
						tree.setSelectionPath(new TreePath( ((DefaultMutableTreeNode)parentNode.getChildAt(0)).getPath()));
					}
				}
				else
				{
					tree.setSelectionPath(new TreePath( ((DefaultMutableTreeNode)parentNode.getChildAt(pos - 1)).getPath()));
				}
				updateButtons();
				advAnimList.updateList();
				
				slidePanel.update();
				slidePanel.render();
				
				redrawTree();
			}
		}
	}
	@Override
	public void focusGained(FocusEvent e) 
	{
	}
	@Override
	public void focusLost(FocusEvent e) 
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			return;
		Vector2D pos = new Vector2D(Float.parseFloat(posX.getText()), Float.parseFloat(posY.getText()));
		AdvEntity advEntity = null;
		advEntity = this.getActualAdvEntity();
		advEntity.setAdvCenter(pos);
		
		updateAdvCenter();
		slidePanel.update();
		slidePanel.render();
	}

	public void modifyingCenter(MouseEvent e)
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			return;
		
		if(isModifyingCenter)
		{
			isModifyingCenter = false;
			centerB.setText("Center");

			//position - le vec translation 0.5*length
			Vector2D pos = new Vector2D(e.getX(), e.getY());
 			Vector2D vec = new Vector2D(slidePanel.getTranslate());
			pos.set(grid.gridRound(pos, vec));
			pos.translate(vec.multiply(-1));
			AdvEntity advEntity = this.getActualAdvEntity();

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
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			return null;
		AdvEntity advEntity = (AdvEntity) ((DefaultMutableTreeNode) selectedNode).getUserObject();
		return advEntity;
	}
	public void updateAdvCenter()
	{
		selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
			return;
		AdvEntity advEntity = getActualAdvEntity();

		posX.setText((((int) (advEntity.getAdvCenter().x*10))/10.0f) + "");
		posY.setText((((int) (advEntity.getAdvCenter().y*10))/10.0f) + "");
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
		{
			this.addObject(copyAdvEntity.clone());
			this.redrawTree();
		}
		
	}
	public void clearCopy()
	{
		copyAdvEntity.clear();
	}
	
	
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		isDragging = true;
		TreePath pathFromPos = tree.getPathForLocation(e.getX(), e.getY());
		if(pathFromPos != null)
		{
			Rectangle bound = tree.getPathBounds(pathFromPos);
			
			if(Math.abs(e.getY() - bound.getMinY()) < 4)
			{
				tree.clearSelection();
				insertNode = (DefaultMutableTreeNode) pathFromPos.getLastPathComponent();
			}
			else
			{
				tree.setSelectionPath(pathFromPos);
				insertNode = null;
			}
		}
		else
		{
			tree.clearSelection();
		}
		
	}
	@Override
	public void mouseMoved(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueChanged(TreeSelectionEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	public void addObject(AdvEntity advEntity)
	{
		advEntity.reset();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if(selectedNode == null)
		{
			selectedNode = root;
		}
		
		AdvEntity actualAdvEntity = (AdvEntity) selectedNode.getUserObject();
		
		actualAdvEntity.addAdvEntity(advEntity);
		
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(advEntity);
		((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
		
		tree.expandPath(new TreePath(selectedNode.getPath()));
		
		//((Layer) root.getUserObject()).build();
		
		redrawTree();
	}
	
	
	public void redrawTree() 
	{
		if(selectedNode == null || selectedNode == root)
		{
			advAnimList.clear();
		}

		TreePath[] selectionPaths = tree.getSelectionPaths();
		//ArrayList<TreePath> selectionsPath = getSelectedPaths();
		
		// First, capture the current expanded state of the JTree
		StringBuilder sb = new StringBuilder();

	    for(int i = 0 ; i < tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(tree.isExpanded(i)){
	            sb.append(tp.toString());
	            sb.append(",");
	        }
	    }
	    String s = sb.toString();

		// Force the JTree to rebuild itself
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload();
		// Finally, recover the old expanded state of the JTree
		for(int i = 0 ; i<tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(s.contains(tp.toString() )){
	            tree.expandRow(i);
	        }   
	    }
		tree.setSelectionPaths(selectionPaths);
	}
	
	public ArrayList<TreePath> getSelectedPaths()
	{
		TreePath[] selectionPaths = tree.getSelectionPaths();
		
		ArrayList<TreePath> treePaths = new ArrayList<TreePath>();
		if(selectionPaths != null)
		{
			for(int i=0; i<selectionPaths.length; i++)
			{
				treePaths.add(selectionPaths[i]);
			}
			
			//On enleve les fils des parents qui sont déjà dedans
			for(int i=0; i<treePaths.size(); i++)
			{
				for(int j=0; j<treePaths.size(); j++)
				{
					if(i != j)
					{
						if(treePaths.get(j).isDescendant(treePaths.get(i)))
						{
							treePaths.remove(i);
							i--;
							break;
						}
					}
				}
			}
		}
		return treePaths;
	}
	public ArrayList<AdvEntity> getSelectedAdvEntities()
	{
		ArrayList<AdvEntity> advEntities = new ArrayList<AdvEntity>();
		ArrayList<TreePath> treePaths = getSelectedPaths();
		for(int i=0; i<treePaths.size(); i++)
		{
			advEntities.add((AdvEntity) ((DefaultMutableTreeNode) treePaths.get(i).getLastPathComponent()).getUserObject());
		}
		return advEntities;
	}
	public void setSelectionPaths(ArrayList<TreePath> treePaths)
	{
		TreePath[] newTreePaths = new TreePath[treePaths.size()];
		for(int i=0; i<treePaths.size(); i++)
		{
			newTreePaths[i] = treePaths.get(i);
		}
		tree.setSelectionPaths(newTreePaths);
	}

	public void recreateNodes(DefaultMutableTreeNode root, AdvEntity advEntity)
	{
		ArrayList<AdvEntity> advEntities = advEntity.getAdvEntities();
		for(int i=0; i<advEntities.size(); i++)
		{
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(advEntities.get(i));
			((DefaultTreeModel) tree.getModel()).insertNodeInto(newNode, root, root.getChildCount());
			recreateNodes(newNode, advEntities.get(i));
		}
	}

	public void loadAdvEntity()
	{
		root.removeAllChildren();
		this.recreateNodes(root, treeAdvEntity);
		this.redrawTree();
	}
	public void updateSelected()
	{
		ArrayList<TreePath> selectionsPath = getSelectedPaths();
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if(selectedNode == null)
				return;
			
			Vector2D pos = new Vector2D(Float.parseFloat(posX.getText()), Float.parseFloat(posY.getText()));
			AdvEntity advEntity = null;
			advEntity = this.getActualAdvEntity();
			advEntity.setAdvCenter(pos);
			
			updateAdvCenter();
			slidePanel.update();
			slidePanel.render();
		}
	}
	@Override
	public void keyTyped(KeyEvent e)
	{

	}
}
