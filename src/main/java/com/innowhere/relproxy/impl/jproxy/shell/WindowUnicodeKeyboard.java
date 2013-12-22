package com.innowhere.relproxy.impl.jproxy.shell;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_NUMPAD1;
import static java.awt.event.KeyEvent.VK_NUMPAD2;
import static java.awt.event.KeyEvent.VK_NUMPAD3;
import static java.awt.event.KeyEvent.VK_NUMPAD4;
import static java.awt.event.KeyEvent.VK_NUMPAD5;
import static java.awt.event.KeyEvent.VK_NUMPAD6;
import static java.awt.event.KeyEvent.VK_NUMPAD7;
import static java.awt.event.KeyEvent.VK_NUMPAD8;
import static java.awt.event.KeyEvent.VK_NUMPAD9;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * http://stackoverflow.com/questions/1248510/convert-string-to-keyevents
 * 
 * @author jmarranz
 */
public class WindowUnicodeKeyboard extends Keyboard
{
    protected Charset cs;
    
    public WindowUnicodeKeyboard(Charset cs)
    {
        this.cs = cs;
    }
    
    @Override
    public void type(char character) {
        try 
        {
            super.type(character);
        }
        catch (IllegalArgumentException e) 
        {               
            ByteBuffer buffer = cs.encode("" + character);

            byte b = buffer.get();
            int bi = b & 0x000000FF;

            
            String unicodeDigits = String.valueOf(bi);         
            robot.keyPress(VK_ALT);
            try
            {
                for (int i = 0; i < unicodeDigits.length(); i++) {
                    typeNumPad(Integer.parseInt(unicodeDigits.substring(i, i + 1)));
                }
            }
            finally
            {
                robot.keyRelease(VK_ALT);            
            }
            
            
            /* Alternativa
            String unicodeDigits = String.valueOf(Character.codePointAt(new char[]{character},0));

            robot.keyPress(VK_ALT);
            for (int i = 0; i < unicodeDigits.length(); i++) {
                typeNumPad(Integer.parseInt(unicodeDigits.substring(i, i + 1)));
            }
            robot.keyRelease(VK_ALT);
            */
        }
    }

    private void typeNumPad(int digit) {
        switch (digit) {
        case 0: doType(VK_NUMPAD0); break;
        case 1: doType(VK_NUMPAD1); break;
        case 2: doType(VK_NUMPAD2); break;
        case 3: doType(VK_NUMPAD3); break;
        case 4: doType(VK_NUMPAD4); break;
        case 5: doType(VK_NUMPAD5); break;
        case 6: doType(VK_NUMPAD6); break;
        case 7: doType(VK_NUMPAD7); break;
        case 8: doType(VK_NUMPAD8); break;
        case 9: doType(VK_NUMPAD9); break;
        }
    }

}