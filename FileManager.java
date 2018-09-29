package Base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.imageio.ImageIO;

import Addons.AdvAnimation;
import Addons.AdvEntity;
import Addons.AdvForm;
import Addons.Animation;
import Addons.Entity;
import Addons.Key;
import Addons.KeyBool;
import Addons.KeyFloat;
import Addons.KeyInt;
import MapEditor.Main;
import Maths.Form;
import Maths.Point2D;
import Maths.PointInt;
import Maths.Vector2D;
import Maths.sRectangle;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.events.Attribute;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//Singleton
public class FileManager
{
	public class SimpleErrorHandler implements ErrorHandler 
	{
	    public void warning(SAXParseException e) throws SAXException 
	    {
	    }

	    public void error(SAXParseException e) throws SAXException 
	    {
	    }

	    public void fatalError(SAXParseException e) throws SAXException 
	    {
	    }
	}
	
	private DocumentBuilderFactory factory;
	
	//parseur
	private DocumentBuilder builder;
	private Document document;
	private TransformerFactory transformerFactory;
	private Transformer transformer;
	
	public final String binFolder = "bin_";
	public final String objectsFolder = binFolder + "/" + "Objects";;
	public final String textureFolder = "pic";
	
	ArrayList<BufferedImage> textures;
	HashMap<String, Integer> names;
	
	//Singleton object
	private static FileManager INSTANCE = new FileManager();
	
	private FileManager()
	{
		textures = new ArrayList<BufferedImage>();
		names = new HashMap<String, Integer>();
		
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		
		transformerFactory = TransformerFactory.newInstance();
		try 
		{
		    builder = factory.newDocumentBuilder();
		    builder.setErrorHandler(new SimpleErrorHandler());
		}
		catch (final ParserConfigurationException e) 
		{
		    e.printStackTrace();
		    assert(false);
		}
		try 
		{
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		} 
		catch (TransformerConfigurationException e) 
		{
			e.printStackTrace();
		}
		initialize();
	}
	public void initialize()
	{
		boolean creation = createFolder(binFolder);
		boolean creation1 = createFolder(objectsFolder);
		loadDefTextures();
	}
	
	//Load
	public BufferedImage getDefTexture(String textureName)
	{
		assert(names.containsKey(textureName));
		
		int i = names.get(textureName);
		return this.textures.get(i);
	}
	private void loadDefTextures()
	{
		ArrayList<String> names = getAllFilesName(textureFolder, true);
		for(int i=0; i<names.size(); i++)
		{
			this.names.put(names.get(i), i);
			textures.add(loadTexture(textureFolder + "/" + names.get(i) , false));
		}
	}
	public BufferedImage loadTexture(String file, boolean isInternal)
	{
		BufferedImage l_texture;
		try 
		{
			if(isInternal)
			{
				URL url = Main.class.getResource(file);
				l_texture = ImageIO.read(url);
			}
			else
				l_texture = ImageIO.read(new File(file));
		} 
		catch (IOException e) 
		{
			l_texture = null;
		}
		return l_texture;
	}
	public ArrayList<Image> loadImages(String file, Graphics g, int size, boolean isInternal)
	{
		//Get images from texture file
		ArrayList<Image> l_images = new ArrayList<Image>();
		BufferedImage l_texture = loadTexture(file, isInternal);
		int cols = (int)(l_texture.getWidth())/size;
		int l_maxImages = (int) ((l_texture.getWidth()*l_texture.getHeight())/(size*size));
		
		//Get the name of the file
		File f = new File(file);  
		String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/")+1);

		for(int i=0; i<l_maxImages; i++)
		{
			Image l_image = new Image();
			l_image.initialize(g, size, size, cols, l_texture, fileName);
			l_image.setCurrentFrame(i);
			l_images.add(l_image);
		}
		return l_images;
	}
	
	//external only
	public ArrayList<String> getAllFilesName(String Directory, boolean canCreate)
	{
		ArrayList<String> files = new ArrayList<String>();
		File folder = new File(Directory);
		if (folder.exists()) 
		{
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) 
			{
				if (listOfFiles[i].isFile()) 
				{
					files.add(listOfFiles[i].getName());
				} 
		    }
		}
		else if(canCreate)
		{
			try
			{
				folder.mkdir();
			}
			catch(SecurityException se)
			{
		        
		    }        
		}
		return files;
	}
	public boolean isFolderExisting(String Directory)
	{
		File folder = new File(Directory);
		return folder.exists();
	}
	public boolean createFolder(String Directory)
	{
		//Si le dossier existe on ne fait rien
		//Si il n'existe pas on le crée
		File folder = new File(Directory);
		if (!folder.exists())
		{
			try
			{
				folder.mkdir();
				return true;
			}
			catch(SecurityException se)
			{
		        
		    }
		}
		return false;
	}
	public boolean isFileExisting(String Directory)
	{
		File file = new File(Directory);
		return file.exists() && file.isFile();
	}
	
	public void createFile()
	{
		document = builder.newDocument();
		document.createAttribute("type");
		document.createAttribute("size");
	}
	public void saveObject(String directory)
	{
		try 
		{
			DOMSource source = new DOMSource(document);
			StreamResult sortieFichier = new StreamResult(new File(objectsFolder + "/" + directory));
			transformer.transform(source, sortieFichier);
		} 
		catch (TransformerConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (TransformerException e) 
		{
			e.printStackTrace();
		}
	}
	public boolean loadFile(String directory)
	{	
		try 
		{
			File file = new File(objectsFolder + "/" + directory);
			if(!file.exists())
				return false;
		    document= builder.parse(file);
		}
		catch (final SAXException e) 
		{
		    //e.printStackTrace();
		    return false;
		}
		catch (final IOException e) 
		{
		    //e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	public int getNodes()
	{
		return document.getChildNodes().getLength();
	}
	public Element getNode(int i)
	{
		return (Element) document.getChildNodes().item(i);
	}
	
	public Document getDocument()
	{
		return document;
	}
	public Element createElement(String name)
	{
		return document.createElement(name);
	}
	static void deleteElement(Element racine, String elementName)
	{
		NodeList listElements = racine.getChildNodes();
		for(int i=0; i<listElements.getLength(); i++)
		{
			Element actual = (Element)listElements.item(i);
			
			//si l'élément est présent, on le supprime.
			if(actual.getNodeName() == elementName)
				racine.removeChild(actual);
		}
	}
	
	//Load
	public Point2D loadPoint2D(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;

		float x = Float.parseFloat(node.getElementsByTagName("x").item(0).getTextContent());
		float y = Float.parseFloat(node.getElementsByTagName("y").item(0).getTextContent());
		
		return new Point2D(x,y);
	}
	public Vector2D loadVector2D(Element racine)
	{
		return new Vector2D(loadPoint2D(racine));
	}
	public PointInt loadPointInt(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		
		int x = Integer.parseInt(node.getElementsByTagName("x").item(0).getTextContent());
		int y = Integer.parseInt(node.getElementsByTagName("y").item(0).getTextContent());
		
		return new PointInt(x,y);
	}
	
	public Form loadForm(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		
		Form form = new Form();
		
		NodeList points = node.getElementsByTagName("Points").item(0).getChildNodes();
		
		//points
		for(int i=0; i<points.getLength(); i++)
			form.addPoint(loadPoint2D((Element) ((Element) points).getElementsByTagName("Point").item(i)));
		
		//center
		form.setCenter(loadPoint2D((Element) ((Element) node.getElementsByTagName("center").item(0)).getElementsByTagName("Point").item(0)));
		return form;
	}
	public AdvForm loadAdvForm(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		
		AdvForm advForm = new AdvForm(loadForm((Element) node.getElementsByTagName("Form").item(0)));
		String value = "";
		
		//isWeapon
		value = ((Element)(node)).getElementsByTagName("isWeapon").item(0).getTextContent();
		advForm.setWeapon(Boolean.parseBoolean(value));
		
		//isHavingLife
		value = ((Element)(node)).getElementsByTagName("isHavingLife").item(0).getTextContent();
		advForm.setHavingLife(Boolean.parseBoolean(value));
		
		//isBlocking
		value = ((Element)(node)).getElementsByTagName("isBlocking").item(0).getTextContent();
		advForm.setBlocking(Boolean.parseBoolean(value));
		
		//isAction
		value = ((Element)(node)).getElementsByTagName("isAction").item(0).getTextContent();
		advForm.setAction(Boolean.parseBoolean(value));
		
		//isLooping
		value = ((Element)(node)).getElementsByTagName("isLooping").item(0).getTextContent();
		advForm.setLooping(Boolean.parseBoolean(value));
		
		//life
		value = ((Element)(node)).getElementsByTagName("life").item(0).getTextContent();
		advForm.setLife(Integer.parseInt(value));
		
		//weaponPos
		advForm.setWeaponPos(loadPoint2D((Element) ((Element) node.getElementsByTagName("weaponPos").item(0)).getElementsByTagName("Point").item(0)));
		
		//weaponVec
		advForm.setWeaponVec(loadVector2D((Element) ((Element) node.getElementsByTagName("weaponVec").item(0)).getElementsByTagName("Point").item(0)));
		
		return advForm;
	}
	
	public SpriteData loadSpriteData(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		SpriteData spriteData = new SpriteData();
		
		//textureName
		value = ((Element) node).getElementsByTagName("textureName").item(0).getTextContent();
		spriteData.textureName = value;
		
		//get texture from textureName
		spriteData.texture = this.getDefTexture(spriteData.textureName);
		
		//flipV
		value = ((Element) node).getElementsByTagName("flipV").item(0).getTextContent();
		spriteData.flipV = Boolean.parseBoolean(value);
		
		//flipH
		value = ((Element) node).getElementsByTagName("flipH").item(0).getTextContent();
		spriteData.flipH = Boolean.parseBoolean(value);
		
		//rect
		spriteData.rect.set(loadForm((Element)node.getElementsByTagName("Form").item(0)));
		
		return spriteData;
	}
	
	public Image loadImage(Element racine)
	{	
		Image image = new Image();
		loadImage(racine, image);
		return image;
	}
	public void loadImage(Element racine, Image image)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		//cols
		value = ((Element) node).getElementsByTagName("cols").item(0).getTextContent();
		image.setCols(Integer.parseInt(value));
		
		//isDisplayingRec
		value = ((Element) node).getElementsByTagName("isDisplayingRec").item(0).getTextContent();
		image.setIsDisplayingRec(Boolean.parseBoolean(value));
	
		//visible
		value = ((Element) node).getElementsByTagName("visible").item(0).getTextContent();
		image.setVisible(Float.parseFloat(value));
		
		//colorFilter
		Element colorFilter = (Element) node.getElementsByTagName("colorFilter").item(0);
		
		value = ((Element) colorFilter).getElementsByTagName("red").item(0).getTextContent();
		int red = Integer.parseInt(value);
		
		value = ((Element) colorFilter).getElementsByTagName("blue").item(0).getTextContent();
		int blue = Integer.parseInt(value);
		
		value = ((Element) colorFilter).getElementsByTagName("green").item(0).getTextContent();
		int green = Integer.parseInt(value);
		
		image.setColorFilter(new Color(red, green, blue));
	
		//SpriteData
		image.setSpriteData(loadSpriteData((Element) node.getElementsByTagName("SpriteData").item(0)));
		
		//Rectangle
		image.setRec(loadForm((Element) ((Element) node.getElementsByTagName("rec").item(0)).getElementsByTagName("Form").item(0)));
		
		//currentFrame
		value = ((Element) node).getElementsByTagName("currentFrame").item(0).getTextContent();
		image.setCurrentFrame(Integer.parseInt(value));
	}
	public Entity loadEntity(Element racine)
	{
		Entity entity = new Entity();
		loadEntity(racine, entity);
		return entity;	
	}
	public void loadEntity(Element racine, Entity entity)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";

		loadImage((Element) node.getElementsByTagName("Image").item(0), entity);
		
		//name
		value = ((Element) node).getElementsByTagName("name").item(0).getTextContent();
		entity.setName(value);
		
		//group
		value = ((Element) node).getElementsByTagName("group").item(0).getTextContent();
		entity.setGroup(value);
		
		//velocity
		entity.setVelocity(loadVector2D((Element) ((Element) node.getElementsByTagName("velocity").item(0)).getElementsByTagName("Point").item(0)));
		
		//acceleration
		entity.setAcceleration(loadVector2D((Element) ((Element) node.getElementsByTagName("acceleration").item(0)).getElementsByTagName("Point").item(0)));
		
		//mass
		value = node.getElementsByTagName("mass").item(0).getTextContent();
		entity.setMass(Float.parseFloat(value));
		
		//isActivated
		value = ((Element) node).getElementsByTagName("isActivated").item(0).getTextContent();
		entity.setActive(Boolean.parseBoolean(value));
		
		//isActivatedCollision
		value = ((Element) node).getElementsByTagName("isActivatedCollision").item(0).getTextContent();
		entity.setActiveCollision(Boolean.parseBoolean(value));
		
		//anim
		entity.setAnim(loadAnimation((Element) node.getElementsByTagName("Animation").item(0)));
	}
	public AdvEntity loadAdvEntity(Element racine)
	{
		AdvEntity advEntity = new AdvEntity();
		loadAdvEntity(racine, advEntity);
		return advEntity;
	}
	public void loadAdvEntity(Element racine, AdvEntity advEntity)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		loadEntity((Element) node.getElementsByTagName("Entity").item(0), advEntity);
		
		//advCenter
		advEntity.setAdvCenter(loadPoint2D((Element) ((Element) node.getElementsByTagName("advCenter").item(0)).getElementsByTagName("Point").item(0)));
		
		//advCenterSave
		advEntity.setAdvCenterSave(loadPoint2D((Element) ((Element) node.getElementsByTagName("advCenterSave").item(0)).getElementsByTagName("Point").item(0)));
		
		//advEntities
		NodeList advEntities = ((Element) node.getElementsByTagName("advEntities").item(0)).getElementsByTagName("AdvEntity");
		for(int i=0; i<advEntities.getLength(); i++)
		{
			//On regarde si le parent est bien le bon node, sinon c'est un sous élément
			/*if(advEntities.item(i).getParentNode().getParentNode().isEqualNode(node))
				advEntity.addAdvEntity(loadAdvEntity((Element) advEntities.item(i)));*/
			if(advEntities.item(i).getParentNode().getParentNode().isSameNode(node))
				advEntity.addAdvEntity(loadAdvEntity((Element) advEntities.item(i)));
		}
		//displayConstruct
		NodeList eLinksDisplay = node.getElementsByTagName("linksDisplay").item(0).getChildNodes();
		
		ArrayList<Integer> linksDisplay = new ArrayList<Integer>();
		for(int i=0; i<eLinksDisplay.getLength(); i++)
			linksDisplay.add(Integer.parseInt(eLinksDisplay.item(i).getTextContent()));
		
		advEntity.reconstructDispFromLinks(linksDisplay);
		
		//AdvAnimation
		advEntity.setAdvAnimation(loadAdvAnimation((Element) node.getElementsByTagName("AdvAnimation").item(0)));
		
	}
	
	public Animation loadAnimation(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		Animation anim = new Animation();
		
		//time
		value = node.getElementsByTagName("time").item(0).getTextContent();
		anim.setTime(Float.parseFloat(value));
		
		//animNames
		NodeList animNames = ((Element) node.getElementsByTagName("animNames").item(0)).getChildNodes();
		for(int i=0; i<animNames.getLength(); i++)
		{
			//actual == null
			Element actualElement = (Element) animNames.item(i);	
			
			//name
			Element name = (Element) actualElement.getElementsByTagName("name").item(0);
			value = name.getTextContent();
			
			//animFrames
			PointInt animFrames = loadPointInt((Element) ((Element) actualElement.getElementsByTagName("animFrames").item(0)).getElementsByTagName("Point").item(0));
			
			//delay
			float delay = Float.parseFloat(actualElement.getElementsByTagName("delay").item(0).getTextContent());
			anim.add(value, animFrames, delay);
			
			//AdvForms
			NodeList ArrayAdvForms = ((Element) actualElement.getElementsByTagName("AdvForms").item(0)).getChildNodes();
			
			anim.setCurrentAnim(i);
			for(int j=0; j<ArrayAdvForms.getLength(); j++)
			{	
				anim.setCurrentFrame(j);
				NodeList AdvForms = ArrayAdvForms.item(j).getChildNodes();
				for(int w=0; w<AdvForms.getLength(); w++)
					anim.addAForm(loadAdvForm((Element) ((Element) AdvForms).getElementsByTagName("AdvForm").item(w)));
			}
		}
		
		//currentAnim
		value = ((Element) node).getElementsByTagName("currentAnim").item(0).getTextContent();
		anim.setCurrentAnim(Integer.parseInt(value));
		
		//currentFrame
		value = ((Element) node).getElementsByTagName("currentFrame").item(0).getTextContent();
		anim.setCurrentFrame(Integer.parseInt(value));
		
		return anim;
	}
	public AdvAnimation loadAdvAnimation(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		AdvAnimation advAnim = new AdvAnimation();
		
		//isPlaying
		value = ((Element) node).getElementsByTagName("isPlaying").item(0).getTextContent();
		advAnim.setIsPlaying(Boolean.parseBoolean(value));
		
		//currentTime
		value = ((Element) node).getElementsByTagName("currentTime").item(0).getTextContent();
		advAnim.setCurrentTime(Float.parseFloat(value));
		
		//savedTime
		value = ((Element) node).getElementsByTagName("savedTime").item(0).getTextContent();
		advAnim.setSavedTime(Float.parseFloat(value));
		
		//savedX
		value = ((Element) node).getElementsByTagName("savedX").item(0).getTextContent();
		advAnim.setSavedPosX(Float.parseFloat(value));
		
		//savedY
		value = ((Element) node).getElementsByTagName("savedY").item(0).getTextContent();
		advAnim.setSavedPosY(Float.parseFloat(value));
		
		//savedScale
		value = ((Element) node).getElementsByTagName("savedScale").item(0).getTextContent();
		advAnim.setSavedScale(Float.parseFloat(value));
		
		//savedRot
		value = ((Element) node).getElementsByTagName("savedRot").item(0).getTextContent();
		advAnim.setSavedRot(Float.parseFloat(value));
		
		//savedVisible
		value = ((Element) node).getElementsByTagName("savedVisible").item(0).getTextContent();
		advAnim.setSavedVisible(Float.parseFloat(value));
		
		//flipV
		value = ((Element) node).getElementsByTagName("flipV").item(0).getTextContent();
		advAnim.setFlipV(Boolean.parseBoolean(value));
		
		//flipH
		value = ((Element) node).getElementsByTagName("flipH").item(0).getTextContent();
		advAnim.setFlipH(Boolean.parseBoolean(value));
		
		//advAnimKeys
		NodeList advAnimKeys =  node.getElementsByTagName("advAnimKeys").item(0).getChildNodes();
		for(int i=0; i<advAnimKeys.getLength(); i++)
		{	
			value = ((Element) advAnimKeys.item(i)).getElementsByTagName("duration").item(0).getTextContent();
			float duration = Float.parseFloat(value);
			
			value = ((Element) advAnimKeys.item(i)).getElementsByTagName("isLooping").item(0).getTextContent();
			boolean isLooping = Boolean.parseBoolean(value);
			
			//name
			value = ((Element) advAnimKeys.item(i)).getElementsByTagName("name").item(0).getTextContent();
				
			advAnim.add(value, duration, isLooping);
		}
		
		//keys
		for(int w=0; w<AdvAnimation.OPTIONS; w++)
		{
			//current

			NodeList ArrayKeys = node.getElementsByTagName(AdvAnimation.getOptionName(w)).item(0).getChildNodes();
			for(int i=0; i<ArrayKeys.getLength(); i++)
			{
				NodeList keys = ArrayKeys.item(i).getChildNodes();
				Element test1 = (Element) ArrayKeys.item(i);
				for(int j=0; j<keys.getLength(); j++)
				{
					Element test = (Element) keys.item(j);
					Key key = loadKeyDerived((Element) keys.item(j));
					advAnim.addKey(w, i, key);
				}
			}
		}
		return advAnim;
	}
	
	public Key loadKey(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		Key key = new Key();
		
		//time
		value = ((Element) node).getElementsByTagName("time").item(0).getTextContent();
		key.time = Float.parseFloat(value);
		
		//isLinear
		value = ((Element) node).getElementsByTagName("isLinear").item(0).getTextContent();
		key.isLinear = Boolean.parseBoolean(value);
		
		return key;
	}
	public void loadKey(Element racine, Key key)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		//time
		value = ((Element) node).getElementsByTagName("time").item(0).getTextContent();
		key.time = Float.parseFloat(value);
		
		//isLinear
		value = ((Element) node).getElementsByTagName("isLinear").item(0).getTextContent();
		key.isLinear = Boolean.parseBoolean(value);
	}
	public <T extends Key> T loadKeyDerived(Element racine)
	{
		
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		Element tag = (Element) node.getElementsByTagName("value").item(0);
		value = ((Element) node.getElementsByTagName("value").item(0)).getAttribute("type");
		if(value.equals("float"))
			return ((T) loadKeyFloat(node));
		else if(value.equals("int"))
			return ((T) loadKeyInt(node));
		else if(value.equals("bool"))
			return ((T) loadKeyBool(node));
		else
			return ((T) loadKey(node));
	
	}
	public KeyFloat loadKeyFloat(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		KeyFloat key = new KeyFloat();
		loadKey(node, key);
		
		value = ((Element) node).getElementsByTagName("value").item(0).getTextContent();
		key.value = Float.parseFloat(value);
		
		return key;
	}
	public KeyInt loadKeyInt(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		KeyInt key = new KeyInt();
		loadKey(node, key);
		
		value = ((Element) node).getElementsByTagName("value").item(0).getTextContent();
		key.value = Integer.parseInt(value);
		
		return key;
	}
	public KeyBool loadKeyBool(Element racine)
	{
		Element node = null;
		if(racine == null)
			node = document.getDocumentElement();
		else
			node = racine;
		String value = "";
		
		KeyBool key = new KeyBool();
		loadKey(node, key);
		
		value = ((Element) node).getElementsByTagName("value").item(0).getTextContent();
		key.value = Boolean.parseBoolean(value);
		
		return key;
	}
	
	//Save
	public void savePoint2D(Element racine, Point2D point)
	{
		Element node = document.createElement("Point");
		node.setAttribute("type", point.getClass().getSimpleName());
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		Element x = document.createElement("x");
		x.setTextContent(point.x + "");
		node.appendChild(x);
		
		Element y = document.createElement("y");
		y.setTextContent(point.y + "");
		node.appendChild(y);
	}
	public void savePointInt(Element racine, PointInt point)
	{
		Element node = document.createElement("Point");
		node.setAttribute("type", point.getClass().getSimpleName());
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		Element x = document.createElement("x");
		x.setTextContent(point.x + "");
		node.appendChild(x);
		
		Element y = document.createElement("y");
		y.setTextContent(point.y + "");
		node.appendChild(y);
	}
	public void saveForm(Element racine, Form form)
	{
		Element node = document.createElement("Form");
		node.setAttribute("type", form.getClass().getSimpleName());
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		Element points = document.createElement("Points");
		node.appendChild(points);
		//points
		for(int i=0; i<form.size(); i++)
			savePoint2D(points, form.getPoints().get(i));
	
		//center
		Element center = document.createElement("center");
		node.appendChild(center);
		savePoint2D(center, form.getCenter());
	}
	public void saveAdvForm(Element racine, AdvForm advForm)
	{
		Element node = document.createElement("AdvForm");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		saveForm(node, advForm);
		
		//isWeapon
		Element isWeapon = document.createElement("isWeapon");
		isWeapon.setTextContent(Boolean.toString(advForm.isWeapon()));
		node.appendChild(isWeapon);
		
		//isHavingLife
		Element isHavingLife = document.createElement("isHavingLife");
		isHavingLife.setTextContent(Boolean.toString(advForm.isHavingLife()));
		node.appendChild(isHavingLife);
		
		//isBlocking
		Element isBlocking = document.createElement("isBlocking");
		isBlocking.setTextContent(Boolean.toString(advForm.isBlocking()));
		node.appendChild(isBlocking);
		
		//isAction
		Element isAction = document.createElement("isAction");
		isAction.setTextContent(Boolean.toString(advForm.isAction()));
		node.appendChild(isAction);
		
		//isLooping
		Element isLooping = document.createElement("isLooping");
		isLooping.setTextContent(Boolean.toString(advForm.isLooping()));
		node.appendChild(isLooping);
		
		//life
		Element life = document.createElement("life");
		life.setTextContent(advForm.getLife() + "");
		node.appendChild(life);
		
		//weaponPos
		Element weaponPos = document.createElement("weaponPos");
		savePoint2D(weaponPos, advForm.getWeaponPos());
		node.appendChild(weaponPos);
		
		//weaponVec
		Element weaponVec = document.createElement("weaponVec");
		savePoint2D(weaponVec, advForm.getWeaponVec());
		node.appendChild(weaponVec);
	}
	public void saveSpriteData(Element racine, SpriteData spriteData)
	{
		Element node = document.createElement("SpriteData");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		//textureName
		Element textureName = document.createElement("textureName");
		textureName.setTextContent(spriteData.textureName);
		node.appendChild(textureName);
		
		//flipH
		Element flipH = document.createElement("flipH");
		flipH.setTextContent(Boolean.toString(spriteData.flipH));
		node.appendChild(flipH);
		
		//flipV
		Element flipV = document.createElement("flipV");
		flipV.setTextContent(Boolean.toString(spriteData.flipV));
		node.appendChild(flipV);
		
		//rect
		saveForm(node, spriteData.rect);
	}
	
	public void saveImage(Element racine, Image image)
	{
		Element node = document.createElement("Image");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		//currentFrame
		Element currentFrame = document.createElement("currentFrame");
		currentFrame.setTextContent(image.getCurrentFrame() + "");
		node.appendChild(currentFrame);
		
		//cols
		Element cols = document.createElement("cols");
		cols.setTextContent(image.getCols() + "");
		node.appendChild(cols);
		
		//isDisplayingRec
		Element isDisplayingRec = document.createElement("isDisplayingRec");
		isDisplayingRec.setTextContent(image.getIsRectDisplaying() + "");
		node.appendChild(isDisplayingRec);
		
		//visible
		Element visible = document.createElement("visible");
		visible.setTextContent(image.getVisible() + "");
		node.appendChild(visible);
		
		//colorFilter
		Element colorFilter = document.createElement("colorFilter");
		
		Element blue = document.createElement("blue");
		blue.setTextContent(image.getColorFilter().getBlue() + "");
		colorFilter.appendChild(blue);
		
		Element green = document.createElement("green");
		green.setTextContent(image.getColorFilter().getGreen() + "");
		colorFilter.appendChild(green);
		
		Element red = document.createElement("red");
		red.setTextContent(image.getColorFilter().getRed() + "");
		colorFilter.appendChild(red);
		
		node.appendChild(colorFilter);
		
		//SpriteData
		saveSpriteData(node, image.getSpriteData());
		
		//Rectangle
		Element rec = document.createElement("rec");
		node.appendChild(rec);
		saveForm(rec, image.getRectangle());
	}
	public void saveEntity(Element racine, Entity entity)
	{
		Element node = document.createElement("Entity");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		saveImage(node, entity);
		
		//name
		Element name = document.createElement("name");
		name.setTextContent(entity.getName());
		node.appendChild(name);
		
		//group
		Element group = document.createElement("group");
		group.setTextContent(entity.getGroup());
		node.appendChild(group);
		
		//velocity
		Element velocity = document.createElement("velocity");
		savePoint2D(velocity, entity.getVelocity());
		node.appendChild(velocity);
		
		//acceleration
		Element acceleration = document.createElement("acceleration");
		savePoint2D(acceleration, entity.getAcceleration());
		node.appendChild(acceleration);
		
		//mass
		Element mass = document.createElement("mass");
		mass.setTextContent(entity.getMass() + "");
		node.appendChild(mass);
		
		//isActivated
		Element isActivated = document.createElement("isActivated");
		isActivated.setTextContent(Boolean.toString(entity.getIsActivated()));
		node.appendChild(isActivated);
		
		//isActivatedCollision
		Element isActivatedCollision = document.createElement("isActivatedCollision");
		isActivatedCollision.setTextContent(Boolean.toString(entity.getIsActivatedCollision()));
		node.appendChild(isActivatedCollision);
		
		//anim
		saveAnimation(node, entity.getAnim());
		
	}
	public void saveAdvEntity(Element racine, AdvEntity advEntity)
	{
		Element node = document.createElement("AdvEntity");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		saveEntity(node, advEntity);
		
		//advCenter
		Element advCenter = document.createElement("advCenter");
		savePoint2D(advCenter, advEntity.getAdvCenter());
		node.appendChild(advCenter);
		
		//advCenterSave
		Element advCenterSave = document.createElement("advCenterSave");
		savePoint2D(advCenterSave, advEntity.getAdvCenterSave());
		node.appendChild(advCenterSave);
		
		//displayConstruct
		ArrayList<Integer> linksDisplay = advEntity.getLinksDisplay();
		
		Element displayConstruct = document.createElement("linksDisplay");
		displayConstruct.setAttribute("size", linksDisplay.size() + "");
		node.appendChild(displayConstruct);
		
		for(int i=0; i<linksDisplay.size(); i++)
		{
			Element entityNumber = document.createElement("_" + i);
			entityNumber.setTextContent(linksDisplay.get(i) + "");
			displayConstruct.appendChild(entityNumber);
		}
		
		saveAdvAnimation(node, advEntity.getAdvAnimation());
		
		//advEntities
		//advEntities doit etre en dernier
		//En effet, lorsqu'on appelle getElementByTag, on récupère tous les sous éléments
		//Donc aussi ceux des advEntities fils.
		Element AdvEntities = document.createElement("advEntities");
		AdvEntities.setAttribute("size", advEntity.getAdvEntites().size() + "");
		node.appendChild(AdvEntities);
		for(int i=0; i<advEntity.getAdvEntites().size(); i++)
		{
			saveAdvEntity(AdvEntities, advEntity.getAdvEntity(i));
		}
	}
	
	public void saveAnimation(Element racine, Animation anim)
	{
		Element node = document.createElement("Animation");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		//time
		Element time = document.createElement("time");
		time.setTextContent(anim.getTime() + "");
		node.appendChild(time);
		
		//currentAnim
		Element currentAnim = document.createElement("currentAnim");
		currentAnim.setTextContent(anim.getCurrentAnim() + "");
		node.appendChild(currentAnim);
		
		//currentFrame
		Element currentFrame = document.createElement("currentFrame");
		currentFrame.setTextContent(anim.getCurrentFrame() + "");
		node.appendChild(currentFrame);
		
		//animNames
		Element animNames = document.createElement("animNames");
		node.appendChild(animNames);
		for(int i=0; i<anim.getNames().size(); i++)
		{
			Element number1 = document.createElement("_" + i);
			animNames.appendChild(number1);
			
			//name
			Element name = document.createElement("name");
			name.setTextContent(anim.getName(i));
			number1.appendChild(name);
			
			//AnimFrames
			Element animFrames = document.createElement("animFrames");
			savePointInt(animFrames, anim.getAnimFrames().get(i));
			number1.appendChild(animFrames);
			
			//delays
			Element delay = document.createElement("delay");
			delay.setTextContent(anim.getDelays().get(i) + "");
			number1.appendChild(delay);
			
			//AdvForms
			Element AdvForms = document.createElement("AdvForms");
			AdvForms.setAttribute("size", anim.getAllAForms().get(i).size() + "");
			number1.appendChild(AdvForms);
			
			for(int j=0; j<anim.getAllAForms().get(i).size(); j++)
			{	
				Element number2 = document.createElement("_" + j + "");
				number2.setAttribute("size", anim.getAllAForms().get(i).get(j).size() + "");
				AdvForms.appendChild(number2);
				
				for(int w=0; w<anim.getAllAForms().get(i).get(j).size(); w++)
				{
					saveAdvForm(number2, anim.getAllAForms().get(i).get(j).get(w));
				}
			}
		}
	}
	public void saveAdvAnimation(Element racine, AdvAnimation advAnim)
	{
		Element node = document.createElement("AdvAnimation");
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		//isPlaying
		Element isPlaying = document.createElement("isPlaying");
		isPlaying.setTextContent(advAnim.getIsPlaying() + "");
		node.appendChild(isPlaying);
		
		//currentTime
		Element currentTime = document.createElement("currentTime");
		currentTime.setTextContent(advAnim.getCurrentTime() + "");
		node.appendChild(currentTime);
		
		//savedTime
		Element savedTime = document.createElement("savedTime");
		savedTime.setTextContent(advAnim.getSavedTime() + "");
		node.appendChild(savedTime);
		
		//savedX
		Element savedX = document.createElement("savedX");
		savedX.setTextContent(advAnim.getSavedPosX() + "");
		node.appendChild(savedX);
		
		//savedY
		Element savedY = document.createElement("savedY");
		savedY.setTextContent(advAnim.getSavedPosY() + "");
		node.appendChild(savedY);
		
		//savedScale
		Element savedScale = document.createElement("savedScale");
		savedScale.setTextContent(advAnim.getSavedScale() + "");
		node.appendChild(savedScale);
		
		//savedRot
		Element savedRot = document.createElement("savedRot");
		savedRot.setTextContent(advAnim.getSavedRot() + "");
		node.appendChild(savedRot);
		
		//savedVisible
		Element savedVisible = document.createElement("savedVisible");
		savedVisible.setTextContent(advAnim.getSavedVisible() + "");
		node.appendChild(savedVisible);
		
		//flipV
		Element flipV = document.createElement("flipV");
		flipV.setTextContent(advAnim.getFlipV() + "");
		node.appendChild(flipV);
		
		//flipH
		Element flipH = document.createElement("flipH");
		flipH.setTextContent(advAnim.getFlipH() + "");
		node.appendChild(flipH);
		
		//advAnimKeys
		Element advAnimKeys = document.createElement("advAnimKeys");
		advAnimKeys.setAttribute("size", advAnim.getAll().size() + "");
		node.appendChild(advAnimKeys);
		for(int i=0; i<advAnim.getAll().size(); i++)
		{
			Element element = document.createElement("Key");
			
			Element name = document.createElement("name");
			name.setTextContent(advAnim.getName(i));
			element.appendChild(name);			
			
			Element duration = document.createElement("duration");
			duration.setTextContent(advAnim.getDuration(i) + "");
			element.appendChild(duration);
			
			Element isLooping = document.createElement("isLooping");
			isLooping.setTextContent(advAnim.getLooping(i) + "");
			element.appendChild(isLooping);
			
			advAnimKeys.appendChild(element);
		}
		
		//keys
		for(int w=0; w<AdvAnimation.OPTIONS; w++)
		{
			//current
			Element current = document.createElement(AdvAnimation.getCurrentName(w));
			current.setTextContent(advAnim.getCurrent(w) + "");
			node.appendChild(current);
			
			//save keys
			ArrayList<? extends ArrayList<? extends Key>> keys = advAnim.getAllKeys(w);
			
			Element eKey = document.createElement(AdvAnimation.getOptionName(w));
			eKey.setAttribute("size", advAnim.getAllKeysPosX().size() + "");
			node.appendChild(eKey);
			for(int i=0; i<keys.size(); i++)
			{
				//size
				Element number = document.createElement("_" + i + "");
				number.setAttribute("size", keys.get(i).size() + "");
				eKey.appendChild(number);
				for(int j=0; j<keys.get(i).size(); j++)
				{
					
					saveKey(number, keys.get(i).get(j));
				}
			}
		}
	}
	public void saveKey(Element racine, Key key)
	{
		Element node = document.createElement("Key");
		node.setAttribute("type", key.getClass().getSimpleName());
		if(racine == null)
			document.appendChild(node);
		else
			racine.appendChild(node);
		
		//time
		Element time = document.createElement("time");
		time.setTextContent(key.time + "");
		node.appendChild(time);
		
		//isLinear
		Element isLinear = document.createElement("isLinear");
		isLinear.setTextContent(key.isLinear + "");
		node.appendChild(isLinear);
		
		//value
		Element value = document.createElement("value");
		node.appendChild(value);
		
		if(key instanceof KeyFloat)
		{
			value.setAttribute("type", "float");
			value.setTextContent(((KeyFloat)key).value + "");
		}
		else if(key instanceof KeyInt)
		{
			value.setAttribute("type", "int");
			value.setTextContent(((KeyInt)key).value + "");
		}
		else if (key instanceof KeyBool)
		{
			value.setAttribute("type", "bool");
			value.setTextContent(((KeyBool)key).value + "");
		}
	}
	
	public static FileManager getInstance()
	{	
		return INSTANCE;
	}
}
