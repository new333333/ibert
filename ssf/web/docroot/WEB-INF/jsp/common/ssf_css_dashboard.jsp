<%
/* *
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

/* CSS Document - container for navigation and search */
	
	/* COLUMN CONTAINER */
#ss_dashboard_content {
/*	position:relative;  Needed at one time, but breaks view in IE6 
	clear:both;
	float:left;	*/
	vertical-align:top;
    /* width:100%;	 width of whole page */
	overflow:hidden; /* This chops off any overhanging divs */
	}

/* COMMON COLUMN SETTINGS */
.ss_colright,.ss_colmid,.ss_colleft{
	float:left;
	width:100%;  /* width of page */
	position:relative;
	}
.ss_col1,.ss_col2,.ss_col3 {
	float:left;
	position:relative;
	padding:0 0 1em 0; /* no left and right padding on columns, we just make them narrower instead 
	only padding top and bottom is included here, make it whatever value you need */
	overflow:hidden;
	}	
/* 3 COLUMN PAGE STYLE SETTINGS */
.ss_tricolumn{	/* 3 Column page style */
	background-color: #FFFFFF; /* right column background color */
	} 
.ss_tricolumn .ss_colmid{
	right: 21%;
	/* width of the right column */
	/* Need to change this to change background color */
	background-color: transparent; /* was #FFFFFF */
	}
.ss_tricolumn .ss_colleft {
	right:20%;				/* placement from right of the middle column */
	}
.ss_tricolumn .ss_col1 {
	width:38%;				/* width of center column content (column width minus padding on either side) */
	left:42%;				/* 100% plus left padding of center column */
	<c:if test="<%= isIE %>">
 		width:37%;
 	</c:if>	
	}
.ss_tricolumn .ss_col2 {
	width:38%;				/* Width of left column content (column width minus padding on either side) */
	left:43%;				/* width of (right column) plus (center column left and right padding) plus (left column left padding) */
	}
.ss_tricolumn .ss_col3 {
	width:20%;				/* Width of right column content (column width minus padding on either side) */
	left:44%;				/* (100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	background-color: #DBE9E8;
	padding: 5px 7px 0px 3px;
	}

div.ss_canvas {
	border-left: solid 1px ${ss_toolbar1_background_color};
	border-right: solid 1px ${ss_toolbar1_background_color};
	border-bottom: solid .3px ${ss_toolbar1_background_color};
	padding-bottom: 0px;
	margin-bottom: 10px;
	background-color: ${ss_style_background_color_side_panel};
	}
#ss_wrap{
	margin: auto;
	min-height:100%;
/*	position:relative;		 Needed at one time, but breaks view in IE6 */
	clear:both;
    width:100%;			/* width of whole page */
	overflow:hidden;	/* This chops off any overhanging divs */
	text-align:left;
	}	
		
/*  THE FOLLOWING DEFINES ALL THE IMAGES FOR THE Title BARS */
	
.ss_tasks_img {
	background-image: url("<html:rootPath/>images/pics/check16x22.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_ping_img {
	background-image: url("<html:rootPath/>images/pics/im_16x16.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}
.ss_cal_img {
	background-image: url("<html:rootPath/>images/pics/calendar16x16_2.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}		
.ss_email_img {
	background-image: url("<html:rootPath/>images/pics/mailicon.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_bookmarks_img {
	background-image: url("<html:rootPath/>images/pics/bookmarks16x16.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_tracked_img {
	background-image: url("<html:rootPath/>images/pics/tracking20x16.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}	
.ss_recentfolder_img {
	background-image: url("<html:rootPath/>images/pics/folder_blue_recent.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}			
/* BOX STYLES*/	

#ss_Box{
	padding: 0;
	margin: 1% 0 2% 0;
	}	
	/*SHARED*/
#ss_networkupdates{
	padding: 0;
	margin: 2% 0 2% 0;
	color:#444444;
	}
#ss_shared{
	margin: 0% 0 5% 0;
	<c:if test="<%= isIE %>">
 	margin: 3px 0 5% 0;
 	</c:if>	
	color:#444444;
	line-height:1.6em;
	}
.ss_shared_para {
	line-height:1.6em;
	padding-top:5px;
	padding-left:1%;
	}
 .ss_shared_para li{
	list-style-type: square;
	padding-bottom:5px;
	}
#ss_shared_para li{
	list-style-type: square;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;	
	}
 	/*TASKS*/
#ss_tasks{
	margin: 1% 0 5% 0;
	padding: 0.5% 0 15px;
	color: #666666;
	line-height: 1.6em;
	}
#ss_tasks_para {
	line-height:1.6em;
	padding:5px 0 0 15px;
	}
#ss_tasks_para .ss_link_1 a {
	text-decoration:underline!important;
	color: #00AEEF!important; 
	}
#ss_tasks_para .ss_link_2 a {
	text-decoration:underline!important;
	color:#4AAA42!important;
	font-size:10px!important; 
	}
#ss_tasks_para {
	list-style-type: square!important;
	padding-top:5px;
	}
	/*COMMON STYLE ITEMS*/	
#ss_hints{
	font-size: 12px;
	line-height: 13px;
	padding-right: 2px;
	padding-bottom:5px;
	color:#f63;
	margin-right: 5px;
	margin-bottom: 5px;
	font-family:'Lucida Sans','Lucida Grande',sans-serif;
	font-style: italic;
	}
#ss_nextPage{			/* to use when need to display left right arrows */
	border-bottom:dotted 1px #5A9A98;
	margin:5px;
	padding-right: 5px;
	
	}
#ss_col3_para {			/* to use for 3rd column displays */
	padding-top:5px;
	margin:5px 0 5px 4px;
	}			
	/*TRACKED ITEMS*/	
#ss_trackedItems{
	margin: 0% 0 2% 0;
	color:#555;
	<c:if test="<%= isIE %>">
 		margin: 0.5% 0 2% 0;
 	</c:if>	
	}
#ss_trackedPeople{
	color:#555;
	background-color:transparent;
	line-height: 1.6em;
	}
#ss_im_status{
	font-size: 11px;
	line-height: 13px;
	padding-left:4px;
	padding-bottom:1px;
	border: 1px dotted #555;
	color:#555;
	background: #FFF;
	margin-bottom: 1.4em;
	}
.ss_im_status_active{
	font-weight: bold;
	background: #FFFBD6!important;	
	}					
	/*CALENDAR*/
#ss_calendar{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	padding: 0.5% 0 0px;
	color: #555555;
	line-height: 1.5em;
	}
#ss_visit_para {
	line-height:1em;
	padding-top:5px;
	}
#ss_visit_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #555555;}
	
	/* ACTIVE LINKS  VISITOR */
#ss_visit_para a{
	text-decoration:underline;
}
	/* HOVER LINKS */
#ss_visit_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	
	/*VIEWED ITEMS*/
#ss_viewedItems {
	line-height:1.2em;
	}
#ss_viewedItems .ss_v_entries ul{
	padding: 0;
	margin-left: 4px;
	}
#ss_viewedItems .ss_v_entries li{
	background-image: url("<html:rootPath/>images/pics/entry_icon.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	padding-left: 30px;
	padding-top: 3px;
	padding-bottom: 1.6em;
	border-bottom:1px dotted #5A9A98;
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}
#ss_viewedItems .ss_v_attachments ul {
	padding: 0;
	margin-left: 4px;
	}
#ss_viewedItems .ss_v_attachments li {
	background-image: url("<html:rootPath/>images/pics/attachment_icon.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	padding-left: 30px;
	padding-top: 3px;
	padding-bottom: 1.6em;
	border-bottom:1px dotted #5A9A98;
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}	
	/* ACTIVE LINKS - VIEWED ITEMS*/	
#ss_viewedItems a{
	text-decoration:none;
	}
	/* ACTIVE HOVER LINKS */
#ss_viewedItems a:hover{
	text-decoration:underline;
	color:#555 !important;
	background-color:#CCCCCC !important;
	}
	/*CALENDAR*/
#ss_cal_para{
	padding: 5px 0 0 15px;
	}
#ss_cal_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #5A9A98;
	}
	/* CALENDAR ACTIVE LINKS */
#ss_cal_para a{
	text-decoration:underline;
	}
	/* CALENDAR HOVER LINKS */
#ss_cal_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	/*MY DOCUMENTS*/
#ss_mydocs_para {
	line-height:1.2em;
	padding:5px 0 0 15px;
	}
#ss_mydocs_para li{
	list-style-type: square;
	padding-bottom:0px;
	border-bottom:1px dotted #5A9A98;
	padding-bottom: 1.6em;
	}
	/* ACTIVE LINK */
#ss_mydocs_para a {					
	text-decoration:underline;
	}
	/* HOVER LINK */
#ss_mydocs_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	/* WHATS HOT */
#ss_whatshot{
	margin: 1% 0 5% 0;
	padding: 0% 0 1% 0;
	color: #666666;
	line-height: 1.6em;
	}
#ss_hot_para {
	line-height:1.2em;
	padding-top:1px;
	}
#ss_hot_para li{
	list-style-type: square;	
	border-bottom:1px dotted #5A9A98;
	margin:0pt 0pt 0pt 15px;
	padding-bottom: 1.6em;
	}	
	
	/* SURVEY*/
#ss_survey{
	margin: 1% 0 5% 0;
	color:#555;
	border:1px solid #5A9A98;
	}
	
	/*TAGS*/
#ss_tags{
	font-size: ${ss_style_font_normalprint};
	color: #CCCCCC !important;
	line-height: 18px;
	margin-left: 4px !important;
	margin-bottom: 10px;
	background: #FFF;
	border: dotted 1px #5A9A98;
	}
	/* NOTES */
#ss_notes{
	margin: 1% 0 5% 0;
	color:#555;
	border:1px solid #5A9A98;
	}
	/* BOOKMARKS */
#ss_bookmarks{
	margin: 1% 0 5% 0;
	color:#555;
	}
	/* ACTIVITIES */
.ss_activity{
	margin: 1% 1% 5% 15px;
	color:#555;
	}
	/* WHATS NEW */
#ss_whatsnew{
	margin: 2% 0 5% 0;
	color:#555;
	}
	/* PERSONAL TRACKER */
#ss_personaltracker{
	margin: 1% 0 5% 0;
	color:#555;
	border:1px solid #A7A9AC
	}
	/* EMAIL */
#ss_email{
	margin: 1% 0 5% 0;
	color:#555;
	font-family:  Arial, sans serif;
	line-height: 1.5em;
	padding: 0;
	}
	/* DOCUMENTS */
#ss_documents{
	margin: 1% 0 5% 0;
	color:#555;
	}
#ss_title{
	margin: 0px 0px 2px 0px;
	font-size: 16px;
	font-weight: 500;
	letter-spacing: -1px !important;
	color: #555;
	font-family: 'Lucida Sans','Lucida Grande',sans-serif;
	}
.ss_title_im{
	background: #FFFFFF;
	}	
#ss_subtitle{
	font-size:14px;
	color:#555555;
	padding: 0.5% 0 1% 6px;
	}

/*  BOX TITLES */
.ss_pt_title{
	color: #555;
	font-size:14px;
	padding: 2px 0px 2px 5px;
	width:100%;
	font-family:'Lucida Sans','Lucida Grande',sans-serif;
	font-style: italic;
	}
/*  BOX TITLE COLORS   */
.ss_blue{
	background-color: #CCD2E3;
	}
.ss_green{
	background-color: transparent;
	}
.ss_orange{	
	background-color: #F93;
	}

/* ******************************  */

.ss_pt_para { 	
	margin: 1%; 
	}
.ss_pt_para ul{
	color:#555555;
}
.ss_pt_para ul li{
	margin-left: -10px;
	list-style-type: disc;
	border-bottom:1px dotted #5A9A98;	
	}
.ss_prioValue{
	font-weight:bold;
	padding:0 3px 0 3px;
	}
/* BACKGROUND COLORS */	
.ss_paraC{
	background-color:transparent;
	padding-left: 15px;	
	}
.ss_paraD{                    
	background-color:transparent;
	padding-bottom: 15px;
	}	
/* TIMEBLOCK STYLES */	
#ss_today {
	margin: 0 0 0 0;
	}
#ss_yesterday{
	margin: 0 0 0 10px;
	}
#ss_lastweek{
	margin: 0 0 0 10px;
	}
/* COMPRESSED BOX*/	
 .ss_closed	{
	height:20px;
	}
 /* when a milestone is overdue */				
 .ss_overdue{
	color: #CC0000;
	font-style: italic;
	}	
	
	/* HIDE TOGGLE*/

.ss_toggle{
	font-size: ${ss_style_font_smallprint};
	display: block;
	padding-right: 5px;
	}
/* =================THESE NEED TO BE CONSOLIDATED======== */


/* to use when need to display summary of an entry */

.ss_summary	{
	font-style: italic;
	color: #555555;
	font-size: 11px;
	line-height: 12px;
	padding: 0;
	}
	
/* to use when need to display twitter status of person */
.ss_status{						
	font-style: italic;
	font-size: 11px;				
	line-height: 1.6em;
	padding: 5px 0 15px 20px;
}
#ss_para {
	border: 1px dotted #5A9A98;
	padding-left: 15px;
	padding-bottom: 15px;
	color: #555555;
	margin-top: 1%;
	line-height: 1.2em;
	}

#ss_para ul{
	color:#555555;
	}
#ss_para ul li{
	list-style-type: square;
	margin-left:-10px;
	}
#ss_para_activity {
	border: 1px dotted #5A9A98;
	padding-left: 15px;
	padding-bottom: 15px;
	color: #555555;
	margin-top: 1%;
	line-height: 1.6em;
	list-style-type: square;
	}
	
/* =========== LINK STYLES===================== */	

/* BACKGROUND HOVER IS FROM THIS CODE */

#ss_para a{
	text-decoration:none!important;
	font-weight: bold;
	color: #555555;
	}
#ss_para a:hover{
	color:#555!important;
	background:#CCC!important;
	}
#ss_para a:visited{
	color:#996699 !important;
	}	
#ss_para .ss_link_3 a:visited{
	color:#9966CC !important;
	}	
	/* PEOPLE LINKS */
.ss_link_1, .ss_link_1 span { 
	font-size:${ss_style_font_normalprint};
	font-style: italic;
	font-weight: 200;
	font-family:'Lucida Sans', 'Lucida Grande', sans-serif;
	letter-spacing:-1px;
	color:#5A9A98!important;
	}
	/* PLACES LINKS*/	
.ss_link_2, .ss_link_2 span	{
	text-decoration: underline!important;
	color:#777!important;
	font-size:${ss_style_font_smallprint}!important;
	}
	/* ENTRY LINKS*/	
.ss_link_3, .ss_link_3 span	{
	color:#555!important;
	font-size:${ss_style_font_normalprint}!important;
	font-weight: bold !important;
	}
.ss_link_4, .ss_link_4 span{
	text-decoration: none!important;
	color:#888!important;
	font-size:${ss_style_font_smallprint}!important;
	padding-left:6px;
	}
.ss_link_5, .ss_link_5 span	{
	text-decoration: underline!important;
	color:#888!important;
	font-size:${ss_style_font_smallprint}!important;
	line-height:14px;
	}
/*======THIS IS FOR THE DISCUSSION PAGE STYLE========*/
.ss_link_7, .ss_link_7 span {
	font-style: italic;
	font-weight: 400;
	font-family:'Lucida Sans', 'Lucida Grande', sans-serif;
	letter-spacing:-1px;
	color:#5A9A98!important;
	font-size:18px;
	}
.ss_link_7 a:hover {
	text-decoration: none;
	color:#999!important;
	}		
.ss_link_8, .ss_link_8 span{
	font-family: ${ss_style_folder_view_font_family};
	color:#5A9A98!important;
	font-size:${ss_style_font_normalprint};
	}
				
/* ============ End link styles =========================== */		


/* HEADER STYLES*/
  	
#ss_topper {
	height:150px;
	width:100%;
	}
#ss_BC_nav {
	background-color:#FFF;
	text-align:left;
	padding:3px 0px 3px 20px;
	width:100%;
	position:relative;
	float:left;			
	}
#ss_BC_nav li{
	text-decoration: none;
	display: inline;
	}	
#ss_toolbar_basic{
	position: relative;
	float: right;		
	text-align: right;
	background-color: #cccccc;
	padding: 7px 20px 3px 0px;
	width: 100%;
	height:18px;
	vertical-align: middle;
	color:#FFFFFF;
	}

/* - MENU TABS FOR RELEVANCE--------------------------- */

#ss_tabsC {
    float:left;
    width:100%;
    background:transparent;
    font-size:93%;
  /*  line-height:normal; */
    border-bottom: 1px solid #666;
    margin-top:0px;
 	<c:if test="<%= isIE %>">
 	    margin-top:20px !important;
 	</c:if>	    
      }
 #ss_tabsC ul { 
    padding:10px 10px 0 10px;
 	<c:if test="<%= isIE %>">
 	    padding:0px 10px 0px 10px;
 	</c:if>	
    list-style:none;
      }
#ss_tabsC li {
    display:inline;
    margin-top:0;
   	padding:0px !important;
      }
 #ss_tabsC a {
    float:left;
    background:url("<html:rootPath/>images/pics/tableftF.gif") no-repeat left top;
    margin:0;
    padding:0 0 0 4px;
      }
 #ss_tabsC a span {
    display:block;
    background:url("<html:rootPath/>images/pics/tabsrightf2.gif") no-repeat right top;
    padding:5px 15px 4px 6px;
    white-space:nowrap !important;

      }
 /* Commented Backslash Hack hides rule from IE5-Mac \ */
 
 #ss_tabsC a span {float:none;}
    /* End IE5-Mac hack */
#ss_tabsC a:hover span {
    color:#FFFFFF;
      }
#ss_tabsC a:hover {   
      background-position:0% -42px;
      }
#ss_tabsC a:hover span {
      background-position:100% -42px;
      }
#ss_tabsC .ss_tabsCCurrent a {
      background-position:0% -42px;
        }
#ss_tabsC .ss_tabsCCurrent a span {
     background-position:100% -42px;
        }	
/* PROFILE STYLES*/

.ss_profileData{
	vertical-align: bottom;	
	}
.ss_profileTitle{
	width: 40%;	
	}	

/* FOOTER STYLES*/

#ss_footer{
	float:left;
	overflow:hidden;
	background: #fff;
	position: relative;
	padding-bottom: 5px;
	width: 100%;
	margin: 10px auto auto auto;
	height: 60px;
	}	
		
/* HEIGHT OF THE FOOTER */
	
#ss_footer li{
	text-decoration: none;
	display: inline;
	}
#ss_footer a { 
	color: #C00;
	text-decoration:none;
	}
#ss_footer a:hover {
	color:#444444;
	text-decoration:none;
	background-color:#fff;
	}		
#ss_task_list{
	padding: 0;
	margin: 1% 0 2% 2%;
	color:#333333;
	border:1px solid #A7A9AC;
	}
#ss_task_list p{
	border-top: 1px solid #A7A9AC;
	padding:4px 5px 7px 5px;
	line-height:2em;
	}
#ss_task_list_hdr{
	background-color: #A7A9AC;
	color: #000000;
	font-size:12px;
	padding: 5px 5px 3px 2px;
	width:100%;
	border-right:1px solid #777777;
	margin:0 auto;
	}	
.ss_para_lists{
	margin-top: 1%;
	padding: 0.5% 1% 1% 5px;
	border: 1px dotted #cccccc;
	}	
.ss_para_lists ul{
	color:#555555;
	}	
.ss_para_lists ul li{
	list-style-type: square;
	margin-left:-10px;
	border-bottom:1px dashed #444444;
	line-height:2.5em;
	padding-bottom:10px;
	padding-right:1%;
	}
#ss_tracker{
	position:absolute;
	display:none;
	visibility:hidden;
	border:1px solid black;
	padding:10px;
	}
/* Calendar need some other settings */
#ss_calendar #ss_para, #ss_calendar .ss_paraC, #ss_calendar #ss_cal_para {
	padding: 0;
}

