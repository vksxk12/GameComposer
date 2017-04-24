/*
 * Copyright 2016 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.gamecomposer.objectinspector.utils;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.validation.Validator;

public class PositiveIntegerPropertyEditor extends AbstractIntegerPropertyEditor {

    public PositiveIntegerPropertyEditor(PropertySheet.Item aItem) {
        super(aItem, Validator.combine(
                Validator.createEmptyValidator("A value is required"),
                Validator.createPredicateValidator(new PositiveIntegerPredicate(), "Value is not a valid number")
        ));
    }
}