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

<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="help.favoritesicon.title"/></span>

<p><ssf:nlt tag="help.favoritesicon.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.favoritesicon.content.using"/></p>

<p><ssf:nlt tag="help.favoritesicon.content.organizing"/></p>

<p><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/></p>

<div style="margin-left:25px;">

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Quick Tips.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt> <ssf:nlt tag="help.globalStrings.newWindow"/></a></p>

</div>
</div>
