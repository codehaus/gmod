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

package groovy.swing.j2d.operations

import java.awt.Image
import java.awt.Shape
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.geom.AffineTransform
import java.beans.PropertyChangeEvent

import groovy.swing.j2d.GraphicsContext
import groovy.swing.j2d.impl.ObservableSupport
import groovy.swing.j2d.impl.ExtPropertyChangeEvent

/**
 * @author Andres Almiray <aalmiray@users.sourceforge.net>
 */
public class TransformationGroup extends ObservableSupport implements Transformation {
    private List transformations = []

    public List getTransformations(){
       return Collections.unmodifiableList(transformations)
    }
    
    public Transformation getAt( int index ){
    	return transformations[index]
    }

    public Transformation getAt( String name ) {
    	return transformations.find { it?.name == name }
    }

    public void addTransformation( Transformation transformation ) {
        if( !transformation ) return
        // make sure transformationGroups are added only once
        if( transformation instanceof TransformationGroup ){
           if( transformations.find{ it == transformation} ) return
        }
        transformations << transformation
        transformation.addPropertyChangeListener( this )
        firePropertyChange( "size", transformations.size()-1, transformations.size() )
    }

    public void removeTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformation.removePropertyChangeListener( this )
        transformations.remove( transformation )
        firePropertyChange( "size", transformations.size()+1, transformations.size() )
    }

    public boolean isEmpty() {
       return transformations.isEmpty()
    }

    public void clear() {
       if( transformations.isEmpty() ) return
       int actualSize = transformations.size()
       transformations.clear()
       firePropertyChange( "size", actualSize, 0 )
    }
    
    public int getSize() {
       return transformations.size()
    }
    
    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( new ExtPropertyChangeEvent(this,event) )
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
          def t = transformation.transform
          if( transformation instanceof TransformationGroup ){
             image = transformation.apply( image, context )
          }else if( t.isIdentity() ){
             // assume that it is a freeze transform
             if( !transform.isIdentity() ){
                interpolation = transformation.interpolation != null ? transformation.interpolation : interpolation
                image = createTransformedImage( transform, image, interpolation, context )
                transform = t.clone()
             }
          }else{
             interpolation = transformation.interpolation != null ? transformation.interpolation : interpolation
             transform.concatenate( t )
          }
       }

       if( !transform.isIdentity() ){
          return createTransformedImage( transform, image, interpolation, context )
       }else{
          return image
       }
    }
    
    public String toString() {
    	"transformations$transformations"
    }

    private BufferedImage createTransformedImage( transform, src, interpolation, context ) {
       AffineTransformOp at = new AffineTransformOp( transform, interpolation )
       BufferedImage dst = at.createCompatibleDestImage(src, src.colorModel)
       return at.filter( src, dst )
    }
}
