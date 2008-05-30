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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

/* CSS Document - container for two column pages */
	

/* 2 COLUMN PAGE STYLE SETTINGS */	

.ss_doublecolumn{
	background-color:#fff;
	text-align:left;
	}
#ss_column_L{
	/* holder for left column */
	}
#ss_column_R{
	/* holder for left column */
	height:100%;
	}	
.ss_doublecolumn .ss_colleft{		/* right column width */
	right:49%;		
	}
.ss_doublecolumn .ss_col1{
	width:49%;						/* left column content width (column width minus left and right padding) */
	left:50%;						/* (right column width) plus (left column padding) */
	background-color:#fff;			/* left column background color */
	}	
.ss_doublecolumn .ss_col2{
	width:49%;						/* right column content width (column width minus left and right padding) */
	left:51%;						/* (right column column width) plus (left column left and right padding) plus (right column left padding) */
	overflow:hidden;
	}	
	
/* TOP PART OF DISCUSSION TOPIC PAGE */	
	
	#ss_diss_inset
	{
		margin:0% 5%;
		width:90%;
		text-align:left;

	}
	#ss_diss_top
	{
		clear:both;
		float: left;
		width: 100%;
		overflow: hidden;
		color: #526394;
		font-family: Arial, Helvetica, sans-serif;

	}
		
	#ss_topic_box { 
		margin:1%;
		margin-bottom:2em;
		border: 1px #29447B solid; 
		padding: 0% 10px 2% 10px;
		font-size:10px;
		font-family: 'Lucida Sans', 'Lucida Grande', sans-serif;
		text-align:left;
		vertical-align:top;
		background-color:#DEE7E7;
		}
		
	#ss_topic_box h1
	{
		padding-top:0px;
		padding-bottom:4px;
		font-size: 20px;
		font-style: italic;
		font-weight: bold;
	}
	
	#ss_topic_folder
	{
		margin-left:1%;
		padding-top:0px;
		padding-bottom:4px;
		font-size: 16px;
		font-style: italic;
		font-weight:bold;
		color: #00ADef;
		text-align:left;
	}
	
	.ss_topic_folder_thread
	{	
		color: #526394;
		font-size:14px;
		font-style:normal;
	}

/**********************************/	

/* DISCUSSION TOPIC FOLDER */	

#ss_folder_inset
	{
		margin:0% 5%;
		width:90%;
		text-align:left;

	}

#ss_topic
{
	padding: 0;
	margin: 2% 0px 2% 5px;
	color:#333333;
	font-family: Arial, Helvetica, sans-serif;

}
#ss_topic_title
{
	padding-left: 28px;
	padding-top: 2px;
	padding-bottom: 3px;
	font-size:14px;
	font-weight:bold;
	font-style:italic;
	font-family: 'Lucida Sans', 'Lucida Grande', sans-serif;	
}

.ss_title_th1
{
	color:#00ADef!important;
	font-size:14px;
	font-weight:bold;
}
#ss_topic_title span
{
	color:#333333;!important;
	font-size:14px;
	font-weight:bold;
}
#ss_topic_nav
{
	text-align:left;
	margin:1%;

	}
.ss_disc_next 
{	
	background-color:#6B737B;
	color:#FFFFFF;
	font-weight:bold;
	font-size:10px;
	}
/* TOPIC FOLDER THEMES */

.ss_disc_th1
{
	background-image: url("<html:rootPath/>images/pics/discussion/blue_topic.png");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #00ADef;
}
.ss_disc_folder_th1
{
	background-image: url("<html:rootPath/>images/icons/folder_blue.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #00ADef!important;
}

.ss_disc_th2
{
	background-image: url("<html:rootPath/>images/novell/icons/workspace.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #EF9418;
}

#ss_topic_desc{
	font-size:11px;
	line-height:13px;
	padding-left:40px;
	padding-right:2px;
	padding-bottom:5px;
	color:#333333;
	margin-right:5px;
	margin-bottom:5px;
	}
#ss_topic_thread{
	font-size:11px;
	line-height:13px;
	padding-left:40px;
	padding-right:2px;
	padding-bottom:0px;
	color:#666666;
	margin-right:5px;
	margin-bottom:2px;
	}	
#ss_topic_thread a
{
	font-size: 11px;
	line-height: 13px;
	font-weight: bold;
	padding-right: 2px;
	padding-bottom: 0px;
	color: #639;
	margin-right: 5px;
	margin-bottom: 2px;
}		

/**********************************/

/*  DISCUSSION TOPIC TABLE STYLES */

.ss_table_data
{
	font-size:10px;
	border-color:#526394;
	border-width:1px;
	border-style:solid;
}
.ss_topic_status {
	font-size:10px;
	font-weight:bold;
	border-style:hidden;
	border:1px solid #ffffff;
	border-bottom:1px solid #526394;
	}	
.ss_topic_table   {
 	border-collapse: collapse;
	font-size:10px;
	border-color:#526394;
	border-width:1px;
	border-style:solid;
	}	
	/* DISCUSSION THREAD LINKS */
.ss_link_6, .ss_link_6 span {
	text-decoration: underline!important;
	color:#333333!important;
	font-size:10px!important;
	}

/****  DISCUSSION THREAD IMAGES ********/
	
	a.ss_new_thread img {
	width: 12px;
	height: 12px;
	margin: 0px;
	padding: 0px 0 5px 3px;
	border: 0px;
	vertical-align: text-bottom;
}

a.ss_new_thread img, a.ss_new_thread:link img , a.ss_new_thread:focus img, a.ss_new_thread:visited img { 
	background: transparent; 
}

a.ss_new_thread:hover img {
    background-position:  left -12px; 
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
a.ss_new_thread:hover {
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
.ss_nowrapFixed {
	white-space:normal!important;
	}
.ss_sliding {
	white-space:nowrap!important;
	}	
#ss_configureCol {
	margin-left:20px;
	padding-bottom:10px;
	}
/**********************************/