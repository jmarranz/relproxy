package com.innowhere.relproxy.impl.jproxy.clsmgr;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorInner extends ClassDescriptor
{
    protected ClassDescriptorSourceUnit parent;
    
    public ClassDescriptorInner(String className,ClassDescriptorSourceUnit parent) 
    {
        super(className);
        this.parent = parent;
    }      
    
    public boolean isInnerClass()
    {
        return true;
    }     
    
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit()
    {
        return parent;
    }
}
