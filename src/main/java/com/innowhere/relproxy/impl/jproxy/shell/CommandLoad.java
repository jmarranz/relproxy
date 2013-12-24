package com.innowhere.relproxy.impl.jproxy.shell;

import com.innowhere.relproxy.impl.jproxy.JProxyUtil;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author jmarranz
 */
public class CommandLoad extends Command
{
    public static final String NAME = "load";
    protected String url;     
    
    public CommandLoad(JProxyShellProcessor parent,String url)
    {
        super(parent,NAME);
        this.url = url;        
    }    
    
    public static CommandLoad createCommandLoad(JProxyShellProcessor parent,String cmd)
    {
        String url = getParameter(NAME,cmd);
        if (url == null)
        {
            System.out.println("Command error: <url> parameter is required");
            return null;
        }

        return new CommandLoad(parent,url);    
    }
    
    @Override
    public boolean run(ClassDescriptorSourceScript scriptClass, SourceScriptInMemory sourceScript)
    {
        try
        {
            byte[] content;
            URI uri = new URI(url);
            if (uri.getScheme() == null) // Archivo
            {
                File file = new File(url);
                content = JProxyUtil.readFile(file);
            }
            else // URL (incluyendo file:///...)
            {
                URL urlObj = new URL(url);
                content = JProxyUtil.readURL(urlObj); // Como no conocemos encoding...
            }

            String code = new String(content,parent.getEncoding()); // Como no conocemos encoding...
            LinkedList<String> lines = new LinkedList<String>();
            Scanner scanner = new Scanner(code);
            while (scanner.hasNextLine()) 
            {
                String line = scanner.nextLine();
                lines.add(line);
            }            
          
            parent.setCodeBuffer(lines);
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
