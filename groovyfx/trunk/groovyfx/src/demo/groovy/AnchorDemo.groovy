/*
* Copyright 2011 the original author or authors.
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


import groovyx.javafx.GroovyFX
import groovyx.javafx.SceneGraphBuilder

GroovyFX.start {
    def sg = new SceneGraphBuilder();

    sg.stage(title: "GroovyFX AnchorPane Demo", width: 650, height:450, visible: true) {
         scene(fill: groovyblue) {
             anchorPane {
                 button("ONE", topAnchor: 10, bottomAnchor: 10, rightAnchor: 110, leftAnchor: 10)
                 button("TWO", rightAnchor: 10, topAnchor: 10)
             }
         }
    }
}


