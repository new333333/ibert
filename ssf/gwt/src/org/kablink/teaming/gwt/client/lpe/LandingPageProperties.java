package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

/**
 * This class holds the properties for a landing page, such as the background color
 * and background image.
 * @author jwootton
 *
 */
public class LandingPageProperties
	implements IsSerializable, VibeRpcResponseData
{
	transient public final String ROOT_ELEMENT_NAME = "landingPageData";
	transient public final String BACKGROUND_ELEMENT_NAME = "background";
	transient public final String PAGE_LAYOUT_ELEMENT_NAME = "pageLayout";
	transient public final String COLOR_ATTRIBUTE_NAME = "color";
	transient public final String IMAGE_NAME_ATTRIBUTE_NAME = "imgName";
	transient public final String REPEAT_ATTRIBUTE_NAME = "repeat";
	transient public final String HIDE_MENU_ATTRIBUTE_NAME = "hideMenu";
	
	private String m_backgroundColor;
	private String m_backgroundImageName;
	private String m_backgroundImageUrl;
	private String m_backgroundRepeat;
	private boolean m_hideMenu;
	private boolean m_inheritProperties;
	
	/**
	 * 
	 */
	public LandingPageProperties()
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundImageUrl = null;
		m_backgroundRepeat = null;
		m_hideMenu = false;
		m_inheritProperties = true;
	}

	/**
	 * 
	 */
	public LandingPageProperties( String propertiesXML )
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundImageUrl = null;
		m_backgroundRepeat = null;
		m_hideMenu = false;
		m_inheritProperties = true;
		
		if ( propertiesXML != null )
		{
			try
			{
				Document doc;
				NodeList nodeList;
				
				doc = XMLParser.parse( propertiesXML );
				
				// Get the <background ...> element.
				nodeList = doc.getElementsByTagName( BACKGROUND_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element backgroundElement;
					
					backgroundElement = (Element) nodeList.item( 0 );
					
					m_backgroundColor = backgroundElement.getAttribute( COLOR_ATTRIBUTE_NAME );
					m_backgroundImageName = backgroundElement.getAttribute( IMAGE_NAME_ATTRIBUTE_NAME );
					m_backgroundRepeat = backgroundElement.getAttribute( REPEAT_ATTRIBUTE_NAME );
				}
				
				// Get the <pageLayout ...> element
				nodeList = doc.getElementsByTagName( PAGE_LAYOUT_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element pageLayoutElement;
					
					pageLayoutElement = (Element) nodeList.item( 0 );
					
					m_hideMenu = Boolean.parseBoolean( pageLayoutElement.getAttribute( HIDE_MENU_ATTRIBUTE_NAME ) );
				}
				
				m_inheritProperties = false;
			}
			catch (DOMParseException ex)
			{
			}
		}
	}
	
	/**
	 * 
	 */
	public void copy( LandingPageProperties lpProperties )
	{
		m_backgroundColor = lpProperties.getBackgroundColor();
		m_backgroundImageName = lpProperties.getBackgroundImageName();
		m_backgroundImageUrl = lpProperties.getBackgroundImageUrl();
		m_backgroundRepeat = lpProperties.getBackgroundRepeat();
		m_hideMenu = lpProperties.getHideMenu();
		m_inheritProperties = lpProperties.getInheritProperties();
	}
	
	/**
	 * 
	 */
	public String getBackgroundColor()
	{
		return m_backgroundColor;
	}
	
	/**
	 * 
	 */
	public String getBackgroundImageName()
	{
		return m_backgroundImageName;
	}
	
	/**
	 * 
	 */
	public String getBackgroundImageUrl()
	{
		return m_backgroundImageUrl;
	}
	
	/**
	 * 
	 */
	public String getBackgroundRepeat()
	{
		return m_backgroundRepeat;
	}

	/**
	 * 
	 */
	public boolean getHideMenu()
	{
		return m_hideMenu;
	}
	
	/**
	 * 
	 */
	public boolean getInheritProperties()
	{
		return m_inheritProperties;
	}
	
	/**
	 * Return the properties as an xml string that looks like the following:
	 *	<landingPageData>
	 * 		<background color="" imgName="" />
	 * 		<pageLayout hideMenu="true | false" />
	 * 	</landingPageData>
	 */
	public String getPropertiesAsXMLString()
	{
		Document doc;
		Element rootElement;
		
		// Are we inheriting the properties?
		if ( m_inheritProperties )
		{
			// Yes, return null to indicate we are inheriting
			return null;
		}
		
		doc = XMLParser.createDocument();
		
		rootElement = doc.createElement( ROOT_ELEMENT_NAME );
		doc.appendChild( rootElement );
		
		// Add the <background color="" imgName="" /> element
		{
			Element backgroundElement;

			backgroundElement = doc.createElement( BACKGROUND_ELEMENT_NAME );
			
			if ( m_backgroundColor != null )
				backgroundElement.setAttribute( COLOR_ATTRIBUTE_NAME, m_backgroundColor );
			
			if ( m_backgroundImageName != null )
			{
				backgroundElement.setAttribute( IMAGE_NAME_ATTRIBUTE_NAME, m_backgroundImageName );
				
				if ( m_backgroundRepeat != null )
					backgroundElement.setAttribute( REPEAT_ATTRIBUTE_NAME, m_backgroundRepeat );
			}
			
			rootElement.appendChild( backgroundElement );
		}
		
		// Add the <pageLayout hideMenu="true | false" /> element
		{
			Element pgLayoutElement;
			
			pgLayoutElement = doc.createElement( PAGE_LAYOUT_ELEMENT_NAME );
			
			pgLayoutElement.setAttribute( HIDE_MENU_ATTRIBUTE_NAME, String.valueOf( m_hideMenu ) );
			
			rootElement.appendChild( pgLayoutElement );
		}
		
		return doc.toString();
	}
	
	/**
	 * 
	 */
	public void setBackgroundColor( String color )
	{
		m_backgroundColor = color;
	}
	
	/**
	 * 
	 */
	public void setBackgroundImgName( String imgName )
	{
		m_backgroundImageName = imgName;
	}
	
	/**
	 * 
	 */
	public void setBackgroundImgUrl( String url )
	{
		m_backgroundImageUrl = url;
	}
	
	/**
	 * 
	 */
	public void setBackgroundRepeat( String repeat )
	{
		m_backgroundRepeat = repeat;
	}
	
	/**
	 * 
	 */
	public void setHideMenu( boolean hide )
	{
		m_hideMenu = hide;
	}
	
	/**
	 * 
	 */
	public void setInheritProperties( boolean inherit )
	{
		m_inheritProperties = inherit;
	}
}
