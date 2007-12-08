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

package org.kordamp.groovy.wings.demo

import java.awt.Color
import org.kordamp.groovy.wings.WingSBuilder
import org.wings.*

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
class GuessingGame {
   GuessingGame(){
      def randomNr = (new Random().nextInt(10) + 1) as String
      def builder = new WingSBuilder()

      /* STitledBorder was removed in wings-3.1
      def border = SBorderFactory.createSTitledBorder(
                    SBorderFactory.createSLineBorder([0,0,255] as Color, 2),
                    "Guessing Game")
      */
      def border = SBorderFactory.createSLineBorder([0,0,255] as Color, 2)
      def font = new SFont( null, SFont.BOLD, 14 )

      def frame = builder.frame( title: "Guessing Game" ) {
         panel( border: border){
            gridLayout( columns: 1, rows: 5, vgap: 10)
            label("Hello World - this is wingS (+WingSBuilder&Groovy)!",
                  font: font)
            label("We want fun, so let's play a game!" +
                  "Try to guess a number between 1 and 10.")
            textField( id: "answer" )
            button( text: "Guess!", actionPerformed: { event ->
               def value = builder.answer.text
               if( value == randomNr ){
                  builder.message.text = "Congratulations! You guessed my number!"
               }else{
                  builder.message.text = "No - '${value}' is not the right number. Try again!"
               }
            })
            label( id: "message" )
         }
      }

      frame.visible = true
   }
}
