/*
 * Copyright 2007 the original author or authors.
 *
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
 */

package org.gradle.impl.api.tasks.compile

import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * @author Hans Dockter
 */
abstract class AbstractOptions {
    void define(Map args) {
        args.each {String key, Object value ->
            this."$key" = value
        }
    }

    Map optionMap() {
        getClass().declaredFields.findAll {Field field -> isOptionField(field)}.inject([:]) {Map optionMap, Field field ->
            addStringifiedValueToMapIfNotNull(optionMap, field)
        }
    }

    // todo: change modifier to private when GROOVY-2565 is fixed.
    protected Map addStringifiedValueToMapIfNotNull(Map map, Field field) {
        def value = this."${field.name}"
        if (value != null) {map[antProperty(field.name)] = antValue(field.name, value)}
        map
    }

    // todo: change modifier to private when GROOVY-2565 is fixed.
    protected boolean isOptionField(Field field) {
        ((field.getModifiers() & Modifier.STATIC) == 0) &&
                (!field.getName().equals("metaClass")) &&
                (!excludedFieldsFromOptionMap().contains(field.name))
    }

    private def antProperty(String fieldName) {
        String antProperty = null
        if (fieldName2AntMap().keySet().contains(fieldName)) {
            antProperty = fieldName2AntMap()[fieldName]
        }
        antProperty ?: fieldName
    }

    private def antValue(String fieldName, def value) {
        String antValue = null
        if (fieldValue2AntMap().keySet().contains(fieldName)) {
            antValue = fieldValue2AntMap()[fieldName]().toString()
        }
        antValue ?: value.toString()
    }

    List excludedFieldsFromOptionMap() {
        []
    }

    Map fieldName2AntMap() {
        [:]
    }

    Map fieldValue2AntMap() {
        [:]
    }
}
