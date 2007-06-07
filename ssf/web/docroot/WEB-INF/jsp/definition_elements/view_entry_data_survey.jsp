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
<%@ page import="java.util.Date" %>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%
	boolean overdue = false;
	if (request.getAttribute("ssDefinitionEntry") != null &&
		((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date") != null) {
		Date dueDate = (Date) ((DefinableEntity)request.getAttribute("ssDefinitionEntry")).getCustomAttribute("due_date").getValue();
		if (dueDate != null) {
			Date now = new Date();
			overdue = dueDate.after(now);
		}
	}
%>
<c:set var="overdue" value="<%= overdue %>" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_survey.js"></script>
<script type="text/javascript">
	ssSurvey.votedLabel = "<ssf:nlt tag="survey.vote.successfull"/>";
</script>

<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>


<c:set var="alreadyVoted" value="false"/>
<form id="ssSurveyForm_${property_name}" method="post">
	<input type="hidden" name="attributeName" value="${property_name}" />
	<c:forEach var="question" items="${ssDefinitionEntry.customAttributes[property_name].value.surveyModel.questions}" >
	<div class="ss_questionContainer">
		<p><c:out value="${question.question}" escapeXml="false"/></p>
		<c:if test="${overdue || question.alreadyVoted}">
			<ol>
			<c:forEach var="answer" items="${question.answers}">
				<li>
					<c:if test="${question.type == 'multiple' || question.type == 'single'}">
						<ssf:drawChart count="${answer.votesCount}" total="${question.totalResponses}"/>
					</c:if>
					<span><c:out value="${answer.text}" escapeXml="false"/></span>
					<div class="ss_clear"></div>
				</li>
			</c:forEach>
			</ol>
		</c:if>
		<c:if test="${!overdue && !question.alreadyVoted}">
			<c:if test="${question.type == 'multiple'}">
				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<li>
						<input type="checkbox" style="width: 19px;" name="answer_${question.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}" value="${answer.index}" />
						<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
					</li>
				</c:forEach>
				</ol>
			</c:if>
			<c:if test="${question.type == 'single'}">
				<ol>
				<c:forEach var="answer" items="${question.answers}">
					<li>
						<input type="radio" style="width: 19px;" name="answer_${question.index}" value="${answer.index}" id="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"/>
						<label for="${ss_namespace}_${property_name}_answer_${question.index}_${answer.index}"><c:out value="${answer.text}" escapeXml="false"/></label>
					</li>
				</c:forEach>
				</ol>
			</c:if>
			<c:if test="${question.type == 'input'}">
				<input type="text" name="answer_${question.index}">
			</c:if>
		</c:if>
	</div>
		<c:set var="alreadyVoted" value="${question.alreadyVoted}"/>
	</c:forEach>

	<c:if test="${!alreadyVoted}">
		<input type="button" value="Vote!" onclick="ssSurvey.vote('ssSurveyForm_${property_name}', ${ssBinder.id}, ${ssDefinitionEntry.id});"/>
	</c:if>	
</form>



</div>