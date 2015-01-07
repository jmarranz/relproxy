package example.javaex;

import org.itsnat.comp.ItsNatComponentManager;
import org.itsnat.comp.text.ItsNatHTMLInputText;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.html.ItsNatHTMLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;

public class JProxyExampleDocument
{
    protected ItsNatHTMLDocument itsNatDoc; // ItsNatHTMLDocument
    protected ItsNatHTMLInputText textInput; // ItsNatHTMLInputText
    protected Element resultsElem; // Element   
  
    
    public JProxyExampleDocument(ItsNatServletRequest request,ItsNatHTMLDocument itsNatDoc,FalseDB db)
    {       
        this.itsNatDoc = itsNatDoc;

        HTMLDocument doc = itsNatDoc.getHTMLDocument();

        ItsNatComponentManager compMgr = itsNatDoc.getItsNatComponentManager();
        this.textInput = (ItsNatHTMLInputText)compMgr.createItsNatComponentById("inputId");


        EventListener listener = new EventListener()
        {        
            @Override
            public void handleEvent(Event evt) 
            {
                String text = textInput.getText(); 
                String comment = "  YES I SAID THAT 1"; // " YES I SAID THAT";                
                resultsElem.setTextContent(text + comment);
            }
        };
        
        Element buttonElem = doc.getElementById("buttonId");
        ((EventTarget)buttonElem).addEventListener("click",listener,false);

        
        this.resultsElem = doc.getElementById("resultsId");            
    }
}
