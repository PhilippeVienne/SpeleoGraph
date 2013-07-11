package org.cds06.speleograph.utils;

import java.util.Collection;

/**
 * This file is created by PhilippeGeek.
 * Distributed on licence GNU GPL V3.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractChangeArrayListObserver<E> implements ArrayListObserver<E> {

    public enum ChangeType {
        ADD,
        REMOVE,
        EDIT
    }

    public abstract void onChange(ChangeType type);

    @Override
    public void onAdd(E element) {
        onChange(ChangeType.ADD);
    }

    @Override
    public void onAdd(int index, E element) {
        onChange(ChangeType.ADD);
    }

    @Override
    public void onAddAll(Collection<? extends E> elements) {
        onChange(ChangeType.ADD);
    }

    @Override
    public void onAddAll(int index, Collection<? extends E> elements) {
        onChange(ChangeType.ADD);
    }

    @Override
    public void onClear() {
        onChange(ChangeType.REMOVE);
    }

    @Override
    public void onRemove(int index) {
        onChange(ChangeType.REMOVE);
    }

    @Override
    public void onRemove(Object obj) {
        onChange(ChangeType.REMOVE);
    }

    @Override
    public void onRemoveAll(Collection<?> c) {
        onChange(ChangeType.REMOVE);
    }

    @Override
    public void onRetainAll(Collection<?> c) {
        onChange(ChangeType.REMOVE);
    }

    @Override
    public void onSet(int index, E element) {
        onChange(ChangeType.EDIT);
    }

    @Override
    public void onSubList(int fromIndex, int toIndex) {
    }
}
