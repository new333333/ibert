/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.taglib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Html;
import com.sitescape.util.Validator;



/**
 * @author Peter Hurley 
 *
 */
public class TreeTag extends TagSupport {
    private String treeName;
    private String indentKey;
    private String topId;
    private String startingId;
    private Document tree;
    private String contextPath;
    private boolean rootOpen = true;
    private boolean allOpen = false;
    private boolean showImages = true;
    private String nodeOpen = "";
    private String highlightNode = "";
    private Collection multiSelect;
    private String multiSelectPrefix; // $type => use type attribute on element
    private String displayStyle;
    private String singleSelectName, singleSelect;
    private boolean nowrap = false;
    private String commonImg;
    private String className = "";
    private Map images;
    private Map imagesOpen;
	private String userDisplayStyle;
	private boolean tableOpened = false;
	private boolean startingIdSeen = false;
	private boolean initOnly = false;
	private boolean noInit = false;
	private boolean flat = false;
	private boolean finished = false;
	private String lastListStyle = "";
	private String showIdRoutine = "";
	
	/**
	 * <code>false</code> checkboxes (multiSelect) name = multiSelectPrefix + binderId, value = null
	 * <code>true</code> checkboxes (multiSelect) name = multiSelectPrefix, value = binderId
	 */
	private boolean fixedMultiSelectParamsMode = false;
    
    
	public int doStartTag() throws JspException {
	    if(treeName == null)
	        throw new JspException("Tree name must be specified");
	    if(tree == null && !initOnly)
	        throw new JspException("Tree document must be specified");
	    this.finished = false;
	    this.tableOpened = false;
	    this.startingIdSeen = false;
	    this.lastListStyle = "";
	    if (this.topId == null) this.topId = "";
	    if (this.indentKey == null) this.indentKey = "";
	    if (this.displayStyle == null) this.displayStyle = "";
	    if (this.multiSelectPrefix == null) this.multiSelectPrefix = "";
	    if (this.showIdRoutine.equals("")) this.showIdRoutine = this.treeName + "_showId";
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			User user = RequestContextHolder.getRequestContext().getUser();
			String colorTheme = user.getTheme();
			if (colorTheme == null || colorTheme.equals("")) colorTheme = WebKeys.THEME_IC_ICE_BLUE; 
			this.userDisplayStyle = user.getDisplayStyle();
	        if (this.userDisplayStyle == null) this.userDisplayStyle = "";

			this.contextPath = req.getContextPath();
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		    setCommonImg(contextPath + "/i/" + colorTheme);
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_forum", Boolean.parseBoolean("true"));
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_WORKSPACE_TREE);
			if (multiSelect != null) {
				//This request is displaying the checkboxes. Remember that in the url
				adapterUrl.setParameter(WebKeys.URL_TREE_SELECT_TYPE, "2");
				adapterUrl.setParameter(WebKeys.URL_TREE_SELECT_ID, multiSelectPrefix);
				adapterUrl.setParameter(WebKeys.URL_TREE_MULTI_SELECT, join(multiSelect, ","));
			} else if (singleSelectName != null) {
				adapterUrl.setParameter(WebKeys.URL_TREE_SELECT_TYPE, "1");
				adapterUrl.setParameter(WebKeys.URL_TREE_SELECT_ID, singleSelectName);
			}
			if (fixedMultiSelectParamsMode) {
				adapterUrl.setParameter("fixedMultiSelectParamsMode", "true");
			}
			if (!this.topId.equals("")) {
				//adapterUrl.setParameter(WebKeys.URL_OPERATION2, this.topId);
			}
			String aUrl = adapterUrl.toString().replaceAll("&", "&amp;");
		    
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			//Get the starting point of the tree
			Element treeRoot = null;
			if (tree != null) treeRoot = (Element)tree.getRootElement();
			if (!this.noInit && Validator.isNull(startingId) || this.initOnly) {
				sb.append("<script type=\"text/javascript\" src=\"").append(contextPath).append("/js/tree/tree_widget.js\"></script>\n");
				sb.append("<script type=\"text/javascript\">\n");
				sb.append("ssTree_defineBasicIcons('"+contextPath+"/images');\n");
				sb.append("var ss_treeAjaxUrl_" + this.treeName + " = '" + aUrl + "';\n");
				sb.append("var ss_treeNotLoggedInMsg = '" + NLT.get("general.notLoggedIn") + "';\n");
				sb.append("var ss_treeShowIdRoutine_"+this.treeName+" = '" + this.showIdRoutine + "';\n");
				//used to match up treehelper on ajax callbacks
				String treeKey="";
				if (treeRoot != null) treeKey = treeRoot.attributeValue("treeKey", "");
				sb.append("var ss_treeKey_"+this.treeName+" = '" + treeKey + "';\n");					
				if (multiSelect != null) {
					//
				} 
				//define variable to hold initial value
				sb.append("var ss_treeSelected_"+this.treeName+" = ");
				if (!Validator.isNull(singleSelect)) {
					 sb.append("'" + singleSelect + "';\n");
				} else {
					sb.append("null;\n");
				}
				String displayStyle = user.getDisplayStyle();
				if (displayStyle == null) displayStyle = "";
				sb.append("var ss_treeDisplayStyle = '" + displayStyle + "';\n");
				sb.append("var ss_treeButtonClose = '" + NLT.get("button.close") + "';\n");
				sb.append("</script>\n\n\n");
				sb.append("<div id=\"ss_hiddenTreeDiv"+treeName+"\" style=\"visibility:hidden;\"></div>\n");
			}

			if (this.initOnly) {
				jspOut.print(sb.toString());
			} else {
				//Mark that the root is the last item in its list
				treeRoot.addAttribute("treeLS","1");
				if (this.rootOpen || this.allOpen || treeRoot.attributeValue("id", "-1").equals(this.nodeOpen)) {
					treeRoot.addAttribute("treeOpen","1");
				} else {
					treeRoot.addAttribute("treeOpen","0");
				}
				List treeRootElements = (List) treeRoot.elements("child");
				if (treeRootElements.size() > 0) {
					treeRoot.addAttribute("treeHasChildren","1");
					treeRoot.addAttribute("treeHasHiddenChildren","0");
				} else {
					treeRoot.addAttribute("treeHasChildren","0");
					if (treeRoot.attributeValue("hasChildren", "").equalsIgnoreCase("true")) {
						treeRoot.addAttribute("treeHasHiddenChildren","1");
					} else {
						treeRoot.addAttribute("treeHasHiddenChildren","0");
					}
				}
				
				//Build a list of elements starting with the root
				List treeElements = new ArrayList();
				treeElements.add(0,treeRoot);
				
				//Process the tree by traversing each branch all the way to its tips
				//  remembering all elements seen along the way
				while (treeElements.size() > 0) {
					//Process the first branch on the list. Then process its children if any.
					Element currentTreeElement = (Element) treeElements.get(0);
					treeElements.remove(0);
	
					//If this branch has children, add those to the front of the processing list
					// Then, loop back to process the children before continuing with the other branches
					List treeElements2 = (List) currentTreeElement.elements("child");
					if (treeElements2.size() > 0) {
						ListIterator it = treeElements2.listIterator();
						while (it.hasNext()) {
							Element nextTreeElement2 = (Element) it.next();
							nextTreeElement2.addAttribute("parentId", nextTreeElement2.getParent().attributeValue("id", ""));
							if (it.hasNext()) {
								nextTreeElement2.addAttribute("treeLS","0");
							} else {
								nextTreeElement2.addAttribute("treeLS","1");							
							}
							if (this.allOpen || nextTreeElement2.attributeValue("id", "-1").equals(this.nodeOpen)) {
								nextTreeElement2.addAttribute("treeOpen","1");
								
								//Make sure the parents are all open, too
								Element parentElement = nextTreeElement2.getParent();
								while (parentElement != null && !parentElement.isRootElement()) {
									parentElement.addAttribute("treeOpen", "1");
									if (parentElement.isRootElement()) {break;}
									parentElement = parentElement.getParent();
								}
							} else {
								nextTreeElement2.addAttribute("treeOpen","0");
							}
							List treeElements3 = (List) nextTreeElement2.elements("child");
							if (treeElements3.size() > 0) {
								nextTreeElement2.addAttribute("treeHasChildren","1");
								nextTreeElement2.addAttribute("treeHasHiddenChildren","0");
							} else {
								nextTreeElement2.addAttribute("treeHasChildren","0");
								if (nextTreeElement2.attributeValue("hasChildren", "").equalsIgnoreCase("true")) {
									nextTreeElement2.addAttribute("treeHasHiddenChildren","1");
								} else {
									nextTreeElement2.addAttribute("treeHasHiddenChildren","0");
								}
							}
						}
						treeElements.addAll(0,treeElements2);
					}
				}
				
				if (this.startingId == null || this.startingId.equals("")) {
					sb.append("<div class=\"ss_treeWidget");
					if (this.nowrap) sb.append(" ss_nowrap");
					sb.append("\">\n");
					
					String mPrefix=this.multiSelectPrefix;
					// hope it's not in use here because type attribute is now unknown
//					if (mPrefix.startsWith("$type")) {
//						mPrefix = mPrefix.replaceFirst("\\$type", e.attributeValue("type", "$type"));
//					}
					
					if (this.multiSelect != null && !this.multiSelect.isEmpty()) {
						Iterator multiSelectIt = this.multiSelect.iterator();
						while (multiSelectIt.hasNext()) {
							String id = multiSelectIt.next().toString();
							String name = "";
							String value = "";
							if (this.fixedMultiSelectParamsMode) {
								name = " name=\"" + mPrefix + "\"";
								value = " value=\"" + id + "\"";
							} else {
								name = " name=\"" + mPrefix + id + "\"";
								value = "";
							}
							sb.append("<input type=\"hidden\" id=\"" + treeName + mPrefix + id + "_lastChoice\"" + name + value + "/>\n");
						}
					}
					
					if (this.singleSelect != null && !this.singleSelect.equals("")) {
						sb.append("<input type=\"hidden\" id=\"" + treeName + "_lastChoice\" name=\"" + this.singleSelectName + "\" value=\"" + this.singleSelect + "\" />\n");
					}
					
				}
				String indentKey = this.indentKey;
				if (this.flat) {
					//This user is in accessibility mode, output a flat version of the tree
					outputTreeNodesFlat(treeRoot, indentKey);
					
				} else {
					jspOut.print(sb.toString());
					
					//Output the tree
					outputTreeNodes(treeRoot, indentKey);
					
					//Close the sortable table if needed
					if (this.tableOpened) {
						jspOut.print("<li></li></ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
						this.tableOpened = false;
					}
				}
				if (this.startingId == null || this.startingId.equals("")) {
					jspOut.print("</div>\n");
				}
			}
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    finally {
	    	this.nowrap = false;
	    	allOpen=false;
	    	showImages=true;
	    	rootOpen=false;
	    	singleSelect=null;
	    	multiSelect=null;
	    	multiSelectPrefix=null;
	    	topId="";
	    	indentKey = "";
	    	showIdRoutine="";
	    	initOnly=false;
	    	noInit=false;
	    	flat=false;
	    }
	    
		return SKIP_BODY;
	}
	
	private void outputTreeNodes(Element e, String indentKey) throws JspException {
		//If processing is finished, just exit.
		if (this.finished) return;
		
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			//Output the element divs
			JspWriter jspOut = pageContext.getOut();
			
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//id
			String s_id = e.attributeValue("id", "");
			String s_binderId = s_id;
			if (s_id.indexOf(".") >= 0) s_binderId = s_id.substring(0, s_id.indexOf("."));
			String s_parentId = e.attributeValue("parentId", "");
			String s_tuple = e.attributeValue("pageTuple", "");
			String s_page = e.attributeValue("page", "");
			if (!s_page.equals("")) {
				s_page += DomTreeBuilder.PAGE_DELIMITER + s_tuple.replaceAll("'", "''");
			}
			String titleClass = "";
			String s_showIdRoutine = showIdRoutine;
			if (s_showIdRoutine.equals("")) s_showIdRoutine = this.treeName + "_showId";
			if (!s_binderId.equals("") && s_binderId.equals(this.highlightNode)) {
				titleClass = "class=\"ss_tree_highlight\"";
			} else {
				if (!className.equals("")) titleClass = "class=\""+className+"\"";
			}
	
			//Text
			String s_text = Html.formatTo(e.attributeValue("title"));
			if (!s_tuple.equals("")) {
				//This title is a range field; format it appropriately
				s_text = getBucketDisplay(Html.formatTo(e.attributeValue("tuple1")));
				s_text += " <img alt=\"\" src=\"" + getImage("/icons/range_arrows.gif") + "\"/> ";
				s_text += getBucketDisplay(Html.formatTo(e.attributeValue("tuple2")));
			}
			if (Validator.isNull(s_text)) s_text = "--" + NLT.get("entry.noTitle") + "--";
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			String s_imageClass = e.attributeValue("imageClass", "ss_twImg");
			boolean displayOnly = GetterUtil.getBoolean((String)e.attributeValue("displayOnly"));
			
			//Url (if any)
			String s_url = (String) e.attributeValue("url");
			if (s_url == null) {
				//Look to see if there are url attributes to build the url
				Element url = e.element("url");
				s_url = "";
				if (url != null && url.attributes().size() > 0) {
					PortletURL portletURL = renderResponse.createActionURL();
					Iterator attrs = url.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						portletURL.setParameter(attr.getName(), attr.getValue());
					}
					portletURL.setWindowState(WindowState.MAXIMIZED);
					portletURL.setPortletMode(PortletMode.VIEW);
					s_url = portletURL.toString();
				} 
			}
			s_url = s_url.replaceAll("&", "&amp;");
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
			boolean hhcn = (e.attributeValue("treeHasHiddenChildren") == "1") ? true : false;
			boolean ino = (e.attributeValue("treeOpen") == "1") ? true : false;
			String action = e.attributeValue("action", "");
			String mPrefix=this.multiSelectPrefix;
			if (mPrefix.startsWith("$type")) {
				mPrefix = mPrefix.replaceFirst("\\$type", e.attributeValue("type", "$type"));
			}
				

			//Show the tree 
			if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
				if (this.multiSelect != null) {
					if (s_id.equals("") || displayOnly) {
						jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("spacer") + "\"/>");
					} else {
						String checked = "";
						if (this.multiSelect.contains(s_binderId)) {
							checked = "checked=\"checked\"";
						}
						jspOut.print("<input type=\"checkbox\" class=\"ss_text\"");
						jspOut.print(" style=\"margin:0px; padding:0px; width:19px;\"");
						String name = "";
						String value = "";
						if (this.fixedMultiSelectParamsMode) {
							name = " name=\"" + mPrefix + "\"";
							value = " value=\"" + s_id + "\"";
						} else {
							name = " name=\"" + mPrefix + s_id + "\"";
							value = "";
						}
						jspOut.print(name);
						jspOut.print(value);
						jspOut.print(" id=\"");
						jspOut.print("ss_tree_checkbox" + treeName + mPrefix + s_id + "\" ");
						jspOut.print(checked);
						if (this.multiSelect.contains(s_binderId)) {
							jspOut.print(" onclick=\"ss_clearMultiSelect('" + treeName + mPrefix + s_id + "')\"");
						}
						jspOut.print("/>");
					}
				} else if (this.singleSelectName != null) {
					//can only select one item from tree, but probably other things going on
					//ie) don't want link to submit form
					if (s_id.equals("") || displayOnly) {
						jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("spacer") + "\"/>");
					} else {
						String checked = "";
						if (s_binderId.equals(this.singleSelect)) checked = "checked=\"checked\"";
						jspOut.print("<input type=\"radio\" class=\"ss_text\"");
						jspOut.print(" style=\"margin:0px; padding:0px; width:19px;\" name=\"");
						jspOut.print(singleSelectName + "\" value=\""+s_binderId + "\" " + checked + " onclick=\"return ss_clearSingleSelect('" + treeName +"');\"/>");
					}
					
				}
			}
			if (this.startingIdSeen) {
				//Add the spacer gifs passed in from the tag
				for (int j = 0; j < indentKey.length(); j++) {
					if (indentKey.substring(j, j+1).equals("s")) {
						jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("spacer") + "\"/>");
					} else if (indentKey.substring(j, j+1).equals("l")) {
						jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("line") + "\"/>");
					}
				}
			}
		
			if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
				// Write out join icons
				if (hcn || hhcn) {
					if (ls) {
						indentKey += "s";
						String classField = "";
						if (!className.equals("")) {
							classField = "class=\""+className+"\"";
						} else {
							classField = "class=\""+titleClass+"\"";
						}
						jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
						jspOut.print("onClick=\"");
						jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_id + "', '" + s_parentId + "', 1, '"+e.attributeValue("image")+"', '"+s_page+"', '"+indentKey+"');return false;\" ");
						jspOut.print("onDblClick=\"");
						jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_id + "', '" + s_parentId + "', 1, '"+e.attributeValue("image")+"', '"+s_page+"', '"+indentKey+"');return false;\" ");
						jspOut.print("style=\"text-decoration: none;\">");
						jspOut.print("<img border=\"0\" alt=\"" +NLT.get("alt.toggleTree")+ "\" id=\"" + this.treeName + "join" + s_id + "\" class=\"");
		
						if (ino) {
							if (s_parentId.equals("")) {
								jspOut.print("ss_twMinusTopBottom"); // minus_top_bottom.gif
							} else {
								jspOut.print("ss_twMinusBottom");	 // minus_bottom.gif
							}
						} else {
							if (s_parentId.equals("")) {
								jspOut.print("ss_twPlusTopBottom");  // plus_top_bottom.gif
							} else {
								jspOut.print("ss_twPlusBottom");     // plus_bottom.gif
							}
						}
		
						jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/></a>");
					}
					else {
						indentKey += "l";
						String classField = "";
						if (!className.equals("")) classField = "class=\""+className+"\"";
						jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
						jspOut.print("onClick=\"");
						jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_id + "', '" + s_parentId + "', 0, '"+e.attributeValue("image")+"', '"+s_page+"', '"+indentKey+"');return false;\" ");
						jspOut.print("onDblClick=\"");
						jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_id + "', '" + s_parentId + "', 0, '"+e.attributeValue("image")+"', '"+s_page+"', '"+indentKey+"');return false;\" ");
						jspOut.print("style=\"text-decoration: none;\">");
						jspOut.print("<img border=\"0\" alt=\"" +NLT.get("alt.toggleTree")+ "\" id=\"" + this.treeName + "join" + s_id + "\" class=\"");
		
						if (ino) {
							jspOut.print("ss_twMinus");	// minus.gif
						} else {
							jspOut.print("ss_twPlus");	// plus.gif
						}
		
						jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/></a>");
					}
				}
				else {
					if (!this.topId.equals(s_id)) {
						indentKey += "s";
						if (ls) {
							jspOut.print("<img class=\"ss_twJoinBottom\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
						} else {
							jspOut.print("<img class=\"ss_twJoin\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
						}
					} else {
						jspOut.print("<img class=\"ss_twNone\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
					}
				}
		
				// Link
				if (this.showImages) {
					if (hcn || hhcn) {
						jspOut.print("<img class=\""+s_imageClass+"\" id=\"");
						jspOut.print(this.treeName);
						jspOut.print("icon" + s_id + "\" alt=\"\" src=\"");
			
						if (ino) {
							jspOut.print(s_imageOpen); // e.g., folder_open.gif
						} else {
							jspOut.print(s_image);	// e.g., folder.gif
						}
			
						jspOut.print("\"/>");
					}
					else {
						jspOut.print("<img alt=\"\" class=\""+s_imageClass+"\" id=\"");
						jspOut.print(this.treeName);
						jspOut.print("icon" + s_id + "\" src=\"" + s_image + "\"/>");
					}
				}
		
				if (!displayOnly) {
					String classField = "";
					if (!className.equals("")) {
						classField = "class=\""+className+"\"";
					} else {
						classField = titleClass;
					}
					jspOut.print("<a "+classField+" href=\"" + ((s_url == null || "".equals(s_url))?"javascript: //;":s_url) + "\" ");
					if (s_id != null && !s_id.equals("")) {
						if (action.equals(""))
							jspOut.print("onClick=\"if (self."+s_showIdRoutine+") {return "+s_showIdRoutine+"('"+s_binderId+"', this);}\" ");
						else
							jspOut.print("onClick=\"if (self."+s_showIdRoutine+") {return "+s_showIdRoutine+"('"+s_binderId+"', this,'"+action+"');}\" ");
										}
					jspOut.print(">");
				}
				jspOut.print("<span " + titleClass + ">");
				jspOut.print(s_text);
				jspOut.print("</span>");
				
				if (!displayOnly) jspOut.print("</a>");

				jspOut.print("<br/>\n");
			}
			
			//See if this is the starting id
			if (this.topId.equals("") || s_id.equals(this.topId) || 
					(this.startingId != null && this.startingId.equals(s_id))) this.startingIdSeen = true;
			
			// Recurse if node has children
			if (hcn) {
				boolean divHasBeenOutput = false;
				if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
					jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\"></div>\n");
					jspOut.print("\n<div class=\"ss_twDiv\" id=\"" + this.treeName + "div" + s_id + "\"");
		
					if (!ino) {
						jspOut.print(" style=\"display: none;\"");
					}
		
					jspOut.print(">\n");
					divHasBeenOutput = true;
				}
	
				ListIterator it2 = e.elements("child").listIterator();
				while (it2.hasNext()) {
					outputTreeNodes((Element) it2.next(), indentKey);
				}
	
				if (divHasBeenOutput) {
					jspOut.print("</div>\n");
				}
			} else if (hhcn) {
				if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
					jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\"></div>\n");
				}
			}
	
			// See if it is time to stop
			if (this.startingId != null && this.startingId.equals(s_id)) {
				this.finished = true;
			}
		}
	    catch(Exception ex) {
	        throw new JspException(ex);
	    }
	}

	private void outputTreeNodesFlat(Element e, String indentKey) throws JspException {
		//If processing is finished, just exit.
		if (this.finished) return;

		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			//Output the element divs
			JspWriter jspOut = pageContext.getOut();
			
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//Text
			String s_text = Html.formatTo(e.attributeValue("title"));
			if (Validator.isNull(s_text)) s_text = "--" + NLT.get("entry.noTitle") + "--";
	
			//id
			String s_id = e.attributeValue("id", "");
			String s_binderId = s_id;
			if (s_id.indexOf(".") >= 0) s_binderId = s_id.substring(0, s_id.indexOf("."));
			String s_parentId = e.attributeValue("parentId", "");
			String s_tuple = e.attributeValue("pageTuple", "");
			String s_page = e.attributeValue("page", "");

			if (!s_page.equals("")) {
				s_page += DomTreeBuilder.PAGE_DELIMITER + s_tuple.replaceAll("'", "''");
			}
			String titleClass = "class=\"ss_twSpan\"";
			if (!s_binderId.equals("") && s_binderId.equals(this.highlightNode)) {
				titleClass = "class=\"ss_twSpan ss_tree_highlight\"";
			} else {
				if (!className.equals("")) titleClass = "class=\"ss_twSpan "+className+"\"";
			}
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			String s_imageClass = e.attributeValue("imageClass", "ss_twImg");
			boolean displayOnly = GetterUtil.getBoolean((String)e.attributeValue("displayOnly"));
			String s_showIdRoutine = showIdRoutine;
			
			//Url (if any)
			String s_url = (String) e.attributeValue("url");
			if (s_url == null) {
				//Look to see if there are url attributes to build the url
				Element url = e.element("url");
				s_url = "";
				if (url != null && url.attributes().size() > 0) {
					PortletURL portletURL = renderResponse.createActionURL();
					Iterator attrs = url.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						portletURL.setParameter(attr.getName(), attr.getValue());
					}
					portletURL.setWindowState(WindowState.MAXIMIZED);
					portletURL.setPortletMode(PortletMode.VIEW);
					s_url = portletURL.toString();
				} 
			}
			s_url = s_url.replaceAll("&", "&amp;");
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
			boolean hhcn = (e.attributeValue("treeHasHiddenChildren") == "1") ? true : false;
			boolean ino = (e.attributeValue("treeOpen") == "1") ? true : false;
			String action = e.attributeValue("action", "");
	
			//Add the spacer gifs passed in from the tag
			for (int j = 0; j < indentKey.length(); j++) {
				if (indentKey.substring(j, j+1).equals("s")) {
					jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("spacer") + "\"/>");
				} else if (indentKey.substring(j, j+1).equals("l")) {
					jspOut.print("<img class=\"ss_twImg\" alt=\"\" src=\"" + getImage("line") + "\"/>");
				}
			}
	
			// Write out join icons
			if (hhcn) {
				if (ls) {
					jspOut.print("<img alt=\""+NLT.get("alt.toggleTree")+"\" id=\"" + this.treeName + "join" + s_id + "\" class=\"");
	
					if (!s_parentId.equals("")) indentKey += "s";
					if (ino) {
						if (s_parentId.equals("")) {
							jspOut.print("ss_twMinusTopBottom");	// minus_top_bottom.gif
						} else {
							jspOut.print("ss_twMinusBottom");	    // minus_bottom.gif
						}
					} else {
						if (s_parentId.equals("")) {
							jspOut.print("ss_twPlusTopBottom");    // plus_top_bottom.gif
						} else {
							jspOut.print("ss_twPlusBottom");       // plus_bottom.gif
						}
					}
	
					jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
				else {
					jspOut.print("<img alt=\""+NLT.get("alt.toggleTree")+"\" id=\"" + this.treeName + "join" + s_id + "\" class=\"");
	
					if (!s_parentId.equals("")) indentKey += "l";
					if (ino) {
						jspOut.print("ss_twMinus");	// minus.gif
					} else {
						jspOut.print("ss_twPlus");	// plus.gif
					}
	
					jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
			} else {
				if (!this.topId.equals(s_id)) {
					if (!s_parentId.equals("")) indentKey += "s";
					if (ls) {
						jspOut.print("<img class=\"ss_twJoinBottom\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
					} else {
						jspOut.print("<img class=\"ss_twJoin\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
					}
				} else {
					jspOut.print("<img class=\"ss_twNone\" alt=\"\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
			}
	
			// Link
			if (this.showImages) {
				if (hcn) {
					jspOut.print("<img alt=\"\" class=\""+s_imageClass+"\" id=\"");
					jspOut.print(this.treeName);
					jspOut.print("icon" + s_id + "\" src=\"");
					jspOut.print(s_imageOpen); // e.g., folder_open.gif
					jspOut.print("\"/>");
				}
				else {
					jspOut.print("<img alt=\"\" class=\""+s_imageClass+"\" id=\"");
					jspOut.print(this.treeName);
					jspOut.print("icon" + s_id + "\" src=\"" + s_image + "\"/>");
				}
			}
	
			//jspOut.print("&nbsp;");
			if (!displayOnly) {
				String classField = "class=\"ss_twA\"";
				if (!className.equals("")) classField = "class=\"ss_twA "+className+"\"";
				jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
				if (s_id != null && !s_id.equals("")) {
					if (action.equals("")) {
						jspOut.print("onClick=\"if (self."+s_showIdRoutine+") {return "+s_showIdRoutine+"('"+s_binderId+"', this);}\" ");
					} else {
						jspOut.print("onClick=\"if (self."+s_showIdRoutine+") {return "+s_showIdRoutine+"('"+s_binderId+"', this,'"+action+"');}\" ");
					}
				}
				jspOut.print(">");
			}
			jspOut.print("<span " + titleClass + ">");
			jspOut.print(s_text);
			jspOut.print("</span>");
			
			if (!displayOnly) jspOut.print("</a>");
			jspOut.print("<br/>\n");
			
			// Recurse if node has children
	
			if (hcn) {
				jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\" class=\"ss_twDiv\"></div>\n");
				jspOut.print("\n<div id=\"" + this.treeName + "div" + s_id + "\" class=\"ss_twDiv\">\n");
	
				ListIterator it2 = e.elements("child").listIterator();
				while (it2.hasNext()) {
					outputTreeNodesFlat((Element) it2.next(), indentKey);
				}
				jspOut.print("</div>\n");
			} else if (hhcn) {
				jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\" class=\"ss_twDiv\"></div>\n");
			}
		}
	    catch(Exception ex) {
	        throw new JspException(ex);
	    }
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setTreeName(String treeName) {
	    this.treeName = treeName;
	}
	
	public void setIndentKey(String indentKey) {
	    this.indentKey = indentKey;
	}
	
	public void setTopId(String topId) {
	    this.topId = topId;
	}
	
	public void setStartingId(String startingId) {
	    this.startingId = startingId;
	}
	
	public void setTreeDocument(Document tree) {
	    this.tree = tree;
	}
	
	public void setRootOpen(boolean rootOpen) {
	    this.rootOpen = rootOpen;
	}
	
	public void setAllOpen(boolean allOpen) {
	    this.allOpen = allOpen;
	}
	
	public void setShowImages(boolean showImages) {
	    this.showImages = showImages;
	}
		
	public void setFlat(boolean flat) {
	    this.flat = flat;
	}
	
	public void setNodeOpen(String nodeOpen) {
	    this.nodeOpen = nodeOpen;
	}
	
	public void setHighlightNode(String highlightNode) {
	    this.highlightNode = highlightNode;
	}
	
	public void setMultiSelect(Collection multiSelect) {
	    this.multiSelect = multiSelect;
	}
	
	public void setMultiSelectPrefix(String multiSelectPrefix) {
	    this.multiSelectPrefix = multiSelectPrefix;
	}
	
	public void setSingleSelect(String singleSelect) {
	    this.singleSelect = singleSelect;
	}
	public void setSingleSelectName(String singleSelectName) {
	    this.singleSelectName = singleSelectName;
	}
	public void setDisplayStyle(String displayStyle) {
	    this.displayStyle = displayStyle;
	}
	
	public void setShowIdRoutine(String showIdRoutine) {
	    this.showIdRoutine = showIdRoutine;
	}
	
	public void setNowrap(boolean nowrap) {
	    this.nowrap = nowrap;
	}
	
	public void setInitOnly(boolean initOnly) {
	    this.initOnly = initOnly;
	}
	
	public void setNoInit(boolean noInit) {
	    this.noInit = noInit;
	}
	
	public void setCommonImg(String commonImg) {
	    this.commonImg = commonImg;
	    this.images = new HashMap();
	    this.imagesOpen = new HashMap();
	    
	    //Common tree icons
	    images.put("root", "/trees/root.gif");
	    images.put("spacer", "/trees/spacer.gif");
	    images.put("line", "/trees/line.gif");
	    images.put("join", "/trees/join.gif");
	    images.put("join_bottom", "/trees/join_bottom.gif");
	    images.put("minus", "/trees/minus.gif");
	    images.put("minus_top", "/trees/minus_top.gif");
	    images.put("minus_bottom", "/trees/minus_bottom.gif");
	    images.put("minus_top_bottom", "/trees/minus_top_bottom.gif");
	    images.put("plus", "/trees/plus.gif");
	    images.put("plus_top", "/trees/plus_top.gif");
	    images.put("plus_top_bottom", "/trees/plus_top_bottom.gif");
	    images.put("plus_bottom", "/trees/plus_bottom.gif");
	    
	    //Container icons
	    images.put("admin_tools", "/trees/admin_tools.gif");
	    images.put("calendar", "/trees/calendar.gif");
	    images.put("discussion", "/trees/discussion.gif");
	    images.put("folder", "/trees/folder.gif");
	    imagesOpen.put("folder", "/trees/folder_open.gif");
	    images.put("people", "/trees/people.gif");
	    images.put("tasks", "/trees/tasks.gif");
	    images.put("workspace", "/trees/workspace.gif");
	    
	    images.put("bullet", "/trees/bullet.gif");
	    images.put("contact", "/trees/contact.gif");
	    images.put("page", "/trees/page.gif");
		
		// File icons		
	    images.put("doc", "/document_library/doc.gif");
	    images.put("pdf", "/document_library/pdf.gif");
	    images.put("ppt", "/document_library/ppt.gif");
	    images.put("rtf", "/document_library/rtf.gif");
	    images.put("sxc", "/document_library/sxc.gif");
	    images.put("sxi", "/document_library/sxi.gif");
	    images.put("sxw", "/document_library/sxw.gif");
	    images.put("txt", "/document_library/txt.gif");
	    images.put("xls", "/document_library/xls.gif");
	    
	}
	
	private String getImage(String image) {
		if (image == null || image.equals("")) image = "page";
		if (image.contains(".")) {
			//This is a graphic, so just return it
			return this.commonImg + image;
		} else {
			return (this.images.containsKey(image)) ? this.commonImg + (String)this.images.get(image) : this.commonImg + (String) this.images.get("page");
		}
	}
	
	private String getImageOpen(String image) {
		if (image == null || image.equals("")) image = "page";
		if (image.contains(".")) {
			//This is a graphic, so just return it
			return this.commonImg + image;
		} else {
			return (this.imagesOpen.containsKey(image)) ? this.commonImg + (String)this.imagesOpen.get(image) : getImage(image);
		}
	}
	
	private String getBucketDisplay(String text) {
		String result = text;
		Pattern p = Pattern.compile("(\\w*)\\W");
		Matcher m = p.matcher(text);
		if (m.find()) {
			result = m.group(1);
		}
		return "<span onMouseOver=\"ss_showBucketText(this, '" + text + "');\" onMouseOut=\"ss_hideBucketText();\" >" + result + "</span>";
	}

   private static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }

	public void setFixedMultiSelectParamsMode(boolean fixedMultiSelectParamsMode) {
		this.fixedMultiSelectParamsMode = fixedMultiSelectParamsMode;
	}

}
