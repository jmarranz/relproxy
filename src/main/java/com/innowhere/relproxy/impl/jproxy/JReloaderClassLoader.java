package com.innowhere.relproxy.impl.jproxy;

import java.net.URL;

/**
 *
 * @author jmarranz
 */
public class JReloaderClassLoader extends ClassLoader
{
    protected JReloaderEngine engine;
    
    public JReloaderClassLoader(JReloaderEngine engine,ClassLoader classLoader)
    {
        super(classLoader);
        
        this.engine = engine;
    }
    
    public JReloaderEngine getJReloaderEngine()
    {
        return engine;
    }
    
    public synchronized Class defineClass(ClassDescriptor classDesc)
    {    
        String className = classDesc.getClassName();
        byte[] classBytes = classDesc.getClassBytes();
        Class clasz = defineClass(className,classBytes, 0, classBytes.length);   
        classDesc.setLastLoadedClass(clasz);
        return clasz;
    }
    
    @Override
    protected synchronized Class<?> findClass(String name) throws ClassNotFoundException 
    {
        Class<?> cls = findLoadedClass(name);
        if (cls == null)
            return getParent().loadClass(name); // Dará un ClassNotFoundException si no puede cargarla
        
        return cls;
    }    

    public synchronized Class loadClass(ClassDescriptor classDesc,boolean resolve)
    {    
        Class clasz = classDesc.getLastLoadedClass();
        if (clasz != null && clasz.getClassLoader() == this) return clasz; // Glup, ya fue cargada
        clasz = defineClass(classDesc); 
	if (resolve) {
	    resolveClass(clasz);
	}        
        return clasz;
    }    
    
    public synchronized Class loadInnerClass(ClassDescriptorSourceFile parentDesc,String innerClassName)
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
    
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException 
    {
        // Inspiraciones en URLClassLoader.findClass y en el propio análisis de ClassLoader.loadClass
        // Lo redefinimos por si acaso porque el objetivo es recargar todas las clases hot-reloaded en este ClassLoader y no delegar en el parent 
        // (el comportamiento por defecto de loadClass) pues las clases cargadas con el parent tenderán a cargar las clases vinculadas con dicho ClassLoader
        
        // En teoría este método redefinido no es necesario porque manualmente detectamos los cambios de código fuente, recompilamos y recargamos explícitamente
        // con defineClass el cual no carga también las innerclasses vinculadas, 
        // pero si el código fuente tiene innerclasses y no ha sido cambiado nunca, las innerclasses pueden no ser conocidas como ClassDescriptor,
        // necesitamos detectar las innerclasses para cargarlas también tras la carga de la clase contenedora,
        // para ello ejecutamos Class.getDeclaredClasses() para que cargue las innerclasses indirectamente, pasando entonces por aquí.

        Class<?> cls = findLoadedClass(name);
        if (cls == null)
        {            
            ClassDescriptor classDesc = engine.getClassDescriptor(name); // Si es una inner class se crea el descriptor y se añade al source file asociado automáticamente
            if (classDesc != null && classDesc.isInnerClass())
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
    
    private synchronized byte[] getClassBytesFromResource(String className)
    {
        String relClassPath = ClassDescriptor.getRelativeClassFilePathFromClassName(className); 
        URL urlClass = getResource(relClassPath);
        if (urlClass == null) return null;
        return JReloaderUtil.readURL(urlClass);    
    }
}
