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

package org.kordamp.groovy.wings;

import org.kordamp.groovy.wings.factory.STextArgWidgetFactory;
import org.kordamp.groovy.wings.factory.XColorPickerFactory;
import org.kordamp.groovy.wings.factory.XDivisionFactory;
import org.kordamp.groovy.wings.factory.XPopupFrameFactory;
import org.kordamp.groovy.wings.factory.XScrollPaneFactory;
import org.kordamp.groovy.wings.factory.YUIxGridFactory;
import org.wingx.XCalendar;
import org.wingx.XInplaceEditor;
import org.wingx.XPageScroller;
import org.wingx.XScrollablePanel;
import org.wingx.XSuggest;
import org.wingx.XTable;
import org.wingx.XTreeTable;

/**
 * A helper class for creating WingS widgets using GroovyMarkup.<br>
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class WingXBuilder extends WingSBuilder {
   public WingXBuilder() {
      registerXWidgets();
   }

   protected void registerXWidgets() {
      registerBeanFactory( "calendar", XCalendar.class );
      registerFactory( "colorPicker", new XColorPickerFactory() );
      registerFactory( "division", new XDivisionFactory() );
      registerBeanFactory( "inplaceEditor", XInplaceEditor.class );
      registerBeanFactory( "xpageScroller", XPageScroller.class );
      registerFactory( "popupFrame", new XPopupFrameFactory() );
      registerBeanFactory( "scrollablePanel", XScrollablePanel.class );
      registerFactory( "xscrollPane", new XScrollPaneFactory() );
      registerFactory( "suggest", new STextArgWidgetFactory( XSuggest.class ) );
      registerBeanFactory( "xtable", XTable.class );
      registerBeanFactory( "treeTable", XTreeTable.class );
      registerFactory( "yuixGrid", new YUIxGridFactory() );
   }
}
