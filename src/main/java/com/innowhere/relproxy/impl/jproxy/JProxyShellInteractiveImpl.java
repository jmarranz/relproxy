package com.innowhere.relproxy.impl.jproxy;

import com.innowhere.relproxy.RelProxyException;
import com.innowhere.relproxy.impl.jproxy.clsmgr.ClassDescriptorSourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.JProxyEngine;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScript;
import com.innowhere.relproxy.impl.jproxy.clsmgr.SourceScriptInMemory;
import com.innowhere.relproxy.impl.jproxy.clsmgr.comp.JProxyCompilationException;
import com.innowhere.relproxy.impl.jproxy.shell.Keyboard;
import com.innowhere.relproxy.impl.jproxy.shell.WindowUnicodeKeyboard;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Alguna inspiración: http://groovy.codehaus.org/Groovy+Shell
 * 
 * @author jmarranz
 */
public class JProxyShellInteractiveImpl extends JProxyShellImpl
{
    protected boolean test = false;
    protected ArrayList<String> codeBuffer = new ArrayList<String>(20);
    
    public void init(String[] args) throws Throwable
    {       
        ClassDescriptorSourceScript script = super.init(args, null);
        
        SourceScriptInMemory sourceScript = (SourceScriptInMemory)script.getSourceScript();

        if (test) 
        { 
            try { Thread.sleep(2); } catch (InterruptedException ex){  }
            execute("System.out.println(\"Hello World\");",script,sourceScript); //  "Object o = null; o.equals(null);"
            return;
        }
        
        loop(script,sourceScript);
    }      
    
    @Override
    public ClassDescriptorSourceScript init(JProxyConfigImpl config,SourceScript scriptFile,ClassLoader classLoader)
    {    
        ClassDescriptorSourceScript script = super.init(config, scriptFile, classLoader);
        
        this.test = config.isTest();
        
        return script;
    }
        
    protected void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        // La primera vez el script es vacío, no hay nada que ejecutar
    }    
    
    @Override
    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute in interactive mode");        
        
        // No tiene sentido especificar un tiempo de scan porque no hay directorio de entrada en el que escanear archivos
        if (config.getScanPeriod() >= 0) // 0 no puede ser porque da error antes pero lo ponemos para reforzar la idea
            throw new RelProxyException("scanPeriod positive value has no sense in interactive execution");        
    }    

    @Override    
    protected SourceScript getSourceScript(String[] args,LinkedList<String> argsToScript) 
    {
        return new SourceScriptInMemory("_jproxyShellInMemoryClass_",""); // La primera vez no hace nada, sirve para "calentar" la app
    }    
    
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }    

    private void loop(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        Scanner sc = new Scanner(System.in,Charset.defaultCharset().name());  // No encuentro nada interesante en http://docs.oracle.com/javase/6/docs/api/java/io/Console.html  
        while(true)
        {
            System.out.print(">");
            String line = sc.nextLine();
            if (!processCommand(line,scriptClass,sourceScript))
                codeBuffer.add(line);
        }
    }
    
    private boolean processCommand(String cmd,ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        cmd = cmd.trim();
        if (cmd.equals("clear"))
        {
            commandClear();
            return true;
        }     
        else if (cmd.equals("display"))
        {
            commandDisplay();
            return true;
        }       
        else if (cmd.equals("edit"))
        {           
            System.out.print(">");            
            Keyboard keyb = new WindowUnicodeKeyboard(); // new Keyboard();
            keyb.type("áéñç");
            
            /*
            seguir;

            try {
                Robot robot = new Robot();

                // Simulate a mouse click
               
                // Simulate a key press
                for(int i = 0; i < 10; i++) { robot.keyPress(KeyEvent.VK_A); robot.keyRelease(KeyEvent.VK_A); }
            robot.keyPress(KeyEvent.);
                return true;
            } catch (AWTException ex) {
                Logger.getLogger(JProxyShellInteractiveImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
*/
        }               
        else if (cmd.equals("exec"))
        {
            commandExec(scriptClass,sourceScript);
            return true;
        }
        else if (cmd.equals("exit"))
        {
            commandExit();
            return true;
        }
        else if (cmd.equals("quit"))
        {
            commandExit();
            return true;
        }        
        return false;
    }
    
    private void execute(String code,ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        sourceScript.setScriptCode(code);
        // Recuerda que cada vez que se obtiene el timestamp se llama a System.currentTimeMillis(), es imposible que el usuario haga algo en menos de 1ms
        
        JProxyEngine engine = getJProxyEngine();
        
        ClassDescriptorSourceScript scriptClass2 = null;
        try
        {
            scriptClass2 = engine.detectChangesInSources();
        }
        catch(JProxyCompilationException ex) 
        {
            System.out.println("Compilation error");
            return;
        }
        
        if (scriptClass2 != scriptClass)
            throw new RelProxyException("Internal Error");
        
        try
        {            
            scriptClass.callMainMethod(new LinkedList<String>());    
        }
        catch(Throwable ex)
        {
            ex.printStackTrace(System.out);
        }
    }    
    
    private void commandClear()
    {
        codeBuffer.clear();
    }
    
    private void commandExit()
    {
        System.exit(0);
    }    
    
    private void commandDisplay()
    {
        int i = 1;
        for(String line : codeBuffer)
        {
            for(int j = 0; j < 3 - String.valueOf(i).length(); j++) System.out.print("0"); 
            System.out.print(i + ">");
            System.out.print(line);                
            System.out.println(); 
            i++;
        }        
    }
    
    private void commandExec(ClassDescriptorSourceScript scriptClass,SourceScriptInMemory sourceScript)
    {
        StringBuilder code = new StringBuilder();
        for(String line : codeBuffer)
        {
            code.append(line);
            code.append("\n");                
        }

        execute(code.toString(),scriptClass,sourceScript);        
    }
}
