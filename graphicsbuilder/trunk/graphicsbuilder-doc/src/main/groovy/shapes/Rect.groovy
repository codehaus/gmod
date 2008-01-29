package shapes

class Rect {
   static description = """
Draws a rectangle defined by a location (x,y) and dimension (width x height).
If arcWidth and arcHeight are defined then it will draw a rounded rectangle.
"""

   static propertyTable = [
     "x"         : [value: 0, type: "int"],
     "y"         : [value: 0, type: "int"],
     "width"     : [value: 10, type: "int"],
     "height"    : [value: 10, type: "int"],
     "arcWidth"  : [type: "int"],
     "arcHeight" : [type: "int"],
   ]

   static examples = [
      [ width: 320,
        height: 100,
        code: {
rect( x: 10, y: 10, width: 300, height: 80, borderColor: 'darkRed', borderWidth: 2, fill: 'red' )
        }
      ],
      [ width: 320,
        height: 100,
        code: {
rect( x: 10, y: 10, width: 300, height: 80, borderColor: 'blue', borderWidth: 2, fill: 'cyan',
      arcWidth: 20, arcHeight: 20 )
      }
      ]
   ]
}