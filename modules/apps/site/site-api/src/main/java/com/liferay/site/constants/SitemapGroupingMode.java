/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.constants;

import java.util.Objects;

/**
 * @author Cheryl Tang
 */
public enum SitemapGroupingMode {

	ASSET_TYPE("asset-type"), PAGE_LAYOUT("page-layout");

	public String getLanguageKey() {
		return _languageKey;
	}

	public enum AssetTypeGroup {

		ASSET_CATEGORY("categories"), JOURNAL_ARTICLE("web-content"),
		LAYOUT("pages"), OBJECT_DEFINITION("object-definitions");

		public static AssetTypeGroup fromName(String name) {
			for (AssetTypeGroup assetTypeGroup : values()) {
				if (Objects.equals(assetTypeGroup.name(), name)) {
					return assetTypeGroup;
				}
			}

			return null;
		}

		public static AssetTypeGroup fromSlug(String slug) {
			for (AssetTypeGroup assetTypeGroup : values()) {
				if (assetTypeGroup._slug.equals(slug)) {
					return assetTypeGroup;
				}
			}

			return null;
		}

		public String getSlug() {
			return _slug;
		}

		private AssetTypeGroup(String slug) {
			_slug = slug;
		}

		private final String _slug;

	}

	private SitemapGroupingMode(String languageKey) {
		_languageKey = languageKey;
	}

	private final String _languageKey;

}