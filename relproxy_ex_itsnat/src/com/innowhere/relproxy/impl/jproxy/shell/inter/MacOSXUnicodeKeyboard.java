package com.innowhere.relproxy.impl.jproxy.shell.inter;

import static java.awt.Event.ALT_MASK;
import java.nio.charset.Charset;

/**
 * 
 * https://discussions.apple.com/thread/1899290
 * http://superuser.com/questions/13086/how-do-you-type-unicode-characters-using-hexadecimal-codes  (buscar OS X)
 * http://controlyourmac.com/2012/05/understanding-mac-keyboard.html
 * 
 * @author jmarranz
 */
public class MacOSXUnicodeKeyboard extends Keyboard
{
    protected Charset cs;
    
    public MacOSXUnicodeKeyboard(Charset cs)
    {
        this.cs = cs;
    }
    
    @Override
    public boolean type(char character) 
    {
        if (super.type(character))
            return true;

        int bi = getUnicodeInt(cs,character);

        String unicodeDigits = Integer.toString(bi,16); // En hexadecimal

        robot.keyPress(ALT_MASK);  // "Since the ALT_MASK modifier is the Option key in OS X" https://developer.apple.com/library/mac/documentation/java/conceptual/java14development/07-NativePlatformIntegration/NativePlatformIntegration.html     
            
        try
        {
            for (int i = 0; i < unicodeDigits.length(); i++) {
                type(unicodeDigits.charAt(i)); 
            }
        }
        finally
        {
            robot.keyRelease(ALT_MASK);                         
        }
        
        return true;
    }

}