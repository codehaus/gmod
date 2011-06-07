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

import javafx.scene.control.*;
import javafx.scene.paint.*;

/**
 *
 * @author jimclarke
 */
class LabeledFactory extends NodeFactory {


    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Control control;
        if (FactoryBuilderSupport.checkValueIsType(value, name, Control.class)) {
            control = value
        } else {
            switch(name) {
                case 'button':
                    control = new Button();
                    break;
                case 'checkBox':
                    control = new CheckBox();
                    break;
                case 'label':
                    control = new Label();
                    break;
                case 'choiceBox':
                    control = new ChoiceBox();
                    break;
                case 'hyperlink':
                    control = new Hyperlink();
                    break;
                case 'tooltip':
                    control = new Tooltip();
                    break;
                case 'radioButton':
                    control = new RadioButton();
                    break;
                case 'toggleButton':
                    control = new ToggleButton();
                    break;
            }
        }
        //FXHelper.fxAttributes(control, attributes);
        return control;
    }

    public void setChild( FactoryBuilderSupport builder, Object parent, Object child ) {
        if(child instanceof Tooltip && parent instanceof Control) {
            ((Control)parent).setTooltip(child);
        }else if(child instanceof Node) {
            ((Labeled)parent).setGraphic(child);
        }else {
            super.setChild(builder, parent, child);
        }
    }
}

