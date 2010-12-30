package org.codehaus.groovy2.mixin.lang;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.groovy2.lang.ClassMetaClass;
import org.codehaus.groovy2.lang.RT;
import org.codehaus.groovy2.lang.ClassMetaClass.Mutator;

/** Special mixin for array of primitives and array of Object.
 */
public class ArrayMixin {
  // groovy meta-interface
  public static int getLength(Object array) {
    return Array.getLength(array);
  }
  
  public static Object getAt(Object array, int index) {
    return Array.get(array, index);
  }

  public static void putAt(Object array, int index, Object value) {
    Array.set(array, index, value);
  }


  // java.util.List interface


  public static int size(Object array) {
    return getLength(array);
  }
  public static boolean isEmpty(Object array) {
    return getLength(array) != 0;
  }
  
  public static boolean contains(Object array, Object o) {
    int length = getLength(array);
    for(int i=0; i<length; i++) {
      if (getAt(array, i).equals(o)) {
        return true;
      }
    }
    return false;
  }

  public static Iterator<Object> iterator(final Object array) {
    final int length = getLength(array);
    return new Iterator<Object>() {
      private int index;
      
      @Override
      public boolean hasNext() {
        return index < length;
      }
      
      @Override
      public Object next() {
        return Array.get(array, index++);
      }
      
      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  
  public static Object[] toArray(Object array) {
    if (array instanceof Object[]) {
      return (Object[])array;
    }
    int length = getLength(array);
    Object[] newArray = new Object[length];
    for(int i=0; i<length; i++) {
      newArray[i] = getAt(array, i);
    }
    return newArray;
  }

  
  @SuppressWarnings("unchecked")
  public static <T> T[] toArray(Object array, T[] a) {
    int length = getLength(array);
    if (length > a.length) {
      a = Arrays.copyOf(a, length);
    }
    for(int i=0; i<length; i++) {
      a[i] = (T)getAt(array, i);
    }
    if (length != a.length) {
      a[length] = null;
    }
    return a;
  }

  // Modification Operations
  public static boolean add(Object array, Object e) {
    throw new UnsupportedOperationException();
  }
  public static boolean remove(Object array, Object o) {
    throw new UnsupportedOperationException();
  }

  // Bulk Modification Operation
  public static boolean containsAll(Object array, Collection<?> c) {
    HashSet<Object> set = new HashSet<Object>();
    int length = getLength(array);
    for(int i=0; i<length; i++) {
      set.add(getAt(array, i));
    }
    return set.containsAll(c);
  }

  
  public static boolean addAll(Object array, Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  public static boolean addAll(Object array, int index, Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  public static boolean removeAll(Object array, Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  public static boolean retainAll(Object array, Collection<?> c) {
    throw new UnsupportedOperationException();
  }
  public static void clear(Object array) {
    throw new UnsupportedOperationException();
  }

  // Comparison and hashing
  public static boolean equals(Object array, Object o) {
    if (!o.getClass().isArray())
      return false;
    int length = getLength(array);
    int length2 = getLength(o);
    if (length != length2) {
      return false;
    }
    
    for(int i=0; i<length; i++) {
      if (!getAt(array, i).equals(getAt(o, i))) {
        return false;
      }
    }
    return true;
  }

  
  public static int hashCode(Object array) {
    if (array instanceof Object[]) {
      return Arrays.hashCode((Object[])array);
    }
    if (array instanceof boolean[]) {
      return Arrays.hashCode((boolean[])array);
    }
    if (array instanceof byte[]) {
      return Arrays.hashCode((byte[])array);
    }
    if (array instanceof short[]) {
      return Arrays.hashCode((short[])array);
    }
    if (array instanceof char[]) {
      return Arrays.hashCode((char[])array);
    }
    if (array instanceof int[]) {
      return Arrays.hashCode((int[])array);
    }
    if (array instanceof long[]) {
      return Arrays.hashCode((long[])array);
    }
    if (array instanceof float[]) {
      return Arrays.hashCode((float[])array);
    }
    if (array instanceof double[]) {
      return Arrays.hashCode((double[])array);
    }
    throw new AssertionError();
  }

  public static String toString(Object array) {
    int length = getLength(array);
    StringBuilder builder = new StringBuilder();
    builder.append('[');
    for(int i=0; i<length; i++) {
      builder.append(getAt(array, i)).append(", ");
    }
    if (length != 0) {
      builder.setLength(builder.length() - 2);
    }
    return builder.append(']').toString();
  }
  

  // Positional Access Operations

  public static Object get(Object array, int index) {
    return getAt(array, index);
  }
  
  public static Object set(Object array, int index, Object value) {
    Object oldValue = getAt(array, index);
    putAt(array, index, value);
    return oldValue;
  }

  public static void add(Object array, int index, Object element) {
    throw new UnsupportedOperationException();
  }
  public static Object remove(Object array, int index) {
    throw new UnsupportedOperationException();
  }


  // Search Operations
  public static int indexOf(Object array, Object o) {
    int length = getLength(array);
    for(int i=0; i<length; i++) {
      if (getAt(array, i).equals(o)) {
        return i;
      }
    }
    return -1;
  }

  public static int lastIndexOf(Object array, Object o) {
    int i = getLength(array);
    for(;--i>=0;) {
      if (getAt(array, i).equals(o)) {
        return i;
      }
    }
    return -1;
  }


  // List Iterators

  public static ListIterator<Object> listIterator(Object array) {
    int length = getLength(array);
    return new ArrayListIterator(0, length, array);
  }

  public static ListIterator<Object> listIterator(Object array, int index) {
    int length = getLength(array);
    if (index<0 || index>length)
      throw new IndexOutOfBoundsException("bad range "+index);
    
    return new ArrayListIterator(index, length, array);
  }
  
  private static final class ArrayListIterator implements ListIterator<Object> {
    private final int length;
    private final Object array;
    private int index;

    ArrayListIterator(int index, int length, Object array) {
      this.index = index;
      this.length = length;
      this.array = array;
    }

    @Override
    public boolean hasNext() {
      return index < length;
    }

    @Override
    public Object next() {
      return getAt(array, index++);
    }

    @Override
    public int nextIndex() {
      return index;
    }

    @Override
    public boolean hasPrevious() {
      return index > 0;
    }

    @Override
    public Object previous() {
      return getAt(array, --index);
    }

    @Override
    public int previousIndex() {
      return index - 1;
    }

    @Override
    public void set(Object e) {
      putAt(array, index, e);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(Object e) {
      throw new UnsupportedOperationException();
    }
  }

  // View

  public static List<Object> subList(final Object array, final int fromIndex, int toIndex) {
    if (fromIndex < 0)
      throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
    int length = getLength(array);
    if (toIndex > length)
      throw new IndexOutOfBoundsException("toIndex = " + toIndex);
    if (fromIndex > toIndex)
      throw new IllegalArgumentException("fromIndex(" + fromIndex +
          ") > toIndex(" + toIndex + ")");
    
    final int size = toIndex - fromIndex;
    return new AbstractList<Object>() {
      @Override
      public Object get(int index) {
        return getAt(array, fromIndex + index);
      }
      @Override
      public Object set(int index, Object value) {
        Object oldValue = getAt(array, fromIndex + index);
        putAt(array, fromIndex + index, oldValue);
        return oldValue;
      }
      @Override
      public int size() {
        return size;
      }
      
      //FIXME should implements listIterator(*) too
    };
  }
  
  public static void __init__(ClassMetaClass metaClass) {
    Mutator mutator = metaClass.mutator();
    try {
      mutator.addMixin(RT.getMetaClass(ArrayMixin.class));
    } finally {
      mutator.close();
    }
  }
}
