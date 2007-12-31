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

package groovy.swing.j2d

import java.awt.Color

/**
 * A collection of named colors.<br>
 * ColorCache registers all Java, CSS2 and HTML colors by default.
 *
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class ColorCache {
    private static final ColorCache instance
    static{
        instance = new ColorCache()
    }

    /**
     * Returns the singleton instance.
     */
    public static ColorCache getInstance() {
        return instance
    }

    private Map customColors = new TreeMap()
    private Map standardColors = new TreeMap()

    private ColorCache() {
        initStandardColors()
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
        Color color = customColors[name]
        if( color == null ){
            color = standardColors[name]
        }
        if( color == null && name.startsWith("#") ){
           def cdef = name[1..-1]
           if( cdef.length() == 3 ){
              color = new Color( Integer.parseInt("${cdef[0]}${cdef[0]}",16),
                                 Integer.parseInt("${cdef[1]}${cdef[1]}",16),
                                 Integer.parseInt("${cdef[2]}${cdef[2]}",16))
              customColors[name] = color
           }else if( cdef.length() == 6 ){
              color = new Color( Integer.parseInt(cdef[0..1],16),
                                 Integer.parseInt(cdef[2..3],16),
                                 Integer.parseInt(cdef[4..5],16))
              customColors[name] = color
           }
        }
        return color
    }

    public Color getColor( Color color ){
       return color
    }

    /**
     * Stores a color on the cache.<br>
     * It will override any existing custom color with the same name.
     *
     * @param name the name of the color to store
     * @param color the Color to store
     */
    public void setColor( String name, Color color ) {
        customColors[name] = color
    }

    private void initStandardColors() {
        standardColors["none"] = new Color(0,0,0,0)

        // java colors
        standardColors["black"] = Color.black
        standardColors["blue"] = Color.blue
        standardColors["cyan"] = Color.cyan
        standardColors["darkGray"] = Color.darkGray
        standardColors["gray"] = Color.gray
        standardColors["green"] = Color.green
        standardColors["lightGray"] = Color.lightGray
        standardColors["magenta"] = Color.magenta
        standardColors["orange"] = Color.orange
        standardColors["pink"] = Color.pink
        standardColors["red"] = Color.red
        standardColors["white"] = Color.white
        standardColors["yellow"] = Color.yellow
        // css colors
        standardColors["fuchsia"] = new Color( 255, 0, 255 )
        standardColors["silver"] = new Color( 192, 192, 192 )
        standardColors["olive"] = new Color( 50, 50, 0 )
        standardColors["purple"] = new Color( 50, 0, 50 )
        standardColors["maroon"] = new Color( 50, 0, 0 )
        standardColors["aqua"] = new Color( 0, 255, 255 )
        standardColors["lime"] = new Color( 0, 255, 0 )
        standardColors["teal"] = new Color( 0, 50, 50 )
        standardColors["navy"] = new Color( 0, 0, 50 )
        // html colors
        standardColors["aliceBlue"] = new Color( 240, 248, 255 )
        standardColors["antiqueWhite"] = new Color( 250, 235, 215 )
        standardColors["aquamarine"] = new Color( 127, 255, 212 )
        standardColors["azure"] = new Color( 240, 255, 255 )
        standardColors["bakersChocolate"] = new Color( 92, 51, 23 )
        standardColors["beige"] = new Color( 245, 245, 220 )
        standardColors["bisque"] = new Color( 255, 228, 196 )
        standardColors["blanchedAlmond"] = new Color( 255, 235, 205 )
        standardColors["blueViolet"] = new Color( 138, 43, 226 )
        standardColors["brass"] = new Color( 181, 166, 66 )
        standardColors["brightGold"] = new Color( 217, 217, 25 )
        standardColors["bronze"] = new Color( 140, 120, 83 )
        standardColors["brown"] = new Color( 165, 42, 42 )
        standardColors["burlyWood"] = new Color( 222, 184, 135 )
        standardColors["cadetBlue"] = new Color( 95, 158, 160 )
        standardColors["chartreuse"] = new Color( 127, 255, 0 )
        standardColors["chocolate"] = new Color( 210, 105, 30 )
        standardColors["coolCopper"] = new Color( 217, 135, 25 )
        standardColors["copper"] = new Color( 184, 115, 51 )
        standardColors["coral"] = new Color( 255, 127, 80 )
        standardColors["cornflowerBlue"] = new Color( 100, 149, 237 )
        standardColors["cornsilk"] = new Color( 255, 248, 220 )
        standardColors["crimson"] = new Color( 220, 20, 60 )
        standardColors["darkBlue"] = new Color( 0, 0, 139 )
        standardColors["darkBrown"] = new Color( 92, 64, 51 )
        standardColors["darkCyan"] = new Color( 0, 139, 139 )
        standardColors["darkGoldenRod"] = new Color( 184, 134, 11 )
        standardColors["darkGreen"] = new Color( 0, 100, 0 )
        standardColors["darkGreenCopper"] = new Color( 74, 118, 110 )
        standardColors["darkKhaki"] = new Color( 189, 183, 107 )
        standardColors["darkMagenta"] = new Color( 139, 0, 139 )
        standardColors["darkOliveGreen"] = new Color( 85, 107, 47 )
        standardColors["darkOrange"] = new Color( 255, 140, 0 )
        standardColors["darkOrchid"] = new Color( 153, 50, 204 )
        standardColors["darkPurple"] = new Color( 135, 31, 120 )
        standardColors["darkRed"] = new Color( 139, 0, 0 )
        standardColors["darkSalmon"] = new Color( 233, 150, 122 )
        standardColors["darkSeaGreen"] = new Color( 143, 188, 143 )
        standardColors["darkSlateBlue"] = new Color( 72, 61, 139 )
        standardColors["darkSlateGray"] = new Color( 47, 79, 79 )
        standardColors["darkTan"] = new Color( 151, 105, 79 )
        standardColors["darkTurquoise"] = new Color( 0, 206, 209 )
        standardColors["darkViolet"] = new Color( 148, 0, 211 )
        standardColors["darkWood"] = new Color( 133, 94, 66 )
        standardColors["deepPink"] = new Color( 255, 20, 147 )
        standardColors["deepSkyBlue"] = new Color( 0, 191, 255 )
        standardColors["dimGray"] = new Color( 105, 105, 105 )
        standardColors["dodgerBlue"] = new Color( 30, 144, 255 )
        standardColors["dustyRose"] = new Color( 133, 99, 99 )
        standardColors["fadedBrown"] = new Color( 245, 204, 176 )
        standardColors["feldspar"] = new Color( 209, 146, 117 )
        standardColors["fireBrick"] = new Color( 178, 34, 34 )
        standardColors["floralWhite"] = new Color( 255, 250, 240 )
        standardColors["forestGreen"] = new Color( 34, 139, 34 )
        standardColors["gainsboro"] = new Color( 220, 220, 220 )
        standardColors["ghostWhite"] = new Color( 248, 248, 255 )
        standardColors["gold"] = new Color( 255, 215, 0 )
        standardColors["goldenRod"] = new Color( 218, 165, 32 )
        standardColors["greenCopper"] = new Color( 82, 127, 118 )
        standardColors["greenYellow"] = new Color( 173, 255, 47 )
        standardColors["honeyDew"] = new Color( 240, 255, 240 )
        standardColors["hotPink"] = new Color( 255, 105, 180 )
        standardColors["hunterGreen"] = new Color( 33, 94, 33 )
        standardColors["indianRed"] = new Color( 205, 92, 92 )
        standardColors["indigo"] = new Color( 75, 0, 130 )
        standardColors["ivory"] = new Color( 255, 255, 240 )
        standardColors["khaki"] = new Color( 240, 230, 140 )
        standardColors["lavender"] = new Color( 230, 230, 250 )
        standardColors["lavenderBlush"] = new Color( 255, 240, 245 )
        standardColors["lawnGreen"] = new Color( 124, 252, 0 )
        standardColors["lemonChiffon"] = new Color( 255, 250, 205 )
        standardColors["lightBlue"] = new Color( 173, 216, 230 )
        standardColors["lightCoral"] = new Color( 240, 128, 128 )
        standardColors["lightCyan"] = new Color( 224, 255, 255 )
        standardColors["lightGoldenRodYellow"] = new Color( 250, 250, 210 )
        standardColors["lightGreen"] = new Color( 144, 238, 144 )
        standardColors["lightPink"] = new Color( 255, 182, 193 )
        standardColors["lightSalmon"] = new Color( 255, 160, 122 )
        standardColors["lightSeaGreen"] = new Color( 32, 178, 170 )
        standardColors["lightSkyBlue"] = new Color( 135, 206, 250 )
        standardColors["lightSlateBlue"] = new Color( 132, 112, 255 )
        standardColors["lightSlateGray"] = new Color( 119, 136, 153 )
        standardColors["lightSteelBlue"] = new Color( 176, 196, 222 )
        standardColors["lightWood"] = new Color( 233, 194, 166 )
        standardColors["lightYellow"] = new Color( 255, 255, 224 )
        standardColors["limeGreen"] = new Color( 50, 205, 50 )
        standardColors["linen"] = new Color( 250, 240, 230 )
        standardColors["mandarinOrange"] = new Color( 228, 120, 51 )
        standardColors["mediumAquaMarine"] = new Color( 102, 205, 170 )
        standardColors["mediumBlue"] = new Color( 0, 0, 205 )
        standardColors["mediumGoldenRod"] = new Color( 234, 234, 174 )
        standardColors["mediumOrchid"] = new Color( 186, 85, 211 )
        standardColors["mediumPurple"] = new Color( 147, 112, 216 )
        standardColors["mediumSeaGreen"] = new Color( 60, 179, 113 )
        standardColors["mediumSlateBlue"] = new Color( 123, 104, 238 )
        standardColors["mediumSpringGreen"] = new Color( 0, 250, 154 )
        standardColors["mediumTurquoise"] = new Color( 72, 209, 204 )
        standardColors["mediumVioletRed"] = new Color( 199, 21, 133 )
        standardColors["mediumWood"] = new Color( 166, 128, 100 )
        standardColors["midnightBlue"] = new Color( 25, 25, 112 )
        standardColors["mintCream"] = new Color( 245, 255, 250 )
        standardColors["mistyRose"] = new Color( 255, 228, 225 )
        standardColors["moccasin"] = new Color( 255, 228, 181 )
        standardColors["navajoWhite"] = new Color( 255, 222, 173 )
        standardColors["navyBlue"] = new Color( 35, 35, 142 )
        standardColors["neonBlue"] = new Color( 77, 77, 255 )
        standardColors["neonPink"] = new Color( 255, 110, 199 )
        standardColors["newMidnightBlue"] = new Color( 0, 0, 156 )
        standardColors["newTan"] = new Color( 235, 199, 158 )
        standardColors["oldGold"] = new Color( 207, 181, 59 )
        standardColors["oldLace"] = new Color( 253, 245, 230 )
        standardColors["oliveDrab"] = new Color( 107, 142, 35 )
        standardColors["orangeRed"] = new Color( 255, 69, 0 )
        standardColors["orchid"] = new Color( 218, 112, 214 )
        standardColors["paleGoldenRod"] = new Color( 238, 232, 170 )
        standardColors["paleGreen"] = new Color( 152, 251, 152 )
        standardColors["paleTurquoise"] = new Color( 175, 238, 238 )
        standardColors["paleVioletRed"] = new Color( 216, 112, 147 )
        standardColors["papayaWhip"] = new Color( 255, 239, 213 )
        standardColors["peachPuff"] = new Color( 255, 218, 185 )
        standardColors["peru"] = new Color( 205, 133, 63 )
        standardColors["plum"] = new Color( 221, 160, 221 )
        standardColors["powderBlue"] = new Color( 176, 224, 230 )
        standardColors["quartz"] = new Color( 217, 217, 243 )
        standardColors["richBlue"] = new Color( 89, 89, 171 )
        standardColors["rosyBrown"] = new Color( 188, 143, 143 )
        standardColors["royalBlue"] = new Color( 65, 105, 225 )
        standardColors["saddleBrown"] = new Color( 139, 69, 19 )
        standardColors["salmon"] = new Color( 250, 128, 114 )
        standardColors["sandyBrown"] = new Color( 244, 164, 96 )
        standardColors["scarlet"] = new Color( 140, 23, 23 )
        standardColors["seaGreen"] = new Color( 46, 139, 87 )
        standardColors["seaShell"] = new Color( 255, 245, 238 )
        standardColors["semiSweetChocolate"] = new Color( 107, 66, 38 )
        standardColors["sienna"] = new Color( 160, 82, 45 )
        standardColors["skyBlue"] = new Color( 135, 206, 235 )
        standardColors["slateBlue"] = new Color( 106, 90, 205 )
        standardColors["slateGray"] = new Color( 112, 128, 144 )
        standardColors["snow"] = new Color( 255, 250, 250 )
        standardColors["spicyPink"] = new Color( 255, 28, 174 )
        standardColors["springGreen"] = new Color( 0, 255, 127 )
        standardColors["steelBlue"] = new Color( 70, 130, 180 )
        standardColors["summerSky"] = new Color( 56, 176, 222 )
        standardColors["tan"] = new Color( 210, 180, 140 )
        standardColors["thistle"] = new Color( 216, 191, 216 )
        standardColors["tomato"] = new Color( 255, 99, 71 )
        standardColors["turquoise"] = new Color( 64, 224, 208 )
        standardColors["veryLightGrey"] = new Color( 205, 205, 205 )
        standardColors["violet"] = new Color( 238, 130, 238 )
        standardColors["violetRed"] = new Color( 208, 32, 144 )
        standardColors["wheat"] = new Color( 245, 222, 179 )
        standardColors["whiteSmoke"] = new Color( 245, 245, 245 )
        standardColors["yellowGreen"] = new Color( 154, 205, 50 )
    }
}