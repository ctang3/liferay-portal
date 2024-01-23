/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClayPanel from '@clayui/panel';
import React from 'react';

import TimelineDropdownMenu from './TimelineDropdownMenu';
import WorkflowStatusLabel from './WorkflowStatusLabel';

const PublicationTimeline = ({timelineItems}) => {
	if (timelineItems && !!timelineItems.length) {
		return (
			<div className="publication-timeline">
				{timelineItems.map((timelineItem) => (
					<ClayPanel
							//NOTE: id is in the response
						key={timelineItem.id}
						style={{borderBottomColor: '#e7e7ed', marginBottom: 0}}
					>
						<ClayPanel.Body>
							<ClayLayout.ContentRow>
								<ClayLayout.ContentCol expand>
									<div>
										<span style={{paddingRight: '10px'}}>
											{/* NOTE: id is in the response */}
											{timelineItem.name}
										</span>

										<WorkflowStatusLabel
												//NOTE: workflowStatus is in the XML response
											workflowStatus={timelineItem.status}
										/>
									</div>

									<div className="text-secondary">
										{/* NOTE: description is in the XML response */}
										{timelineItem.description}
									</div>

									<div className="text-secondary">
										{/* NOTE: statusMessage is in the XML response */}
										{timelineItem.statusMessage}
									</div>
								</ClayLayout.ContentCol>

								<ClayLayout.ContentCol>
									<TimelineDropdownMenu
										// NOTE: deleteURL could be replaced by an API endpoint, but may not have a deletion confirmation prompt for the user if using API?
										deleteURL={
											timelineItem.dropdownMenu.deleteURL
										}
										// NOTE: editURL is a link to the publication editing page. Not sure if the put or patch API andpoints can do the same.
										editURL={
											timelineItem.dropdownMenu.editURL
										}
										// NOTE: revertURL is not part of the API's XML response, but may be able to construct URL ourselves if we can get CTProcessId.
										revertURL={
											timelineItem.dropdownMenu.revertURL
										}
										// NOTE: reviewURL is a link to the review changes page, not an API endpoint.
										reviewURL={
											timelineItem.dropdownMenu.reviewURL
										}
									/>
								</ClayLayout.ContentCol>
							</ClayLayout.ContentRow>
						</ClayPanel.Body>
					</ClayPanel>
				))}
			</div>
		);
	}

	return (
		<div className="publication-timeline timeline">
			{Liferay.Language.get('no-publications-were-found')}
		</div>
	);
};

export default PublicationTimeline;
