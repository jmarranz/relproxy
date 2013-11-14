package com.innowhere.relproxy.impl.gproxy;

import com.innowhere.relproxy.gproxy.GProxyGroovyScriptEngine;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 *
 * @author jmarranz
 */

public class GProxyVersionedObject<T>
{    
    protected T obj;
    protected String path;    
    protected GProxyReloadableInvocationHandler parent;
    
    public GProxyVersionedObject(T obj,GProxyReloadableInvocationHandler parent)
    {
        this.obj = obj;
        this.parent = parent;
        this.path = obj.getClass().getName().replace('.','/');
    }        

    public T getCurrent()
    {
        return obj;
    }

    private <T> Class<T> reloadClass(String path) throws groovy.util.ScriptException
    {
        GProxyGroovyScriptEngine engine = parent.getGProxyImpl().getGProxyGroovyScriptEngine();
        
        try
        {   
            return engine.loadScriptByName(path + ".groovy");  //inexp/groovyex/GroovyExampleLoadListener.groovy
        }
        catch(Exception ex)
        {
            ex.printStackTrace(System.err);
            return null;
        }                  
    }

    public T getNewVersion() throws Throwable 
    {
        Class<T> newClass = reloadClass(path);
        if (newClass == null)
            return obj;

        Class oldClass = obj.getClass();            
        if (newClass != oldClass)
        {
            /*
            if (reloadAllClasses)
            {
                Class[] loadedClasses = engine.getGroovyClassLoader().getLoadedClasses();
                for(Class cls : loadedClasses)
                {
                    reloadClass(cls);
                }
            }
            */

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
                if ( (!fieldOld.getName().startsWith("__timeStamp__") && !fieldOld.getName().equals(fieldNew.getName())) || 
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