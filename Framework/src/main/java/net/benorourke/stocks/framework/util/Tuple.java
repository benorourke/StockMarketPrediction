package net.benorourke.stocks.framework.util;

/**
 * A simple wrapper class for storing two objects together
 * @param <K> type of the first object
 * @param <V> type of the second object
 */
public class Tuple<K, V>
{
    private K a;
    private V b;

    public Tuple(K a, V b)
    {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode()
    {
        return a.hashCode() * 31 + b.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof Tuple) ? obj.hashCode() == hashCode() : false;
    }

    public K getA()
    {
        return a;
    }

    public void setA(K a)
    {
        this.a = a;
    }

    public V getB()
    {
        return b;
    }

    public void setB(V b)
    {
        this.b = b;
    }
}
