/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.action.metadata;

import com.liferay.headless.admin.taxonomy.internal.resource.v1_0.AssetLibraryResourceImpl;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.vulcan.dto.action.ActionInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Javier Gamarra
 * @generated
 */
public abstract class BaseAssetLibraryDTOActionMetadataProvider {

	public BaseAssetLibraryDTOActionMetadataProvider() {
		_actionInfos.put(
			"delete",
			new ActionInfo(
				getDeleteActionName(), AssetLibraryResourceImpl.class,
				getDeleteResourceMethodName()));
		_actionInfos.put(
			"get",
			new ActionInfo(
				getGetActionName(), AssetLibraryResourceImpl.class,
				getGetResourceMethodName()));
		_actionInfos.put(
			"replace",
			new ActionInfo(
				getReplaceActionName(), AssetLibraryResourceImpl.class,
				getReplaceResourceMethodName()));
		_actionInfos.put(
			"update",
			new ActionInfo(
				getUpdateActionName(), AssetLibraryResourceImpl.class,
				getUpdateResourceMethodName()));
	}

	public final ActionInfo getActionInfo(String actionName) {
		return _actionInfos.get(actionName);
	}

	public final Set<String> getActionNames() {
		return _actionInfos.keySet();
	}

	public abstract String getPermissionName();

	protected String getDeleteActionName() {
		return ActionKeys.DELETE;
	}

	protected abstract String getDeleteResourceMethodName();

	protected String getGetActionName() {
		return ActionKeys.VIEW;
	}

	protected abstract String getGetResourceMethodName();

	protected String getReplaceActionName() {
		return ActionKeys.UPDATE;
	}

	protected abstract String getReplaceResourceMethodName();

	protected String getUpdateActionName() {
		return ActionKeys.UPDATE;
	}

	protected abstract String getUpdateResourceMethodName();

	protected final void registerActionInfo(
		ActionInfo actionInfo, String actionName) {

		_actionInfos.put(actionName, actionInfo);
	}

	private final Map<String, ActionInfo> _actionInfos = new HashMap<>();

}