/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

/**
 * @author Rubén Pulido
 */
public class AssetVocabularyConstants {

	public static final int VISIBILITY_TYPE_INTERNAL = 1;

	public static final int VISIBILITY_TYPE_PUBLIC = 0;

	public static final int[] VISIBILITY_TYPES = {
		VISIBILITY_TYPE_INTERNAL, VISIBILITY_TYPE_PUBLIC
	};

	public static int fromString(String visibilityType) {
		return switch (visibilityType.toUpperCase()) {
			case "INTERNAL" -> VISIBILITY_TYPE_INTERNAL;
			case "PUBLIC" -> VISIBILITY_TYPE_PUBLIC;
			default -> throw new IllegalArgumentException(
				"Invalid visibility type: " + visibilityType
			);
		};
	}
}