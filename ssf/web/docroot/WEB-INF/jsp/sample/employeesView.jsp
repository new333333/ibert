<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1>SiteScape Employees</h1>

<table border="0" cellpadding="4">
	<tr><th>First Name</th><th>Last Name</th><th>Salary</th><th></th></tr>
	<c:forEach items="${employees}" var="employee">
		<tr>
			<td>${employee.firstName}</td>
			<td>${employee.lastName}</td>
			<td align="right">${employee.salary}</td>
			<td>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="incrementSalary"/>
						<portlet:param name="employee" value="${employee.key}"/>
						<portlet:param name="increment" value="1000"/>
					</portlet:actionURL>"><img title="Increase Salary" src="<html:imagesPath/>sample/increase.png" border=0 /></a>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="incrementSalary"/>
						<portlet:param name="employee" value="${employee.key}"/>
						<portlet:param name="increment" value="-1000"/>
					</portlet:actionURL>"><img title="Decrease Salary" src="<html:imagesPath/>sample/decrease.png" border=0 /></a>
				<a href="<portlet:renderURL>
						<portlet:param name="action" value="editEmployee"/>
						<portlet:param name="employee" value="${employee.key}"/>
					</portlet:renderURL>"><img title="Edit Employee Details" src="<html:imagesPath/>sample/edit.png" border=0 /></a>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="deleteEmployee"/>
						<portlet:param name="employee" value="${employee.key}"/>
					</portlet:actionURL>"><img title="Delete Employee" src="<html:imagesPath/>sample/delete.png" border=0 /></a>
			</td>
		</tr>
	</c:forEach>
		<tr>
			<td colspan="4">
				<a href="<portlet:renderURL>
						<portlet:param name="action" value="editEmployee"/>
					</portlet:renderURL>"><img title="Add New Employee" src="<html:imagesPath/>sample/new.png" border=0 /> Add New Employee</a>
			</td>
		</tr>
</table>
