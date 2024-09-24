<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/publications/init.jsp" %>

<liferay-util:html-top>
	<link href="<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathModule() + "/change-tracking-web/publications/css/ChangeTrackingIndicator.css") %>" rel="stylesheet" />
</liferay-util:html-top>

<%
ViewTimelineHistoryDisplayContext viewTimelineHistoryDisplayContext = (ViewTimelineHistoryDisplayContext)request.getAttribute(CTWebKeys.VIEW_TIMELINE_HISTORY_DISPLAY_CONTEXT);
%>

<clay:container-fluid>
	<frontend-data-set:headless-display
		apiURL="<%= viewTimelineHistoryDisplayContext.getAPIURL() %>"
		fdsActionDropdownItems="<%= viewTimelineHistoryDisplayContext.getFDSActionDropdownItems() %>"
		id="<%= PublicationsFDSNames.PUBLICATIONS_TIMELINE_HISTORY %>"
		itemsPerPage="<%= 10 %>"
		selectedItemsKey="id"
		showManagementBar="<%= false %>"
		showPagination="<%= true %>"
		showSearch="<%= false %>"
	/>
</clay:container-fluid>