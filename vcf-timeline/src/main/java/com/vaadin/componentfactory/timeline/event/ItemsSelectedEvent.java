/*-
 * #%L
 * Timeline
 * %%
 * Copyright (C) 2021 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.vaadin.componentfactory.timeline.event;

import java.util.Collection;

import com.vaadin.componentfactory.timeline.Timeline;
import com.vaadin.flow.component.ComponentEvent;

/**
 * @author Andreas Huber
 *
 */
public class ItemsSelectedEvent extends ComponentEvent<Timeline> {

	private final Collection<String> itemIds;

	/**
	 *
	 */
	private static final long serialVersionUID = -2858158864379660327L;

	public ItemsSelectedEvent(final Timeline source, final Collection<String> itemIds, final boolean fromClient) {
		super(source, fromClient);
		this.itemIds = itemIds;
	}

	public Collection<String> getItemIds() {
		return itemIds;
	}

	public Timeline getTimeline() {
		return (Timeline) source;
	}
}
