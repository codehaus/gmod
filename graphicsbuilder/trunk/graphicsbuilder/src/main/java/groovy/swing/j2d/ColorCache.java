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

package groovy.swing.j2d;

import java.awt.Color;
import java.util.Map;
import java.util.TreeMap;

/**
 * A collection of named colors.<br>
 * ColorCache registers all Java, CSS2 and HTML colors by default.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorCache {
    private static final ColorCache instance;
    static{
        instance = new ColorCache();
    }

    /**
     * Returns the singleton instance.
     */
    public static ColorCache getInstance() {
        return instance;
    }

    private Map customColors = new TreeMap();
    private Map standardColors = new TreeMap();

    private ColorCache() {
        initStandardColors();
    }

    /**
     * Retrieves a Color from the cache.<br>
     * It will look first into the user-defined custom colors, if not found then
     * it will try the standard colors.
     *
     * @param name the name of the color to retrieve
     * @return the named color, null if not found
     */
    public Color getColor( String name ) {
        Color color = (Color) customColors.get( name );
        if( color == null ){
            color = (Color) standardColors.get( name );
        }
        return color;
    }

    /**
     * Stores a color on the cache.<br>
     * It will override any existing custom color with the same name.
     *
     * @param name the name of the color to store
     * @param color the Color to store
     */
    public void setColor( String name, Color color ) {
        customColors.put( name, color );
    }

    private void initStandardColors() {
        // java colors
        standardColors.put( "black", Color.black );
        standardColors.put( "blue", Color.blue );
        standardColors.put( "cyan", Color.cyan );
        standardColors.put( "darkGray", Color.darkGray );
        standardColors.put( "gray", Color.gray );
        standardColors.put( "green", Color.green );
        standardColors.put( "lightGray", Color.lightGray );
        standardColors.put( "magenta", Color.magenta );
        standardColors.put( "orange", Color.orange );
        standardColors.put( "pink", Color.pink );
        standardColors.put( "red", Color.red );
        standardColors.put( "white", Color.white );
        standardColors.put( "yellow", Color.yellow );
        // css colors
        standardColors.put( "fuchsia", new Color( 255, 0, 255 ) );
        standardColors.put( "silver", new Color( 192, 192, 192 ) );
        standardColors.put( "olive", new Color( 50, 50, 0 ) );
        standardColors.put( "purple", new Color( 50, 0, 50 ) );
        standardColors.put( "maroon", new Color( 50, 0, 0 ) );
        standardColors.put( "aqua", new Color( 0, 255, 255 ) );
        standardColors.put( "lime", new Color( 0, 255, 0 ) );
        standardColors.put( "teal", new Color( 0, 50, 50 ) );
        standardColors.put( "navy", new Color( 0, 0, 50 ) );
        // html colors
        standardColors.put( "aliceBlue", new Color( 240, 248, 255 ) );
        standardColors.put( "antiqueWhite", new Color( 250, 235, 215 ) );
        standardColors.put( "aquamarine", new Color( 127, 255, 212 ) );
        standardColors.put( "azure", new Color( 240, 255, 255 ) );
        standardColors.put( "bakersChocolate", new Color( 92, 51, 23 ) );
        standardColors.put( "beige", new Color( 245, 245, 220 ) );
        standardColors.put( "bisque", new Color( 255, 228, 196 ) );
        standardColors.put( "blanchedAlmond", new Color( 255, 235, 205 ) );
        standardColors.put( "blueViolet", new Color( 138, 43, 226 ) );
        standardColors.put( "brass", new Color( 181, 166, 66 ) );
        standardColors.put( "brightGold", new Color( 217, 217, 25 ) );
        standardColors.put( "bronze", new Color( 140, 120, 83 ) );
        standardColors.put( "brown", new Color( 165, 42, 42 ) );
        standardColors.put( "burlyWood", new Color( 222, 184, 135 ) );
        standardColors.put( "cadetBlue", new Color( 95, 158, 160 ) );
        standardColors.put( "chartreuse", new Color( 127, 255, 0 ) );
        standardColors.put( "chocolate", new Color( 210, 105, 30 ) );
        standardColors.put( "coolCopper", new Color( 217, 135, 25 ) );
        standardColors.put( "copper", new Color( 184, 115, 51 ) );
        standardColors.put( "coral", new Color( 255, 127, 80 ) );
        standardColors.put( "cornflowerBlue", new Color( 100, 149, 237 ) );
        standardColors.put( "cornsilk", new Color( 255, 248, 220 ) );
        standardColors.put( "crimson", new Color( 220, 20, 60 ) );
        standardColors.put( "darkBlue", new Color( 0, 0, 139 ) );
        standardColors.put( "darkBrown", new Color( 92, 64, 51 ) );
        standardColors.put( "darkCyan", new Color( 0, 139, 139 ) );
        standardColors.put( "darkGoldenRod", new Color( 184, 134, 11 ) );
        standardColors.put( "darkGreen", new Color( 0, 100, 0 ) );
        standardColors.put( "darkGreenCopper", new Color( 74, 118, 110 ) );
        standardColors.put( "darkKhaki", new Color( 189, 183, 107 ) );
        standardColors.put( "darkMagenta", new Color( 139, 0, 139 ) );
        standardColors.put( "darkOliveGreen", new Color( 85, 107, 47 ) );
        standardColors.put( "darkOrange", new Color( 255, 140, 0 ) );
        standardColors.put( "darkOrchid", new Color( 153, 50, 204 ) );
        standardColors.put( "darkPurple", new Color( 135, 31, 120 ) );
        standardColors.put( "darkRed", new Color( 139, 0, 0 ) );
        standardColors.put( "darkSalmon", new Color( 233, 150, 122 ) );
        standardColors.put( "darkSeaGreen", new Color( 143, 188, 143 ) );
        standardColors.put( "darkSlateBlue", new Color( 72, 61, 139 ) );
        standardColors.put( "darkSlateGray", new Color( 47, 79, 79 ) );
        standardColors.put( "darkTan", new Color( 151, 105, 79 ) );
        standardColors.put( "darkTurquoise", new Color( 0, 206, 209 ) );
        standardColors.put( "darkViolet", new Color( 148, 0, 211 ) );
        standardColors.put( "darkWood", new Color( 133, 94, 66 ) );
        standardColors.put( "deepPink", new Color( 255, 20, 147 ) );
        standardColors.put( "deepSkyBlue", new Color( 0, 191, 255 ) );
        standardColors.put( "dimGray", new Color( 105, 105, 105 ) );
        standardColors.put( "dodgerBlue", new Color( 30, 144, 255 ) );
        standardColors.put( "dustyRose", new Color( 133, 99, 99 ) );
        standardColors.put( "fadedBrown", new Color( 245, 204, 176 ) );
        standardColors.put( "feldspar", new Color( 209, 146, 117 ) );
        standardColors.put( "fireBrick", new Color( 178, 34, 34 ) );
        standardColors.put( "floralWhite", new Color( 255, 250, 240 ) );
        standardColors.put( "forestGreen", new Color( 34, 139, 34 ) );
        standardColors.put( "gainsboro", new Color( 220, 220, 220 ) );
        standardColors.put( "ghostWhite", new Color( 248, 248, 255 ) );
        standardColors.put( "gold", new Color( 255, 215, 0 ) );
        standardColors.put( "goldenRod", new Color( 218, 165, 32 ) );
        standardColors.put( "greenCopper", new Color( 82, 127, 118 ) );
        standardColors.put( "greenYellow", new Color( 173, 255, 47 ) );
        standardColors.put( "honeyDew", new Color( 240, 255, 240 ) );
        standardColors.put( "hotPink", new Color( 255, 105, 180 ) );
        standardColors.put( "hunterGreen", new Color( 33, 94, 33 ) );
        standardColors.put( "indianRed", new Color( 205, 92, 92 ) );
        standardColors.put( "indigo", new Color( 75, 0, 130 ) );
        standardColors.put( "ivory", new Color( 255, 255, 240 ) );
        standardColors.put( "khaki", new Color( 240, 230, 140 ) );
        standardColors.put( "lavender", new Color( 230, 230, 250 ) );
        standardColors.put( "lavenderBlush", new Color( 255, 240, 245 ) );
        standardColors.put( "lawnGreen", new Color( 124, 252, 0 ) );
        standardColors.put( "lemonChiffon", new Color( 255, 250, 205 ) );
        standardColors.put( "lightBlue", new Color( 173, 216, 230 ) );
        standardColors.put( "lightCoral", new Color( 240, 128, 128 ) );
        standardColors.put( "lightCyan", new Color( 224, 255, 255 ) );
        standardColors.put( "lightGoldenRodYellow", new Color( 250, 250, 210 ) );
        standardColors.put( "lightGreen", new Color( 144, 238, 144 ) );
        standardColors.put( "lightPink", new Color( 255, 182, 193 ) );
        standardColors.put( "lightSalmon", new Color( 255, 160, 122 ) );
        standardColors.put( "lightSeaGreen", new Color( 32, 178, 170 ) );
        standardColors.put( "lightSkyBlue", new Color( 135, 206, 250 ) );
        standardColors.put( "lightSlateBlue", new Color( 132, 112, 255 ) );
        standardColors.put( "lightSlateGray", new Color( 119, 136, 153 ) );
        standardColors.put( "lightSteelBlue", new Color( 176, 196, 222 ) );
        standardColors.put( "lightWood", new Color( 233, 194, 166 ) );
        standardColors.put( "lightYellow", new Color( 255, 255, 224 ) );
        standardColors.put( "limeGreen", new Color( 50, 205, 50 ) );
        standardColors.put( "linen", new Color( 250, 240, 230 ) );
        standardColors.put( "mandarinOrange", new Color( 228, 120, 51 ) );
        standardColors.put( "mediumAquaMarine", new Color( 102, 205, 170 ) );
        standardColors.put( "mediumBlue", new Color( 0, 0, 205 ) );
        standardColors.put( "mediumGoldenRod", new Color( 234, 234, 174 ) );
        standardColors.put( "mediumOrchid", new Color( 186, 85, 211 ) );
        standardColors.put( "mediumPurple", new Color( 147, 112, 216 ) );
        standardColors.put( "mediumSeaGreen", new Color( 60, 179, 113 ) );
        standardColors.put( "mediumSlateBlue", new Color( 123, 104, 238 ) );
        standardColors.put( "mediumSpringGreen", new Color( 0, 250, 154 ) );
        standardColors.put( "mediumTurquoise", new Color( 72, 209, 204 ) );
        standardColors.put( "mediumVioletRed", new Color( 199, 21, 133 ) );
        standardColors.put( "mediumWood", new Color( 166, 128, 100 ) );
        standardColors.put( "midnightBlue", new Color( 25, 25, 112 ) );
        standardColors.put( "mintCream", new Color( 245, 255, 250 ) );
        standardColors.put( "mistyRose", new Color( 255, 228, 225 ) );
        standardColors.put( "moccasin", new Color( 255, 228, 181 ) );
        standardColors.put( "navajoWhite", new Color( 255, 222, 173 ) );
        standardColors.put( "navyBlue", new Color( 35, 35, 142 ) );
        standardColors.put( "neonBlue", new Color( 77, 77, 255 ) );
        standardColors.put( "neonPink", new Color( 255, 110, 199 ) );
        standardColors.put( "newMidnightBlue", new Color( 0, 0, 156 ) );
        standardColors.put( "newTan", new Color( 235, 199, 158 ) );
        standardColors.put( "oldGold", new Color( 207, 181, 59 ) );
        standardColors.put( "oldLace", new Color( 253, 245, 230 ) );
        standardColors.put( "oliveDrab", new Color( 107, 142, 35 ) );
        standardColors.put( "orangeRed", new Color( 255, 69, 0 ) );
        standardColors.put( "orchid", new Color( 218, 112, 214 ) );
        standardColors.put( "paleGoldenRod", new Color( 238, 232, 170 ) );
        standardColors.put( "paleGreen", new Color( 152, 251, 152 ) );
        standardColors.put( "paleTurquoise", new Color( 175, 238, 238 ) );
        standardColors.put( "paleVioletRed", new Color( 216, 112, 147 ) );
        standardColors.put( "papayaWhip", new Color( 255, 239, 213 ) );
        standardColors.put( "peachPuff", new Color( 255, 218, 185 ) );
        standardColors.put( "peru", new Color( 205, 133, 63 ) );
        standardColors.put( "plum", new Color( 221, 160, 221 ) );
        standardColors.put( "powderBlue", new Color( 176, 224, 230 ) );
        standardColors.put( "quartz", new Color( 217, 217, 243 ) );
        standardColors.put( "richBlue", new Color( 89, 89, 171 ) );
        standardColors.put( "rosyBrown", new Color( 188, 143, 143 ) );
        standardColors.put( "royalBlue", new Color( 65, 105, 225 ) );
        standardColors.put( "saddleBrown", new Color( 139, 69, 19 ) );
        standardColors.put( "salmon", new Color( 250, 128, 114 ) );
        standardColors.put( "sandyBrown", new Color( 244, 164, 96 ) );
        standardColors.put( "scarlet", new Color( 140, 23, 23 ) );
        standardColors.put( "seaGreen", new Color( 46, 139, 87 ) );
        standardColors.put( "seaShell", new Color( 255, 245, 238 ) );
        standardColors.put( "semiSweetChocolate", new Color( 107, 66, 38 ) );
        standardColors.put( "sienna", new Color( 160, 82, 45 ) );
        standardColors.put( "skyBlue", new Color( 135, 206, 235 ) );
        standardColors.put( "slateBlue", new Color( 106, 90, 205 ) );
        standardColors.put( "slateGray", new Color( 112, 128, 144 ) );
        standardColors.put( "snow", new Color( 255, 250, 250 ) );
        standardColors.put( "spicyPink", new Color( 255, 28, 174 ) );
        standardColors.put( "springGreen", new Color( 0, 255, 127 ) );
        standardColors.put( "steelBlue", new Color( 70, 130, 180 ) );
        standardColors.put( "summerSky", new Color( 56, 176, 222 ) );
        standardColors.put( "tan", new Color( 210, 180, 140 ) );
        standardColors.put( "thistle", new Color( 216, 191, 216 ) );
        standardColors.put( "tomato", new Color( 255, 99, 71 ) );
        standardColors.put( "turquoise", new Color( 64, 224, 208 ) );
        standardColors.put( "veryLightGrey", new Color( 205, 205, 205 ) );
        standardColors.put( "violet", new Color( 238, 130, 238 ) );
        standardColors.put( "violetRed", new Color( 208, 32, 144 ) );
        standardColors.put( "wheat", new Color( 245, 222, 179 ) );
        standardColors.put( "whiteSmoke", new Color( 245, 245, 245 ) );
        standardColors.put( "yellowGreen", new Color( 154, 205, 50 ) );
    }
}