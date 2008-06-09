<%
/**
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<style>
.ss_credits_title {
	padding-left:4px;
}
a:link {
	text-decoration:none !important;
}
a:hover {
	text-decoration:underline !important;
}
</style>

<div class="ss_portlet_style ss_portlet">
  <div class="ss_style">
  
  <div align="right">
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post" 
	  action="<portlet:renderURL windowState="normal"/>">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
	</form>
  </div>
	<ssf:expandableArea titleClass="ss_credits_title" title="ICEcore is brought to you by <a href=\"http://sitescape.com\">SiteScape, Inc.</a>">
		<style>
		<!--
		 /* Font Definitions */
		 @font-face
			{font-family:Wingdings;
			panose-1:5 0 0 0 0 0 0 0 0 0;}
		@font-face
			{font-family:Tahoma;
			panose-1:2 11 6 4 3 5 4 4 2 4;}
		@font-face
			{font-family:"Century Schoolbook";
			panose-1:2 4 6 4 5 5 5 2 3 4;}
		@font-face
			{font-family:"Times New Roman Bold";
			panose-1:0 0 0 0 0 0 0 0 0 0;}
		 /* Style Definitions */
		 p.MsoNormal, li.MsoNormal, div.MsoNormal
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		h1
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		h2
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:1.0in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;}
		h3
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:1.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;}
		h4
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			layout-grid-mode:line;
			font-weight:normal;}
		h5
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:2.0in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			layout-grid-mode:line;
			font-weight:normal;}
		h6
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:normal;
			font-style:italic;}
		p.MsoHeading7, li.MsoHeading7, div.MsoHeading7
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoHeading8, li.MsoHeading8, div.MsoHeading8
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-style:italic;}
		p.MsoHeading9, li.MsoHeading9, div.MsoHeading9
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:3.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:bold;
			font-style:italic;}
		p.MsoToc1, li.MsoToc1, div.MsoToc1
			{margin-top:6.0pt;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			text-transform:uppercase;}
		p.MsoToc2, li.MsoToc2, div.MsoToc2
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:12.25pt;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-variant:small-caps;}
		p.MsoToc3, li.MsoToc3, div.MsoToc3
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:6.0pt;
			margin-left:23.75pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc4, li.MsoToc4, div.MsoToc4
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc5, li.MsoToc5, div.MsoToc5
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:48.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc6, li.MsoToc6, div.MsoToc6
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:60.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc7, li.MsoToc7, div.MsoToc7
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:1.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc8, li.MsoToc8, div.MsoToc8
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:84.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoToc9, li.MsoToc9, div.MsoToc9
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:96.0pt;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoCommentText, li.MsoCommentText, div.MsoCommentText
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";}
		p.MsoHeader, li.MsoHeader, div.MsoHeader
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoFooter, li.MsoFooter, div.MsoFooter
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoEnvelopeAddress, li.MsoEnvelopeAddress, div.MsoEnvelopeAddress
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:2.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.MsoEnvelopeReturn, li.MsoEnvelopeReturn, div.MsoEnvelopeReturn
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";}
		p.MsoToaHeading, li.MsoToaHeading, div.MsoToaHeading
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		p.MsoTitle, li.MsoTitle, div.MsoTitle
			{margin-top:12.0pt;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:center;
			font-size:16.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		a:link, span.MsoHyperlink
			{color:blue;
			text-decoration:underline;}
		a:visited, span.MsoHyperlinkFollowed
			{color:purple;
			text-decoration:underline;}
		p.MsoCommentSubject, li.MsoCommentSubject, div.MsoCommentSubject
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:10.0pt;
			font-family:"Times New Roman";
			font-weight:bold;}
		p.MsoAcetate, li.MsoAcetate, div.MsoAcetate
			{margin:0in;
			margin-bottom:.0001pt;
			font-size:8.0pt;
			font-family:Tahoma;}
		p.1-2-3Bold, li.1-2-3Bold, div.1-2-3Bold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBold, li.1-2-3NoBold, div.1-2-3NoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBoldD, li.1-2-3NoBoldD, div.1-2-3NoBoldD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.1-2-3NoBoldL, li.1-2-3NoBoldL, div.1-2-3NoBoldL
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.a-b-c-Bold, li.a-b-c-Bold, div.a-b-c-Bold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.a-b-c-NoBold, li.a-b-c-NoBold, div.a-b-c-NoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.A-B-CBold, li.A-B-CBold, div.A-B-CBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.A-B-CNoBold, li.A-B-CNoBold, div.A-B-CNoBold
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock, li.hkBlock, div.hkBlock
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1, li.hkBlock1, div.hkBlock1
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1D, li.hkBlock1D, div.hkBlock1D
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1J, li.hkBlock1J, div.hkBlock1J
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlock1JD, li.hkBlock1JD, div.hkBlock1JD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockD, li.hkBlockD, div.hkBlockD
			{margin:0in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockJ, li.hkBlockJ, div.hkBlockJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBlockJD, li.hkBlockJD, div.hkBlockJD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBod, li.hkBod, div.hkBod
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodD, li.hkBodD, div.hkBodD
			{margin:0in;
			margin-bottom:.0001pt;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodJ, li.hkBodJ, div.hkBodJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:justify;
			text-indent:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkBodJD, li.hkBodJD, div.hkBodJD
			{margin:0in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHang, li.hkHang, div.hkHang
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangD, li.hkHangD, div.hkHangD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-indent:-.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangJ, li.hkHangJ, div.hkHangJ
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			text-indent:-.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkHangJD, li.hkHangJD, div.hkHangJD
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			text-indent:-.5in;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hknotaryblk, li.hknotaryblk, div.hknotaryblk
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuote, li.hkQuote, div.hkQuote
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuote1, li.hkQuote1, div.hkQuote1
			{margin-top:0in;
			margin-right:1.0in;
			margin-bottom:12.0pt;
			margin-left:1.0in;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteD, li.hkQuoteD, div.hkQuoteD
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteJ, li.hkQuoteJ, div.hkQuoteJ
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:12.0pt;
			margin-left:.5in;
			text-align:justify;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkQuoteJD, li.hkQuoteJD, div.hkQuoteJD
			{margin-top:0in;
			margin-right:.5in;
			margin-bottom:0in;
			margin-left:.5in;
			margin-bottom:.0001pt;
			text-align:justify;
			line-height:200%;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkSign, li.hkSign, div.hkSign
			{margin-top:30.0pt;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkSigTxt, li.hkSigTxt, div.hkSigTxt
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:0in;
			margin-left:3.0in;
			margin-bottom:.0001pt;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";}
		p.hkTitleC, li.hkTitleC, div.hkTitleC
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			text-align:center;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman Bold";
			font-weight:bold;
			text-decoration:underline;}
		p.hkTitleL, li.hkTitleL, div.hkTitleL
			{margin-top:0in;
			margin-right:0in;
			margin-bottom:12.0pt;
			margin-left:0in;
			page-break-after:avoid;
			font-size:12.0pt;
			font-family:"Times New Roman";
			text-decoration:underline;}
		 /* Page Definitions */
		 @page Section1
			{size:8.5in 11.0in;
			margin:1.0in 1.0in 1.0in 1.0in;}
		div.Section1
			{page:Section1;}
		 /* List Definitions */
		 ol
			{margin-bottom:0in;}
		ul
			{margin-bottom:0in;}
		-->
		</style>
		
		<div class=Section1>
		
		
		<p class=MsoNormal align=center style='text-align:center'><b>&nbsp;</b></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><span
		style='font-size:11.0pt;font-family:Arial'>SITESCAPE, INC.� </span></b></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><span
		style='font-size:11.0pt;font-family:Arial'>END USER LICENSE AGREEMENT</span></b></p>
		
		<p class=MsoNormal><span style='font-size:11.0pt;font-family:Arial'>&nbsp;</span></p>
		
		<p class=MsoNormal align=center style='text-align:center'><b><i><span
		style='font-size:11.0pt;font-family:Arial'>This is a legal document - retain
		for your records</span></i></b></p>
		
		<p class=hkBod><span style='font-size:11.0pt;font-family:Arial'>&nbsp;</span></p>
		
		<p class=hkTitleC align=left style='text-align:left'><span style='font-size:
		11.0pt;font-family:Arial'>Important Notice to User:� Read Carefully </span></p>
		
		<p class=hkTitleC><span style='font-size:11.0pt;font-family:Arial;font-weight:
		normal;text-decoration:none'>&nbsp;</span></p>
		
		<p class=hkTitleC align=left style='text-align:left'><span style='font-size:
		11.0pt;font-family:Arial;font-weight:normal;text-decoration:none'>This End User
		License Agreement (&quot;EULA&quot;) is a legal document between the user
		("you" or "Customer") and SiteScape, Inc. (&quot;SiteScape&quot;). It is
		important that you read this document before installing or using the SiteScape software
		(the &quot;Software,&quot; as further defined below) and any accompanying
		documentation, including, without limitation printed materials, �online� files,
		or electronic documentation (&quot;Documentation&quot;). By clicking the
		&quot;I accept&quot; and &quot;Next&quot; buttons below, or by installing, or
		otherwise using the Software, you agree to be bound by the terms of this EULA as
		well as the SiteScape Privacy Policy (&quot;Privacy Policy&quot;) including,
		without limitation, the warranty disclaimers, limitation of liability, data use
		and termination provisions below. You agree that this agreement is enforceable
		like any written agreement negotiated and signed by you. If you do not agree, you
		are not licensed to use the Software, and you must destroy any downloaded
		copies of the Software in your possession or control. Please go to our Web site
		at http://www.sitescape.com/eula to download and print a copy of this EULA for
		your files and http://www.sitescape.com/privacy to review the privacy policy.</span></p>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>1.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>BACKGROUND</span></h1>
		
		<p class=hkBod><span style='font-size:11.0pt;font-family:Arial'>SiteScape holds
		the copyright to ICEcore, an open source software application, which is
		governed by a separate license agreement even though it may be delivered to
		Customers as part of a package that includes the Software governed by this
		Agreement.� SiteScape has developed, for use in conjunction with ICEcore, certain
		proprietary Software that provides additional features and capabilities not
		available with ICEcore.� Your rights and obligations relating to the Software are
		governed by this EULA.</span></p>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>2.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Definitions.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>ICEcore</i>&quot;
		has the meaning set out in Section </span><span
		style='font-size:11.0pt;font-family:Arial'>5</span><span style='font-size:11.0pt;
		font-family:Arial'> (ICEcore; Open Source).</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Documentation</i>"
		means the manuals and other material, whether in hard copy or electronic form,
		that are delivered with the Software or provided to Customer by SiteScape and that
		include technical information about the Software and its functional
		specifications.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Major
		Release</i>" means a release of the Software with modified or added functionality,
		features, and/or error correction that is designated by SiteScape, in its sole
		discretion, as a major release.� Without limiting the foregoing, Major Releases
		are generally, but not always, identified by means of a new integer before the
		decimal point in the version number.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Nonconformity</i>"</span><span
		style='font-size:11.0pt;font-family:Arial'> shall mean a material error that is
		attributable to SiteScape which prevents substantial performance of a principal
		function of the Software, as set forth in the Documentation in effect for the
		applicable version of the Software, and which error is reproducible by
		SiteScape at its facility. </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Permitted
		Number</i>" means the number of Users authorized to Use the Software in
		accordance with this EULA, as specified at the time that you purchased licenses
		to the Software.� </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>Premium</i><i> Module</i>&quot; means additional component(s) of the Software
		that SiteScape may make available to Customer and which provide premium
		features or functionality.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>g.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>&quot;<i>Common
		Public Attribution License</i>&quot; has the meaning set out in Section 5 (ICEcore;
		Open Source).</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>h.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Software</i>"
		means the SiteScape software product delivered in connection with this EULA, in
		its standard, unmodified object code form; any accompanying database(s); all
		related Documentation and technical information; and any Updates,
		modifications, enhancements and additional components that SiteScape may
		provide to Customer and which become part of, or interoperate with, the software.�
		Software shall also include: (i) any accompanying security device and may
		include sublicensed software that SiteScape has obtained under license, (ii)
		any Premium Module(s) that are (a) delivered to Customer with the Software, (b)
		later acquired by Customer that become a part of, or interoperate with, the
		Software.� "Software" shall not include ICEcore.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Update</i>"
		means a patch, work-around, error correction, or minor release for the Software
		that is made generally available by SiteScape, but does not include any new
		Major Release.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>j.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>Use</i>"
		means copying, storing, loading, installing, executing, or displaying the
		Software in the course of Customer�s normal business operations.� Use is
		limited to the type of operations described in the Documentation, solely to
		process Customer�s own work.� Use specifically excludes any ASP, service bureau,
		internet or web-hosting, or time-share services to third parties without
		SiteScape�s prior written consent, which consent may be withheld or denied in
		SiteScape�s sole and absolute discretion, and which is subject to payment of
		such additional license fees and maintenance fees as SiteScape may require.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>k.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>"<i>User</i>" shall
		mean any specific, identifiable person or device that is authorized or
		authenticated to Use the Software by means of a username or user ID.� </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>3.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Grant</span><span
		style='font-size:11.0pt;font-family:Arial'> of Rights.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>License
		grant.</span></b><span style='font-size:11.0pt;font-family:Arial'>� Upon your
		acceptance of this EULA, and subject to its terms and conditions, SiteScape
		grants you a non-exclusive, non-transferable, non-sublicenseable, perpetual
		(except in the event of termination of this EULA) license to Use the Software only
		in object code form on any number of workstations or file servers for local or
		wide area computer networks at any of Customer�s locations in the United States
		(but not including its subsidiaries or related or affiliated companies), but
		only by the Permitted Number of Users.� Customer has no right to Use the source
		code for the Software.� Customer is expressly prohibited from allowing or
		permitting any third-party usage of the Software without the prior written
		consent of SiteScape, which consent may be conditioned upon Customer�s advance
		payment to SiteScape for any additional license fees for such expanded use.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Server use.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>� You may install one copy of the
		Software on your computer file server for the purpose of downloading and
		installing the Software onto other computers within your internal network up to
		the Permitted Number of computers. No other network use is permitted, including
		without limitation using the Software either directly or through commands, data
		or instructions from or to a computer not part of your internal network, for
		Internet or Web-hosting services or by any user not licensed to use this copy
		of the Software through a valid license from SiteScape. �If you have purchased
		Concurrent User Licenses as defined in Section 3.(c) you may install a copy of the
		Software on a terminal server within your internal network for the sole and
		exclusive purpose of permitting individual users within your organization to
		access and use the Software through a terminal server session from another
		computer on the network provided that the total number of users that access or
		use the Software on such network or terminal server does not exceed the
		Permitted Number. </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Concurrent
		Use.</span></b><span style='font-size:11.0pt;font-family:Arial'> If you have
		licensed a &quot;Concurrent-User&quot; version of the Software, you may install
		the Software on any compatible computers, up to three (3) times the Permitted
		Number of users, <b>provided</b> that only the Permitted Number of users
		actually use the Software at the same time. The Permitted Number of concurrent
		users shall be specified at such time as you purchase the Software licenses.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Updates.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>� Customer acknowledges and agrees
		that the continued integrity of the Software and SiteScape�s obligations under
		this EULA are dependent upon and subject to the proper Use and maintenance of
		the Software by Customer and its Users.� Proper Use and maintenance means that Customer
		will install, maintain, and Use the Software according to the Documentation
		supplied by SiteScape, follow SiteScape�s instructions for installing Updates and
		for correcting and circumventing bugs, and abide by all of the terms of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Restrictions.</span></b><span
		style='font-size:11.0pt;font-family:Arial'>� Customer is prohibited from:</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>removing or
		altering any notices of intellectual property and/or proprietary rights that
		are embedded in or otherwise provided with the Software or any copy thereof;� </span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>copying (except
		as specifically permitted in this EULA), distributing, renting, leasing, exporting,
		granting a security interest in, sublicensing or otherwise transferring all or
		any portion of the Software or the rights in or to the Software;</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iii.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>modifying, adapting,
		translating, reverse engineering, decompiling, disabling any control feature,
		or creating derivative works based on the Software, or otherwise creating the
		source code from the object code of the Software, except as expressly authorized
		by SiteScape in writing; or</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iv.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>permitting any
		Use of the Software that is not expressly authorized by this EULA, including Use
		by any third parties or Users not authorized by this EULA to Use the Software.</span></h3>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer may
		copy the Documentation only to the extent necessary to Use the Software,
		provided that you reproduce SiteScape�s copyright notices and other notices of
		intellectual property and/or proprietary rights, and further provided that all
		such copies shall be subject to all terms, conditions, and obligations of this EULA.�
		Customer shall not modify or alter the Documentation in any manner.</span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>4.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Ownership</span><span
		style='font-size:11.0pt;font-family:Arial;font-weight:normal'>.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		acknowledges that SiteScape (together with its third party licensors) owns all
		right and title to the Software and to all intellectual property rights (including,
		without limitation, all applicable rights to patents, copyrights, trademarks,
		and trade secrets in and related to the Software) in the Software (including
		without limitation any images, data, animations, video, audio, music, and text
		incorporated into the Software).� </span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that the Software is protected by U.S. patent 
		and copyright laws and international treaties.� Nothing in this EULA constitutes
		a waiver of SiteScape�s rights under U.S. or international copyright or patent law
		or any other federal or state law.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that this EULA is for a license of limited rights
		to the Software, and is not a sale.� SiteScape owns all title to the Software
		and to any copies of the Software delivered to Customer.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer
		understands and acknowledges that its rights to Use the Software are specified
		in this EULA and SiteScape retains all rights that are not expressly granted to
		Customer in this Agreement.� </span></h2>
		
		<h1><span style='font-size:
		11.0pt;font-family:Arial;text-transform:uppercase'>5.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>ICEcore; Open
		Source</span><span style='font-size:11.0pt;font-family:Arial'>.</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>The Software may
		be bundled with, and designed to interoperate with, software made available by
		SiteScape under the Common Public Attribution License v.1.0 (available at <a
		href="http://www.opensource.org/licenses/cpal_1.0">http://www.opensource.org/licenses/cpal_1.0</a>)
		(&quot;ICEcore&quot;).� Customer's rights and obligations with respect to the
		Software are governed by this EULA.� Customer's rights and obligations with
		respect to ICEcore are governed by the Common Public Attribution License.</span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>6.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>DISCLAIMER OF WARRANTIES.� </span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>THE SOFTWARE
		IS PROVIDED "AS IS" AND SITESCAPE MAKES NO REPRESENTATIONS OR WARRANTIES AS TO
		ITS USE OR PERFORMANCE.� SITESCAPE AND ITS SUPPLIERS DO NOT AND CANNOT WARRANT
		THE PERFORMANCE OR RESULTS YOU MAY OBTAIN BY USING THE SOFTWARE, AND TO THE MAXIMUM
		EXTENT PERMITTED BY LAW SITESCAPE SPECIFICALLY DISCLAIMS ALL WARRANTIES,
		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY WARRANTIES OF
		MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, AND ALL WARRANTIES OF
		TITLE, NONINTERFERENCE, AND NONINFRINGEMENT.</span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA does <b>not</b>
		include technical support by SiteScape. Customer may purchase support services
		from SiteScape under a separate agreement.� Customer is solely responsible for providing
		support to Users.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Parties other
		than SiteScape may provide modifications, enhancements, and additional
		components (Third Party Additions&quot;) designed to become part of, or
		interoperate with, the Software or ICEcore.� SiteScape is not responsible for
		any Third Party Additions and expressly disclaims all warranties with respect
		to Third Party Additions.� </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>7.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>LIMITATION OF
		LIABILITY</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>IN NO EVENT
		WILL SITESCAPE BE LIABLE FOR ANY INDIRECT, SPECIAL, INCIDENTAL, CONSEQUENTIAL, EXEMPLARY
		OR PUNITIVE DAMAGES, OR ANY DAMAGES RELATED TO LOSS OF USE, DATA, SOFTWARE,
		BUSINESS, PROFITS OR GOODWILL, WORK STOPPAGE, OR ANY OTHER COMMERCIAL DAMAGES
		OR LOSSES, ARISING IN CONTRACT, TORT OR OTHERWISE, EVEN IF SITESCAPE HAS
		KNOWLEDGE OF THE POTENTIAL LOSS OR DAMAGE.� SITESCAPE SHALL NOT BE LIABLE FOR
		DAMAGES, CLAIMS BY THIRD PARTIES, OR ANY OTHER CLAIM FOR ANY CAUSE WHATSOEVER, REGARDLESS
		OF THE FORM OF ACTION, ARISING MORE THAN ONE (1) YEAR AFTER THE DISCOVERY OF
		THE NONCONFORMITY RESULTING IN ANY SUCH DAMAGE.� </span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>IN NO EVENT
		SHALL SITESCAPE�S LIABILITY TO CUSTOMER, IF ANY, EXCEED THE FEES PAID TO
		SITESCAPE UNDER THIS AGREEMENT FOR THE PARTICULAR SOFTWARE OR SERVICE THAT IS
		THE SUBJECT OF THE CLAIM.� </span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>CUSTOMER ACCEPTS
		THE TERMS AND CONDITIONS OF THIS LICENSE AGREEMENT WITH THE UNDERSTANDING THAT
		SITESCAPE�S LIABILITY IS LIMITED, THE FEES PAYABLE HEREUNDER HAVE BEEN AND WILL
		BE CALCULATED ACCORDINGLY, AND THAT CUSTOMER MAY REDUCE ITS RISK FURTHER BY
		MAKING APPROPRIATE PROVISION FOR INSURANCE.� CUSTOMER FURTHER AGREE TO MITIGATE
		ALL LOSSES OR DAMAGES.</span></b></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;text-transform:uppercase'>8.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Term and
		Termination. </span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA is
		effective until terminated.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer may
		terminate this EULA at any time by providing written notice to SiteScape and destroying
		the Software and documentation together with all copies.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>This EULA will
		terminate immediately and automatically if Customer fails to pay any sum of
		money past due or owing to SiteScape within 15 days after written notice from
		SiteScape, or fails to comply with or breaches any other term or condition of
		this EULA or any Support Services Agreement.� Termination of this EULA will be
		without prejudice to SiteScape�s other rights and remedies under this EULA, and
		SiteScape will further have the right to pursue any other remedy available to
		it at law or in equity or under any statute, and shall be entitled to recover
		its costs and attorneys� fees incurred in connection with any legal action or
		proceeding relating to this EULA.� The termination of this Agreement will not
		relieve Customer of any obligation under this Agreement that that arose prior
		to such termination or which is identified as continuing thereafter, nor shall
		it operate as a cancellation of any indebtedness owed or accruing to
		SiteScape.� </span></h2>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>9.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>SOFTWARE
		ACTIVATION, UPDATES, AND AUDIT RIGHTS</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer is
		hereby notified that the Software will contain license keys intended to ensure
		that the use limits of a particular license will not be exceeded.� SiteScape
		may use your internal network for license metering between installed versions
		of the Software.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Software
		Activation. The Software may use your internal network and Internet connection
		for the purpose of transmitting license-related data at the time of
		installation, registration, use, or update to a SiteScape-operated license
		server and validating the authenticity of the license-related data in order to
		protect SiteScape against unlicensed or illegal use of the Software and to
		improve customer service. Activation is based on the exchange of license
		related data between your computer and the SiteScape license server. You agree
		that SiteScape may use these measures and you agree to follow, and not attempt
		to circumvent, any applicable requirements.</span></b></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>LiveUpdate. </span></b><span
		style='font-size:11.0pt;font-family:Arial'>�SiteScape may provide a LiveUpdate
		notification service to you. SiteScape may use your internal network and
		Internet connection for the purpose of transmitting license-related data to a
		SiteScape-operated LiveUpdate server to validate your license at appropriate
		intervals and determine if there is any update available for you.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Use of Data.</span></b><span
		style='font-size:11.0pt;font-family:Arial'> The terms and conditions of the
		Privacy Policy are set out in full at http://www.sitescape.com/privacy and are
		incorporated by reference into this EULA. By your acceptance of the terms of
		this EULA or use of the Software, you authorize the collection, use and
		disclosure of information collected by SiteScape for the purposes provided for
		in this EULA and/or the Privacy Policy as revised from time to time. European
		users understand and consent to the processing of personal information in the 
		United States for the purposes described herein. SiteScape has the right in its sole
		discretion to amend this provision of the EULA and/or Privacy Policy at any
		time. You are encouraged to review the terms of the Privacy Policy as posted on
		the SiteScape Web site from time to time.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Customer agrees
		that:</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>Upon request
		from SiteScape or SiteScape�s authorized representative, Customer will conduct
		a self-audit of its Use of the Software by running a script provided by
		SiteScape (the "Authorized Script") on the database associated with the
		Software.� Customer shall provide to SiteScape the raw, unmodified output
		generated by the Authorized Script in electronic form within thirty (30)
		business days of receiving the request from SiteScape.� Customer shall pay
		license fees (and any fees for Support Services) for any Use of the Software in
		excess of the Permitted Number revealed by such self-audit within 30 days of
		the date of any invoice issued by SiteScape.</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>In addition, SiteScape
		may, not more than once every year, direct an independent firm of its choosing
		to audit, upon seven (7) days� notice and during normal business hours, the
		number of Users of the Software.� If the number of Users identified by the
		auditors exceeds the Permitted Number, Customer will be invoiced for the
		license fees attributable to the number of Users in excess of the Permitted
		Number plus the costs associated with the audit, which invoice shall be payable
		within fifteen (15) days of the date of the invoice.� </span></h3>
		
		<h1><span style='font-size:11.0pt;font-family:Arial;
		text-transform:uppercase'>10.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>MISCELLANEOUS</span></h1>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>a.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>No Misuse or
		Modification</span></b><span style='font-size:11.0pt;font-family:Arial'>.� The
		Software is intended for Use as specified in this Agreement and in accordance
		with the Documentation.� Customer agrees to hold harmless, indemnify, and
		defend SiteScape, its officers, directors, employees, and agents, from and
		against any loss, claim or damages (including reasonable attorneys� fees)
		arising out of or relating to any claim</span></h2>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that Customer
		has encoded, compressed, copied, or transmitted any materials (other than the
		materials provided by SiteScape) in connection with the Software in violation
		of another party�s rights or in violation of any law;</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>ii.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that Customer
		has misused, allowed to be misused, or modified the Software in any manner not
		expressly permitted by this EULA; or</span></h3>
		
		<h3><span style='font-size:11.0pt;font-family:Arial'>iii.<span
		style='font:7.0pt "Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><span style='font-size:11.0pt;font-family:Arial'>that any content
		added to or incorporated into the Software by Customer either independently of,
		or pursuant to, the Common Public Attribution License, infringes any
		intellectual property rights of any third party.</span></h3>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>b.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Government
		End Users</span></b><span style='font-size:11.0pt;font-family:Arial'>.� The
		license granted under this EULA does not constitute a response by SiteScape to
		any request for proposals, bid solicitation or other invitation or offer to
		contract by any governmental authority but instead constitutes an offer to
		enter into a license agreement only upon the terms and conditions set forth
		herein.� If the United States Government or any other governmental authority
		shall seek to acquire the Software and its acquisition of such Software would
		result in the U.S. Government or such other governmental authority having
		rights in any software that are at variance with the terms and conditions of
		this EULA, SiteScape shall not be bound by any such rights unless it shall have
		expressly entered into an amendment of this EULA that shall set forth such
		rights in accordance with any applicable governmental rules or regulations,
		including the Federal Acquisition Regulation and the Defense Federal
		Acquisition Regulation Supplement.� The Software is a "commercial item," as
		that term is defined in 48 C.F.R. �2.101.� The contractor/manufacturer is
		SiteScape, Inc., 12 Clock Tower Place, Suite 210, Maynard, MA 01754.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>c.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Export
		Controls</span></b><span style='font-size:11.0pt;font-family:Arial'>.� Customer
		agrees that the Software will not be shipped, transferred or exported into any
		country or used in any manner prohibited by the United States Export
		Administration Act or any other export laws, restrictions or regulations.� In
		addition, if the Software is identified as an export controlled item under any
		such export laws, Customer represents and warrants that it is not a citizen, or
		otherwise located within, an embargoed nation and that Customer is not
		otherwise prohibited from receiving the Software.� Customer is responsible for
		obtaining any and all required governmental authorizations required in
		connection with Customer�s Use of the Software, including, without limitation,
		any export or import licenses and foreign exchange permits.� SiteScape shall
		not be liable if any such authorization is delayed, denied, revoked, restricted
		or not renewed and you shall bear all risks and costs associated with such
		activities.� If Customer is importing or exporting the Software outside of the 
		United States, Customer agrees to indemnify and hold SiteScape harmless from and against
		any import and export duties or other claims arising from such importation or
		exportation.� All rights to Use the Software are granted on condition that such
		rights are forfeited if Customer fails to comply with the terms of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>d.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Installation</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� Except as otherwise provided
		herein or as agreed between the parties, Customer is responsible for
		installation, management, and operation of the Software.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>e.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Survival</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� The terms of Sections 4, 6, 7, 9,
		and 10, but not any grant of rights to Use the Software, shall survive the
		termination of this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>f.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Governing
		Law; Venue</span></b><span style='font-size:11.0pt;font-family:Arial'>.� This EULA
		is governed by the internal laws of the Commonwealth of Massachusetts without
		regard to its conflict of laws principles.� By executing and accepting this EULA,
		Customer consents irrevocably to jurisdiction and venue in the state and
		federal courts sitting in the Commonwealth of Massachusetts.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>g.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Equitable
		Relief</span></b><span style='font-size:11.0pt;font-family:Arial'>.� Customer acknowledges
		that its breach of the terms and restrictions of this EULA will cause SiteScape
		irreparable harm for which the recovery of money damages would be inadequate.�
		Therefore, Customer agrees that SiteScape will be entitled to obtain injunctive
		relief to enforce the terms of this EULA and to protect its rights in the
		Software, in addition to any other remedies that may be available to it at law,
		in equity, or under any statute.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>h.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Waiver</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� No failure or delay on the part
		of SiteScape in exercising any right or remedy provided in this EULA shall
		operate as a waiver of such right nor shall any single or partial exercise of
		or failure to exercise any such right or remedy preclude SiteScape from further
		exercising such right or remedy under this EULA.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>i.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Assignment</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� This EULA may not be assigned
		(whether in whole or in part, and whether by merger, operation of law or
		otherwise) by Customer without SiteScape�s prior written consent. �SiteScape
		may assign its rights and obligations under this EULA without notice or
		consent.� This EULA shall be binding upon any permitted successors and assigns.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>j.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Severability</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� If any provision of this EULA is
		held by a court of competent jurisdiction to be illegal, invalid, or
		unenforceable, the remaining provisions shall remain in full force and effect.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>k.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Entire
		Agreement; Amendment</span></b><span style='font-size:11.0pt;font-family:Arial'>.�
		This EULA, together with all exhibits or other documents attached to, delivered
		with, or expressly referenced by it, constitutes the entire agreement of the
		parties with respect to its subject matter and supersedes all previous and
		contemporaneous communications, presentations, quotations, or agreements
		regarding its subject matter,� No waiver, alteration, modification, or
		cancellation of any of the provisions of this EULA shall be binding unless made
		in writing and signed by an authorized officer of SiteScape.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>l.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Headings</span></b><span
		style='font-size:11.0pt;font-family:Arial'>.� The headings in this EULA are for
		purposes of reference only and shall not in any way limit or affect the meaning
		or interpretation of any of the terms hereof.</span></h2>
		
		<h2><span style='font-size:11.0pt;font-family:Arial'>m.<span style='font:7.0pt 
		"Times New Roman"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span></span><b><span style='font-size:11.0pt;font-family:Arial'>Software Licensed From 
		Third Parties</span></b><span style='font-size:11.0pt;font-family:Arial'>. 
		The Software may contain modules or components licensed from third parties, as 
		identified in the accompanying Documentation (collectively referred to herein 
		as "Third Party Components"), the use of which must comply with the terms of the 
		respective licenses listed for each such Third Party Component (the licenses 
		applicable to the Third Party Components are collectively referred to herein 
		as "Third Party Licenses").   The license granted pursuant to this Agreement 
		does not limit your rights under, or grant you rights that supersede, the terms 
		of the Third Party Licenses.  You agree to defend, indemnify and hold SiteScape, 
		its affiliates and their respective officers, directors, employees, agents and 
		representatives harmless from and against any liabilities, losses, expenses, 
		costs or damages (including attorneys� fees) arising from or in any manner relating 
		to any claim or action based upon your breach of the terms and conditions of the 
		Third Party Licenses associated with the Software licensed under this Agreement. </span></h2>

		<p class=MsoNormal style='margin-top:6.0pt'><span style='font-size:8.0pt'>#
		4771339_v1a</span></p>
		
		</div>
</ssf:expandableArea><p style="padding-top:6px;"/>

<br/>
<br/>
<span class="ss_bold">
Portions of this software are subject to copyrights and licenses of third parties.
<br/>
The following are the licenses which may apply to the third party software:
</span>
<br/>
<br/>

<ssf:expandableArea titleClass="ss_credits_title" title="Stellent">
<pre>
Stellent Html Export &copy; 1991-2006 Stellent Chicago, Inc. All rights reserved.
Stellent Image Export &copy; 1991-2006 Stellent Chicago, Inc. All rights reserved.
Stellent Search Export &copy; 1991-2006 Stellent Chicago, Inc. All rights reserved.

</pre></ssf:expandableArea><p style="padding-top:6px;"/>



<ssf:expandableArea titleClass="ss_credits_title" title="ASL2.0">
<pre>
Copyright 2007, 2008 SiteScape, Inc. 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

	http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="ANTLR">
<pre>
http://www.antlr.org/license.html

ANTLR 3 License

[The BSD License]
Copyright (c) 2003-2007, Terence Parr
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
    * Neither the name of the author nor the names of its contributors may be 
      used to endorse or promote products derived from this software without 
      specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
OF THE POSSIBILITY OF SUCH DAMAGE. 
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="aopalliance">
<pre>
PUBLIC DOMAIN
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="BouncyCastle">
<pre>
http://www.bouncycastle.org/licence.html

Copyright (c) 2000 - 2006 The Legion Of The Bouncy Castle (http://www.bouncycastle.org)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
associated documentation files (the "Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="SUN">
<pre>
http://developers.sun.com/license/berkeley_license.html

Copyright 1994-2007 Sun Microsystems, Inc. All Rights Reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:
 

    * Redistribution of source code must retain the above copyright notice, 
	  this list of conditions and the following disclaimer.

    * Redistribution in binary form must reproduce the above copyright notice, 
	  this list of conditions and the following disclaimer in the documentation 
	  and/or other materials provided with the distribution.

 
Neither the name of Sun Microsystems, Inc. or the names of contributors may 
be used to endorse or promote products derived from this software without specific 
prior written permission.
 
This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS 
OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED 
WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT,
ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT 
BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING 
OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS 
LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, 
INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF 
OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGES.
 
You acknowledge that this software is not designed, licensed or intended for 
use in the design, construction, operation or maintenance of any nuclear 
facility. 
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="LGPL">
<pre>
http://www.gnu.org/licenses/lgpl-3.0.txt

		   GNU LESSER GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.


  This version of the GNU Lesser General Public License incorporates
the terms and conditions of version 3 of the GNU General Public
License, supplemented by the additional permissions listed below.

  0. Additional Definitions. 

  As used herein, "this License" refers to version 3 of the GNU Lesser
General Public License, and the "GNU GPL" refers to version 3 of the GNU
General Public License.

  "The Library" refers to a covered work governed by this License,
other than an Application or a Combined Work as defined below.

  An "Application" is any work that makes use of an interface provided
by the Library, but which is not otherwise based on the Library.
Defining a subclass of a class defined by the Library is deemed a mode
of using an interface provided by the Library.

  A "Combined Work" is a work produced by combining or linking an
Application with the Library.  The particular version of the Library
with which the Combined Work was made is also called the "Linked
Version".

  The "Minimal Corresponding Source" for a Combined Work means the
Corresponding Source for the Combined Work, excluding any source code
for portions of the Combined Work that, considered in isolation, are
based on the Application, and not on the Linked Version.

  The "Corresponding Application Code" for a Combined Work means the
object code and/or source code for the Application, including any data
and utility programs needed for reproducing the Combined Work from the
Application, but excluding the System Libraries of the Combined Work.

  1. Exception to Section 3 of the GNU GPL.

  You may convey a covered work under sections 3 and 4 of this License
without being bound by section 3 of the GNU GPL.

  2. Conveying Modified Versions.

  If you modify a copy of the Library, and, in your modifications, a
facility refers to a function or data to be supplied by an Application
that uses the facility (other than as an argument passed when the
facility is invoked), then you may convey a copy of the modified
version:

   a) under this License, provided that you make a good faith effort to
   ensure that, in the event an Application does not supply the
   function or data, the facility still operates, and performs
   whatever part of its purpose remains meaningful, or

   b) under the GNU GPL, with none of the additional permissions of
   this License applicable to that copy.

  3. Object Code Incorporating Material from Library Header Files.

  The object code form of an Application may incorporate material from
  a header file that is part of the Library.  You may convey such object
  code under terms of your choice, provided that, if the incorporated
  material is not limited to numerical parameters, data structure
  layouts and accessors, or small macros, inline functions and templates
  (ten or fewer lines in length), you do both of the following:

   a) Give prominent notice with each copy of the object code that the
   Library is used in it and that the Library and its use are
   covered by this License.

   b) Accompany the object code with a copy of the GNU GPL and this license
   document.

  4. Combined Works.

  You may convey a Combined Work under terms of your choice that,
  taken together, effectively do not restrict modification of the
  portions of the Library contained in the Combined Work and reverse
  engineering for debugging such modifications, if you also do each of
  the following:

   a) Give prominent notice with each copy of the Combined Work that
   the Library is used in it and that the Library and its use are
   covered by this License.

   b) Accompany the Combined Work with a copy of the GNU GPL and this license
   document.

   c) For a Combined Work that displays copyright notices during
   execution, include the copyright notice for the Library among
   these notices, as well as a reference directing the user to the
   copies of the GNU GPL and this license document.

   d) Do one of the following:

       0) Convey the Minimal Corresponding Source under the terms of this
       License, and the Corresponding Application Code in a form
       suitable for, and under terms that permit, the user to
       recombine or relink the Application with a modified version of
       the Linked Version to produce a modified Combined Work, in the
       manner specified by section 6 of the GNU GPL for conveying
       Corresponding Source.

       1) Use a suitable shared library mechanism for linking with the
       Library.  A suitable mechanism is one that (a) uses at run time
       a copy of the Library already present on the user's computer
       system, and (b) will operate properly with a modified version
       of the Library that is interface-compatible with the Linked
       Version. 

   e) Provide Installation Information, but only if you would otherwise
   be required to provide such information under section 6 of the
   GNU GPL, and only to the extent that such information is
   necessary to install and execute a modified version of the
   Combined Work produced by recombining or relinking the
   Application with a modified version of the Linked Version. (If
   you use option 4d0, the Installation Information must accompany
   the Minimal Corresponding Source and Corresponding Application
   Code. If you use option 4d1, you must provide the Installation
   Information in the manner specified by section 6 of the GNU GPL
   for conveying Corresponding Source.)

  5. Combined Libraries.

  You may place library facilities that are a work based on the
  Library side by side in a single library together with other library
  facilities that are not Applications and are not covered by this
  License, and convey such a combined library under terms of your
  choice, if you do both of the following:

   a) Accompany the combined library with a copy of the same work based
   on the Library, uncombined with any other library facilities,
   conveyed under the terms of this License.

   b) Give prominent notice with the combined library that part of it
   is a work based on the Library, and explaining where to find the
   accompanying uncombined form of the same work.

  6. Revised Versions of the GNU Lesser General Public License.

  The Free Software Foundation may publish revised and/or new versions
  of the GNU Lesser General Public License from time to time. Such new
  versions will be similar in spirit to the present version, but may
  differ in detail to address new problems or concerns.

  Each version is given a distinguishing version number. If the
  Library as you received it specifies that a certain numbered version
  of the GNU Lesser General Public License "or any later version"
  applies to it, you have the option of following the terms and
  conditions either of that published version or of any later version
  published by the Free Software Foundation. If the Library as you
  received it does not specify a version number of the GNU Lesser
  General Public License, you may choose any version of the GNU Lesser
  General Public License ever published by the Free Software Foundation.

  If the Library as you received it specifies that a proxy can decide
  whether future versions of the GNU Lesser General Public License shall
  apply, that proxy's public statement of acceptance of any version is
  permanent authorization for you to choose that version for the
  Library.


</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="DOM4J">
<pre>
http://www.dom4j.org/license.html

 Redistribution and use of this software and associated documentation ("Software"), 
 with or without modification, are permitted provided that the following conditions 
 are met:

   1. Redistributions of source code must retain copyright statements and notices. 
      Redistributions must also contain a copy of this document.
   2. Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.
   3. The name "DOM4J" must not be used to endorse or promote products derived from 
      this Software without prior written permission of MetaStuff, Ltd. For written 
      permission, please contact dom4j-info@metastuff.com.
   4. Products derived from this Software may not be called "DOM4J" nor may "DOM4J" 
      appear in their names without prior written permission of MetaStuff, Ltd. 
      DOM4J is a registered trademark of MetaStuff, Ltd.
   5. Due credit should be given to the DOM4J Project - http://www.dom4j.org

THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND ANY 
EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved. 
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="EASYMOCK">
<pre>
http://www.easymock.org/License.html

EasyMock 2 License (MIT License)
Copyright (c) 2001-2007 OFFIS, Tammo Freese.

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included 
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
OTHER DEALINGS IN THE SOFTWARE. 
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="iCal4j">
<pre>
http://ical4j.sourceforge.net/license.html

Copyright (c) 2007, Ben Fortuna
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright 
	  notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright 
	  notice, this list of conditions and the following disclaimer in 
	  the documentation and/or other materials provided with the 
	  distribution.
    * Neither the name of Ben Fortuna nor the names of any other 
	contributors may be used to endorse or promote products derived 
	from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="Jaxen">
<pre>
http://jaxen.org/

Copyright 2003-2006 The Werken Company. All Rights Reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are
 met:

  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.

  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.

  * Neither the name of the Jaxen Project nor the names of its
    contributors may be used to endorse or promote products derived 
    from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="JAXRPC">
<pre>
http://www.opensource.org/licenses/cddl1.php


COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0

1. Definitions.

1.1. "Contributor" means each individual or entity that
creates or contributes to the creation of Modifications.

1.2. "Contributor Version" means the combination of the
Original Software, prior Modifications used by a
Contributor (if any), and the Modifications made by that
particular Contributor.

1.3. "Covered Software" means (a) the Original Software, or
(b) Modifications, or (c) the combination of files
containing Original Software with files containing
Modifications, in each case including portions thereof.

1.4. "Executable" means the Covered Software in any form
other than Source Code.

1.5. "Initial Developer" means the individual or entity
that first makes Original Software available under this
License.

1.6. "Larger Work" means a work which combines Covered
Software or portions thereof with code not governed by the
terms of this License.

1.7. "License" means this document.

1.8. "Licensable" means having the right to grant, to the
maximum extent possible, whether at the time of the initial
grant or subsequently acquired, any and all of the rights
conveyed herein.

1.9. "Modifications" means the Source Code and Executable
form of any of the following:

A. Any file that results from an addition to,
deletion from or modification of the contents of a
file containing Original Software or previous
Modifications;

B. Any new file that contains any part of the
Original Software or previous Modification; or

C. Any new file that is contributed or otherwise made
available under the terms of this License.

1.10. "Original Software" means the Source Code and
Executable form of computer software code that is
originally released under this License.

1.11. "Patent Claims" means any patent claim(s), now owned
or hereafter acquired, including without limitation,
method, process, and apparatus claims, in any patent
Licensable by grantor.

1.12. "Source Code" means (a) the common form of computer
software code in which modifications are made and (b)
associated documentation included in or with such code.

1.13. "You" (or "Your") means an individual or a legal
entity exercising rights under, and complying with all of
the terms of, this License. For legal entities, "You"
includes any entity which controls, is controlled by, or is
under common control with You. For purposes of this
definition, "control" means (a) the power, direct or
indirect, to cause the direction or management of such
entity, whether by contract or otherwise, or (b) ownership
of more than fifty percent (50%) of the outstanding shares
or beneficial ownership of such entity.

2. License Grants.

2.1. The Initial Developer Grant.

Conditioned upon Your compliance with Section 3.1 below and
subject to third party intellectual property claims, the
Initial Developer hereby grants You a world-wide,
royalty-free, non-exclusive license:

(a) under intellectual property rights (other than
patent or trademark) Licensable by Initial Developer,
to use, reproduce, modify, display, perform,
sublicense and distribute the Original Software (or
portions thereof), with or without Modifications,
and/or as part of a Larger Work; and

(b) under Patent Claims infringed by the making,
using or selling of Original Software, to make, have
made, use, practice, sell, and offer for sale, and/or
otherwise dispose of the Original Software (or
portions thereof).

(c) The licenses granted in Sections 2.1(a) and (b)
are effective on the date Initial Developer first
distributes or otherwise makes the Original Software
available to a third party under the terms of this
License.

(d) Notwithstanding Section 2.1(b) above, no patent
license is granted: (1) for code that You delete from
the Original Software, or (2) for infringements
caused by: (i) the modification of the Original
Software, or (ii) the combination of the Original
Software with other software or devices.

2.2. Contributor Grant.

Conditioned upon Your compliance with Section 3.1 below and
subject to third party intellectual property claims, each
Contributor hereby grants You a world-wide, royalty-free,
non-exclusive license:

(a) under intellectual property rights (other than
patent or trademark) Licensable by Contributor to
use, reproduce, modify, display, perform, sublicense
and distribute the Modifications created by such
Contributor (or portions thereof), either on an
unmodified basis, with other Modifications, as
Covered Software and/or as part of a Larger Work; and

(b) under Patent Claims infringed by the making,
using, or selling of Modifications made by that
Contributor either alone and/or in combination with
its Contributor Version (or portions of such
combination), to make, use, sell, offer for sale,
have made, and/or otherwise dispose of: (1)
Modifications made by that Contributor (or portions
thereof); and (2) the combination of Modifications
made by that Contributor with its Contributor Version
(or portions of such combination).

(c) The licenses granted in Sections 2.2(a) and
2.2(b) are effective on the date Contributor first
distributes or otherwise makes the Modifications
available to a third party.

(d) Notwithstanding Section 2.2(b) above, no patent
license is granted: (1) for any code that Contributor
has deleted from the Contributor Version; (2) for
infringements caused by: (i) third party
modifications of Contributor Version, or (ii) the
combination of Modifications made by that Contributor
with other software (except as part of the
Contributor Version) or other devices; or (3) under
Patent Claims infringed by Covered Software in the
absence of Modifications made by that Contributor.

3. Distribution Obligations.

3.1. Availability of Source Code.

Any Covered Software that You distribute or otherwise make
available in Executable form must also be made available in
Source Code form and that Source Code form must be
distributed only under the terms of this License. You must
include a copy of this License with every copy of the
Source Code form of the Covered Software You distribute or
otherwise make available. You must inform recipients of any
such Covered Software in Executable form as to how they can
obtain such Covered Software in Source Code form in a
reasonable manner on or through a medium customarily used
for software exchange.

3.2. Modifications.

The Modifications that You create or to which You
contribute are governed by the terms of this License. You
represent that You believe Your Modifications are Your
original creation(s) and/or You have sufficient rights to
grant the rights conveyed by this License.

3.3. Required Notices.

You must include a notice in each of Your Modifications
that identifies You as the Contributor of the Modification.
You may not remove or alter any copyright, patent or
trademark notices contained within the Covered Software, or
any notices of licensing or any descriptive text giving
attribution to any Contributor or the Initial Developer.

3.4. Application of Additional Terms.

You may not offer or impose any terms on any Covered
Software in Source Code form that alters or restricts the
applicable version of this License or the recipients'
rights hereunder. You may choose to offer, and to charge a
fee for, warranty, support, indemnity or liability
obligations to one or more recipients of Covered Software.
However, you may do so only on Your own behalf, and not on
behalf of the Initial Developer or any Contributor. You
must make it absolutely clear that any such warranty,
support, indemnity or liability obligation is offered by
You alone, and You hereby agree to indemnify the Initial
Developer and every Contributor for any liability incurred
by the Initial Developer or such Contributor as a result of
warranty, support, indemnity or liability terms You offer.

3.5. Distribution of Executable Versions.

You may distribute the Executable form of the Covered
Software under the terms of this License or under the terms
of a license of Your choice, which may contain terms
different from this License, provided that You are in
compliance with the terms of this License and that the
license for the Executable form does not attempt to limit
or alter the recipient's rights in the Source Code form
from the rights set forth in this License. If You
distribute the Covered Software in Executable form under a
different license, You must make it absolutely clear that
any terms which differ from this License are offered by You
alone, not by the Initial Developer or Contributor. You
hereby agree to indemnify the Initial Developer and every
Contributor for any liability incurred by the Initial
Developer or such Contributor as a result of any such terms
You offer.

3.6. Larger Works.

You may create a Larger Work by combining Covered Software
with other code not governed by the terms of this License
and distribute the Larger Work as a single product. In such
a case, You must make sure the requirements of this License
are fulfilled for the Covered Software.

4. Versions of the License.

4.1. New Versions.

Sun Microsystems, Inc. is the initial license steward and
may publish revised and/or new versions of this License
from time to time. Each version will be given a
distinguishing version number. Except as provided in
Section 4.3, no one other than the license steward has the
right to modify this License.

4.2. Effect of New Versions.

You may always continue to use, distribute or otherwise
make the Covered Software available under the terms of the
version of the License under which You originally received
the Covered Software. If the Initial Developer includes a
notice in the Original Software prohibiting it from being
distributed or otherwise made available under any
subsequent version of the License, You must distribute and
make the Covered Software available under the terms of the
version of the License under which You originally received
the Covered Software. Otherwise, You may also choose to
use, distribute or otherwise make the Covered Software
available under the terms of any subsequent version of the
License published by the license steward.

4.3. Modified Versions.

When You are an Initial Developer and You want to create a
new license for Your Original Software, You may create and
use a modified version of this License if You: (a) rename
the license and remove any references to the name of the
license steward (except to note that the license differs
from this License); and (b) otherwise make it clear that
the license contains terms which differ from this License.

5. DISCLAIMER OF WARRANTY.

COVERED SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS"
BASIS, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED,
INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
SOFTWARE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR
PURPOSE OR NON-INFRINGING. THE ENTIRE RISK AS TO THE QUALITY AND
PERFORMANCE OF THE COVERED SOFTWARE IS WITH YOU. SHOULD ANY
COVERED SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE
INITIAL DEVELOPER OR ANY OTHER CONTRIBUTOR) ASSUME THE COST OF
ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF
WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF
ANY COVERED SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
DISCLAIMER.

6. TERMINATION.

6.1. This License and the rights granted hereunder will
terminate automatically if You fail to comply with terms
herein and fail to cure such breach within 30 days of
becoming aware of the breach. Provisions which, by their
nature, must remain in effect beyond the termination of
this License shall survive.

6.2. If You assert a patent infringement claim (excluding
declaratory judgment actions) against Initial Developer or
a Contributor (the Initial Developer or Contributor against
whom You assert such claim is referred to as "Participant")
alleging that the Participant Software (meaning the
Contributor Version where the Participant is a Contributor
or the Original Software where the Participant is the
Initial Developer) directly or indirectly infringes any
patent, then any and all rights granted directly or
indirectly to You by such Participant, the Initial
Developer (if the Initial Developer is not the Participant)
and all Contributors under Sections 2.1 and/or 2.2 of this
License shall, upon 60 days notice from Participant
terminate prospectively and automatically at the expiration
of such 60 day notice period, unless if within such 60 day
period You withdraw Your claim with respect to the
Participant Software against such Participant either
unilaterally or pursuant to a written agreement with
Participant.

6.3. In the event of termination under Sections 6.1 or 6.2
above, all end user licenses that have been validly granted
by You or any distributor hereunder prior to termination
(excluding licenses granted to You by any distributor)
shall survive termination.

7. LIMITATION OF LIABILITY.

UNDER NO CIRCUMSTANCES AND UNDER NO LEGAL THEORY, WHETHER TORT
(INCLUDING NEGLIGENCE), CONTRACT, OR OTHERWISE, SHALL YOU, THE
INITIAL DEVELOPER, ANY OTHER CONTRIBUTOR, OR ANY DISTRIBUTOR OF
COVERED SOFTWARE, OR ANY SUPPLIER OF ANY OF SUCH PARTIES, BE
LIABLE TO ANY PERSON FOR ANY INDIRECT, SPECIAL, INCIDENTAL, OR
CONSEQUENTIAL DAMAGES OF ANY CHARACTER INCLUDING, WITHOUT
LIMITATION, DAMAGES FOR LOST PROFITS, LOSS OF GOODWILL, WORK
STOPPAGE, COMPUTER FAILURE OR MALFUNCTION, OR ANY AND ALL OTHER
COMMERCIAL DAMAGES OR LOSSES, EVEN IF SUCH PARTY SHALL HAVE BEEN
INFORMED OF THE POSSIBILITY OF SUCH DAMAGES. THIS LIMITATION OF
LIABILITY SHALL NOT APPLY TO LIABILITY FOR DEATH OR PERSONAL
INJURY RESULTING FROM SUCH PARTY'S NEGLIGENCE TO THE EXTENT
APPLICABLE LAW PROHIBITS SUCH LIMITATION. SOME JURISDICTIONS DO
NOT ALLOW THE EXCLUSION OR LIMITATION OF INCIDENTAL OR
CONSEQUENTIAL DAMAGES, SO THIS EXCLUSION AND LIMITATION MAY NOT
APPLY TO YOU.

8. U.S. GOVERNMENT END USERS.

The Covered Software is a "commercial item," as that term is
defined in 48 C.F.R. 2.101 (Oct. 1995), consisting of "commercial
computer software" (as that term is defined at 48 C.F.R. �
252.227-7014(a)(1)) and "commercial computer software
documentation" as such terms are used in 48 C.F.R. 12.212 (Sept.
1995). Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1
through 227.7202-4 (June 1995), all U.S. Government End Users
acquire Covered Software with only those rights set forth herein.
This U.S. Government Rights clause is in lieu of, and supersedes,
any other FAR, DFAR, or other clause or provision that addresses
Government rights in computer software under this License.

9. MISCELLANEOUS.

This License represents the complete agreement concerning subject
matter hereof. If any provision of this License is held to be
unenforceable, such provision shall be reformed only to the
extent necessary to make it enforceable. This License shall be
governed by the law of the jurisdiction specified in a notice
contained within the Original Software (except to the extent
applicable law, if any, provides otherwise), excluding such
jurisdiction's conflict-of-law provisions. Any litigation
relating to this License shall be subject to the jurisdiction of
the courts located in the jurisdiction and venue specified in a
notice contained within the Original Software, with the losing
party responsible for costs, including, without limitation, court
costs and reasonable attorneys' fees and expenses. The
application of the United Nations Convention on Contracts for the
International Sale of Goods is expressly excluded. Any law or
regulation which provides that the language of a contract shall
be construed against the drafter shall not apply to this License.
You agree that You alone are responsible for compliance with the
United States export administration regulations (and the export
control laws and regulation of any other countries) when You use,
distribute or otherwise make available any Covered Software.

10. RESPONSIBILITY FOR CLAIMS.

As between Initial Developer and the Contributors, each party is
responsible for claims and damages arising, directly or
indirectly, out of its utilization of rights under this License
and You agree to work with Initial Developer and Contributors to
distribute such responsibility on an equitable basis. Nothing
herein is intended or shall be deemed to constitute any admission
of liability.

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="JCR">
<pre>
Day Management AG ("Licensor") is willing to license this specification to you
ONLY UPON THE CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS
LICENSE AGREEMENT ("Agreement"). Please read the terms and conditions of this
Agreement carefully.

Content Repository for JavaTM Technology API Specification ("Specification")
Version: 1.0
Status: FCS
Release: 11 May 2005

Copyright 2005 Day Management AG
Barfnsserplatz 6, 4001 Basel, Switzerland.
All rights reserved.

NOTICE; LIMITED LICENSE GRANTS

1. License for Purposes of Evaluation and Developing Applications. Licensor
hereby grants you a fully-paid, non-exclusive, non-transferable, worldwide,
limited license (without the right to sublicense), under Licensor's applicable
intellectual property rights to view, download, use and reproduce the
Specification only for the purpose of internal evaluation. This includes
developing applications intended to run on an implementation of the
Specification provided that such applications do not themselves implement any
portion(s) of the Specification.

2. License for the Distribution of Compliant Implementations. Licensor also
grants you a perpetual, non-exclusive, non-transferable, worldwide, fully
paid-up, royalty free, limited license (without the right to sublicense) under
any applicable copyrights or, subject to the provisions of subsection 4 below,
patent rights it may have covering the Specification to create and/or distribute
an Independent Implementation of the Specification that: (a) fully implements
the Specification including all its required interfaces and functionality; (b)
does not modify, subset, superset or otherwise extend the Licensor Name Space,
or include any public or protected packages, classes, Java interfaces, fields or
methods within the Licensor Name Space other than those required/authorized by
the Specification or Specifications being implemented; and (c) passes the
Technology Compatibility Kit (including satisfying the requirements of the
applicable TCK Users Guide) for such Specification ("Compliant Implementation").
In addition, the foregoing license is expressly conditioned on your not acting
outside its scope. No license is granted hereunder for any other purpose
(including, for example, modifying the Specification, other than to the extent
of your fair use rights, or distributing the Specification to third parties).

3. Pass-through Conditions. You need not include limitations (a)-(c) from the
previous paragraph or any other particular "pass through" requirements in any
license You grant concerning the use of your Independent Implementation or
products derived from it. However, except with respect to Independent
Implementations (and products derived from them) that satisfy limitations
(a)-(c) from the previous paragraph, You may neither: (a) grant or otherwise
pass through to your licensees any licenses under Licensor's applicable
intellectual property rights; nor (b) authorize your licensees to make any
claims concerning their implementation's compliance with the Specification.

4. Reciprocity Concerning Patent Licenses. With respect to any patent claims
covered by the license granted under subparagraph 2 above that would be
infringed by all technically feasible implementations of the Specification, such
license is conditioned upon your offering on fair, reasonable and
non-discriminatory terms, to any party seeking it from You, a perpetual,
non-exclusive, non-transferable, worldwide license under Your patent rights that
are or would be infringed by all technically feasible implementations of the
Specification to develop, distribute and use a Compliant Implementation.

5. Definitions. For the purposes of this Agreement: "Independent Implementation"
shall mean an implementation of the Specification that neither derives from any
of Licensor's source code or binary code materials nor, except with an
appropriate and separate license from Licensor, includes any of Licensor's
source code or binary code materials; "Licensor Name Space" shall mean the
public class or interface declarations whose names begin with "java", "javax",
"javax.jcr" or their equivalents in any subsequent naming convention adopted by
Licensor through the Java Community Process, or any recognized successors or
replacements thereof; and "Technology Compatibility Kit" or "TCK" shall mean the
test suite and accompanying TCK User's Guide provided by Licensor which
corresponds to the particular version of the Specification being tested.

6. Termination. This Agreement will terminate immediately without notice from
Licensor if you fail to comply with any material provision of or act outside the
scope of the licenses granted above.

7. Trademarks. No right, title, or interest in or to any trademarks, service
marks, or trade names of Licensor is granted hereunder. Java is a registered
trademark of Sun Microsystems, Inc. in the United States and other countries.

8. Disclaimer of Warranties. The Specification is provided "AS IS". LICENSOR
MAKES NO REPRESENTATIONS OR WARRANTIES, EITHER EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
NON-INFRINGEMENT (INCLUDING AS A CONSEQUENCE OF ANY PRACTICE OR IMPLEMENTATION
OF THE SPECIFICATION), OR THAT THE CONTENTS OF THE SPECIFICATION ARE SUITABLE
FOR ANY PURPOSE. This document does not represent any commitment to release or
implement any portion of the Specification in any product.

The Specification could include technical inaccuracies or typographical errors.
Changes are periodically added to the information therein; these changes will be
incorporated into new versions of the Specification, if any. Licensor may make
improvements and/or changes to the product(s) and/or the program(s) described in
the Specification at any time. Any use of such changes in the Specification will
be governed by the then-current license for the applicable version of the
Specification.

9. Limitation of Liability. TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT
WILL LICENSOR BE LIABLE FOR ANY DAMAGES, INCLUDING WITHOUT LIMITATION, LOST
REVENUE, PROFITS OR DATA, OR FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR
PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
ARISING OUT OF OR RELATED TO ANY FURNISHING, PRACTICING, MODIFYING OR ANY USE OF
THE SPECIFICATION, EVEN IF LICENSOR HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
DAMAGES.

10. Report. If you provide Licensor with any comments or suggestions in
connection with your use of the Specification ("Feedback"), you hereby: (i)
agree that such Feedback is provided on a non-proprietary and non-confidential
basis, and (ii) grant Licensor a perpetual, non-exclusive, worldwide, fully
paid-up, irrevocable license, with the right to sublicense through multiple
levels of sublicensees, to incorporate, disclose, and use without limitation the
Feedback for any purpose related to the Specification and future versions,
implementations, and test suites thereof.


</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="JDOM">
<pre>
http://www.jdom.org

 Copyright (C) 2000-2004 Jason Hunter & Brett McLaughlin.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.
 
 4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
 Alternatively, the acknowledgment may be graphical using the logos 
 available at http://www.jdom.org/images/logos.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.

 This software consists of voluntary contributions made by many 
 individuals on behalf of the JDOM Project and was originally 
 created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
 Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
 on the JDOM Project, please see <http://www.jdom.org/>. 


</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="JUNIT">
<pre>
CPL 1.0 
Common Public License - v 1.0 


THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS COMMON 
PUBLIC LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF 
THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT. 


1. DEFINITIONS 

"Contribution" means: 

a) in the case of the initial Contributor, the initial code and documentation 
   distributed under this Agreement, and
b) in the case of each subsequent Contributor:
	i) changes to the Program, and
	ii) additions to the Program;
	
where such changes and/or additions to the Program originate from and 
are distributed by that particular Contributor. A Contribution 'originates' 
from a Contributor if it was added to the Program by such Contributor 
itself or anyone acting on such Contributor's behalf. Contributions do 
not include additions to the Program which: (i) are separate modules 
of software distributed in conjunction with the Program under their 
own license agreement, and (ii) are not derivative works of the Program. 

"Contributor" means any person or entity that distributes the Program. 


"Licensed Patents " mean patent claims licensable by a Contributor 
which are necessarily infringed by the use or sale of its Contribution 
alone or when combined with the Program. 


"Program" means the Contributions distributed in accordance with 
this Agreement. 


"Recipient" means anyone who receives the Program under this Agreement, 
including all Contributors. 


2. GRANT OF RIGHTS 

a) Subject to the terms of this Agreement, each Contributor hereby grants 
   Recipient a non-exclusive, worldwide, royalty-free copyright license to 
   reproduce, prepare derivative works of, publicly display, publicly 
   perform, distribute and sublicense the Contribution of such Contributor, 
   if any, and such derivative works, in source code and object code form.
   
b) Subject to the terms of this Agreement, each Contributor hereby grants 
   Recipient a non-exclusive, worldwide, royalty-free patent license under 
   Licensed Patents to make, use, sell, offer to sell, import and otherwise 
   transfer the Contribution of such Contributor, if any, in source code and 
   object code form. This patent license shall apply to the combination of 
   the Contribution and the Program if, at the time the Contribution is added 
   by the Contributor, such addition of the Contribution causes such 
   combination to be covered by the Licensed Patents. The patent license 
   shall not apply to any other combinations which include the Contribution. 
   No hardware per se is licensed hereunder. 
   
c) Recipient understands that although each Contributor grants the licenses to 
   its Contributions set forth herein, no assurances are provided by any Contributor 
   that the Program does not infringe the patent or other intellectual property 
   rights of any other entity. Each Contributor disclaims any liability to 
   Recipient for claims brought by any other entity based on infringement of 
   intellectual property rights or otherwise. As a condition to exercising the 
   rights and licenses granted hereunder, each Recipient hereby assumes sole 
   responsibility to secure any other intellectual property rights needed, if 
   any. For example, if a third party patent license is required to allow 
   Recipient to distribute the Program, it is Recipient's responsibility to 
   acquire that license before distributing the Program.

d) Each Contributor represents that to its knowledge it has sufficient 
   copyright rights in its Contribution, if any, to grant the copyright 
   license set forth in this Agreement. 
   
3. REQUIREMENTS 

A Contributor may choose to distribute the Program in object code form under 
its own license agreement, provided that: 

a) it complies with the terms and conditions of this Agreement; and
b) its license agreement:
  i) effectively disclaims on behalf of all Contributors all warranties and 
     conditions, express and implied, including warranties or conditions of 
     title and non-infringement, and implied warranties or conditions of 
     merchantability and fitness for a particular purpose; 
   
  ii) effectively excludes on behalf of all Contributors all liability for 
      damages, including direct, indirect, special, incidental and consequential 
	  damages, such as lost profits; 
  iii) states that any provisions which differ from this Agreement are 
       offered by that Contributor alone and not by any other party; and
	   
  iv) states that source code for the Program is available from such 
       Contributor, and informs licensees how to obtain it in a reasonable 
	   manner on or through a medium customarily used for software exchange. 
	   
When the Program is made available in source code form: 

  a) it must be made available under this Agreement; and 
  b) a copy of this Agreement must be included with each copy of the Program. 

Contributors may not remove or alter any copyright notices contained 
within the Program. 


Each Contributor must identify itself as the originator of its Contribution, 
if any, in a manner that reasonably allows subsequent Recipients to identify 
the originator of the Contribution. 


4. COMMERCIAL DISTRIBUTION 

Commercial distributors of software may accept certain responsibilities 
with respect to end users, business partners and the like. While this 
license is intended to facilitate the commercial use of the Program, the 
Contributor who includes the Program in a commercial product offering 
should do so in a manner which does not create potential liability for 
other Contributors. Therefore, if a Contributor includes the Program in 
a commercial product offering, such Contributor ("Commercial Contributor") 
hereby agrees to defend and indemnify every other Contributor ("Indemnified 
Contributor") against any losses, damages and costs (collectively "Losses") 
arising from claims, lawsuits and other legal actions brought by a third 
party against the Indemnified Contributor to the extent caused by the acts 
or omissions of such Commercial Contributor in connection with its 
distribution of the Program in a commercial product offering. The 
obligations in this section do not apply to any claims or Losses relating 
to any actual or alleged intellectual property infringement. In order to 
qualify, an Indemnified Contributor must: a) promptly notify the Commercial 
Contributor in writing of such claim, and b) allow the Commercial Contributor 
to control, and cooperate with the Commercial Contributor in, the defense 
and any related settlement negotiations. The Indemnified Contributor may 
participate in any such claim at its own expense. 


For example, a Contributor might include the Program in a commercial 
product offering, Product X. That Contributor is then a Commercial 
Contributor. If that Commercial Contributor then makes performance 
claims, or offers warranties related to Product X, those performance 
claims and warranties are such Commercial Contributor's responsibility 
alone. Under this section, the Commercial Contributor would have to 
defend claims against the other Contributors related to those performance 
claims and warranties, and if a court requires any other Contributor to 
pay any damages as a result, the Commercial Contributor must pay those damages. 


5. NO WARRANTY 

EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN 
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS 
OR IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF 
TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. 
Each Recipient is solely responsible for determining the appropriateness of 
using and distributing the Program and assumes all risks associated with 
its exercise of rights under this Agreement, including but not limited to 
the risks and costs of program errors, compliance with applicable laws, 
damage to or loss of data, programs or equipment, and unavailability or 
interruption of operations. 


6. DISCLAIMER OF LIABILITY 

EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, NEITHER RECIPIENT NOR 
ANY CONTRIBUTORS SHALL HAVE ANY LIABILITY FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING 
WITHOUT LIMITATION LOST PROFITS), HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION 
OF THE PROGRAM OR THE EXERCISE OF ANY RIGHTS GRANTED HEREUNDER, EVEN IF 
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. 


7. GENERAL 

If any provision of this Agreement is invalid or unenforceable under 
applicable law, it shall not affect the validity or enforceability of 
the remainder of the terms of this Agreement, and without further 
action by the parties hereto, such provision shall be reformed to 
the minimum extent necessary to make such provision valid and enforceable. 


If Recipient institutes patent litigation against a Contributor with 
respect to a patent applicable to software (including a cross-claim 
or counterclaim in a lawsuit), then any patent licenses granted by 
that Contributor to such Recipient under this Agreement shall terminate 
as of the date such litigation is filed. In addition, if Recipient 
institutes patent litigation against any entity (including a cross-claim 
or counterclaim in a lawsuit) alleging that the Program itself 
(excluding combinations of the Program with other software or hardware) 
infringes such Recipient's patent(s), then such Recipient's rights 
granted under Section 2(b) shall terminate as of the date such litigation 
is filed. 


All Recipient's rights under this Agreement shall terminate if it fails 
to comply with any of the material terms or conditions of this Agreement 
and does not cure such failure in a reasonable period of time after 
becoming aware of such noncompliance. If all Recipient's rights under 
this Agreement terminate, Recipient agrees to cease use and distribution 
of the Program as soon as reasonably practicable. However, Recipient's 
obligations under this Agreement and any licenses granted by Recipient 
relating to the Program shall continue and survive. 


Everyone is permitted to copy and distribute copies of this Agreement, 
but in order to avoid inconsistency the Agreement is copyrighted and 
may only be modified in the following manner. The Agreement Steward 
reserves the right to publish new versions (including revisions) of 
this Agreement from time to time. No one other than the Agreement Steward 
has the right to modify this Agreement. IBM is the initial Agreement 
Steward. IBM may assign the responsibility to serve as the Agreement 
Steward to a suitable separate entity. Each new version of the Agreement 
will be given a distinguishing version number. The Program (including 
Contributions) may always be distributed subject to the version of the 
Agreement under which it was received. In addition, after a new version 
of the Agreement is published, Contributor may elect to distribute the 
Program (including its Contributions) under the new version. Except as 
expressly stated in Sections 2(a) and 2(b) above, Recipient receives 
no rights or licenses to the intellectual property of any Contributor 
under this Agreement, whether expressly, by implication, estoppel or 
otherwise. All rights in the Program not expressly granted under this 
Agreement are reserved. 


This Agreement is governed by the laws of the State of New York and 
the intellectual property laws of the United States of America. No 
party to this Agreement will bring a legal action under this Agreement 
more than one year after the cause of action arose. Each party waives 
its rights to a jury trial in any resulting litigation. 

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="MYSQL">
<pre>
GPL http://www.gnu.org/licenses/gpl-3.0.txt

                    GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

                            Preamble

  The GNU General Public License is a free, copyleft license for
software and other kinds of works.

  The licenses for most software and other practical works are designed
to take away your freedom to share and change the works.  By contrast,
the GNU General Public License is intended to guarantee your freedom to
share and change all versions of a program--to make sure it remains free
software for all its users.  We, the Free Software Foundation, use the
GNU General Public License for most of our software; it applies also to
any other work released this way by its authors.  You can apply it to
your programs, too.

  When we speak of free software, we are referring to freedom, not
price.  Our General Public Licenses are designed to make sure that you
have the freedom to distribute copies of free software (and charge for
them if you wish), that you receive source code or can get it if you
want it, that you can change the software or use pieces of it in new
free programs, and that you know you can do these things.

  To protect your rights, we need to prevent others from denying you
these rights or asking you to surrender the rights.  Therefore, you have
certain responsibilities if you distribute copies of the software, or if
you modify it: responsibilities to respect the freedom of others.

  For example, if you distribute copies of such a program, whether
gratis or for a fee, you must pass on to the recipients the same
freedoms that you received.  You must make sure that they, too, receive
or can get the source code.  And you must show them these terms so they
know their rights.

  Developers that use the GNU GPL protect your rights with two steps:
(1) assert copyright on the software, and (2) offer you this License
giving you legal permission to copy, distribute and/or modify it.

  For the developers' and authors' protection, the GPL clearly explains
that there is no warranty for this free software.  For both users' and
authors' sake, the GPL requires that modified versions be marked as
changed, so that their problems will not be attributed erroneously to
authors of previous versions.

  Some devices are designed to deny users access to install or run
modified versions of the software inside them, although the manufacturer
can do so.  This is fundamentally incompatible with the aim of
protecting users' freedom to change the software.  The systematic
pattern of such abuse occurs in the area of products for individuals to
use, which is precisely where it is most unacceptable.  Therefore, we
have designed this version of the GPL to prohibit the practice for those
products.  If such problems arise substantially in other domains, we
stand ready to extend this provision to those domains in future versions
of the GPL, as needed to protect the freedom of users.

  Finally, every program is threatened constantly by software patents.
States should not allow patents to restrict development and use of
software on general-purpose computers, but in those that do, we wish to
avoid the special danger that patents applied to a free program could
make it effectively proprietary.  To prevent this, the GPL assures that
patents cannot be used to render the program non-free.

  The precise terms and conditions for copying, distribution and
modification follow.

                       TERMS AND CONDITIONS

  0. Definitions.

  "This License" refers to version 3 of the GNU General Public License.

  "Copyright" also means copyright-like laws that apply to other kinds of
works, such as semiconductor masks.

  "The Program" refers to any copyrightable work licensed under this
License.  Each licensee is addressed as "you".  "Licensees" and
"recipients" may be individuals or organizations.

  To "modify" a work means to copy from or adapt all or part of the work
in a fashion requiring copyright permission, other than the making of an
exact copy.  The resulting work is called a "modified version" of the
earlier work or a work "based on" the earlier work.

  A "covered work" means either the unmodified Program or a work based
on the Program.

  To "propagate" a work means to do anything with it that, without
permission, would make you directly or secondarily liable for
infringement under applicable copyright law, except executing it on a
computer or modifying a private copy.  Propagation includes copying,
distribution (with or without modification), making available to the
public, and in some countries other activities as well.

  To "convey" a work means any kind of propagation that enables other
parties to make or receive copies.  Mere interaction with a user through
a computer network, with no transfer of a copy, is not conveying.

  An interactive user interface displays "Appropriate Legal Notices"
to the extent that it includes a convenient and prominently visible
feature that (1) displays an appropriate copyright notice, and (2)
tells the user that there is no warranty for the work (except to the
extent that warranties are provided), that licensees may convey the
work under this License, and how to view a copy of this License.  If
the interface presents a list of user commands or options, such as a
menu, a prominent item in the list meets this criterion.

  1. Source Code.

  The "source code" for a work means the preferred form of the work
for making modifications to it.  "Object code" means any non-source
form of a work.

  A "Standard Interface" means an interface that either is an official
standard defined by a recognized standards body, or, in the case of
interfaces specified for a particular programming language, one that
is widely used among developers working in that language.

  The "System Libraries" of an executable work include anything, other
than the work as a whole, that (a) is included in the normal form of
packaging a Major Component, but which is not part of that Major
Component, and (b) serves only to enable use of the work with that
Major Component, or to implement a Standard Interface for which an
implementation is available to the public in source code form.  A
"Major Component", in this context, means a major essential component
(kernel, window system, and so on) of the specific operating system
(if any) on which the executable work runs, or a compiler used to
produce the work, or an object code interpreter used to run it.

  The "Corresponding Source" for a work in object code form means all
the source code needed to generate, install, and (for an executable
work) run the object code and to modify the work, including scripts to
control those activities.  However, it does not include the work's
System Libraries, or general-purpose tools or generally available free
programs which are used unmodified in performing those activities but
which are not part of the work.  For example, Corresponding Source
includes interface definition files associated with source files for
the work, and the source code for shared libraries and dynamically
linked subprograms that the work is specifically designed to require,
such as by intimate data communication or control flow between those
subprograms and other parts of the work.

  The Corresponding Source need not include anything that users
can regenerate automatically from other parts of the Corresponding
Source.

  The Corresponding Source for a work in source code form is that
same work.

  2. Basic Permissions.

  All rights granted under this License are granted for the term of
copyright on the Program, and are irrevocable provided the stated
conditions are met.  This License explicitly affirms your unlimited
permission to run the unmodified Program.  The output from running a
covered work is covered by this License only if the output, given its
content, constitutes a covered work.  This License acknowledges your
rights of fair use or other equivalent, as provided by copyright law.

  You may make, run and propagate covered works that you do not
convey, without conditions so long as your license otherwise remains
in force.  You may convey covered works to others for the sole purpose
of having them make modifications exclusively for you, or provide you
with facilities for running those works, provided that you comply with
the terms of this License in conveying all material for which you do
not control copyright.  Those thus making or running the covered works
for you must do so exclusively on your behalf, under your direction
and control, on terms that prohibit them from making any copies of
your copyrighted material outside their relationship with you.

  Conveying under any other circumstances is permitted solely under
the conditions stated below.  Sublicensing is not allowed; section 10
makes it unnecessary.

  3. Protecting Users' Legal Rights From Anti-Circumvention Law.

  No covered work shall be deemed part of an effective technological
measure under any applicable law fulfilling obligations under article
11 of the WIPO copyright treaty adopted on 20 December 1996, or
similar laws prohibiting or restricting circumvention of such
measures.

  When you convey a covered work, you waive any legal power to forbid
circumvention of technological measures to the extent such circumvention
is effected by exercising rights under this License with respect to
the covered work, and you disclaim any intention to limit operation or
modification of the work as a means of enforcing, against the work's
users, your or third parties' legal rights to forbid circumvention of
technological measures.

  4. Conveying Verbatim Copies.

  You may convey verbatim copies of the Program's source code as you
receive it, in any medium, provided that you conspicuously and
appropriately publish on each copy an appropriate copyright notice;
keep intact all notices stating that this License and any
non-permissive terms added in accord with section 7 apply to the code;
keep intact all notices of the absence of any warranty; and give all
recipients a copy of this License along with the Program.

  You may charge any price or no price for each copy that you convey,
and you may offer support or warranty protection for a fee.

  5. Conveying Modified Source Versions.

  You may convey a work based on the Program, or the modifications to
produce it from the Program, in the form of source code under the
terms of section 4, provided that you also meet all of these conditions:

    a) The work must carry prominent notices stating that you modified
    it, and giving a relevant date.

    b) The work must carry prominent notices stating that it is
    released under this License and any conditions added under section
    7.  This requirement modifies the requirement in section 4 to
    "keep intact all notices".

    c) You must license the entire work, as a whole, under this
    License to anyone who comes into possession of a copy.  This
    License will therefore apply, along with any applicable section 7
    additional terms, to the whole of the work, and all its parts,
    regardless of how they are packaged.  This License gives no
    permission to license the work in any other way, but it does not
    invalidate such permission if you have separately received it.

    d) If the work has interactive user interfaces, each must display
    Appropriate Legal Notices; however, if the Program has interactive
    interfaces that do not display Appropriate Legal Notices, your
    work need not make them do so.

  A compilation of a covered work with other separate and independent
works, which are not by their nature extensions of the covered work,
and which are not combined with it such as to form a larger program,
in or on a volume of a storage or distribution medium, is called an
"aggregate" if the compilation and its resulting copyright are not
used to limit the access or legal rights of the compilation's users
beyond what the individual works permit.  Inclusion of a covered work
in an aggregate does not cause this License to apply to the other
parts of the aggregate.

  6. Conveying Non-Source Forms.

  You may convey a covered work in object code form under the terms
of sections 4 and 5, provided that you also convey the
machine-readable Corresponding Source under the terms of this License,
in one of these ways:

    a) Convey the object code in, or embodied in, a physical product
    (including a physical distribution medium), accompanied by the
    Corresponding Source fixed on a durable physical medium
    customarily used for software interchange.

    b) Convey the object code in, or embodied in, a physical product
    (including a physical distribution medium), accompanied by a
    written offer, valid for at least three years and valid for as
    long as you offer spare parts or customer support for that product
    model, to give anyone who possesses the object code either (1) a
    copy of the Corresponding Source for all the software in the
    product that is covered by this License, on a durable physical
    medium customarily used for software interchange, for a price no
    more than your reasonable cost of physically performing this
    conveying of source, or (2) access to copy the
    Corresponding Source from a network server at no charge.

    c) Convey individual copies of the object code with a copy of the
    written offer to provide the Corresponding Source.  This
    alternative is allowed only occasionally and noncommercially, and
    only if you received the object code with such an offer, in accord
    with subsection 6b.

    d) Convey the object code by offering access from a designated
    place (gratis or for a charge), and offer equivalent access to the
    Corresponding Source in the same way through the same place at no
    further charge.  You need not require recipients to copy the
    Corresponding Source along with the object code.  If the place to
    copy the object code is a network server, the Corresponding Source
    may be on a different server (operated by you or a third party)
    that supports equivalent copying facilities, provided you maintain
    clear directions next to the object code saying where to find the
    Corresponding Source.  Regardless of what server hosts the
    Corresponding Source, you remain obligated to ensure that it is
    available for as long as needed to satisfy these requirements.

    e) Convey the object code using peer-to-peer transmission, provided
    you inform other peers where the object code and Corresponding
    Source of the work are being offered to the general public at no
    charge under subsection 6d.

  A separable portion of the object code, whose source code is excluded
from the Corresponding Source as a System Library, need not be
included in conveying the object code work.

  A "User Product" is either (1) a "consumer product", which means any
tangible personal property which is normally used for personal, family,
or household purposes, or (2) anything designed or sold for incorporation
into a dwelling.  In determining whether a product is a consumer product,
doubtful cases shall be resolved in favor of coverage.  For a particular
product received by a particular user, "normally used" refers to a
typical or common use of that class of product, regardless of the status
of the particular user or of the way in which the particular user
actually uses, or expects or is expected to use, the product.  A product
is a consumer product regardless of whether the product has substantial
commercial, industrial or non-consumer uses, unless such uses represent
the only significant mode of use of the product.

  "Installation Information" for a User Product means any methods,
procedures, authorization keys, or other information required to install
and execute modified versions of a covered work in that User Product from
a modified version of its Corresponding Source.  The information must
suffice to ensure that the continued functioning of the modified object
code is in no case prevented or interfered with solely because
modification has been made.

  If you convey an object code work under this section in, or with, or
specifically for use in, a User Product, and the conveying occurs as
part of a transaction in which the right of possession and use of the
User Product is transferred to the recipient in perpetuity or for a
fixed term (regardless of how the transaction is characterized), the
Corresponding Source conveyed under this section must be accompanied
by the Installation Information.  But this requirement does not apply
if neither you nor any third party retains the ability to install
modified object code on the User Product (for example, the work has
been installed in ROM).

  The requirement to provide Installation Information does not include a
requirement to continue to provide support service, warranty, or updates
for a work that has been modified or installed by the recipient, or for
the User Product in which it has been modified or installed.  Access to a
network may be denied when the modification itself materially and
adversely affects the operation of the network or violates the rules and
protocols for communication across the network.

  Corresponding Source conveyed, and Installation Information provided,
in accord with this section must be in a format that is publicly
documented (and with an implementation available to the public in
source code form), and must require no special password or key for
unpacking, reading or copying.

  7. Additional Terms.

  "Additional permissions" are terms that supplement the terms of this
License by making exceptions from one or more of its conditions.
Additional permissions that are applicable to the entire Program shall
be treated as though they were included in this License, to the extent
that they are valid under applicable law.  If additional permissions
apply only to part of the Program, that part may be used separately
under those permissions, but the entire Program remains governed by
this License without regard to the additional permissions.

  When you convey a copy of a covered work, you may at your option
remove any additional permissions from that copy, or from any part of
it.  (Additional permissions may be written to require their own
removal in certain cases when you modify the work.)  You may place
additional permissions on material, added by you to a covered work,
for which you have or can give appropriate copyright permission.

  Notwithstanding any other provision of this License, for material you
add to a covered work, you may (if authorized by the copyright holders of
that material) supplement the terms of this License with terms:

    a) Disclaiming warranty or limiting liability differently from the
    terms of sections 15 and 16 of this License; or

    b) Requiring preservation of specified reasonable legal notices or
    author attributions in that material or in the Appropriate Legal
    Notices displayed by works containing it; or

    c) Prohibiting misrepresentation of the origin of that material, or
    requiring that modified versions of such material be marked in
    reasonable ways as different from the original version; or

    d) Limiting the use for publicity purposes of names of licensors or
    authors of the material; or

    e) Declining to grant rights under trademark law for use of some
    trade names, trademarks, or service marks; or

    f) Requiring indemnification of licensors and authors of that
    material by anyone who conveys the material (or modified versions of
    it) with contractual assumptions of liability to the recipient, for
    any liability that these contractual assumptions directly impose on
    those licensors and authors.

  All other non-permissive additional terms are considered "further
restrictions" within the meaning of section 10.  If the Program as you
received it, or any part of it, contains a notice stating that it is
governed by this License along with a term that is a further
restriction, you may remove that term.  If a license document contains
a further restriction but permits relicensing or conveying under this
License, you may add to a covered work material governed by the terms
of that license document, provided that the further restriction does
not survive such relicensing or conveying.

  If you add terms to a covered work in accord with this section, you
must place, in the relevant source files, a statement of the
additional terms that apply to those files, or a notice indicating
where to find the applicable terms.

  Additional terms, permissive or non-permissive, may be stated in the
form of a separately written license, or stated as exceptions;
the above requirements apply either way.

  8. Termination.

  You may not propagate or modify a covered work except as expressly
provided under this License.  Any attempt otherwise to propagate or
modify it is void, and will automatically terminate your rights under
this License (including any patent licenses granted under the third
paragraph of section 11).

  However, if you cease all violation of this License, then your
license from a particular copyright holder is reinstated (a)
provisionally, unless and until the copyright holder explicitly and
finally terminates your license, and (b) permanently, if the copyright
holder fails to notify you of the violation by some reasonable means
prior to 60 days after the cessation.

  Moreover, your license from a particular copyright holder is
reinstated permanently if the copyright holder notifies you of the
violation by some reasonable means, this is the first time you have
received notice of violation of this License (for any work) from that
copyright holder, and you cure the violation prior to 30 days after
your receipt of the notice.

  Termination of your rights under this section does not terminate the
licenses of parties who have received copies or rights from you under
this License.  If your rights have been terminated and not permanently
reinstated, you do not qualify to receive new licenses for the same
material under section 10.

  9. Acceptance Not Required for Having Copies.

  You are not required to accept this License in order to receive or
run a copy of the Program.  Ancillary propagation of a covered work
occurring solely as a consequence of using peer-to-peer transmission
to receive a copy likewise does not require acceptance.  However,
nothing other than this License grants you permission to propagate or
modify any covered work.  These actions infringe copyright if you do
not accept this License.  Therefore, by modifying or propagating a
covered work, you indicate your acceptance of this License to do so.

  10. Automatic Licensing of Downstream Recipients.

  Each time you convey a covered work, the recipient automatically
receives a license from the original licensors, to run, modify and
propagate that work, subject to this License.  You are not responsible
for enforcing compliance by third parties with this License.

  An "entity transaction" is a transaction transferring control of an
organization, or substantially all assets of one, or subdividing an
organization, or merging organizations.  If propagation of a covered
work results from an entity transaction, each party to that
transaction who receives a copy of the work also receives whatever
licenses to the work the party's predecessor in interest had or could
give under the previous paragraph, plus a right to possession of the
Corresponding Source of the work from the predecessor in interest, if
the predecessor has it or can get it with reasonable efforts.

  You may not impose any further restrictions on the exercise of the
rights granted or affirmed under this License.  For example, you may
not impose a license fee, royalty, or other charge for exercise of
rights granted under this License, and you may not initiate litigation
(including a cross-claim or counterclaim in a lawsuit) alleging that
any patent claim is infringed by making, using, selling, offering for
sale, or importing the Program or any portion of it.

  11. Patents.

  A "contributor" is a copyright holder who authorizes use under this
License of the Program or a work on which the Program is based.  The
work thus licensed is called the contributor's "contributor version".

  A contributor's "essential patent claims" are all patent claims
owned or controlled by the contributor, whether already acquired or
hereafter acquired, that would be infringed by some manner, permitted
by this License, of making, using, or selling its contributor version,
but do not include claims that would be infringed only as a
consequence of further modification of the contributor version.  For
purposes of this definition, "control" includes the right to grant
patent sublicenses in a manner consistent with the requirements of
this License.

  Each contributor grants you a non-exclusive, worldwide, royalty-free
patent license under the contributor's essential patent claims, to
make, use, sell, offer for sale, import and otherwise run, modify and
propagate the contents of its contributor version.

  In the following three paragraphs, a "patent license" is any express
agreement or commitment, however denominated, not to enforce a patent
(such as an express permission to practice a patent or covenant not to
sue for patent infringement).  To "grant" such a patent license to a
party means to make such an agreement or commitment not to enforce a
patent against the party.

  If you convey a covered work, knowingly relying on a patent license,
and the Corresponding Source of the work is not available for anyone
to copy, free of charge and under the terms of this License, through a
publicly available network server or other readily accessible means,
then you must either (1) cause the Corresponding Source to be so
available, or (2) arrange to deprive yourself of the benefit of the
patent license for this particular work, or (3) arrange, in a manner
consistent with the requirements of this License, to extend the patent
license to downstream recipients.  "Knowingly relying" means you have
actual knowledge that, but for the patent license, your conveying the
covered work in a country, or your recipient's use of the covered work
in a country, would infringe one or more identifiable patents in that
country that you have reason to believe are valid.

  If, pursuant to or in connection with a single transaction or
arrangement, you convey, or propagate by procuring conveyance of, a
covered work, and grant a patent license to some of the parties
receiving the covered work authorizing them to use, propagate, modify
or convey a specific copy of the covered work, then the patent license
you grant is automatically extended to all recipients of the covered
work and works based on it.

  A patent license is "discriminatory" if it does not include within
the scope of its coverage, prohibits the exercise of, or is
conditioned on the non-exercise of one or more of the rights that are
specifically granted under this License.  You may not convey a covered
work if you are a party to an arrangement with a third party that is
in the business of distributing software, under which you make payment
to the third party based on the extent of your activity of conveying
the work, and under which the third party grants, to any of the
parties who would receive the covered work from you, a discriminatory
patent license (a) in connection with copies of the covered work
conveyed by you (or copies made from those copies), or (b) primarily
for and in connection with specific products or compilations that
contain the covered work, unless you entered into that arrangement,
or that patent license was granted, prior to 28 March 2007.

  Nothing in this License shall be construed as excluding or limiting
any implied license or other defenses to infringement that may
otherwise be available to you under applicable patent law.

  12. No Surrender of Others' Freedom.

  If conditions are imposed on you (whether by court order, agreement or
otherwise) that contradict the conditions of this License, they do not
excuse you from the conditions of this License.  If you cannot convey a
covered work so as to satisfy simultaneously your obligations under this
License and any other pertinent obligations, then as a consequence you may
not convey it at all.  For example, if you agree to terms that obligate you
to collect a royalty for further conveying from those to whom you convey
the Program, the only way you could satisfy both those terms and this
License would be to refrain entirely from conveying the Program.

  13. Use with the GNU Affero General Public License.

  Notwithstanding any other provision of this License, you have
permission to link or combine any covered work with a work licensed
under version 3 of the GNU Affero General Public License into a single
combined work, and to convey the resulting work.  The terms of this
License will continue to apply to the part which is the covered work,
but the special requirements of the GNU Affero General Public License,
section 13, concerning interaction through a network will apply to the
combination as such.

  14. Revised Versions of this License.

  The Free Software Foundation may publish revised and/or new versions of
the GNU General Public License from time to time.  Such new versions will
be similar in spirit to the present version, but may differ in detail to
address new problems or concerns.

  Each version is given a distinguishing version number.  If the
Program specifies that a certain numbered version of the GNU General
Public License "or any later version" applies to it, you have the
option of following the terms and conditions either of that numbered
version or of any later version published by the Free Software
Foundation.  If the Program does not specify a version number of the
GNU General Public License, you may choose any version ever published
by the Free Software Foundation.

  If the Program specifies that a proxy can decide which future
versions of the GNU General Public License can be used, that proxy's
public statement of acceptance of a version permanently authorizes you
to choose that version for the Program.

  Later license versions may give you additional or different
permissions.  However, no additional obligations are imposed on any
author or copyright holder as a result of your choosing to follow a
later version.

  15. Disclaimer of Warranty.

  THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY
APPLICABLE LAW.  EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT
HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS IS" WITHOUT WARRANTY
OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM
IS WITH YOU.  SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF
ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

  16. Limitation of Liability.

  IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING
WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS
THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY
GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE
USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF
DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD
PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS),
EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGES.

  17. Interpretation of Sections 15 and 16.

  If the disclaimer of warranty and limitation of liability provided
above cannot be given local legal effect according to their terms,
reviewing courts shall apply local law that most closely approximates
an absolute waiver of all civil liability in connection with the
Program, unless a warranty or assumption of liability accompanies a
copy of the Program in return for a fee.

                     END OF TERMS AND CONDITIONS

            How to Apply These Terms to Your New Programs

  If you develop a new program, and you want it to be of the greatest
possible use to the public, the best way to achieve this is to make it
free software which everyone can redistribute and change under these terms.

  To do so, attach the following notices to the program.  It is safest
to attach them to the start of each source file to most effectively
state the exclusion of warranty; and each file should have at least
the "copyright" line and a pointer to where the full notice is found.

    <one line to give the program's name and a brief idea of what it does.>
    Copyright (C) <year>  <name of author>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

Also add information on how to contact you by electronic and paper mail.

  If the program does terminal interaction, make it output a short
notice like this when it starts in an interactive mode:

    <program>  Copyright (C) <year>  <name of author>
    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.
    This is free software, and you are welcome to redistribute it
    under certain conditions; type `show c' for details.

The hypothetical commands `show w' and `show c' should show the appropriate
parts of the General Public License.  Of course, your program's commands
might be different; for a GUI interface, you would use an "about box".

  You should also get your employer (if you work as a programmer) or school,
if any, to sign a "copyright disclaimer" for the program, if necessary.
For more information on this, and how to apply and follow the GNU GPL, see
<http://www.gnu.org/licenses/>.

  The GNU General Public License does not permit incorporating your program
into proprietary programs.  If your program is a subroutine library, you
may consider it more useful to permit linking proprietary applications with
the library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.  But first, please read
<http://www.gnu.org/philosophy/why-not-lgpl.html>.

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="SLF4J">
<pre>
http://www.slf4j.org/license.html

Copyright (c) 2004-2007 QOS.ch 
All rights reserved. 

Permission is hereby granted, free of charge, to any person obtaining 
a copy of this software and associated documentation files (the 
"Software"), to deal in the Software without restriction, including 
without limitation the rights to use, copy, modify, merge, publish, 
distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to 
the following conditions: 
The above copyright notice and this permission notice shall be 
included in all copies or substantial portions of the Software. 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="TimeLine">
<pre>
http://simile.mit.edu/license.html

� Copyright The SIMILE Project 2003-2005.
   
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="Trove">
<pre>
http://trove4j.sourceforge.net/html/license.html

The source code for GNU Trove is licensed under the Lesser GNU Public License (LGPL).

    Copyright (c) 2001, Eric D. Friedman All Rights Reserved. This library 
	is free software; you can redistribute it and/or modify it under the 
	terms of the GNU Lesser General Public License as published by the Free 
	Software Foundation; either version 2.1 of the License, or (at your option) 
	any later version. This library is distributed in the hope that it will 
	be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
	of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
	General Public License for more details. You should have received a 
	copy of the GNU Lesser General Public License along with this program; 
	if not, write to the Free Software Foundation, Inc., 59 Temple Place 
	- Suite 330, Boston, MA 02111-1307, USA.


Two classes (HashFunctions and PrimeFinder) included in Trove are licensed 
under the following terms:

    Copyright (c) 1999 CERN - European Organization for Nuclear Research. 
	Permission to use, copy, modify, distribute and sell this software and 
	its documentation for any purpose is hereby granted without fee, 
	provided that the above copyright notice appear in all copies and 
	that both that copyright notice and this permission notice appear in 
	supporting documentation. CERN makes no representations about the 
	suitability of this software for any purpose. It is provided "as is" 
	without expressed or implied warranty.

</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="Liferay">
<pre>
http://www.liferay.com/web/guest/products/licensing

(Uses MIT Licensing: http://www.opensource.org/licenses/mit-license.php)
</pre></ssf:expandableArea><p style="padding-top:6px;"/>

<ssf:expandableArea titleClass="ss_credits_title" title="JBoss">
<pre>
LGPL - http://jboss.com/opensource/lgpl/faq
</pre></ssf:expandableArea><p style="padding-top:6px;"/>



<br/>
<br/>
	<form class="ss_portlet_style ss_form" id="${ssNamespace}_btnForm" 
	  name="${ssNamespace}_btnForm" method="post" 
	  action="<portlet:renderURL windowState="normal"/>">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
	</form>
  </div>
</div>
