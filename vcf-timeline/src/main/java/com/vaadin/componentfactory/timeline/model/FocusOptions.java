/*
* Copyright (c) 2020 - 2022 IoT Invent GmbH
* All rights reserved.
*/
package com.vaadin.componentfactory.timeline.model;

import java.util.Optional;

import elemental.json.Json;
import elemental.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Yevgen Turchanin
 *
 */
@Data
@AllArgsConstructor
@Builder
public class FocusOptions {

	private Boolean zoom = Boolean.TRUE;

	private Boolean animation = Boolean.TRUE;

	public FocusOptions() {
	}

	public String toJSON() {
		final JsonObject js = Json.createObject();
		Optional.ofNullable(getAnimation()).ifPresent(v -> js.put("animation", v));
		Optional.ofNullable(getZoom()).ifPresent(v -> js.put("zoom", v));
		return js.toJson();
	}
}
