/*
 * Copyright 2007-2008 the original author or authors.
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
    
    def enabled = true

    public List getTransformations(){
       return Collections.unmodifiableList(this.@transformations)
    }
    
    public Transformation getAt( int index ){
    	return this.@transformations[index]
    }

    public Transformation getAt( String name ) {
    	return this.@transformations.find { it?.name == name }
    }

    public void addTransformation( Transformation transformation ) {
        if( !transformation ) return
        // make sure transformationGroups are added only once
        if( transformation instanceof TransformationGroup ){
           if( this.@transformations.find{ it == transformation} ) return
        }
        this.@transformations << transformation
        transformation.addPropertyChangeListener( this )
        if( enabled ) firePropertyChange( "size", this.@transformations.size()-1, this.@transformations.size() )
    }

    public void removeTransformation( Transformation transformation ) {
        if( !transformation ) return
        transformation.removePropertyChangeListener( this )
        this.@transformations.remove( transformation )
        if( enabled ) firePropertyChange( "size", this.@transformations.size()+1, this.@transformations.size() )
    }

    public boolean isEnabled() {
       def b = this.@transformations.any { it.enabled }
       b ? this.@enabled : false
    }
    
    public void setEnabled( boolean enabled ){
    	if( this.@enabled != enabled ){
    		this.@enabled = enabled
    		firePropertyChange( "enabled", !enabled, enabled )
    	}
    }
    
    public boolean isEmpty() {
       return this.@transformations.isEmpty()
    }

    public void clear() {
       if( this.@transformations.isEmpty() ) return
       int actualSize = this.@transformations.size()
       this.@transformations.clear()
       if( enabled ) firePropertyChange( "size", actualSize, 0 )
    }
    
    public int getSize() {
       return this.@transformations.size()
    }
    
    public void propertyChange( PropertyChangeEvent event ) {
       firePropertyChange( new ExtPropertyChangeEvent(this,event) )
    }

    public AffineTransform getTransform() {
       return null
    }

    public AffineTransform getConcatenatedTransform() {
       def transform = new AffineTransform()
       this.@transformations.each { transformation ->
          def t = transformation.transform
          if( t && !t.isIdentity() ){
             transform.concatenate( t )
          }
       }
       return transform
    }

    public Shape apply( Shape shape ) {
       if( isEmpty() || !enabled ) return shape
       def transform = new AffineTransform()
       this.@transformations.each { transformation ->
          if( !transformation.enabled ) return
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
       if( isEmpty() || !enabled ) return image
       def transform = new AffineTransform()
       def interpolation = AffineTransformOp.TYPE_NEAREST_NEIGHBOR
       this.@transformations.each { transformation ->
          if( !transformation.enabled ) return
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
    
    public Iterator iterator() {
        this.@transformations.iterator()
    }

    /* ===== OPERATOR OVERLOADING ===== */

    public TransformationGroup leftShift( Transformation transformation ) {
       addTransformation( transformation )
       this
    }
    
    private BufferedImage createTransformedImage( transform, src, interpolation, context ) {
       AffineTransformOp at = new AffineTransformOp( transform, interpolation )
       BufferedImage dst = at.createCompatibleDestImage(src, src.colorModel)
       return at.filter( src, dst )
    }
}
