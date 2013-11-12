
package com.innowhere.relproxy.jproxy;

import com.innowhere.relproxy.impl.jproxy.JReloaderEngine;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 *
 * @author jmarranz
 */
public class JProxy 
{
    protected static JReloaderEngine engine;
    protected static boolean developmentMode = false;
    protected static JProxyListener reloadListener;
    
    public static void init(boolean devMode,String pathInput,String classFolder,long scanPeriod,Iterable<String> compilationOptions,DiagnosticCollector<JavaFileObject> diagnostics,JProxyListener relListener)
    {
        JReloaderEngine theEngine = null;
        if (devMode)
        {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();      
            theEngine = new JReloaderEngine(classLoader,pathInput,classFolder,scanPeriod,compilationOptions,diagnostics);          
        }
        
        engine = theEngine;        
        developmentMode = devMode;
        reloadListener = relListener;
    }
    
    public static class VersionedObject<T>
    {    
        protected T obj;
        protected String className;    
        
        public VersionedObject(T obj)
        {
            this.obj = obj;
            this.className = obj.getClass().getName();
        }        
        
        public static <T> VersionedObject create(T obj)
        {
            if (obj == null) return null;
            return new VersionedObject(obj);
        }
        
        public T getCurrent()
        {
            return obj;
        }
        
        public static <T> Class<T> reloadClass(String className) throws groovy.util.ScriptException
        {
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
    
    public static class ReloadableInvocationHandler<T> implements InvocationHandler
    {
        protected VersionedObject<T> verObj;
        
        public ReloadableInvocationHandler(T obj)
        {
            this.verObj = new VersionedObject<T>(obj);
        }
        
        public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
        {
            T oldObj = verObj.getCurrent();
            T obj = verObj.getNewVersion();

            if (oldObj != obj && reloadListener != null)
                reloadListener.onReload(oldObj,obj,proxy, method,args);  
            
            return method.invoke(obj, args);
        }        
    }
    
    public static <T> T create(T obj,Class<T> clasz)
    {
        if (!developmentMode || engine == null)
            return obj;
        
        if (obj == null) return null;
        
        InvocationHandler handler = new ReloadableInvocationHandler<T>(obj);
        
        T proxy = (T)Proxy.newProxyInstance(obj.getClass().getClassLoader(),new Class[] { clasz }, handler);   
        return proxy;
    }
    

}
