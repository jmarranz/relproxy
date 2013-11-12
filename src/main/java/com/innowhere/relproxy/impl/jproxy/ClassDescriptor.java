package com.innowhere.relproxy.impl.jproxy;

import java.io.File;

/**
 *
 * @author jmarranz
 */
public abstract class ClassDescriptor 
{
    protected String className; // El nombre basado en puntos pero usando $ en el caso de innerclasses
    protected String simpleClassName; // className sin el package
    protected String packageName; // El package pero acabado en un "." o bien "" si no hay package, el motivo de acabar en un punto es simplemente para poder concatenar ciegamente el package y el simpleClassName
    protected byte[] classBytes;
    protected Class clasz;    
    
    public ClassDescriptor(String className) 
    {
        this.className = className;
        int pos = className.lastIndexOf('.');
        this.simpleClassName = (pos != -1) ? className.substring(pos + 1) : className;
        this.packageName = (pos != -1) ? className.substring(0,pos + 1) : "";  // SE INCLUYE EL . en el caso de existir package
    }
    
    public abstract boolean isInnerClass();
    
    public String getClassName() 
    {
        return className;
    }
        
    public String getSimpleClassName()
    {
        return simpleClassName;
    }
    
    public String getPackageName()
    {
        return packageName;
    }    
    
    public byte[] getClassBytes() 
    {
        return classBytes;
    }
    
    public void setClassBytes(byte[] classBytes)
    {
        this.classBytes = classBytes;
    }

    public Class getLastLoadedClass() 
    {
        return clasz;
    }

    public void setLastLoadedClass(Class clasz) 
    {
        this.clasz = clasz;
    }    

    public void resetLastLoadedClass()
    {
        setLastLoadedClass(null); 
    }    
    
    /*
    public String getClassFileNameFromClassName()
    {    
        return getClassFileNameFromClassName(className);
    }
    */
    
    public static String getClassFileNameFromClassName(String className)
    {
        // Es válido también para las innerclasses (ej Nombre$Otro => Nombre$Otro.class,  Nombre$1 => Nombre$1.class, Nombre$1Nombre => Nombre$1Nombre.class 
        int pos = className.lastIndexOf(".");
        if(pos != -1) className = className.substring(pos + 1);
        return className + ".class";    
    }
    
    public static String getRelativeClassFilePathFromClassName(String className)
    {
        return className.replace('.','/') + ".class";    // alternativa: className.replaceAll("\\.", "/") + ".class"
    }
    
    public static String getRelativePackagePathFromClassName(String className)
    {
        String packageName = className.replace('.','/');  
        int pos = packageName.lastIndexOf('/');
        if (pos == -1) return packageName;
        return packageName.substring(0,pos);
    }    
    
    public static String getAbsoluteClassFilePathFromClassNameAndClassPath(String className,String classPath)
    {
        String relativePath = getRelativeClassFilePathFromClassName(className);
        classPath = classPath.trim();
        if (!classPath.endsWith("/") && !classPath.endsWith("\\")) classPath += File.separatorChar;        
        return classPath + relativePath; 
    }    
    
    public static String getClassNameFromRelativeClassFilePath(String path)
    {
        // Ej. org/w3c/dom/Element.class => org.w3c.dom.Element
        String binaryName = path.replaceAll("/", ".");
        return binaryName.replaceAll(".class$", "");    // El $ indica "el .class del final" 
    }
    
    public static String getClassNameFromPackageAndClassFileName(String packageName,String fileName)
    {
        String className = packageName + "." + fileName;
        return className.replaceAll(".class$", "");    // El $ indica "el .class del final" 
    }
}
