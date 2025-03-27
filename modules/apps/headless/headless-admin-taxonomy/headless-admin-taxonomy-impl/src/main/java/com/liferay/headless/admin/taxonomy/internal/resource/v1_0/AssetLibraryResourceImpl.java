/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.resource.v1_0;

import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.internal.dto.v1_0.util.AssetLibraryUtil;
import com.liferay.headless.admin.taxonomy.resource.v1_0.AssetLibraryResource;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-library.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetLibraryResource.class
)
public class AssetLibraryResourceImpl extends BaseAssetLibraryResourceImpl {

	@Override
	public Page<AssetLibrary> getTaxonomyVocabularyAssetLibrariesPage(
			Long taxonomyVocabularyId)
		throws Exception {

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(taxonomyVocabularyId);

		List<AssetLibrary> assetLibraries = new ArrayList<>();

		for (AssetVocabularyGroupRel assetVocabularyGroupRel :
				assetVocabularyGroupRels) {

			AssetLibrary assetLibrary = _toAssetLibrary(
				groupLocalService.fetchGroup(
					assetVocabularyGroupRel.getGroupId()));

			assetLibraries.add(assetLibrary);
		}

		return Page.of(assetLibraries);
	}

	private AssetLibrary _toAssetLibrary(Group group) {
		return AssetLibraryUtil.toAssetLibrary(group, contextAcceptLanguage);
	}

	@Reference
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

}