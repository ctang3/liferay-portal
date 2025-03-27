/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.action.metadata;

import com.liferay.portal.kernel.model.Group;

/**
 * @author Javier Gamarra
 */
public class AssetLibraryDTOActionMetadataProvider
	extends BaseAssetLibraryDTOActionMetadataProvider {

	@Override
	public String getPermissionName() {
		return Group.class.getName();
	}

	@Override
	protected String getDeleteResourceMethodName() {
		return null;
	}

	@Override
	protected String getGetResourceMethodName() {
		return null;
	}

	@Override
	protected String getReplaceResourceMethodName() {
		return null;
	}

	@Override
	protected String getUpdateResourceMethodName() {
		return null;
	}

}