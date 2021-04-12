/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Locale;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(immediate = true, service = CTDisplayRenderer.class)
public class DDMFormInstanceRecordCTDisplayRenderer
	extends BaseCTDisplayRenderer<DDMFormInstanceRecord> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		Group group = _groupLocalService.getGroup(
			ddmFormInstanceRecord.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group,
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_form_instance_record.jsp"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setParameter(
			"formInstanceRecordId", ddmFormInstanceRecord.getPrimaryKey()
		).setParameter(
			"formInstanceId", ddmFormInstanceRecord.getFormInstanceId()
		).buildString();
	}

	@Override
	public Class<DDMFormInstanceRecord> getModelClass() {
		return DDMFormInstanceRecord.class;
	}

	@Override
	public String getTitle(
			Locale locale, DDMFormInstanceRecord ddmFormInstanceRecord)
		throws PortalException {

		return String.valueOf(ddmFormInstanceRecord.getPrimaryKey());
	}

	@Override
	protected void buildDisplay(
			DisplayBuilder<DDMFormInstanceRecord> displayBuilder)
		throws PortalException {

		DDMFormInstanceRecord ddmFormInstanceRecord = displayBuilder.getModel();

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		displayBuilder.display(
			"created-by",
			() -> {
				String userName = ddmFormInstanceRecord.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"version", ddmFormInstanceRecord.getVersion()
		).display(
			"default-language", ddmFormValues.getDefaultLocale()
		).display(
			"name", ddmFormInstance.getName(displayBuilder.getLocale())
		).display(
			"description",
			ddmFormInstance.getDescription(displayBuilder.getLocale())
		);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}