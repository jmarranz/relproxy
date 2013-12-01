/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.innowhere.relproxy.impl.jproxy.clsmgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileRegistry
{
    protected Map<String,ClassDescriptorSourceFile> sourceFileMapByClassName;
    
    public ClassDescriptorSourceFileRegistry()
    {
        this.sourceFileMapByClassName = new HashMap<String,ClassDescriptorSourceFile>();
    }

    public boolean isEmpty()
    {
        return sourceFileMapByClassName.isEmpty();
    }
      
    public Collection<ClassDescriptorSourceFile> getClassDescriptorSourceFileColl()
    {
        return sourceFileMapByClassName.values();
    }
    
    public ClassDescriptorSourceFile getClassDescriptorSourceFile(String className)
    {
        return sourceFileMapByClassName.get(className);
    }        
    
    public void removeClassDescriptorSourceFile(String className)
    {
        sourceFileMapByClassName.remove(className);
    }            
    
    public void addClassDescriptorSourceFile(ClassDescriptorSourceFile sourceFile)
    {
        sourceFileMapByClassName.put(sourceFile.getClassName(), sourceFile);
    }
    
    public ClassDescriptor getClassDescriptor(String className)
    {
        // Puede ser el de una innerclass
        // Las innerclasses no están como tales en sourceFileMap pues sólo está la clase contenedora pero también la consideramos hotloadable
        String parentClassName;
        int pos = className.lastIndexOf('$');        
        boolean inner;        
        if (pos != -1)
        {
            parentClassName = className.substring(0, pos);
            inner = true;
        }
        else
        {
            parentClassName = className;
            inner = false;
        }
        ClassDescriptorSourceFile sourceDesc = sourceFileMapByClassName.get(parentClassName);        
        if (!inner) return sourceDesc;
        return sourceDesc.getInnerClassDescriptor(className,true);
    }    
}
