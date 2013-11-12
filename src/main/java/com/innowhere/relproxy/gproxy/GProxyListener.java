package com.innowhere.relproxy.gproxy;

import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 */
public interface GProxyListener
{
    public void onReload(Object objOld,Object objNew,Object proxy, Method method, Object[] args);
}
