/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.constants;

import java.util.List;
import java.util.Map;

/**
 * @author Cheryl Tang
 */
public class SitemapGroupingModeConstants {

	public static final String ASSET_TYPE = "ASSET_TYPE";

	public static final String PAGE_LAYOUT = "PAGE_LAYOUT";

	public static final List<String> values = List.of(ASSET_TYPE, PAGE_LAYOUT);

	public static String getLanguageKey(String mode) {
		return _languageKeys.get(mode);
	}

	public static class AssetTypeGroup {

		public static final String ASSET_CATEGORY = "ASSET_CATEGORY";

		public static final String ASSET_CATEGORY_CLASS_NAME =
			"com.liferay.asset.kernel.model.AssetCategory";

		public static final String JOURNAL_ARTICLE = "JOURNAL_ARTICLE";

		public static final String JOURNAL_ARTICLE_CLASS_NAME =
			"com.liferay.journal.model.JournalArticle";

		public static final String LAYOUT = "LAYOUT";

		public static final String LAYOUT_CLASS_NAME =
			"com.liferay.portal.kernel.model.Layout";

		public static final String OBJECT_ENTRIES = "OBJECT_ENTRIES";

		public static final String OBJECT_ENTRY_CLASS_NAME =
			"com.liferay.object.model.ObjectEntry";

		public static final List<String> names = List.of(
			ASSET_CATEGORY, JOURNAL_ARTICLE, LAYOUT, OBJECT_ENTRIES);

		public static String fromSlug(String slug) {
			return _namesBySlug.get(slug);
		}

		public static String getSlug(String name) {
			return _slugs.get(name);
		}

		private static final Map<String, String> _namesBySlug = Map.of(
			"categories", ASSET_CATEGORY, "web-content", JOURNAL_ARTICLE,
			"pages", LAYOUT, "object-entries", OBJECT_ENTRIES);
		private static final Map<String, String> _slugs = Map.of(
			ASSET_CATEGORY, "categories", JOURNAL_ARTICLE, "web-content",
			LAYOUT, "pages", OBJECT_ENTRIES, "object-entries");

	}

	private static final Map<String, String> _languageKeys = Map.of(
		ASSET_TYPE, "asset-type", PAGE_LAYOUT, "page-layout");

}