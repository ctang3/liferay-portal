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

package com.liferay.change.tracking.internal.conflict.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class CTConflictTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollectionA = _ctCollectionLocalService.addCTCollection(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			CTConflictTest.class.getName() + "A", null);

		_ctCollectionB = _ctCollectionLocalService.addCTCollection(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			CTConflictTest.class.getName() + "B", null);

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testResolveModificationConflictMax() throws PortalException {
		DLFolder prodDLFolder = _dlFolderLocalService.addFolder(
			_group.getCreatorUserId(), _group.getGroupId(), _group.getGroupId(),
			false, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollectionA.getCtCollectionId())) {

			_dlFolderLocalService.addFolder(
				_group.getCreatorUserId(), _group.getGroupId(),
				_group.getGroupId(), false, prodDLFolder.getFolderId(),
				_ctCollectionA.getName(), null, false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		DLFolder ctCollectionBDLFolder;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollectionB.getCtCollectionId())) {

			ctCollectionBDLFolder = _dlFolderLocalService.addFolder(
				_group.getCreatorUserId(), _group.getGroupId(),
				_group.getGroupId(), false, prodDLFolder.getFolderId(),
				_ctCollectionB.getName(), null, false,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
		}

		Assert.assertNotEquals(
			ctCollectionBDLFolder.getLastPostDate(),
			prodDLFolder.getLastPostDate());

		_ctProcessLocalService.addCTProcess(
			_ctCollectionB.getUserId(), _ctCollectionB.getCtCollectionId());

		prodDLFolder = _dlFolderLocalService.fetchDLFolder(
			prodDLFolder.getFolderId());

		ctCollectionBDLFolder = _dlFolderLocalService.fetchDLFolder(
			ctCollectionBDLFolder.getFolderId());

		Assert.assertEquals(
			ctCollectionBDLFolder.getLastPostDate(),
			prodDLFolder.getLastPostDate());
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTProcessLocalService _ctProcessLocalService;

	@Inject
	private static DLFolderLocalService _dlFolderLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollectionA;

	@DeleteAfterTestRun
	private CTCollection _ctCollectionB;

	@DeleteAfterTestRun
	private Group _group;

}