package com.innowhere.relproxy.impl.gproxy.core;

import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class GProxyInvocationHandler<T> extends GenericProxyInvocationHandler
{
    public GProxyInvocationHandler(T obj,GProxyImpl root)
    {
        super(root);
        this.verObj = new GProxyVersionedObject<T>(obj,this);
    }

    public GProxyImpl getGProxyImpl()
    {
        return (GProxyImpl)root;
    }

}