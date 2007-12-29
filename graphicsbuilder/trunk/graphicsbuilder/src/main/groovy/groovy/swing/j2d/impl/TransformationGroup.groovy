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

package groovy.swing.j2d.impl

import java.awt.Image
import java.awt.Shape
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent
import groovy.swing.j2d.GraphicsContext

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformationGroup extends ObservableSupport implements Transformation {
    private static final String OLDVALUE = "_OLD_VALUE_"
    private static final String NEWVALUE = "_NEW_VALUE_"

    private List transformations = []

    public List getTransformations(){
       return Collections.unmodifiableList(transformations)
    }

    public void addTransformation( Transformation transformation ) {
        if( !transformation ) return
        // make sure transformationGroups are added only once
        if( transformation instanceof TransformationGroup ){
           if( transformations.find{ it == transformation} ) return
        }
        transformations << transformation
        transformation.addPropertyChangeListener( this )
    }

    public void removeTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformation.removePropertyChangeListener( this )
        transformations.remove( transformation )
    }

    public boolean isEmpty() {
       return transformations.isEmpty()
    }

    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( "transform", OLDVALUE, NEWVALUE )
    }

    public AffineTransform getTransform() {
       return null
    }

    public AffineTransform getConcatenatedTransform() {
       def transform = new AffineTransform()
       transformations.each { transformation ->
          def t = transformation.transform
          if( t && !t.isIdentity() ){
             transform.concatenate( t )
          }
       }
       return transform
    }

    public Shape apply( Shape shape ) {
       if( isEmpty() ) return shape
       def transform = new AffineTransform()
       transformations.each { transformation ->
          def t = transformation.transform
          if( transformation instanceof TransformationGroup ){
             shape = transformation.apply( shape )
          }else if( t.isIdentity() ){
             // assume that it is a freeze transform
             if( !transform.isIdentity() ){
                shape = transform.createTransformedShape( shape )
                transform = t.clone()
             }
          }else{
             transform.concatenate( t )
          }
       }

       if( !transform.isIdentity() ){
          return transform.createTransformedShape( shape )
       }else{
          return shape
       }
    }

    public Image apply( Image image, GraphicsContext context ) {
       if( isEmpty() ) return image
       def transform = new AffineTransform()
       def interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR
       transformations.each { transformation ->
          interpolation = transformation.interpolation != null ? transformation.interpolation : interpolation
          def t = transformation.transform
          if( transformation instanceof TransformationGroup ){
             image = transformation.apply( image )
          }else if( t.isIdentity() ){
             // assume that it is a freeze transform
             if( !transform.isIdentity() ){
                image = createTransformedImage( transform, image, interpolation, context )
                transform = t.clone()
             }
          }else{
             transform.concatenate( t )
          }
       }

       if( !transform.isIdentity() ){
          return createTransformedImage( transform, image, interpolation, context )
       }else{
          return image
       }
    }

    private BufferedImage createTransformedImage( transform, src, interpolation, context ) {
       AffineTransformOp at = new AffineTransformOp( transform, interpolation )
       BufferedImage dst = at.createCompatibleDestImage(src, src.colorModel)
       return at.filter( src, dst )
    }
}