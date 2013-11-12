package com.innowhere.relproxy.impl.jproxy;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorInner extends ClassDescriptor
{
    protected ClassDescriptorSourceFile parent;
    
    public ClassDescriptorInner(String className,ClassDescriptorSourceFile parent) 
    {
        super(className);
        this.parent = parent;
    }      
    
    public boolean isInnerClass()
    {
        return true;
    }     
    
    public ClassDescriptorSourceFile getClassDescriptorSourceFile()
    {
        return parent;
    }
}
