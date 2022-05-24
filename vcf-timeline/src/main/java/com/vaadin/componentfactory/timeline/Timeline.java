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

package com.vaadin.componentfactory.timeline;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.timeline.event.ItemDoubleClickEvent;
import com.vaadin.componentfactory.timeline.event.ItemRemoveEvent;
import com.vaadin.componentfactory.timeline.event.ItemResizeEvent;
import com.vaadin.componentfactory.timeline.event.ItemsDragAndDropEvent;
import com.vaadin.componentfactory.timeline.event.ItemsSelectedEvent;
import com.vaadin.componentfactory.timeline.model.AxisOrientation;
import com.vaadin.componentfactory.timeline.model.Item;
import com.vaadin.componentfactory.timeline.model.SnapStep;
import com.vaadin.componentfactory.timeline.model.TimelineOptions;
import com.vaadin.componentfactory.timeline.util.TimelineUtil;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.internal.Pair;
import com.vaadin.flow.shared.Registration;

/**
 * Timeline component definition. Timeline uses vis-timeline component to display data in time (see more at
 * https://github.com/visjs/vis-timeline).
 */
@SuppressWarnings("serial")
@NpmPackage(value = "vis-timeline", version = "7.5.1")
@NpmPackage(value = "moment", version = "2.29.3")
@JsModule("./src/arrow.js")
@JsModule("./src/vcf-timeline.js")
@CssImport("vis-timeline/styles/vis-timeline-graph2d.min.css")
@CssImport("./styles/timeline.css")
public class Timeline extends Div {

	private List<Item> items = new ArrayList<>();

	private final TimelineOptions timelineOptions = new TimelineOptions();

	private List<String> selectedItemsIdsList = new ArrayList<>();

	private Map<String, Pair<LocalDateTime, LocalDateTime>> movedItemsMap = new HashMap<>();

	private Map<String, Pair<LocalDateTime, LocalDateTime>> movedItemsOldValuesMap = new HashMap<>();

	public Timeline() {
		setId("visualization" + hashCode());
		setWidthFull();
		setClassName("timeline");
	}

	public Timeline(final List<Item> items) {
		this();
		this.items = new ArrayList<>(items);
	}

	protected TimelineOptions getTimelineOptions() {
		return timelineOptions;
	}

	@Override
	protected void onAttach(final AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		selectedItemsIdsList = new ArrayList<>();
		movedItemsMap = new HashMap<>();
		movedItemsOldValuesMap = new HashMap<>();
		initTimeline();
	}

	private void initTimeline() {
		getElement().executeJs("vcftimeline.create($0, $1, $2)", this, "[" + convertItemsToJson() + "]",
				getTimelineOptions().toJSON());
	}

	private String convertItemsToJson() {
		return items != null ? items.stream().map(item -> item.toJSON()).collect(Collectors.joining(",")) : "";
	}

	/**
	 * Add a new item to the timeline.
	 *
	 * @param item
	 *            the new item to add to the timeline
	 */
	public void addItem(final Item item) {
		items.add(item);
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.addItem($0, $1)", this, item.toJSON());
		}
	}

	public void setItems(final List<Item> items) {
		this.items = new ArrayList<>(items);
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.setItems($0, $1)", this, "[" + convertItemsToJson() + "]");
		}
	}

	/**
	 * Return the list of items that are currently part of the timeline.
	 *
	 * @return the list of items of the timeline
	 */
	public List<Item> getItems() {
		return items;
	}

	/**
	 * Remove all items from list
	 */
	public void clearItems() {
		items.clear();
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.setItems($0, $1)", this, "[" + convertItemsToJson() + "]");
		}
	}

	/**
	 * Sets visible range for timeline.
	 *
	 * @param min
	 *            minimum date
	 * @param max
	 *            maximum date
	 */
	public void setTimelineRange(final LocalDateTime min, final LocalDateTime max) {
		getTimelineOptions().min = min;
		getTimelineOptions().max = max;
		updateTimelineOptions();
	}

	/**
	 * Sets orientation of the timeline axis. By default axis is on top.
	 *
	 * @param axisOrientation
	 *            orientation of the timeline axis
	 */
	public void setAxisOrientation(final AxisOrientation axisOrientation) {
		getTimelineOptions().axisOrientation = axisOrientation.getName();
		updateTimelineOptions();
	}

	/**
	 * Sets whether the timeline can be zoomed by pinching or scrolling in the window. By default, timeline is zoomable.
	 * Option moveable shoul be true.
	 *
	 * @param zoomable
	 *            true if timeline is zoomable
	 */
	public void setZoomable(final boolean zoomable) {
		getTimelineOptions().zoomable = zoomable;
		updateTimelineOptions();
	}

	/**
	 * Sets wheter the timeline can be moved by dragging the window. By default, timeline is moveable.
	 *
	 * @param moveable
	 *            true if timeline is moveable
	 */
	public void setMoveable(final boolean moveable) {
		getTimelineOptions().moveable = moveable;
		updateTimelineOptions();
	}

	/**
	 * Sets zoom range for timeline.
	 *
	 * @param zoomMin
	 *            minimum zoom interval
	 * @param zoomMax
	 *            maximum zoom interval
	 */
	public void setZoomRange(final Long zoomMin, final Long zoomMax) {
		getTimelineOptions().zoomMin = zoomMin;
		getTimelineOptions().zoomMax = zoomMax;
		updateTimelineOptions();
	}

	/**
	 * Sets wheter the items in the timeline can be selected. By default, items are selectables.
	 *
	 * @param selectable
	 *            true if times can be selected
	 */
	public void setSelectable(final boolean selectable) {
		getTimelineOptions().selectable = selectable;
		updateTimelineOptions();
	}

	/**
	 * Sets whether a vertical bar at current time is displayed. By default, not current time is displayed.
	 *
	 * @param showCurrentTime
	 *            true if current time is shown
	 */
	public void setShowCurentTime(final boolean showCurrentTime) {
		getTimelineOptions().showCurrentTime = showCurrentTime;
		updateTimelineOptions();
	}

	/**
	 * Sets the height of the timeline. Value can be in pixles or as percentaje (e.g. "300px"). When height is undefined
	 * or null, the height of the timeline is automatically adjusted to fit the contents.
	 */
	@Override
	public void setHeight(final String height) {
		getTimelineOptions().height = height;
		updateTimelineOptions();
	}

	/** Sets the maximum height for the timeline. */
	@Override
	public void setMaxHeight(final String maxHeight) {
		getTimelineOptions().maxHeight = maxHeight;
		updateTimelineOptions();
	}

	/**
	 * Sets the initial start date for the axis of the timeline. If it's not provided, the earliest date present in the
	 * events is taken as start date.
	 *
	 * If autoZoom is true, this option will be override.
	 *
	 * @param start
	 *            initial start date
	 */
	public void setStart(final LocalDateTime start) {
		getTimelineOptions().start = start;
		updateTimelineOptions();
	}

	public LocalDateTime getStart() {
		return getTimelineOptions().start;
	}

	/**
	 * Sets whether all range should be visible at once. Only works if a range was defined by calling
	 * {@link #setTimelineRange}. It will set start and end for timeline axis.
	 *
	 * @param autoZoom
	 *            true if autozoom is allowed
	 */
	public void setAutoZoom(final boolean autoZoom) {
		getTimelineOptions().autoZoom = autoZoom;
		updateTimelineOptions();
	}

	/**
	 * The initial end date for the axis of the timeline. If not provided, the latest date present in the items set is
	 * taken as end date.
	 *
	 * If autoZoom is true, this option will be override.
	 *
	 * @param end
	 *            initial end date
	 */
	public void setEnd(final LocalDateTime end) {
		getTimelineOptions().end = end;
		updateTimelineOptions();
	}

	public LocalDateTime getEnd() {
		return getTimelineOptions().end;
	}

	/**
	 * Sets whether items will be stack on top of each other if they overlap. By default item will not stack.
	 *
	 * @param stack
	 *            true if items should stack
	 */
	public void setStack(final boolean stack) {
		getTimelineOptions().stack = stack;
		updateTimelineOptions();
	}

	/**
	 * Sets whether multiple items can be selected. Option selectable should be true. By default, multiselect is
	 * disabled.
	 *
	 * @param multiselect
	 *            true if multiselect is allowed
	 */
	public void setMultiselect(final boolean multiselect) {
		getTimelineOptions().multiselect = multiselect;
		updateTimelineOptions();
	}

	/**
	 * Sets whether tooltips will be displaying for items with defined titles. By default, tooltips will be visibles.
	 *
	 * @param showTooltips
	 *            true if tooltips should be shown
	 */
	public void setShowTooltips(final boolean showTooltips) {
		getTimelineOptions().showTooltips = showTooltips;
		updateTimelineOptions();
	}

	/**
	 * Updates content of an existing item.
	 *
	 * @param itemId
	 *            id of item to be updated
	 * @param newContent
	 *            new item content
	 */
	public void updateItemContent(final String itemId, final String newContent) {
		items.stream().filter(i -> itemId.equals(i.getId())).findFirst().ifPresent(item -> {
			item.setContent(newContent);
		});
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.updateItemContent($0, $1, $2)", this, itemId, newContent);
		}
	}

	/**
	 * Sets snap value. It can be an hour, half an hour or fifteen minutes. By default it is set at fifteeen minutes.
	 *
	 * @param snapStep
	 *            snap value
	 */
	public void setSnapStep(final SnapStep snapStep) {
		getTimelineOptions().snapStep = snapStep.getMinutes();
		updateTimelineOptions();
	}

	/**
	 * Sets zoom option for timeline.
	 *
	 * @param zoomOption
	 *            integer representing days for zooming
	 */
	public void setZoomOption(final Integer zoomOption) {
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.setZoomOption($0, $1)", this, zoomOption);
		}
		updateTimelineOptions();
	}

	/**
	 * Updates timeline options after timeline creation.
	 */
	private void updateTimelineOptions() {
		if (getElement().getNode().isAttached()) {
			getElement().executeJs("vcftimeline.setOptions($0, $1)", this, getTimelineOptions().toJSON());
		}
	}

	/**
	 * Call from client when an item is moved (dragged and dropped or resized).
	 *
	 * @param itemId
	 *            id of the moved item
	 * @param itemNewStart
	 *            new start date of the moved item
	 * @param itemNewEnd
	 *            new end date of the moved item
	 * @param resizedItem
	 *            true if item was resized
	 */
	@ClientCallable
	public void onMove(final String itemId, final String itemNewStart, final String itemNewEnd,
			final boolean resizedItem) {
		final LocalDateTime newStart = TimelineUtil.convertLocalDateTime(itemNewStart);
		final LocalDateTime newEnd = TimelineUtil.convertLocalDateTime(itemNewEnd);

		if (resizedItem) {
			fireItemResizeEvent(itemId, newStart, newEnd, true);
		} else {
			handleDragAndDrop(itemId, newStart, newEnd, true);
		}
	}

	/**
	 * Fires a {@link ItemResizeEvent}.
	 *
	 * @param itemId
	 *            id of the item that was moved
	 * @param newStart
	 *            new start date for the item
	 * @param newEnd
	 *            new end date for the item
	 * @param fromClient
	 *            if event comes from client
	 */
	protected void fireItemResizeEvent(final String itemId, final LocalDateTime newStart, final LocalDateTime newEnd,
			final boolean fromClient) {
		final ItemResizeEvent event = new ItemResizeEvent(this, itemId, newStart, newEnd, fromClient);
		RuntimeException exception = null;

		try {
			fireEvent(event);
		} catch (final RuntimeException e) {
			exception = e;
			event.setCancelled(true);
		}

		if (event.isCancelled()) {
			// if update is cancelled, revert item resizing
			revertMove(itemId);
			// if exception was catch, re-throw exception
			if (exception != null) {
				throw exception;
			}
		} else {
			// update item in list
			updateItemRange(itemId, newStart, newEnd);
		}
	}

	/**
	 * Handle item moved by drag and drop.
	 *
	 * @param itemId
	 *            id of the item that was moved
	 * @param newStart
	 *            new start date for the item
	 * @param newEnd
	 *            new end date for the item
	 * @param fromClient
	 *            if event comes from client
	 */
	protected void handleDragAndDrop(final String itemId, final LocalDateTime newStart, final LocalDateTime newEnd,
			final boolean fromClient) {
		// save current moved item - itemId - new start and new end
		movedItemsMap.put(itemId, new Pair<>(newStart, newEnd));
		// save original start and end for the moved item
		final Item movedItem = items.stream().filter(i -> itemId.equals(i.getId())).findFirst().get();
		movedItemsOldValuesMap.put(itemId, new Pair<>(movedItem.getStart(), movedItem.getEnd()));

		// if all selected items have been processed
		if (selectedItemsIdsList.size() == movedItemsMap.size()) {
			// update items with new start and end range values
			updateMovedItemsRange();
			final List<Item> updatedItems = items.stream().filter(item -> movedItemsMap.containsKey(item.getId()))
					.collect(Collectors.toList());
			final ItemsDragAndDropEvent event = new ItemsDragAndDropEvent(this, updatedItems, fromClient);
			RuntimeException exception = null;

			try {
				fireEvent(event);
			} catch (final RuntimeException e) {
				exception = e;
				event.setCancelled(true);
			}

			if (event.isCancelled()) {
				// if move is cancelled, revert move for all dragged items
				revertMovedItemsRange();
				movedItemsMap.clear();
				movedItemsOldValuesMap.clear();
				// if exception was catch, re-throw the exception for error handling
				if (exception != null) {
					throw exception;
				}
			} else {
				// if move not cancelled, keep updated values
				movedItemsMap.clear();
				movedItemsOldValuesMap.clear();
			}
		}
	}

	private void revertMovedItemsRange() {
		for (final String itemId : movedItemsOldValuesMap.keySet()) {
			items.stream().filter(i -> itemId.equals(i.getId())).findFirst().ifPresent(item -> {
				item.setStart(movedItemsOldValuesMap.get(itemId).getFirst());
				item.setEnd(movedItemsOldValuesMap.get(itemId).getSecond());
			});
		}

		for (final String itemId : movedItemsOldValuesMap.keySet()) {
			revertMove(itemId);
		}
	}

	private void revertMove(final String itemId) {
		final Item item = items.stream().filter(i -> itemId.equals(i.getId())).findFirst().orElse(null);
		if (item != null) {
			getElement().executeJs("vcftimeline.revertMove($0, $1, $2)", this, itemId, item.toJSON());
		}
	}

	private void updateMovedItemsRange() {
		for (final String itemId : movedItemsMap.keySet()) {
			updateItemRange(itemId, movedItemsMap.get(itemId).getFirst(), movedItemsMap.get(itemId).getSecond());
		}
	}

	private void updateItemRange(final String itemId, final LocalDateTime newStart, final LocalDateTime newEnd) {
		items.stream().filter(i -> itemId.equals(i.getId())).findFirst().ifPresent(item -> {
			item.setStart(newStart);
			item.setEnd(newEnd);
		});
	}

	/**
	 * Adds a listener for {@link ItemResizeEvent} to the component.
	 *
	 * @param listener
	 *            the listener to be added
	 */
	public Registration addItemResizeListener(final ComponentEventListener<ItemResizeEvent> listener) {
		return addListener(ItemResizeEvent.class, listener);
	}

	/**
	 * Removes an item.
	 *
	 * @param item
	 *            item to be removed.
	 */
	public void removeItem(final Item item) {
		getElement().executeJs("vcftimeline.removeItem($0, $1)", this, item.getId());
	}

	/**
	 * Call from client when an item is removed.
	 *
	 * @param itemId
	 *            id of the removed item
	 */
	@ClientCallable
	public void onRemove(final String itemId) {
		fireItemRemoveEvent(itemId, true);
	}

	/**
	 * Fires a {@link ItemRemoveEvent}.
	 *
	 * @param itemId
	 *            id of the removed item
	 * @param fromClient
	 *            if event comes from client
	 */
	public void fireItemRemoveEvent(final String itemId, final boolean fromClient) {
		final ItemRemoveEvent event = new ItemRemoveEvent(this, itemId, fromClient);
		// update items list
		items.removeIf(item -> itemId.equals(item.getId()));
		fireEvent(event);
	}

	/**
	 * Adds a listener for {@link ItemRemoveEvent} to the component.
	 *
	 * @param listener
	 *            the listener to be added.
	 */
	public Registration addItemRemoveListener(final ComponentEventListener<ItemRemoveEvent> listener) {
		return addListener(ItemRemoveEvent.class, listener);
	}

	/**
	 * Call from client when items are selected.
	 *
	 * @param selectedItemsIds
	 *            list of selected items
	 */
	@ClientCallable
	public void onSelect(final String selectedItemsIds) {
		selectedItemsIdsList.clear();
		selectedItemsIdsList.addAll(Arrays.asList(selectedItemsIds.split(",")));
		fireItemsSelectedEvent(new ArrayList<>(selectedItemsIdsList), true);
	}

	public void fireItemsSelectedEvent(final Collection<String> selected, final boolean fromClient) {
		final ItemsSelectedEvent event = new ItemsSelectedEvent(this, selected, fromClient);
		fireEvent(event);
	}

	public Registration addItemsSelectedListener(final ComponentEventListener<ItemsSelectedEvent> listener) {
		return addListener(ItemsSelectedEvent.class, listener);
	}

	/**
	 * Call from client when items are selected.
	 *
	 * @param selectedItemsIds
	 *            list of selected items
	 */
	@ClientCallable
	public void onDoubleClick(final String itemId) {
		fireItemDoubleClickEvent(itemId, true);
	}

	public void fireItemDoubleClickEvent(final String itemId, final boolean fromClient) {
		final ItemDoubleClickEvent event = new ItemDoubleClickEvent(this, itemId, fromClient);
		fireEvent(event);
	}

	public Registration addItemDoubleClickListener(final ComponentEventListener<ItemDoubleClickEvent> listener) {
		return addListener(ItemDoubleClickEvent.class, listener);
	}

	/**
	 * Adds a listener for {@link ItemsDragAndDropEvent} to the component.
	 *
	 * @param listener
	 *            the listener to be added
	 */
	public Registration addItemsDragAndDropListener(final ComponentEventListener<ItemsDragAndDropEvent> listener) {
		return addListener(ItemsDragAndDropEvent.class, listener);
	}

	/**
	 * Sets whether tooltip should be displayed while updating an item.
	 *
	 * @param tooltip
	 *            true if tooltip is allowed
	 */
	public void setTooltipOnItemUpdateTime(final boolean tooltip) {
		getTimelineOptions().tooltipOnItemUpdateTime = tooltip;
		updateTimelineOptions();
	}

	/**
	 * Sets the date format for the dates displayed in the on update item tooltip.
	 *
	 * @param dateFormat
	 *            format for tooltip dates
	 */
	public void setTooltipOnItemUpdateTimeDateFormat(final String dateFormat) {
		getTimelineOptions().tooltipOnItemUpdateTimeDateFormat = dateFormat;
		updateTimelineOptions();
	}

	/**
	 * Sets the template for the tooltip displayed on item update.
	 * <p>
	 * To reference item start and end dates, please use item.start and item.end to be able to parse the template in the
	 * client-side.
	 * <p>
	 * E.g.: Starting at item.start, ending at item.end.
	 *
	 * @param template
	 *            the template shown in the tooltip
	 */
	public void setTooltipOnItemUpdateTimeTemplate(final String template) {
		getTimelineOptions().tooltipOnItemUpdateTimeTemplate = template;
		updateTimelineOptions();
	}

}
