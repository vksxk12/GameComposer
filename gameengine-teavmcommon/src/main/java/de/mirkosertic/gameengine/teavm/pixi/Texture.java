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
package de.mirkosertic.gameengine.teavm.pixi;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public abstract class Texture implements JSObject {

    @JSBody(params = {"aResourceName"}, script = "return PIXI.Texture.fromImage(aResourceName);")
    public static native Texture createTextureFromImage(String aResourceName);

    @JSBody(params = {"aFrameName"}, script = "return PIXI.Texture.fromFrame(aFrameName);")
    public static native Texture createFromFrame(String aFrameName);

    public abstract void destroy();
}
