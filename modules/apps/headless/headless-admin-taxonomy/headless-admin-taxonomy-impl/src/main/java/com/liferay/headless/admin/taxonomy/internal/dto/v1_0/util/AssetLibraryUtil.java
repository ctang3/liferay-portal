/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.dto.v1_0.util;

import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Cheryl Tang
 */
public class AssetLibraryUtil {

	public static AssetLibrary toAssetLibrary(
		Group group, AcceptLanguage contextAcceptLanguage) {

		return new AssetLibrary() {
			{
				setId(group::getGroupId);
				setName(
					() -> group.getName(
						contextAcceptLanguage.getPreferredLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						contextAcceptLanguage.isAcceptAllLanguages(),
						group.getNameMap()));
			}
		};
	}

}