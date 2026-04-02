/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.constants;

/**
 * @author Cheryl Tang
 */
public enum SitemapGroupingMode {

	ASSET_TYPE("asset-type"), PAGE_LAYOUT("page-layout");

	public String getLanguageKey() {
		return _languageKey;
	}

	public enum AssetTypeGroup {

		ASSET_CATEGORY, JOURNAL_ARTICLE, LAYOUT_SET, OBJECT_ENTRY

	}

	private SitemapGroupingMode(String languageKey) {
		_languageKey = languageKey;
	}

	private final String _languageKey;

}