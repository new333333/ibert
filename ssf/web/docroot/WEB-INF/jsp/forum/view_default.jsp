<% // Default view if the forum has not been set up yet %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_portlet">
<span class="ss_content"><i>[This forum has not been configured yet.]</i></span>
<br/>
<br/>
<table border="0" cellpadding="4" cellspacing="0" width="100%">
<th align="left">Forum administration menu</th>
<tr><td></td></tr>
<tr>
	<td>
		<ul>
			<li>
				<font class="beta" size="2"><a class="gamma" 
				  href="<portlet:renderURL windowState="maximized">
				  	<portlet:param name="action" value="configure_forum" />
				  	<portlet:param name="forumId" value="${ssFolder.id}" />
		    		</portlet:renderURL>">Configure forum</a></font>
			</li>
			<li>
				<font class="beta" size="2"><a class="gamma" 
				  href="<portlet:actionURL windowState="maximized">
				  <portlet:param name="action" value="definition_builder" />
				  	<portlet:param name="forumId" value="${ssFolder.id}"/>
		    		</portlet:actionURL>">Definition builder</a></font>
			</li>
		</ul>
	</td>
</tr>
</table>

</div>


