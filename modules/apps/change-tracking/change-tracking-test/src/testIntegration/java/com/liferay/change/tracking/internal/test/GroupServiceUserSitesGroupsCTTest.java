/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Users_GroupsTable;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Samuel Trong Tran
 */
@RunWith(Arquillian.class)
public class GroupServiceUserSitesGroupsCTTest {

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
			0, GroupServiceUserSitesGroupsCTTest.class.getName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_ctCollectionLocalService.deleteCTCollection(_ctCollection);

		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testGetUserSitesGroups() throws Exception {
		Group group = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			group = GroupTestUtil.addGroup();
		}

		List<Object> productionRows = _groupLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				Users_GroupsTable.INSTANCE.groupId
			).from(
				Users_GroupsTable.INSTANCE
			).where(
				Users_GroupsTable.INSTANCE.userId.eq(
					TestPropsValues.getUserId()
				).and(
					Users_GroupsTable.INSTANCE.groupId.eq(
						group.getGroupId()
					).and(
						Users_GroupsTable.INSTANCE.ctCollectionId.eq(
							CTConstants.CT_COLLECTION_ID_PRODUCTION)
					).and(
						Users_GroupsTable.INSTANCE.companyId.eq(
							TestPropsValues.getCompanyId())
					)
				)
			));

		Assert.assertEquals(
			productionRows.toString(), 1, productionRows.size());

		List<Object> ctCollectionRows = _groupLocalService.dslQuery(
			DSLQueryFactoryUtil.selectDistinct(
				Users_GroupsTable.INSTANCE.groupId
			).from(
				Users_GroupsTable.INSTANCE
			).where(
				Users_GroupsTable.INSTANCE.userId.eq(
					TestPropsValues.getUserId()
				).and(
					Users_GroupsTable.INSTANCE.groupId.eq(
						group.getGroupId()
					).and(
						Users_GroupsTable.INSTANCE.ctCollectionId.eq(
							_ctCollection.getCtCollectionId())
					).and(
						Users_GroupsTable.INSTANCE.companyId.eq(
							TestPropsValues.getCompanyId())
					)
				)
			));

		Assert.assertEquals(
			ctCollectionRows.toString(), 0, ctCollectionRows.size());
	}

	private static CTCollection _ctCollection;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	private static Group _group;

	@Inject
	private static GroupLocalService _groupLocalService;

}