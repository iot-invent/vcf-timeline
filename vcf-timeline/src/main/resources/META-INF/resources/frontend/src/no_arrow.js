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
/**
 * timeline-arrows
 * https://github.com/javdome/timeline-arrows
 *
 * Class to easily draw lines to connect items in the vis Timeline module.
 *
 * @version 3.1.0
 * @date    2021-04-06
 *
 * @copyright (c) Javi Domenech (javdome@gmail.com) 
 *
 *
 * @license
 * timeline-arrows is dual licensed under both
 *
 *   1. The Apache 2.0 License
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   and
 *
 *   2. The MIT License
 *      http://opensource.org/licenses/MIT
 *
 * timeline-arrows may be distributed under either license.
 */

 export default class Arrow {

    constructor(timeline) {
        this._timeline = timeline;
        this._dependency = [];
        this._dependencyPath = [];
        this._minItemHeight = null;
    }
  
    _initialize() {
    }
    
    _createPath(){
    }

    _drawDependencies() {
    }


    //Función que recibe in Item y devuelve la posición en pantalla del item.
    _getItemPos (item) {
        let left_x = item.left;

        let top_y = item.parent.top + item.parent.height - item.top - item.height;
        return {
            left: left_x,
            top: top_y,
            right: left_x + item.width,
            bottom: top_y + item.height,
            mid_x: left_x + item.width / 2,
            mid_y: item.top + this._minItemHeight / 2,
            width: item.width,
            height: item.height,
        }
    }

    addArrow (dep) {
    }

    getArrow (id) {
        return null;
    }
    
    //Función que recibe el id de una flecha y la elimina.
    removeArrow(id) {
    }

    //Función que recibe el id de un item y elimina la flecha.
    removeArrowbyItemId(id) {
        return [];
    }

    _clearAllArrows(){
    }

    setDependencies(dependencies) {
    }

    _getMinItemHeight(){
        return minHeight;
    }

  }
