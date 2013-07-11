package org.cds06.speleograph.utils;

import java.util.Collection;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public abstract class AbstractArrayListObserver<E> implements ArrayListObserver<E> {

    @Override
    public void onAdd( E element ) {

    }

    @Override
    public void onAdd( int index, E element ) {

    }

    @Override
    public void onAddAll( Collection<? extends E> elements ) {

    }

    @Override
    public void onAddAll( int index, Collection<? extends E> elements ) {

    }

    @Override
    public void onClear() {

    }

    @Override
    public void onRemove( int index ) {

    }

    @Override
    public void onRemove( Object obj ) {

    }

    @Override
    public void onRemoveAll( Collection<?> c ) {

    }

    @Override
    public void onRetainAll( Collection<?> c ) {

    }

    @Override
    public void onSet( int index, E element ) {

    }

    @Override
    public void onSubList( int fromIndex, int toIndex ) {

    }

}
