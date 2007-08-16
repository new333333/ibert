<%
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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
/* *************************************************************
   *    css_theme_iccg       ICEcore Cool Green
   **************************************************************/
<% //FONT STYLES %>
	<c:set var="ss_style_font_family" value="Lucida Sans Unicode, Arial, sans-serif" scope="request"/>
<% // Do NOT change ss_style_folder_view_font_family without a very good reason. %>
	<c:set var="ss_style_folder_view_font_family" value="Arial, sans-serif" scope="request"/>
	<c:set var="ss_style_title_font_family" value="Arial, Helvetica, sans-serif" scope="request"/>

<% //FONT SIZES %>
	<c:set var="ss_style_font_size" value="12px" scope="request"/>
	<c:set var="ss_style_font_finestprint" value="9px" scope="request"/>
	<c:set var="ss_style_font_fineprint" value="10px" scope="request"/>
	<c:set var="ss_style_font_smallprint" value="11px" scope="request"/>
	<c:set var="ss_style_font_normalprint" value="12px" scope="request"/>
	<c:set var="ss_style_font_largeprint" value="13px" scope="request"/>
	<c:set var="ss_style_font_largerprint" value="14px" scope="request"/>
	<c:set var="ss_style_font_largestprint" value="15px" scope="request"/>
	<c:set var="ss_style_font_input_size" value="11px" scope="request"/>

	<c:set var="ss_style_brightest" value="1.0" scope="request"/>
	<c:set var="ss_style_brighter" value="0.8" scope="request"/>
	<c:set var="ss_style_bright" value="0.7" scope="request"/>
	<c:set var="ss_style_dim" value="0.6" scope="request"/>
	<c:set var="ss_style_very_dim" value="0.4" scope="request"/>
	
<% //ACCESS CONTROL TABLE %>
	<c:set var="ss_table_font_family" value="Lucida Sans Unicode, Arial, Helvetica, sans-serif" scope="request"/>
	<c:set var="ss_table_background_color_background" value="#FFFFFF" scope="request"/>
	<c:set var="ss_table_background_color_head" value="#CCCCCC" scope="request"/>
	<c:set var="ss_table_background_color_odd_row" value="#ECECEC" scope="request"/>
	<c:set var="ss_table_background_color_even_row" value="#FFFFFF" scope="request"/>
	<c:set var="ss_table_background_color_row_hover" value="#FFAAFF" scope="request"/>
	
<% //BACKGROUND COLORS %>
	<c:set var="ss_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_background_color_side_panel" value="#EAF7DD" scope="request"/>
	<c:set var="ss_style_background_color_side_panel_featured" value="#E9EDF3" scope="request"/>
    <c:set var="ss_style_background_color_opaque" value="#FAFAFA" scope="request"/>
	<c:set var="ss_style_component_toolbar_background_color" value="#ECECEC" scope="request"/>
	<c:set var="ss_blog_footer_color" value="#EAF7DD" scope="request"/>

<% //BLOG %>
	<c:set var="ss_blog_summary_title_background_color" value="#ADD6AD" scope="request"/>
	<c:set var="ss_blog_content_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_blog_sidebar_background_color" value="#CECECE" scope="request"/>
	<c:set var="ss_blog_sidebar_box_outline" value="#ADD6AD" scope="request"/>
	
<% //BORDER COLORS %>
	<c:set var="ss_style_border_color" value="#999999" scope="request"/>
	<c:set var="ss_style_border_color_light" value="#CCCCCC" scope="request"/>
	<c:set var="ss_style_text_color" value="#333333" scope="request"/>
    <c:set var="ss_style_gray_color" value="#333333" scope="request"/>
    <c:set var="ss_style_light_color" value="#999999" scope="request"/>
    <c:set var="ss_style_border_color_dark_hue" value="#ADD6AD" scope="request"/>
	
<% //BOX	 %>
	<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
	<c:set var="ss_box_title_color" value="#990099" scope="request"/>
	<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>
	
<% //BUTTON %>
	<c:set var="ss_linkbutton_background_color" value="#DDF0DD" scope="request"/>
	<c:set var="ss_linkbutton_outline_color" value="#77AA77" scope="request"/>
	<c:set var="ss_linkbutton_text_color" value="#333333" scope="request"/>
	<c:set var="ss_linkbutton_link_hover_color" value="#666666" scope="request"/>
	<c:set var="ss_linkbutton_border_color_in" value="#77AA77" scope="request"/>
	<c:set var="ss_linkbutton_border_color_out" value="#666666" scope="request"/>
	
<% //CALENDAR %>
	<c:set var="ss_calendar_today_background_color" value="#ffe8ff" scope="request"/>
	<c:set var="ss_calendar_notInView_background_color" value="#f7f7f7" scope="request"/>
	
<%	 //MENU %>
	<c:set var="background_light" value="#E9EDF3" scope="request"/>
	
	

<%--
  --
  -- SIDEBAR / ACCESSORY COLORS
  --
--%><c:set var="ss_panel_header_bar_title_color" value="#666666" scope="request"/><%--
--%><c:set var="ss_dashboard_table_border_color" value="green" scope="request"/><%--
--%><%--
  --
  -- ENTRIES
  --
--%>
	<c:set var="ss_entry_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_entry_description_background_color" value="#E8F7EF" scope="request"/>
	<c:set var="ss_entry_description_border_color" value="#FFFFFF" scope="request"/>
	
<% //FORMS	 %>
	<c:set var="ss_form_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_component_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_form_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
	<c:set var="ss_form_text_color" value="#333333" scope="request"/>
    <c:set var="ss_form_gray_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_form_element_color" value="#ECECEC" scope="request"/>
	<c:set var="ss_form_element_text_color" value="#333333" scope="request"/>
	<c:set var="ss_form_element_readonly_color" value="InfoBackground" scope="request"/>
	<c:set var="ss_style_text_field_background_color" value="#FFEECC" scope="request"/> 
	<c:set var="ss_style_text_field_border_color" value="#F0C0E0" scope="request"/>
	
<% //FOOTER COLORS %>
    <c:set var="ss_style_footer_text_color" value="green" scope="request"/>
    <c:set var="ss_style_footer_font" value=" normal 11px Arial, Helvetica" scope="request"/>
    
<% //GALLERY %>
	<c:set var="ss_gallery_background_color" value="#F0F0F0" scope="request"/>
	<c:set var="ss_gallery_image_background_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_gallery_anchor_color" value="#333333" scope="request"/>
	<c:set var="ss_gallery_anchor_hover_color" value="#003782" scope="request"/>
	<c:set var="ss_gallery_rule_color" value="#CCCCCC" scope="request"/>
	
<% //GUESTBOOK %>
	<c:set var="ss_guestbook_rule_color" value="#AFC8E3" scope="request"/>
	
<% //HELP COLORS %>
	<c:set var="ss_help_spot_background_color" value="#E8F7EF" scope="request"/>
	<c:set var="ss_help_panel_background_color" value="#ffffff" scope="request"/>
	<c:set var="ss_lightBox_background_color" value="#ECECEC" scope="request"/>	
				
<% //HEADER COLORS	 %>
    <c:set var="ss_style_header_bar_background" value="#DBE6F2" scope="request"/>
    <c:set var="ss_style_header_bar_title_color" value="#333333" scope="request"/>
    <c:set var="ss_style_header_bar_title_link_color" value="#333333" scope="request"/>	

<% //ICON LABEL COLORS	 %> 
	<c:set var="ss_style_header_bar_background" value="#DBE6F2" scope="request"/>

<%  //LINK COLORS   %>
	<c:set var="ss_style_link_color" value="#333333" scope="request"/>
	<c:set var="ss_style_link_visited_color" value="#333333" scope="request"/>	
	<c:set var="ss_style_link_hover_color" value="#77AA77" scope="request"/> 	

<%  //LOGO   %>
 	<c:set var="ss_logo_text" value="#BE9E83" scope="request"/>
 		
<% //METADATA COLORS %>

    <c:set var="ss_style_metadata_color" value="#666666" scope="request"/>   
    
<% //MUTED %>
	<c:set var="ss_style_muted_foreground_color" value="#333333" scope="request"/>
	<c:set var="ss_style_muted_label_color" value="#666666" scope="request"/>
	<c:set var="ss_style_muted_tag_color" value="#008237" scope="request"/>
	
			
<% //PORTLET COLORS	 %>
	<c:set var="ss_portlet_style_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_portlet_style_text_color" value="#333333" scope="request"/>
	<c:set var="ss_portlet_style_inherit_font_specification" value="false" scope="request"/> 
	
<% //PROFILE COLORS	 %>
	<c:set var="ss_profileBox1_background_color" value="#DFFFD5" scope="request"/>
	
	
<% //SLIDING TABLE %>
	<c:set var="ss_sliding_table_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_sliding_table_text_color" value="#333333" scope="request"/>
	<c:set var="ss_sliding_table_link_hover_color" value="#BBBBBB" scope="request"/>
	<c:set var="ss_sliding_table_row0_background_color" value="#FFFFFF" scope="request"/>
	<c:set var="ss_sliding_table_row1_background_color" value="#DBF2E6" scope="request"/>

<% //TAG %>
	<c:set var="ss_tag_color" value="#996699" scope="request"/>
	<c:set var="ss_tag_pane_background_color" value="transparent" scope="request"/>

	<c:set var="ss_diff_color_added" value="#FFAAFF" scope="request"/>
	<c:set var="ss_diff_color_deleted" value="red" scope="request"/>
	<c:set var="ss_diff_color_same" value="lightblue" scope="request"/>	

<% //TOOLBARs %>

    <c:set var="ss_toolbar_background_color" value="#DBDBDB" scope="request"/>
    <c:set var="ss_toolbar_text_color" value="#000000" scope="request"/>
    <c:set var="ss_toolbar_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
    <c:set var="ss_toolbar_border_color" value="#ADD6AD" scope="request"/>
    <c:set var="ss_toolbar_dropdown_menu_color" value="#666666" scope="request"/>
    <c:set var="ss_toolbar_inactive" value="#999999" scope="request"/>
    
	<c:set var="ss_toolbar1_background_color" value="#ADD6AD" scope="request"/>
	<c:set var="ss_toolbar1_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar1_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar1_border_color" value="#3366CC" scope="request"/>
	<c:set var="ss_toolbar1_dropdown_menu_color" value="#ADD6AD" scope="request"/>
	<c:set var="ss_toolbar1_inactive" value="#BFCA8A" scope="request"/>


	<c:set var="ss_toolbar2_background_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_toolbar2_text_color" value="#333333" scope="request"/>
	<c:set var="ss_toolbar2_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
	<c:set var="ss_toolbar2_border_color" value="#33CC66" scope="request"/>
	<c:set var="ss_toolbar2_dropdown_menu_color" value="#666666" scope="request"/>
	<c:set var="ss_toolbar2_inactive" value="#999999" scope="request"/>

	<c:set var="ss_toolbar4_background_color" value="#E9EDF3" scope="request"/>
	<c:set var="ss_toolbar4_text_color" value="#666666" scope="request"/>
	<c:set var="ss_toolbar4_link_hover_color" value="#E8F7EF" scope="request"/>
	<c:set var="ss_toolbar4_border_color" value="#DBF2E6" scope="request"/>
	<c:set var="ss_toolbar4_dropdown_menu_color" value="#DBF2E6" scope="request"/>
	<c:set var="ss_toolbar4_inactive" value="#333333" scope="request"/>
	
	<c:set var="ss_folder_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_folder_line_highlight_color" value="#CCCCCC" scope="request"/>	

	<c:set var="ss_page_nav_background_color" value="#AABBAA" scope="request"/>		

	<% //REPLYS %>
	<c:set var="ss_replies_background_color" value="#DBF2E6" scope="request"/>
	<c:set var="edit_text_color" value="#33CC33" scope="request"/>
	
<% //TITLE	 %>
	<c:set var="ss_title_line_color" value="#008237" scope="request"/>

<% //TREE	 %>
	<c:set var="ss_tree_highlight_line_color" value="#996699" scope="request"/>

<% //??	 %>
	<c:set var="ss_generic_border_color" value="#CCCCCC" scope="request"/>
	<c:set var="ss_generic_border_shadow_color" value="#666666" scope="request"/>

<% //?? %>
	<c:set var="ss_style_drop_highlight" value="#FFFFFF" scope="request"/>
	<c:set var="ss_style_drop_shadow" value="#666666" scope="request"/>