<%
// The dashboard "iframe" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div align="${ssDashboard.dashboard.components[ssDashboardId].data.align[0]}">
<iframe src="${ssDashboard.dashboard.components[ssDashboardId].data.url[0]}"
  style="width: ${ssDashboard.dashboard.components[ssDashboardId].data.width[0]};
  height: ${ssDashboard.dashboard.components[ssDashboardId].data.height[0]};" >xxx</iframe>
</div>
