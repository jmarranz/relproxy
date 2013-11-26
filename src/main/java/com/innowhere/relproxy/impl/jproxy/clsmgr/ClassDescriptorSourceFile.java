package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.io.File;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public abstract class ClassDescriptorSourceFile extends ClassDescriptor
{
    protected JReloaderEngine engine;
    protected long timestamp;
    protected File sourceFile; 
    protected LinkedList<ClassDescriptorInner> innerClasses;
    
    public ClassDescriptorSourceFile(JReloaderEngine engine,String className,File sourceFile, long timestamp) 
    {
        super(className);
        this.engine = engine;
        this.sourceFile = sourceFile;
        this.timestamp = timestamp;
    }

    public static ClassDescriptorSourceFile create(boolean script,JReloaderEngine engine,String className,File sourceFile, long timestamp)
    {
        if (script)
            return new ClassDescriptorSourceFileScript(engine,className,sourceFile,timestamp);  
        else
            return new ClassDescriptorSourceFileJava(engine,className,sourceFile,timestamp);
    }
    
    public boolean isInnerClass()
    {
        return false;
    }    
    
    public File getSourceFile()
    {
        return sourceFile;
    }
    
    public long getTimestamp() 
    {
        return timestamp;
    }

    public void updateTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void cleanOnSourceCodeChanged()
    {
        // Como ha cambiado la clase, reseteamos las dependencias        
        setClassBytes(null);
        setLastLoadedClass(null);
        clearInnerClassDescriptors(); // El código fuente nuevo puede haber cambiado totalmente las innerclasses antiguas (añadido, eliminado)
    }
    
    public boolean isInnerClass(String className)
    {
        int pos = className.lastIndexOf('$');
        if (pos == -1)
            return false; // No es innerclass
        String baseClassName = className.substring(0, pos);
        return this.className.equals(baseClassName); // Si es false es que es una innerclass pero de otra clase
    }
    
    public LinkedList<ClassDescriptorInner> getInnerClassDescriptors()
    {
        return innerClasses;
    }
    
    public void clearInnerClassDescriptors()
    {
        if (innerClasses != null)
            innerClasses.clear();       
    }
    
    public ClassDescriptorInner getInnerClassDescriptor(String className,boolean addWhenMissing)
    {
        if (innerClasses != null)
        {
            for(ClassDescriptorInner classDesc : innerClasses)
            {
                if (classDesc.getClassName().equals(className))
                    return classDesc;
            }
        }
        
        if (!addWhenMissing) return null;
        
        return addInnerClassDescriptor(className);
    }
        
    public ClassDescriptorInner addInnerClassDescriptor(String className)
    {
        if (!isInnerClass(className))
            return null;
        
        if (innerClasses == null)
            innerClasses = new LinkedList<ClassDescriptorInner>();
        
        ClassDescriptorInner classDesc = new ClassDescriptorInner(className,this);
        innerClasses.add(classDesc);
        return classDesc;
    }    
    
    @Override
    public void resetLastLoadedClass()
    {
        super.resetLastLoadedClass();

        LinkedList<ClassDescriptorInner> innerClassDescList = getInnerClassDescriptors();
        if (innerClassDescList != null)
        {
            for(ClassDescriptorInner innerClassDesc : innerClassDescList)
                innerClassDesc.resetLastLoadedClass();             
        }   
    }
    
}
