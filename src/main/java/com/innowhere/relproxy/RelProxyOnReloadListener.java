package com.innowhere.relproxy;

import java.lang.reflect.Method;

/**
 *
 * @author Jose Maria Arranz Santamaria
 */
public interface RelProxyOnReloadListener
{
    public void onReload(Object objOld,Object objNew,Object proxy, Method method, Object[] args);
}
