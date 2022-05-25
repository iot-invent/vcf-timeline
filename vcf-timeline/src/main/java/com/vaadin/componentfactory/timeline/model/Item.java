/*
* Copyright (c) 2020 - 2022 IoT Invent GmbH
* All rights reserved.
*/
package com.vaadin.componentfactory.timeline.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

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

import elemental.json.Json;
import elemental.json.JsonObject;

/** Representation of a timeline item. */
public class Item {

	private String id;

	private Object start;

	private Object end;

	private String content;

	private Boolean editable;

	private Boolean updateTime;

	private Boolean remove;

	private String title;

	private String className;

	public Item() {
	}

	public Item(final LocalDateTime start, final LocalDateTime end) {
		super();
		setStart(start);
		setEnd(end);
	}

	public Item(final LocalDateTime start, final LocalDateTime end, final String content) {
		this(start, end);
		setContent(content);
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public <T> T getStart() {
		return (T) start;
	}

	public void setStart(final LocalDateTime start) {
		this.start = start;
	}

	public void setStart(final Long start) {
		this.start = start;
	}

	@SuppressWarnings("unchecked")
	public <T> T getEnd() {
		return (T) end;
	}

	public void setEnd(final LocalDateTime end) {
		this.end = end;
	}

	public void setEnd(final Long end) {
		this.end = end;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(final Boolean editable) {
		this.editable = editable;
	}

	public Boolean getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(final Boolean updateTime) {
		this.updateTime = updateTime;
	}

	public Boolean getRemove() {
		return remove;
	}

	public void setRemove(final Boolean remove) {
		this.remove = remove;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(final String className) {
		this.className = className;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Item other = (Item) obj;
		return Objects.equals(id, other.id);
	}

	public String toJSON() {
		final JsonObject js = Json.createObject();
		Optional.ofNullable(getId()).ifPresent(v -> js.put("id", v));
		Optional.ofNullable(getContent()).ifPresent(v -> js.put("content", v));
		Optional.ofNullable(getStart()).ifPresent(v -> js.put("start", v.toString()));
		Optional.ofNullable(getEnd()).ifPresent(v -> {
			js.put("end", v.toString());
		});

		Optional.ofNullable(getEditable()).ifPresent(v -> {
			if (v && (getUpdateTime() != null || getRemove() != null)) {
				final JsonObject optionsJs = Json.createObject();
				Optional.ofNullable(getUpdateTime()).ifPresent(u -> optionsJs.put("updateTime", u));
				Optional.ofNullable(getRemove()).ifPresent(r -> optionsJs.put("remove", r));
				js.put("editable", optionsJs);
			} else {
				js.put("editable", v);
			}
		});

		Optional.ofNullable(getTitle()).ifPresent(v -> js.put("title", v));
		Optional.ofNullable(getClassName()).ifPresent(v -> js.put("className", v));
		return js.toJson();
	}
}
