/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions

import javax.imageio.ImageIO

class ViewUtils {
    static loadImage = {String filename ->
        URL imageUrl = Thread.currentThread().getContextClassLoader().getResource(filename)
        return ImageIO.read(imageUrl)
    }

    static icons = [:]

    static {
        icons.unreadEntryIcon = ViewUtils.loadImage("zeusboxstudio-feedicons2/RSS_file_16.png")
        icons.readEntryIcon = ViewUtils.loadImage("zeusboxstudio-feedicons2/mark_as_read_16.png")
    }
}