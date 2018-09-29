package EntityEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import Base.FileManager;
import Base.Image;
import MapEditor.ComboItem;
import MapEditor.EntityListRenderer;
import Maths.Rectangle;
import Maths.Vector2D;
import Maths.Vector2D;

public class Select extends Container implements ActionListener, MouseListener, ItemListener
{
	private ArrayList<ArrayList<Image>> imagesPerText;
	private ArrayList<String> texturesName;
	private HashMap<String, Integer> texturesNameSearch;
	private BufferedImage zoomSurface;
	private JLabel selectImageLabel, selectedItemLabel, zoomImageLabel;
	private JComboBox<ComboItem> comboTextures;
	private JScrollPane imagesScroller;
	
	private EntityListRenderer entityListRenderer;
	private final String folder = "pic";
	private int currentTexture;
	private Graphics zoomG;
	private boolean isInitialized;
	
	private DefaultListModel<String> listModel;
	private JList imagesList;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Select(int width, int height)
	{
		super();
		
		listModel = new DefaultListModel<String>();
		imagesList = new JList(listModel);
		imagesScroller = new JScrollPane(imagesList);
		imagesScroller.setPreferredSize(new Dimension(width, height - width));
		entityListRenderer = new EntityListRenderer();
		imagesList.setCellRenderer(entityListRenderer);
		imagesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		imagesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		imagesList.setVisibleRowCount(-1);
		
		texturesName = new ArrayList<String>();
		texturesNameSearch = new HashMap<>();
		imagesPerText = new ArrayList<ArrayList<Image>>();
		
		//creation des buff
		zoomSurface = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
		zoomG = zoomSurface.getGraphics();
		selectedItemLabel = new JLabel("Frame: 0-0");
		selectedItemLabel.setPreferredSize(new Dimension(width, height-width));
		zoomImageLabel = new JLabel(new ImageIcon(zoomSurface));
		
		//change la vitesse de defilement du scrolling
		//selectScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        
		//creation du menu déroulant
		comboTextures = new JComboBox();
		comboTextures.addItemListener(this);
		comboTextures.setPreferredSize(new Dimension(150,30));
		
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setPreferredSize(new Dimension(width + 5, height + 50));
        this.add(imagesScroller);
        this.add(zoomImageLabel);
        this.add(comboTextures);
        this.add(selectedItemLabel);
        
		updateTexturesName();
		loadTexture();
		updateList();
		
		imagesList.addMouseListener(this);
		
		isInitialized = true;
		renderZoom();
		this.repaint();
	}
	public boolean isInitialized()
	{
		return isInitialized;
	}
	public void reset()
	{
		listModel.clear();
		comboTextures.setEnabled(true);
		renderZoom();
		this.repaint();
	}
	public int getCurrentTexture()
	{
		return currentTexture;
	}
	
	//external
	public void updateTexturesName()
	{
		FileManager fileM = FileManager.getInstance();
		ArrayList<String> l_listOfFiles = fileM.getAllFilesName(folder, true);
		
		if(l_listOfFiles.size() == 0)
		{
			currentTexture = -1;
			return;
		}
		texturesName.clear();
		texturesNameSearch.clear();
		imagesPerText.clear();
		comboTextures.removeAllItems();
		//On récupère le nom de la texture, les images
		for(int i=0; i<l_listOfFiles.size(); i++)
		{
			ArrayList<Image> l_images = new ArrayList<Image>();
			texturesName.add(l_listOfFiles.get(i));
			//On construit l'arbre pour chercher le nom de la texture
			texturesNameSearch.put(l_listOfFiles.get(i), i);
			imagesPerText.add(l_images);
			comboTextures.addItem(new ComboItem(l_listOfFiles.get(i), i));
		}
	}
	public void loadTexture()
	{
		//load images 1 time per texture
		if(currentTexture == -1)
			return;
		if(imagesPerText.get(currentTexture).size() != 0)
			return;
		
		FileManager fileM = FileManager.getInstance();
		String textureName = texturesName.get(currentTexture);
		String size = "";
		int index = textureName.length() - 5;
		
		//On récupère les informations sur l'image d'après le nom
		while(textureName.charAt(index) != '-')
		{
			size = textureName.charAt(index) + size;
			index--;
			assert index >= 0: "Error Nom image: nomImage-taille.png, exemple tileset-17.png";
		}
		
		int sizeImage = Integer.parseInt(size);
		ArrayList<Image> l_images = fileM.loadImages(folder + "/" +  textureName, null, sizeImage, false);
		imagesPerText.set(currentTexture, l_images);
	}
	public void updateList()
	{
		imagesList.clearSelection();
		listModel.clear();
		int selectedText = comboTextures.getSelectedIndex();
		if(selectedText != -1)
		{
			for(int i=0; i<imagesPerText.get(selectedText).size(); i++)
			{
				Image l_image = imagesPerText.get(selectedText).get(i);

				l_image.reset();
				l_image.setSize(EntityListRenderer.SIZE);
				l_image.setLeftPos(new Vector2D(0,0));
				
				Rectangle rec = l_image.getRectangle();
				ArrayList<Vector2D> vecs = rec.getPointsWorld();
				
				entityListRenderer.render(i, l_image);
				listModel.addElement("");
			}
			if(listModel.size() > 0)
				imagesList.setSelectedIndex(0);
		}
	}
	
	public void enableCombo(boolean b)
	{
		comboTextures.setEnabled(b);
	}
	public int getSelectedIndex()
	{
		if(imagesList.getSelectedIndices().length != 0)
			return imagesList.getSelectedIndices()[0];
		else
			return -1;
	}
	public int getSelected2Index()
	{
		if(imagesList.getSelectedIndices().length > 1)
			return imagesList.getSelectedIndices()[imagesList.getSelectedIndices().length - 1];
		else
			return getSelectedIndex();
	}
	public Image getActualImage()
	{
		return imagesPerText.get(currentTexture).get(imagesList.getSelectedIndex()).clone();
	}
	public Image getImage(int text, int image)
	{
		if(imagesPerText.size() != 0)
			return imagesPerText.get(text).get(image).clone();
		return null;
	}
	public int getFile()
	{
		return currentTexture; 
	}
	public boolean setTexture(String textureName)
	{
		if(!texturesNameSearch.containsKey(textureName))
			return false;
		int selected = texturesNameSearch.get(textureName);
		boolean isEnabled = comboTextures.isEnabled();
		comboTextures.setEnabled(false);
		comboTextures.setSelectedIndex(selected);
		comboTextures.setEnabled(isEnabled);
		return true;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if(!isInitialized)
			return;
		Vector2D mouse = new Vector2D(e.getX(), e.getY());
		//On récupère calcul le numéro de l'image séléctionnée
		selectedItemLabel.setText("Frame: " + this.getSelectedIndex() + "-" + this.getSelected2Index()  + "");
		
		renderZoom();
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
	
	}
	
	//affichage de la photo zoomée
	public void renderZoom()
	{
		//on remplie le buff de zoom en blanc
		zoomG.setColor(Color.black);
		zoomG.fillRect(0,0,zoomSurface.getWidth(),zoomSurface.getHeight());
		
		int selectedImage = this.getSelectedIndex();
		if(selectedImage != -1)
		{
			//on actualise l'image zoomée
			Image l_zoomImage = imagesPerText.get(currentTexture).get(selectedImage).clone();
			l_zoomImage.setPos(new Vector2D(zoomSurface.getWidth()*0.5f,zoomSurface.getHeight()*0.5f));
			l_zoomImage.setRadians(0, new Vector2D(1,0));
			l_zoomImage.setSize(Math.min(zoomSurface.getWidth(), zoomSurface.getHeight()));
			l_zoomImage.setGraphics(zoomG);
			l_zoomImage.draw();
		}
	}
	@Override
	public void itemStateChanged(ItemEvent e) 
	{
		if(!isInitialized)
			return;
		ComboItem l_comboItem = (ComboItem) (e.getItem());
		
		selectedItemLabel.setText("Frame: " + imagesList.getSelectedIndex());	
		currentTexture = (int) l_comboItem.getValue();
		loadTexture();
		updateList();
		
		renderZoom();
		this.repaint();
	}
	public int getMaxImage()
	{
		return imagesPerText.get(currentTexture).size();
	}
}
