package com.innowhere.relproxy;

import java.lang.reflect.Method;

/**
 *
 * @author jmarranz
 */
public interface RelProxyOnReloadListener
{
    public void onReload(Object objOld,Object objNew,Object proxy, Method method, Object[] args);
}
