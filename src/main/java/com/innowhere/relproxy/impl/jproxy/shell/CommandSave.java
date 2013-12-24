package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import java.io.File;
import java.util.List;

/**
 *
 * @author jmarranz
 */
public class CommandSave extends Command
{
    public static final String NAME = "save";
    protected String path;     
    
    public CommandSave(JProxyShellProcessor parent,String url)
    {
        super(parent,NAME);
        this.path = url;        
    }    
    
    public static CommandSave createCommandSave(JProxyShellProcessor parent,String cmd)
    {
        String url = getParameter(NAME,cmd);
        if (url == null)
        {
            System.out.println("Command error: <path> parameter is required");
            return null;
        }

        return new CommandSave(parent,url);    
    }
    
    @Override
    public boolean run(ClassDescriptorSourceScript scriptClass, SourceScriptInMemory sourceScript)
    {
        try
        {
            
            List<String> codeBuffer = parent.getCodeBuffer();
            StringBuilder code = new StringBuilder();
            for(String line : codeBuffer)
            {
                code.append(line);
                code.append("\n");                
            }
            byte[] content = code.toString().getBytes(parent.getEncoding()); // Como no conocemos encoding...
            JProxyUtil.saveFile(new File(path),content);
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }    
    
    @Override
    public void runPostCommand()
    {
    }
}
