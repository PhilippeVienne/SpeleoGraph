package org.cds06.speleograph.utils;

import java.util.Collection;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
public interface ArrayListObserver<E> {
    public void onAdd( E element );

    public void onAdd( int index, E element );

    public void onAddAll( Collection<? extends E> elements );

    public void onAddAll( int index, Collection<? extends E> elements );

    public void onClear();

    public void onRemove( int index );

    public void onRemove( Object obj );

    public void onRemoveAll( Collection<?> c );

    public void onRetainAll( Collection<?> c );

    public void onSet( int index, E element );

    public void onSubList( int fromIndex, int toIndex );
}
