/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.item.selector.web.internal.display.context;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Jonathan McCann
 */
public class ObjectDefinitionDisplayContext {

	public ObjectDefinitionDisplayContext(
		HttpServletRequest httpServletRequest,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectDefinitionSettingLocalService objectDefinitionSettingLocalService,
		String objectDefinitionSettingName, PortletURL portletURL,
		RenderRequest renderRequest) {

		_httpServletRequest = httpServletRequest;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectDefinitionSettingLocalService =
			objectDefinitionSettingLocalService;
		_objectDefinitionSettingName = objectDefinitionSettingName;
		_portletURL = portletURL;
		_renderRequest = renderRequest;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public SearchContainer<ObjectDefinition>
			getObjectDefinitionSearchContainer()
		throws PortalException {

		if (_objectDefinitionSearchContainer != null) {
			return _objectDefinitionSearchContainer;
		}

		SearchContainer<ObjectDefinition> objectDefinitionSearchContainer =
			new SearchContainer<>(
				_renderRequest, _portletURL, null, "there-are-no-objects");

		objectDefinitionSearchContainer.setId("selectObjectDefinition");
		objectDefinitionSearchContainer.setOrderByCol(_getOrderByCol());
		objectDefinitionSearchContainer.setOrderByType(_getOrderByType());

		String columnName = objectDefinitionSearchContainer.getOrderByCol();

		if (columnName.equals("modified-date")) {
			columnName = "modifiedDate";
		}

		OrderByComparator<ObjectDefinition> orderByComparator =
			OrderByComparatorFactoryUtil.create(
				"ObjectDefinition", columnName,
				Objects.equals(_getOrderByType(), "asc"));

		if (Validator.isNotNull(_objectDefinitionSettingName)) {
			objectDefinitionSearchContainer.setResultsAndTotal(
				_getObjectDefinitions(orderByComparator));
		}
		else {
			objectDefinitionSearchContainer.setResultsAndTotal(
				_objectDefinitionLocalService.getObjectDefinitions(
					_themeDisplay.getCompanyId(), true, false,
					WorkflowConstants.STATUS_APPROVED,
					objectDefinitionSearchContainer.getStart(),
					objectDefinitionSearchContainer.getEnd(),
					orderByComparator));
		}

		_objectDefinitionSearchContainer = objectDefinitionSearchContainer;

		return _objectDefinitionSearchContainer;
	}

	private List<ObjectDefinition> _getObjectDefinitions(
		OrderByComparator<ObjectDefinition> orderByComparator) {

		List<ObjectDefinition> objectDefinitions = new ArrayList<>(
			_objectDefinitionLocalService.getObjectDefinitions(
				_themeDisplay.getCompanyId(), true, false,
				WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, orderByComparator));

		for (ObjectDefinition objectDefinition :
				_objectDefinitionLocalService.getObjectDefinitions(
					_themeDisplay.getCompanyId(), true, true,
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, orderByComparator)) {

			ObjectDefinitionSetting objectDefinitionSetting =
				_objectDefinitionSettingLocalService.
					fetchObjectDefinitionSetting(
						objectDefinition.getObjectDefinitionId(),
						_objectDefinitionSettingName);

			if ((objectDefinitionSetting != null) &&
				GetterUtil.getBoolean(objectDefinitionSetting.getValue())) {

				objectDefinitions.add(objectDefinition);
			}
		}

		objectDefinitions.sort(orderByComparator);

		return objectDefinitions;
	}

	private String _getOrderByCol() {
		if (_orderByCol != null) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_httpServletRequest, ObjectPortletKeys.OBJECT_DEFINITIONS,
			"object-definition-order-by-col", "label");

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (_orderByType != null) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_httpServletRequest, ObjectPortletKeys.OBJECT_DEFINITIONS,
			"object-definition-order-by-type", "asc");

		return _orderByType;
	}

	private final HttpServletRequest _httpServletRequest;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private SearchContainer<ObjectDefinition> _objectDefinitionSearchContainer;
	private final ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;
	private final String _objectDefinitionSettingName;
	private String _orderByCol;
	private String _orderByType;
	private final PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final ThemeDisplay _themeDisplay;

}