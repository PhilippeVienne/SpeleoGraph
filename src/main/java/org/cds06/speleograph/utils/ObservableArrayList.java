package org.cds06.speleograph.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public class ObservableArrayList<E> extends ArrayList<E> {

    private List<ArrayListObserver<E>> observers = null;
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *                                            is negative
     */
    public ObservableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ObservableArrayList() {
    }

    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public ObservableArrayList(Collection<? extends E> c) {
        super(c);
    }

    @Override
    public boolean add( E e ) {

        boolean result = super.add( e );

        if ( result ) {

            for ( ArrayListObserver<E> o : getObservers() ) {

                o.onAdd( e );

            }

        }

        return result;

    }

    @Override
    public void add( int index, E element ) {

        super.add( index, element );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onAdd( index, element );

        }

    }

    @Override
    public boolean addAll( Collection<? extends E> c ) {

        boolean result = super.addAll( c );

        if ( result ) {

            for ( ArrayListObserver<E> o : getObservers() ) {

                o.onAddAll( c );

            }

        }

        return result;

    }

    @Override
    public boolean addAll( int index, Collection<? extends E> c ) {

        boolean result = super.addAll( index, c );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onAddAll( index, c );

        }

        return result;

    }

    @Override
    public void clear() {

        super.clear();

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onClear();

        }

    }

    public List<ArrayListObserver<E>> getObservers() {

        if ( observers == null ) {

            observers = new ArrayList<ArrayListObserver<E>>();

        }

        return observers;

    }

    public void registerObserver( ArrayListObserver<E> observer ) {

        getObservers().add( observer );

    }

    @Override
    public E remove( int index ) {

        E toRet = super.remove( index );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onRemove( index );

        }

        return toRet;

    }

    @Override
    public boolean remove( Object obj ) {

        boolean result = super.remove( obj );

        if ( result ) {

            for ( ArrayListObserver<E> o : getObservers() ) {

                o.onRemove( obj );

            }

        }

        return result;

    }

    @Override
    public boolean removeAll( Collection<?> c ) {

        boolean result = super.removeAll( c );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onRemoveAll( c );

        }

        return result;

    }

    @Override
    public boolean retainAll( Collection<?> c ) {

        boolean result = super.retainAll( c );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onRetainAll( c );

        }

        return result;

    }

    @Override
    public E set( int index, E element ) {

        E toRet = super.set( index, element );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onSet( index, element );

        }

        return toRet;

    }

    @Override
    public List<E> subList( int fromIndex, int toIndex ) {

        List<E> toRet = super.subList( fromIndex, toIndex );

        for ( ArrayListObserver<E> o : getObservers() ) {

            o.onSubList( fromIndex, toIndex );

        }

        return toRet;

    }

    public void unregisterObserver( ArrayListObserver<E> observer ) {

        getObservers().remove( observer );

    }

}
