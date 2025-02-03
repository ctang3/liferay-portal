/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class KBArticleCTTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, LayoutCTTest.class.getName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testPublishCTCollectionWithContentUpdated()
		throws PortalException {

		KBArticle kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			kbArticle.setContent(RandomTestUtil.randomString());

			kbArticle = _kbArticleLocalService.updateKBArticle(kbArticle);
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

		KBArticle productionKBArticle = _kbArticleLocalService.fetchKBArticle(
			kbArticle.getKbArticleId());

		Assert.assertNotNull(productionKBArticle);

		Assert.assertEquals(
			kbArticle.getContent(), productionKBArticle.getContent());
	}

	@Test
	public void testPublishCTCollectionWithKBArticleAdded()
		throws PortalException {

		KBArticle kbArticle;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());
		}

		_ctProcessLocalService.addCTProcess(
			_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

		Assert.assertEquals(
			kbArticle,
			_kbArticleLocalService.fetchKBArticle(kbArticle.getKbArticleId()));
	}

	@Test
	public void testPublishCTCollectionWithKBArticleDeleted()
		throws PortalException {

		KBArticle kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_kbArticleLocalService.deleteKBArticle(kbArticle);

			Assert.assertNull(
				_kbArticleLocalService.fetchKBArticle(
					kbArticle.getKbArticleId()));

			try (SafeCloseable safeCloseable2 =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				Assert.assertEquals(
					kbArticle,
					_kbArticleLocalService.fetchKBArticle(
						kbArticle.getKbArticleId()));
			}
		}

		CTEntry ctEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection.getCtCollectionId(),
			_classNameLocalService.getClassNameId(KBArticle.class),
			kbArticle.getKbArticleId());

		Assert.assertNotNull(ctEntry);

		Assert.assertEquals(
			CTConstants.CT_CHANGE_TYPE_DELETION, ctEntry.getChangeType());

		_ctProcessLocalService.addCTProcess(
			_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

		Assert.assertNull(
			_kbArticleLocalService.fetchKBArticle(kbArticle.getKbArticleId()));
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTEntryLocalService _ctEntryLocalService;

	@Inject
	private static CTProcessLocalService _ctProcessLocalService;

	@Inject
	private static KBArticleLocalService _kbArticleLocalService;

	private CTCollection _ctCollection;
	private Group _group;

}