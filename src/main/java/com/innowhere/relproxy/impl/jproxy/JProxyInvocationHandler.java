
package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 */
public class JProxyInvocationHandler<T> extends GenericProxyInvocationHandler
{  
    public JProxyInvocationHandler(T obj,JProxyImpl root)
    {
        super(root);
        this.verObj = new JProxyVersionedObject<T>(obj,this);
    }

    public JProxyImpl getJProxyImpl()
    {
        return (JProxyImpl)root;
    }    
    
}