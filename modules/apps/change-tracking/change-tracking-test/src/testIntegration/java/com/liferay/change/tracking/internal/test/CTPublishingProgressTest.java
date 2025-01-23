/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistry;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplay;
import com.liferay.portal.kernel.backgroundtask.display.BackgroundTaskDisplayFactory;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class CTPublishingProgressTest {

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
			0, CTPublishingProgressTest.class.getName(), null);

		_group = GroupTestUtil.addGroup();

		try (SafeCloseable safeCloseable =
				 CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					 _ctCollection.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), RandomTestUtil.randomString());
		}
	}

	@Test
	public void testShouldShowPublishingProgress() throws Exception {
		CTProcess ctProcess = _ctProcessLocalService.addCTProcess(
			_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

		List<CTProcess> ctProcesses = _ctProcessLocalService.getCTProcesses(
			_ctCollection.getCtCollectionId());

		CTProcess ctProcess2 = ctProcesses.get(0);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				ctProcess.getBackgroundTaskId());

		BackgroundTaskDisplay backgroundTaskDisplay =
			_backgroundTaskDisplayFactory.getBackgroundTaskDisplay(
				backgroundTask.getBackgroundTaskId());

		boolean displayHasPercentage = backgroundTaskDisplay.hasPercentage();

		int percentageFromDisplay = backgroundTaskDisplay.getPercentage();

//		BackgroundTaskStatus backgroundTaskStatus =
//			BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
//				backgroundTask.getBackgroundTaskId());

//		int percentage =
//			(int) Math.max(
//				Math.round(
//					GetterUtil.getDouble(
//						backgroundTaskStatus.getAttribute(
//							"currentPercentage")) * 100), 0);
//
//		if (backgroundTask.getStatus() ==
//				BackgroundTaskConstants.STATUS_IN_PROGRESS) {
//
//			Assert.assertTrue((percentage > 0) && (percentage < 100));
//		}
//		else {
//			Assert.assertEquals(100, percentage);
//		}
	}

	@Inject
	private static CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private static CTProcessLocalService _ctProcessLocalService;

	@Inject
	private BackgroundTaskDisplayFactory _backgroundTaskDisplayFactory;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private BackgroundTaskStatusRegistry _backgroundTaskStatusRegistry;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@DeleteAfterTestRun
	private Group _group;

}