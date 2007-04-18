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
<form action="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="advanced_search"/>
					<portlet:param name="tabTitle" value=""/>
					<portlet:param name="newTab" value="1"/>
					</portlet:actionURL>" method="post" onSubmit="return ss_prepareAdditionalSearchOptions();" id="advSearchForm">
	<div id="ss_searchForm_container">
		<div id="ss_searchForm">
			<div id="ss_searchForm_main">
				<h4><ssf:nlt tag="searchForm.quicksearch.Title"/></h4>
				<a href="<portlet:actionURL windowState="maximized" portletMode="view">
					<portlet:param name="action" value="advanced_search"/>
					<portlet:param name="tabTitle" value=""/>
					<portlet:param name="newTab" value="1"/>
					</portlet:actionURL>" class="ss_advanced"><ssf:nlt tag="navigation.search.advanced"/></a>
				<div class="ss_clear"></div>
								
				<table>
					<tr><th><ssf:nlt tag="searchForm.searchText"/>:</th>
						<td><input type="text" name="searchText" id="searchText"/></td>
						<td>
							<a class="ss_searchButton" href="javascript: ss_search();" ><img src="<html:imagesPath/>pics/1pix.gif" /></a>
							<input type="hidden" name="quickSearch" value="true"/>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</form>
