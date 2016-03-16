package com.innowhere.relproxy.impl;

import com.innowhere.relproxy.RelProxyException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyVersionedObject
{
    protected Object obj;    
    protected GenericProxyInvocationHandler parent;
    
    public GenericProxyVersionedObject(Object obj,GenericProxyInvocationHandler parent)
    {
        this.obj = obj;    
        this.parent = parent;
    }
    
    protected static void getTreeFields(Class clasz,Object obj,ArrayList<Field> fieldList,ArrayList<Object> valueList) throws IllegalAccessException
    {    
        getFields(clasz,obj,fieldList,valueList);
        Class superClass = clasz.getSuperclass();
        if (superClass != null)
            getTreeFields(superClass,obj,fieldList,valueList);
    }

    protected static void getFields(Class clasz,Object obj,ArrayList<Field> fieldList,ArrayList<Object> valueList) throws IllegalAccessException
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
    
    public Object getCurrent()
    {
        return obj;
    }    
    
    public Object getNewVersion() throws Throwable 
    {
        Class<?> newClass = reloadClass();
        if (newClass == null)
            return obj;

        Class oldClass = obj.getClass();            
        if (newClass != oldClass)
        {
            this.obj = copy(oldClass,obj,newClass);
        }
        
        return obj;
    }    
    
    private Object copy(Class oldClass,Object oldObj,Class newClass) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException
    {
        Object newObj;

        ArrayList<Field> fieldListOld = new ArrayList<Field>();
        ArrayList<Object> valueListOld = new ArrayList<Object>();              

        getTreeFields(oldClass,oldObj,fieldListOld,valueListOld);

        Class<?> enclosingClassNew = newClass.getEnclosingClass();
        if (enclosingClassNew == null)
        {
            Constructor construc;
            try
            {
                construc = newClass.getConstructor(new Class[0]);
            }
            catch(NoSuchMethodException ex)
            {                
                throw new RelProxyException("Cannot reload " + newClass.getName() + " a default empty of params constructor is required",ex);
            }                
            newObj = construc.newInstance();                
        }
        else
        {
            // En el caso de inner class o anonymous inner class el constructor por defecto se obtiene de forma diferente, útil para los EventListener de ItsNat
            Constructor construc;
            try
            {
                construc = newClass.getDeclaredConstructor(new Class[]{enclosingClassNew});                 
            }
            catch(NoSuchMethodException ex) // Yo creo que nunca ocurre al menos no en anonymous inner classes pero por si acaso
            {                
                throw new RelProxyException("Cannot reload " + newClass.getName() + " a default empty of params constructor is required",ex);
            }
            construc.setAccessible(true);  // Necesario

            // http://stackoverflow.com/questions/1816458/getting-hold-of-the-outer-class-object-from-the-inner-class-object    


            Field enclosingFieldOld;
            try { enclosingFieldOld = oldClass.getDeclaredField("this$0"); }
            catch (NoSuchFieldException ex) { throw new RelProxyException(ex);  }
            enclosingFieldOld.setAccessible(true);
            Object enclosingObjectOld = enclosingFieldOld.get(oldObj);                
            Object enclosingObjectNew = copy(enclosingObjectOld.getClass(),enclosingObjectOld,enclosingClassNew);              

            newObj = construc.newInstance(enclosingObjectNew);                
        }


        ArrayList<Field> fieldListNew = new ArrayList<Field>();

        getTreeFields(newClass,newObj,fieldListNew,null);                

        if (fieldListOld.size() != fieldListNew.size()) throw new RelProxyException("Cannot reload " + newClass.getName() + " number of fields have changed, redeploy");

        for(int i = 0; i < fieldListOld.size(); i++) 
        {
            Field fieldOld = fieldListOld.get(i);
            Field fieldNew = fieldListNew.get(i);
            if (enclosingClassNew != null && fieldOld.getName().equals("this$0") && fieldNew.getName().equals("this$0")) 
                continue; // Ya están correctamente definidos

            if ( (!ignoreField(fieldOld) && !fieldOld.getName().equals(fieldNew.getName())) || 
                  !fieldOld.getType().equals(fieldNew.getType()))
                throw new RelProxyException("Cannot reload " + newClass.getName() + " fields have changed, redeploy");

            Object fieldObj = valueListOld.get(i);
            fieldNew.setAccessible(true);
            int modifiersNew = fieldNew.getModifiers();                
            boolean isStaticFinal = Modifier.isStatic(modifiersNew) && Modifier.isFinal(modifiersNew);
            Field modifiersField = null;
            if (isStaticFinal) 
            {
                // http://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
                try {
                    modifiersField = Field.class.getDeclaredField("modifiers");
                }
                catch (NoSuchFieldException ex) { throw new RelProxyException(ex); }
                modifiersField.setAccessible(true);
                modifiersField.setInt(fieldNew, fieldNew.getModifiers() & ~Modifier.FINAL);  // Quitamos el modifier final
            }                  

            fieldNew.set(newObj, fieldObj);

            if (modifiersField != null)
            {
                modifiersField.setInt(fieldNew, fieldNew.getModifiers() & ~Modifier.FINAL);  // Restauramos el modifier final
            }
        }    
        return newObj;
    }
    
    protected abstract <T> Class<T> reloadClass();    
    protected abstract boolean ignoreField(Field field);    
}
