<% // Blog view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<style type="text/css">
	body {
		min-width: 730px;         /* 2 x (LC fullwidth + CC padding) + RC fullwidth */
	}

	.blog_container2 {
		padding-right: 200px;     /* RC fullwidth + CC padding */
		overflow: hidden;
	}
	
	.blog_column2 {
		position: relative;
		float: left;
		padding-top: 1em;
		text-align: justify;
		overflow: hidden;
	}
	
	.blog_center_column2 {
        padding: 10px 10px;       /* CC padding */
		width: 100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_right_column2 {
		width: 160px;             /* RC width */
		padding: 0 10px;          /* RC padding */
		margin-right: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_container3 {
        padding-left: 220px;      /* LC fullwidth */
		padding-right: 170px;     /* RC fullwidth + CC padding */
		overflow: hidden;
	}
	
	.blog_column3 {
		position: relative;
		float: left;
		padding-top: 1em;
		text-align: justify;
		overflow: hidden;
	}
	
	.blog_center_column3 {
        padding: 10px 10px;       /* CC padding */
		width: 100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_left_column3 {
		width: 200px;             /* LC width */
		padding: 0 10px;          /* LC padding */
		right: 240px;             /* LC fullwidth + CC padding */
		margin-left: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_right_column3 {
		width: 130px;             /* RC width */
		padding: 0 10px;          /* RC padding */
		margin-right: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_footer {
		clear: both;
	}
	
	/*** IE Fix ***/
	* html .blog_left_column3 {
		left: 210px;              /* RC fullwidth */
	}

	.blog_footer-wrapper {
		float: left;
		position: relative;
		width: 100%;
		padding-bottom: 31001px;
		margin-bottom: -31000px;
		background: #FFF;         /*** Same as body background ***/
		overflow: hidden;
	}

	/*** Just for Looks ***/

	.blog_header, .blog_footer {
		padding: 0.3em 0;
		overflow: hidden;
	}

</style>


<script type="text/javascript">
function ss_loadEntry(obj,id) {
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="Loading" text="Loading..."/>");
	highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
</div>
<br>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999)
function ss_blog_sidebar_date_callback() {
	var url = "<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" >
    </ssf:url>";
	var formObj = document.ss_blog_sidebar_date_form
	url += "\&year=" + formObj.ss_blog_sidebar_date_year.value;
	url += "\&month=" + formObj.ss_blog_sidebar_date_month.value;
	url += "\&day=" + formObj.ss_blog_sidebar_date_date.value;
	url += "\&rn=" + rn++
	self.location.href = url;
}
</script>

<div class="folder" id="ss_blog_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/blog/blog_folder_listing.jsp" %>
</div>
