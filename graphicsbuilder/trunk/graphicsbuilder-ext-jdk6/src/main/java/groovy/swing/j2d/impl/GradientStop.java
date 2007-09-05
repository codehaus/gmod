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
 */

package groovy.swing.j2d.impl;

import java.awt.Color;

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class GradientStop {
   private Color color;
   private boolean dirty;
   private float offset;

   public Color getColor() {
      return color;
   }

   public float getOffset() {
      return offset;
   }

   public void setColor( Color color ) {
      if( this.color != color ){
         dirty = true;
      }
      this.color = color;
   }

   public void setOffset( float offset ) {
      if( this.offset != offset ){
         dirty = true;
      }
      this.offset = offset;
   }

   protected boolean isDirty() {
      return dirty;
   }

   protected void setDirty( boolean dirty ) {
      this.dirty = dirty;
   }
}