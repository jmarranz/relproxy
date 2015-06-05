package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;
import com.innowhere.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptor;
import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.net.URL;

/**
 *
 * @author jmarranz
 */
public class JProxyClassLoader extends ClassLoader
{
    protected final JProxyEngine engine;
    
    public JProxyClassLoader(JProxyEngine engine)
    {
        super(engine.getRootClassLoader());
        
        this.engine = engine;
    }
    
    public Class defineClass(ClassDescriptor classDesc)
    {    
        Object monitor = engine.getMonitor();
        synchronized(monitor)
        {
            String className = classDesc.getClassName();
            byte[] classBytes = classDesc.getClassBytes();
            Class clasz = defineClass(className,classBytes, 0, classBytes.length);   
            classDesc.setLastLoadedClass(clasz);
            return clasz;            
        }
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException 
    {
        Object monitor = engine.getMonitor();
        synchronized(monitor)
        {        
            return loadClass(name,true); 
        	
        	/*
            Class<?> cls = findLoadedClass(name);
            if (cls == null)
            	return loadClass(name,true);            
            
            //if (cls == null)
            //    return getParent().loadClass(name); // DarÃ¡ un ClassNotFoundException si no puede cargarla

            return cls;
            */
        }
    }    

    public Class loadClass(ClassDescriptor classDesc,boolean resolve)
    {    
        Object monitor = engine.getMonitor();
        synchronized(monitor)
        {               
            Class clasz = classDesc.getLastLoadedClass();
            if (clasz != null && clasz.getClassLoader() == this) return clasz; // Glup, ya fue cargada
            clasz = defineClass(classDesc); 
            if (resolve) {
                resolveClass(clasz);
            }        
            return clasz;
        }
    }    
    
    public Class loadInnerClass(ClassDescriptorSourceUnit parentDesc,String innerClassName)
    {
        Object monitor = engine.getMonitor();
        synchronized(monitor)
        {         
            ClassDescriptor classDesc = parentDesc.getInnerClassDescriptor(innerClassName,false);
            if (classDesc == null || classDesc.getClassBytes() == null)
            {
                byte[] classBytes = getClassBytesFromResource(innerClassName);  
                if (classBytes == null) return null;            
                if (classDesc == null) classDesc = parentDesc.addInnerClassDescriptor(innerClassName);
                classDesc.setClassBytes(classBytes);
            }       

            return defineClass(classDesc); 
        }
    }
    
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException 
    {
        // Inspiraciones en URLClassLoader.findClass y en el propio análisis de ClassLoader.loadClass
        // Lo redefinimos por si acaso porque el objetivo es recargar todas las clases hot-reloaded en este ClassLoader y no delegar en el parent 
        // (el comportamiento por defecto de loadClass) pues las clases cargadas con el parent tenderán a cargar las clases vinculadas con dicho ClassLoader
        
        // En teoría este método redefinido no es necesario porque manualmente detectamos los cambios de código fuente, recompilamos y recargamos explícitamente
        // con defineClass el cual no carga también las innerclasses vinculadas, 
        // pero si el código fuente tiene innerclasses y no ha sido cambiado nunca, las innerclasses pueden no ser conocidas como ClassDescriptor,
        // necesitamos detectar las innerclasses para cargarlas también tras la carga de la clase contenedora,
        // para ello ejecutamos Class.getDeclaredClasses() para que cargue las innerclasses indirectamente, pasando entonces por aquí.

        // Hay un caso más y es el de la clase base que es una clase hot loadable con su propio archivo, al ejecutar el defineClass en la clase
        // derivada la clase base también debe cargarse en ese momento y es posible que no haya sido hecho explícitamente (por ej porque no ha cambiado o su carga va después)
        // por lo que pasaremos por aquí y debemos cargarla aquí, luego no hay problema de recarga explícita porque sabemos que ha sido ya cargada
        // y tampoco hay problema de auto-salvado del .class o eliminación del mismo puesto que al ser un archivo fuente normal se tratará por si mismo
        // aunque la carga en el class loader se haya hecho a través de una clase derivada quizás antes
        
        Object monitor = engine.getMonitor();
        synchronized(monitor)
        {        
            Class<?> cls = findLoadedClass(name);
            if (cls == null)
            {            
                ClassDescriptor classDesc = engine.getClassDescriptor(name); // Si es una inner class se crea el descriptor y se añade al source file asociado automáticamente
                if (classDesc != null)
                {
                    byte[] classBytes = classDesc.getClassBytes();
                    if (classBytes == null) 
                    {
                        classBytes = getClassBytesFromResource(name);   // No puede ser nulo
                        classDesc.setClassBytes(classBytes);
                    }

                    cls = defineClass(classDesc); 
                }
            
                if (cls == null)
                {
                    cls = getParent().loadClass(name); // Dará un ClassNotFoundException si no puede cargarla
                }
            }        
        
            if (cls == null) throw new ClassNotFoundException(name);

            if (resolve) {
                resolveClass(cls);
            }
            return cls;
        }
    }
    
    private byte[] getClassBytesFromResource(String className)
    {
        String relClassPath = ClassDescriptor.getRelativeClassFilePathFromClassName(className); 
        URL urlClass = getResource(relClassPath);
        if (urlClass == null) return null;
        return JProxyUtil.readURL(urlClass);    
    }
}
