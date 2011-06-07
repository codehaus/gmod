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
package groovyx.javafx.factory

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Camera;
import javafx.scene.paint.*;
import javafx.scene.Cursor;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jimclarke
 */
class SceneWrapper {
    public Parent parent;
    public double width = -1;
    public double height = -1;
    public Camera camera;
    public Cursor cursor;
    public Paint fill = Color.WHITE;
    public List<String> stylesheets;


    public Scene createScene() {
        Scene scene =  new Scene(parent, width, height, fill);
        if(camera != null)
                scene.setCamera(camera);
        if(cursor != null)
                scene.setCursor(cursor);
        return scene;
    }
}

