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

    private Map cache = new TreeMap();

    private ColorCache() {
        initColors();
    }

    /**
     * Retrieves a Color from the cache.
     *
     * @param name the name of the color to retrieve
     * @return the named color, null if not found
     */
    public Color getColor( String name ) {
        return (Color) cache.get( name );
    }

    /**
     * Stores a color on the cache.<br>
     * It will override any existing color with the same name.
     *
     * @param name the name of the color to store
     * @param color the Color to store
     */
    public void setColor( String name, Color color ) {
        cache.put( name, color );
    }

    private void initColors() {
        // java colors
        setColor( "black", Color.black );
        setColor( "blue", Color.blue );
        setColor( "cyan", Color.cyan );
        setColor( "darkGray", Color.darkGray );
        setColor( "gray", Color.gray );
        setColor( "green", Color.green );
        setColor( "lightGray", Color.lightGray );
        setColor( "magenta", Color.magenta );
        setColor( "orange", Color.orange );
        setColor( "pink", Color.pink );
        setColor( "red", Color.red );
        setColor( "white", Color.white );
        setColor( "yellow", Color.yellow );
        // css colors
        setColor( "fuchsia", new Color( 255, 0, 255 ) );
        setColor( "silver", new Color( 192, 192, 192 ) );
        setColor( "olive", new Color( 50, 50, 0 ) );
        setColor( "purple", new Color( 50, 0, 50 ) );
        setColor( "maroon", new Color( 50, 0, 0 ) );
        setColor( "aqua", new Color( 0, 255, 255 ) );
        setColor( "lime", new Color( 0, 255, 0 ) );
        setColor( "teal", new Color( 0, 50, 50 ) );
        setColor( "navy", new Color( 0, 0, 50 ) );
        // html colors
        setColor( "aliceBlue", new Color( 240, 248, 255 ) );
        setColor( "antiqueWhite", new Color( 250, 235, 215 ) );
        setColor( "aquamarine", new Color( 127, 255, 212 ) );
        setColor( "aquamarine", new Color( 112, 219, 147 ) );
        setColor( "azure", new Color( 240, 255, 255 ) );
        setColor( "bakersChocolate", new Color( 92, 51, 23 ) );
        setColor( "beige", new Color( 245, 245, 220 ) );
        setColor( "bisque", new Color( 255, 228, 196 ) );
        setColor( "blanchedAlmond", new Color( 255, 235, 205 ) );
        setColor( "blueViolet", new Color( 138, 43, 226 ) );
        setColor( "brass", new Color( 181, 166, 66 ) );
        setColor( "brightGold", new Color( 217, 217, 25 ) );
        setColor( "bronze", new Color( 140, 120, 83 ) );
        setColor( "brown", new Color( 165, 42, 42 ) );
        setColor( "burlyWood", new Color( 222, 184, 135 ) );
        setColor( "cadetBlue", new Color( 95, 158, 160 ) );
        setColor( "chartreuse", new Color( 127, 255, 0 ) );
        setColor( "chocolate", new Color( 210, 105, 30 ) );
        setColor( "coolCopper", new Color( 217, 135, 25 ) );
        setColor( "copper", new Color( 184, 115, 51 ) );
        setColor( "coral", new Color( 255, 127, 80 ) );
        setColor( "cornflowerBlue", new Color( 100, 149, 237 ) );
        setColor( "cornsilk", new Color( 255, 248, 220 ) );
        setColor( "crimson", new Color( 220, 20, 60 ) );
        setColor( "darkBlue", new Color( 0, 0, 139 ) );
        setColor( "darkBrown", new Color( 92, 64, 51 ) );
        setColor( "darkCyan", new Color( 0, 139, 139 ) );
        setColor( "darkGoldenRod", new Color( 184, 134, 11 ) );
        setColor( "darkGreen", new Color( 0, 100, 0 ) );
        setColor( "darkGreenCopper", new Color( 74, 118, 110 ) );
        setColor( "darkKhaki", new Color( 189, 183, 107 ) );
        setColor( "darkMagenta", new Color( 139, 0, 139 ) );
        setColor( "darkOliveGreen", new Color( 85, 107, 47 ) );
        setColor( "darkOliveGreen", new Color( 79, 79, 47 ) );
        setColor( "darkOrange", new Color( 255, 140, 0 ) );
        setColor( "darkOrchid", new Color( 153, 50, 204 ) );
        setColor( "darkPurple", new Color( 135, 31, 120 ) );
        setColor( "darkRed", new Color( 139, 0, 0 ) );
        setColor( "darkSalmon", new Color( 233, 150, 122 ) );
        setColor( "darkSeaGreen", new Color( 143, 188, 143 ) );
        setColor( "darkSlateBlue", new Color( 72, 61, 139 ) );
        setColor( "darkSlateGray", new Color( 47, 79, 79 ) );
        setColor( "darkTan", new Color( 151, 105, 79 ) );
        setColor( "darkTurquoise", new Color( 0, 206, 209 ) );
        setColor( "darkViolet", new Color( 148, 0, 211 ) );
        setColor( "darkWood", new Color( 133, 94, 66 ) );
        setColor( "deepPink", new Color( 255, 20, 147 ) );
        setColor( "deepSkyBlue", new Color( 0, 191, 255 ) );
        setColor( "dimGray", new Color( 105, 105, 105 ) );
        setColor( "dodgerBlue", new Color( 30, 144, 255 ) );
        setColor( "dustyRose", new Color( 133, 99, 99 ) );
        setColor( "fadedBrown", new Color( 245, 204, 176 ) );
        setColor( "feldspar", new Color( 209, 146, 117 ) );
        setColor( "fireBrick", new Color( 178, 34, 34 ) );
        setColor( "floralWhite", new Color( 255, 250, 240 ) );
        setColor( "forestGreen", new Color( 34, 139, 34 ) );
        setColor( "gainsboro", new Color( 220, 220, 220 ) );
        setColor( "ghostWhite", new Color( 248, 248, 255 ) );
        setColor( "gold", new Color( 255, 215, 0 ) );
        setColor( "goldenRod", new Color( 218, 165, 32 ) );
        setColor( "greenCopper", new Color( 82, 127, 118 ) );
        setColor( "greenYellow", new Color( 173, 255, 47 ) );
        setColor( "honeyDew", new Color( 240, 255, 240 ) );
        setColor( "hotPink", new Color( 255, 105, 180 ) );
        setColor( "hunterGreen", new Color( 33, 94, 33 ) );
        setColor( "indianRed", new Color( 205, 92, 92 ) );
        setColor( "indigo", new Color( 75, 0, 130 ) );
        setColor( "ivory", new Color( 255, 255, 240 ) );
        setColor( "khaki", new Color( 240, 230, 140 ) );
        setColor( "lavender", new Color( 230, 230, 250 ) );
        setColor( "lavenderBlush", new Color( 255, 240, 245 ) );
        setColor( "lawnGreen", new Color( 124, 252, 0 ) );
        setColor( "lemonChiffon", new Color( 255, 250, 205 ) );
        setColor( "lightBlue", new Color( 173, 216, 230 ) );
        setColor( "lightCoral", new Color( 240, 128, 128 ) );
        setColor( "lightCyan", new Color( 224, 255, 255 ) );
        setColor( "lightGoldenRodYellow", new Color( 250, 250, 210 ) );
        setColor( "lightGreen", new Color( 144, 238, 144 ) );
        setColor( "lightPink", new Color( 255, 182, 193 ) );
        setColor( "lightSalmon", new Color( 255, 160, 122 ) );
        setColor( "lightSeaGreen", new Color( 32, 178, 170 ) );
        setColor( "lightSkyBlue", new Color( 135, 206, 250 ) );
        setColor( "lightSlateBlue", new Color( 132, 112, 255 ) );
        setColor( "lightSlateGray", new Color( 119, 136, 153 ) );
        setColor( "lightSteelBlue", new Color( 176, 196, 222 ) );
        setColor( "lightWood", new Color( 233, 194, 166 ) );
        setColor( "lightYellow", new Color( 255, 255, 224 ) );
        setColor( "limeGreen", new Color( 50, 205, 50 ) );
        setColor( "linen", new Color( 250, 240, 230 ) );
        setColor( "mandarinOrange", new Color( 228, 120, 51 ) );
        setColor( "mediumAquaMarine", new Color( 102, 205, 170 ) );
        setColor( "mediumBlue", new Color( 0, 0, 205 ) );
        setColor( "mediumGoldenRod", new Color( 234, 234, 174 ) );
        setColor( "mediumOrchid", new Color( 186, 85, 211 ) );
        setColor( "mediumPurple", new Color( 147, 112, 216 ) );
        setColor( "mediumSeaGreen", new Color( 60, 179, 113 ) );
        setColor( "mediumSlateBlue", new Color( 123, 104, 238 ) );
        setColor( "mediumSpringGreen", new Color( 0, 250, 154 ) );
        setColor( "mediumTurquoise", new Color( 72, 209, 204 ) );
        setColor( "mediumVioletRed", new Color( 199, 21, 133 ) );
        setColor( "mediumWood", new Color( 166, 128, 100 ) );
        setColor( "midnightBlue", new Color( 25, 25, 112 ) );
        setColor( "mintCream", new Color( 245, 255, 250 ) );
        setColor( "mistyRose", new Color( 255, 228, 225 ) );
        setColor( "moccasin", new Color( 255, 228, 181 ) );
        setColor( "navajoWhite", new Color( 255, 222, 173 ) );
        setColor( "navyBlue", new Color( 35, 35, 142 ) );
        setColor( "neonBlue", new Color( 77, 77, 255 ) );
        setColor( "neonPink", new Color( 255, 110, 199 ) );
        setColor( "newMidnightBlue", new Color( 0, 0, 156 ) );
        setColor( "newTan", new Color( 235, 199, 158 ) );
        setColor( "oldGold", new Color( 207, 181, 59 ) );
        setColor( "oldLace", new Color( 253, 245, 230 ) );
        setColor( "oliveDrab", new Color( 107, 142, 35 ) );
        setColor( "orangeRed", new Color( 255, 69, 0 ) );
        setColor( "orchid", new Color( 218, 112, 214 ) );
        setColor( "paleGoldenRod", new Color( 238, 232, 170 ) );
        setColor( "paleGreen", new Color( 152, 251, 152 ) );
        setColor( "paleTurquoise", new Color( 175, 238, 238 ) );
        setColor( "paleVioletRed", new Color( 216, 112, 147 ) );
        setColor( "papayaWhip", new Color( 255, 239, 213 ) );
        setColor( "peachPuff", new Color( 255, 218, 185 ) );
        setColor( "peru", new Color( 205, 133, 63 ) );
        setColor( "plum", new Color( 221, 160, 221 ) );
        setColor( "powderBlue", new Color( 176, 224, 230 ) );
        setColor( "quartz", new Color( 217, 217, 243 ) );
        setColor( "richBlue", new Color( 89, 89, 171 ) );
        setColor( "rosyBrown", new Color( 188, 143, 143 ) );
        setColor( "royalBlue", new Color( 65, 105, 225 ) );
        setColor( "saddleBrown", new Color( 139, 69, 19 ) );
        setColor( "salmon", new Color( 250, 128, 114 ) );
        setColor( "sandyBrown", new Color( 244, 164, 96 ) );
        setColor( "scarlet", new Color( 140, 23, 23 ) );
        setColor( "seaGreen", new Color( 46, 139, 87 ) );
        setColor( "seaShell", new Color( 255, 245, 238 ) );
        setColor( "semiSweetChocolate", new Color( 107, 66, 38 ) );
        setColor( "sienna", new Color( 160, 82, 45 ) );
        setColor( "skyBlue", new Color( 135, 206, 235 ) );
        setColor( "slateBlue", new Color( 106, 90, 205 ) );
        setColor( "slateGray", new Color( 112, 128, 144 ) );
        setColor( "snow", new Color( 255, 250, 250 ) );
        setColor( "spicyPink", new Color( 255, 28, 174 ) );
        setColor( "springGreen", new Color( 0, 255, 127 ) );
        setColor( "steelBlue", new Color( 70, 130, 180 ) );
        setColor( "steelBlue", new Color( 35, 107, 142 ) );
        setColor( "summerSky", new Color( 56, 176, 222 ) );
        setColor( "tan", new Color( 210, 180, 140 ) );
        setColor( "thistle", new Color( 216, 191, 216 ) );
        setColor( "tomato", new Color( 255, 99, 71 ) );
        setColor( "turquoise", new Color( 64, 224, 208 ) );
        setColor( "veryLightGrey", new Color( 205, 205, 205 ) );
        setColor( "violet", new Color( 238, 130, 238 ) );
        setColor( "violetRed", new Color( 208, 32, 144 ) );
        setColor( "wheat", new Color( 245, 222, 179 ) );
        setColor( "whiteSmoke", new Color( 245, 245, 245 ) );
        setColor( "yellowGreen", new Color( 154, 205, 50 ) );
    }
}