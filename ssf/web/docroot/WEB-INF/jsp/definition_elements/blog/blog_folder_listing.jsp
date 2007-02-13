<% //View the listing part of a blog folder %>

  <table class="ss_blog" cellspacing="0" cellpadding="0" width="100%">
    <tr>
      <td class="ss_blog_content" width="80%" valign="top">
		  <c:forEach var="entry" items="${ssFolderEntries}" >
			<div class="ss_blog_content">
			  <c:set var="ss_blog_docId" value="${entry._docId}" scope="request"/>
			  <c:set var="ss_blog_workflowStateCaption" value="" scope="request"/>
			  <c:set var="ss_blog_reply_url" value="${ssBlogEntries[entry._docId].replyBlogUrl}" scope="request"/>
			  <c:if test="${!empty entry._workflowStateCaption}">
			  <c:set var="ss_blog_workflowStateCaption" value="${entry._workflowStateCaption}" scope="request"/>
			  </c:if>
			  <ssf:displayConfiguration 
			    configDefinition="${ssBlogEntries[entry._docId].ssConfigDefinition}" 
			    configElement="${ssBlogEntries[entry._docId].ssConfigElement}" 
			    configJspStyle="view"
			    processThisItem="true" 
			    entry="${ssBlogEntries[entry._docId].entry}" />
			</div>
		  </c:forEach>
	  </td>
	  <td class="ss_blog_sidebar" valign="top">
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="blog.calendar"/></div>
		<br>
		<div class="ss_blog_sidebar_hole">
		<div id="ss_blog_sidebar_date_popup"></div>
 		 <form name="ss_blog_sidebar_date_form" style="display:inline;">
		  <ssf:datepicker id="ss_blog_sidebar_date" 
            calendarDivId="ss_blog_sidebar_date_popup"
            formName="ss_blog_sidebar_date_form"
            immediateMode="true" initDate="${ssFolderEndDate}"
			callbackRoutine="ss_blog_sidebar_date_callback" />
          </form>
         </div>
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="blog.archives"/></div>
		<table>
		<c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  <tr>
		  <td><a href="${ssBlogMonthUrls[monthYear.key]}" 
		  class="<c:if test="${!empty selectedYearMonth && selectedYearMonth == ssBlogMonthTitles[monthYear.key]}">ss_bold</c:if>">
		  <c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a></td>
		  <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  </tr>
		</c:forEach>
		</table>
		
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
		
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>

		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
	  </td>
    </tr>
  </table>
