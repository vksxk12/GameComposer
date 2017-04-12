/*
 * Copyright 2017 Mirko Sertic
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
package de.mirkosertic.gameengine.teavmwasm;

import de.mirkosertic.gameengine.core.Logger;
import org.teavm.interop.Import;

public class WASMLogger implements Logger {

    public static final WASMLogger INSTANCE = new WASMLogger();

    @Import(module = "log", name = "log_string")
    public static native void log(String aValue);

    @Override
    public void info(String aMessage) {
        log("INFO : " + aMessage);
    }

    @Override
    public void error(String aMessage) {
        log("ERROR : " + aMessage);
    }

    @Override
    public void time(String aLabel) {
    }

    @Override
    public void timeEnd(String aLabel) {
    }
}
