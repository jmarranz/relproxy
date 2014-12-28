package com.innowhere.relproxy.impl.jproxy.core.clsmgr;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import java.io.File;

/**
 *
 * @author jmarranz
 */
public class SourceScriptRootFileOtherExt extends SourceScriptRootFile
{
    public SourceScriptRootFileOtherExt(File sourceFile,FolderSourceList folderSourceList)
    {
        super(sourceFile,folderSourceList);
    }  
    
    @Override
    public String getScriptCode(String encoding,boolean[] hasHashBang)
    {
        String codeBody = JProxyUtil.readTextFile(sourceFile,encoding);         
        // Eliminamos la primera línea #!  (debe estar en la primera línea y sin espacios antes)
        if (codeBody.startsWith("#!"))
        {
            hasHashBang[0] = true;        
            int pos = codeBody.indexOf('\n');
            if (pos != -1) // Rarísimo que sólo esté el hash bang (script vacío)
            {
                codeBody = codeBody.substring(pos + 1);
            }    
        }
        else hasHashBang[0] = false;
        return codeBody;
    }        
}
