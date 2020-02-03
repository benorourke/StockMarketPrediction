package net.ben.stocks.framework.util;

public class Tuple<K, V>
{
    private K a;
    private V b;

    public Tuple(K a, V b)
    {
        this.a = a;
        this.b = b;
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
