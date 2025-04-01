/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import { fetch } from 'frontend-js-web';

import { IVocabulary } from '../types/IVocabulary';

const HEADERS = new Headers({
	'Accept': 'application/json',
	'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
	'Content-Type': 'application/json',
});

async function createVocabulary(siteId: number, vocabulary: IVocabulary) {
	const url: string = `/o/headless-admin-taxonomy/v1.0/sites/${siteId}/taxonomy-vocabularies`;

	const response = await fetch(url, {
		body: JSON.stringify(vocabulary),
		headers: HEADERS,
		method: 'POST',
	});

	if (response.ok) {
		return await response.json();
	}

	const {title} = await response.json();

	throw new Error(title);
}

async function fetchVocabulary<IVocabulary>(
	vocabularyId: number
) {
	const url: string = `/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyId}`;

	return await fetch(url, {
		headers: HEADERS,
		method: 'GET',
	}).then((response) => {
		if (response.ok) {
			return response.json();
		} else {
			throw new Error(response.statusText);
		}
	});
}

async function updateVocabulary(siteId: number, vocabulary: IVocabulary) {
	const url: string = `/o/headless-admin-taxonomy/v1.0/sites/${siteId}/taxonomy-vocabularies/${vocabulary.id}`;

	const response = await fetch(url, {
		body: JSON.stringify(vocabulary),
		headers: HEADERS,
		method: 'PUT',
	});

	if (response.ok) {
		return await response.json();
	}

	const {title} = await response.json();

	throw new Error(title);
}

export default {createVocabulary, fetchVocabulary, updateVocabulary};
