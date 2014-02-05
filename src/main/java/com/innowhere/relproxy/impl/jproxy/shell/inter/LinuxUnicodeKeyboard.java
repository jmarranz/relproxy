package com.innowhere.relproxy.impl.jproxy.shell.inter;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_U;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 * 
 * http://superuser.com/questions/59418/how-to-type-special-characters-in-linux
 * 
 * @author jmarranz
 */
public class LinuxUnicodeKeyboard extends Keyboard
{
    protected Charset cs;
    
    public LinuxUnicodeKeyboard(Charset cs)
    {
        this.cs = cs;
    }
    
    @Override
    public boolean type(char character) 
    {
        if (super.type(character))
            return true;

        ByteBuffer buffer = cs.encode("" + character);

        byte b = buffer.get();
        int bi = b & 0x000000FF;


        String unicodeDigits = Integer.toString(bi,16); // En hexadecimal

        robot.keyPress(VK_CONTROL);        
        robot.keyPress(VK_SHIFT);
        
        type('u');
            
        // Pero dejamos pulsadas CTRL y SHIFT mientras 
        try
        {
            for (int i = 0; i < unicodeDigits.length(); i++) {
                type(unicodeDigits.charAt(i)); 
            }
        }
        finally
        {
            robot.keyRelease(VK_CONTROL);             
            robot.keyRelease(VK_SHIFT);            
        }
        
        return true;
    }

}