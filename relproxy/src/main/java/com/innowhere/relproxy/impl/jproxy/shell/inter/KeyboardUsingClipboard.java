package com.innowhere.relproxy.impl.jproxy.shell.inter;


import com.innowhere.relproxy.RelProxyException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.nio.charset.Charset;

/**
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 * http://en.wikipedia.org/wiki/Unicode_input#Hexadecimal_code_input
 * http://stackoverflow.com/questions/9814701/accent-with-robot-keypress
 * 
 * @author jmarranz
 */
public class KeyboardUsingClipboard extends Keyboard implements ClipboardOwner
{
    protected final Robot robot;
    protected Charset cs;
   
    
    public KeyboardUsingClipboard(Charset cs) 
    {
        this.cs = cs;
        try
        {
            this.robot = new Robot();
        }
        catch (AWTException ex)
        {
            throw new RelProxyException(ex);
        }
    }

    public static KeyboardUsingClipboard create(Charset cs)
    {
        return new KeyboardUsingClipboard(cs);
    }    

    
    @Override
    public void type(CharSequence characters) 
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection( characters.toString() );
        clipboard.setContents(stringSelection, this);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents)
    {

    }

}
