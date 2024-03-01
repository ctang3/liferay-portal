/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR
 * LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.performance.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.internal.test.GroupServiceUserSitesGroupsCTTest;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
public class PerformanceResearchTest {

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

	@Test
	public void testCreateTestData() throws Exception {
		// TODO: Test all
	}

	/**
	 * Web Content with 2 paragraphs and 1 image
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreateWebContentData() throws Exception {

	}

	/**
	 * Pages with a Web Content Display portlet. Each portlet is configured to display a single web content.
	 *
	 * @throws Exception
	 */
	@Test
	public void testCreatePageData() throws Exception {

	}

	@Test
	public void testCreateImageData() throws Exception {
		long startImageTimer = System.currentTimeMillis();

		int count = 1000;

		for (int i = 0; i < count; i++) {
			TempFileEntryUtil.addTempFileEntry(
				_group.getGroupId(), TestPropsValues.getUserId(),
				CTCollection.class.getName(), "image.jpg", _getInputStream(),
				ContentTypes.IMAGE_JPEG);
		}

		long endImageTimer = System.currentTimeMillis();

		long deltaImageCreation = endImageTimer - startImageTimer;

		System.out.println(
			"Created "+ count + " times in " + deltaImageCreation + " ms");

		Assert.assertTrue(deltaImageCreation < (_CI_MULTIPLIER * testCreateTestDataExpectedTime()));
	}

	@Test
	public void testCreateUserData() throws Exception {
		long startUserTimer = System.currentTimeMillis();

		int count = 1000;

		for (int i = 0; i < count; i++) {
			UserTestUtil.addUser();
		}

		long endUserTimer = System.currentTimeMillis();

		long deltaUserCreation = endUserTimer - startUserTimer;

		System.out.println(
			"Created "+ count + " times in " + deltaUserCreation + " ms");

		Assert.assertTrue(deltaUserCreation < (_CI_MULTIPLIER * testCreateTestDataExpectedTime()));
	}

	/**
	 * @return the expected running time of testConsumeValues on a local
	 * machine
	 */
	protected long testCreateTestDataExpectedTime() {
		return 10000000000000L;
	}

	private InputStream _getInputStream() {
		return new ByteArrayInputStream("test".getBytes());
	}

	/**
	 * To avoid random heavy CI load
	 */
	private static final long _CI_MULTIPLIER = 3;

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@DeleteAfterTestRun
	private Group _group;

}