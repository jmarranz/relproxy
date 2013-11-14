package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.impl.jproxy.clsmgr.JReloaderEngine;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 *
 * @author jmarranz
 * @param <T>
 */
public class JProxyVersionedObject<T>
{    
    protected T obj;
    protected JProxyReloadableInvocationHandler parent;
    protected String className;    

    public JProxyVersionedObject(T obj,JProxyReloadableInvocationHandler parent)
    {
        this.obj = obj;
        this.parent = parent;
        this.className = obj.getClass().getName();
    }        

    public T getCurrent()
    {
        return obj;
    }

    public <T> Class<T> reloadClass(String className) throws groovy.util.ScriptException
    {
        JReloaderEngine engine = parent.getJProxyImpl().getJReloaderEngine();        
        return (Class<T>)engine.findClass(className);           
    }

    public T getNewVersion() throws Throwable 
    {
        Class<T> newClass = reloadClass(className);
        if (newClass == null)
            return obj;

        Class oldClass = obj.getClass();            
        if (newClass != oldClass)
        {

            ArrayList<Field> fieldListOld = new ArrayList<Field>();
            ArrayList<Object> valueListOld = new ArrayList<Object>();              

            getTreeFields(oldClass,obj,fieldListOld,valueListOld);

            try
            {
            this.obj = newClass.getConstructor(new Class[0]).newInstance();            
            }
            catch(NoSuchMethodException ex)
            {
                throw new RuntimeException("Cannot reload " + newClass.getName() + " a default empty of params constructor is required",ex);
            }

            ArrayList<Field> fieldListNew = new ArrayList<Field>();

            getTreeFields(newClass,obj,fieldListNew,null);                

            if (fieldListOld.size() != fieldListNew.size()) throw new RuntimeException("Cannot reload " + newClass.getName() + " number of fields have changed, redeploy");

            for(int i = 0; i < fieldListOld.size(); i++) 
            {
                Field fieldOld = fieldListOld.get(i);
                Field fieldNew = fieldListNew.get(i);
                if ( !fieldOld.getName().equals(fieldNew.getName()) || 
                     !fieldOld.getType().equals(fieldNew.getType()))
                    throw new RuntimeException("Cannot reload " + newClass.getName() + " fields have changed, redeploy");

                Object fieldObj = valueListOld.get(i);
                fieldNew.setAccessible(true);
                fieldNew.set(obj, fieldObj);
            }
        }

        return obj;
    }

    private static void getTreeFields(Class clasz,Object obj,ArrayList<Field> fieldList,ArrayList<Object> valueList) throws IllegalAccessException
    {    
        getFields(clasz,obj,fieldList,valueList);
        Class superClass = clasz.getSuperclass();
        if (superClass != null)
            getTreeFields(superClass,obj,fieldList,valueList);
    }

    private static void getFields(Class clasz,Object obj,ArrayList<Field> fieldList,ArrayList<Object> valueList) throws IllegalAccessException
    {
        Field[] fieldListClass = clasz.getDeclaredFields();             
        for(int i = 0; i < fieldListClass.length; i++)
        {
            Field field = fieldListClass[i];           
            fieldList.add(field);
            if (valueList != null)
            {
                field.setAccessible(true);                   
                Object value = field.get(obj);            
                valueList.add(value);
            }
        }             
    }        
}