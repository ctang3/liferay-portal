/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.site.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.provider.SitemapURLProvider;

import java.io.Serializable;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Proves LPD-97386: CMS content authored in a CMS space (an Asset Library
 * connected to a site) must appear in the connected site's XML sitemap. A CMS
 * space is a depot group and CMS content is a depot-scoped object entry stored
 * under the space's group. The site is connected to the space through a
 * standard {@code DepotEntryGroupRel}.
 *
 * <p>
 * This test fails on the current code because
 * {@code ObjectEntrySitemapURLProvider} enumerates object entries only in
 * {@code layoutSet.getGroupId()} (the site group) and never follows the
 * connected depot groups resolved by {@code SiteConnectedGroupGroupProvider}.
 * It passes once the provider iterates the connected and ancestor site and
 * depot group ids.
 * </p>
 *
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class ObjectEntrySitemapURLProviderConnectedAssetLibraryTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutSet = _layoutSetLocalService.getLayoutSet(
			_group.getGroupId(), false);

		_initThemeDisplay();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectField"
				).objectFieldSettings(
					Collections.emptyList()
				).build()),
			ObjectDefinitionConstants.SCOPE_DEPOT);

		_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS,
			String.valueOf(_depotEntry.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), _group.getGroupId());

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				_PID_SITEMAP_COMPANY_CONFIGURATION,
				HashMapDictionaryBuilder.<String, Object>put(
					"companySitemapObjectDefinitionIds",
					new String[] {
						String.valueOf(
							_objectDefinition.getObjectDefinitionId())
					}
				).build());

		DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			_group.getGroupId(),
			_portal.getClassNameId(_objectDefinition.getClassName()), null,
			true, WorkflowConstants.STATUS_APPROVED);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyConfigurationTemporarySwapper.close();
	}

	@Test
	public void testVisitLayoutSetIncludesConnectedAssetLibraryObjectEntries()
		throws Exception {

		ObjectEntry objectEntry = _addObjectEntry();

		Element rootElement = _getRootElement();

		_objectEntrySitemapURLProvider.visitLayoutSet(
			rootElement, _layoutSet, _themeDisplay);

		Assert.assertTrue(rootElement.hasContent());

		boolean matched = false;

		for (Element element : rootElement.elements()) {
			String objectEntryURL = element.elementText("loc");

			if ((objectEntryURL != null) &&
				objectEntryURL.contains(
					String.valueOf(objectEntry.getObjectEntryId()))) {

				matched = true;

				break;
			}
		}

		Assert.assertTrue(matched);
	}

	private static void _initThemeDisplay() throws Exception {
		_themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			_group.getCompanyId());

		_themeDisplay.setCompany(company);

		_themeDisplay.setLanguageId(_group.getDefaultLanguageId());
		_themeDisplay.setLayoutSet(_layoutSet);
		_themeDisplay.setLocale(
			LocaleUtil.fromLanguageId(_group.getDefaultLanguageId()));
		_themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		_themeDisplay.setPortalDomain(company.getVirtualHostname());
		_themeDisplay.setPortalURL(company.getPortalURL(_group.getGroupId()));
		_themeDisplay.setRequest(new MockHttpServletRequest());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setServerPort(PortalUtil.getPortalServerPort(false));
		_themeDisplay.setSignedIn(true);
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	private ObjectEntry _addObjectEntry() throws Exception {
		return _objectEntryLocalService.addObjectEntry(
			_depotEntry.getGroupId(), TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectField", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				_depotEntry.getGroupId(), TestPropsValues.getUserId()));
	}

	private Element _getRootElement() {
		Document document = _saxReader.createDocument();

		document.setXMLEncoding("UTF-8");

		Element rootElement = document.addElement(
			"urlset", "http://www.sitemaps.org/schemas/sitemap/0.9");

		rootElement.addAttribute(
			"xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		rootElement.addAttribute(
			"xsi:schemaLocation",
			"http://www.w3.org/1999/xhtml " +
				"http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd");
		rootElement.addAttribute("xmlns:xhtml", "http://www.w3.org/1999/xhtml");

		return rootElement;
	}

	private static final String _PID_SITEMAP_COMPANY_CONFIGURATION =
		"com.liferay.site.internal.configuration.SitemapCompanyConfiguration";

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static DepotEntry _depotEntry;

	@Inject
	private static DepotEntryGroupRelLocalService
		_depotEntryGroupRelLocalService;

	@Inject
	private static DepotEntryLocalService _depotEntryLocalService;

	private static Group _group;
	private static LayoutSet _layoutSet;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private static Portal _portal;

	private static ThemeDisplay _themeDisplay;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.site.provider.ObjectEntrySitemapURLProvider",
		type = SitemapURLProvider.class
	)
	private SitemapURLProvider _objectEntrySitemapURLProvider;

	@Inject
	private SAXReader _saxReader;

}