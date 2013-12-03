package com.innowhere.relproxy;

import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 */
public interface RelProxyListener
{
    public void onReload(Object objOld,Object objNew,Object proxy, Method method, Object[] args);
}
