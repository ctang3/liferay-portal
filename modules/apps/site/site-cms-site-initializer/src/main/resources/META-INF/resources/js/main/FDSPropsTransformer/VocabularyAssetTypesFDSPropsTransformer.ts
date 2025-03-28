/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '@liferay/frontend-data-set-web';

import VocabularyAssetTypesRenderer from './cell_renderers/VocabularyAssetTypesRenderer';

export default function VocabularyAssetTypesFDSPropsTransformer({
	...otherProps
}: {
	otherProps: any;
}) {
	return {
		...otherProps,
		customRenderers: {
			tableCell: [
				{
					component: VocabularyAssetTypesRenderer,
					name: 'customVocabularyAssetTypesRenderer',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
	};
}
