package example.javaex;

import example.javaex.hotreload.JProxyExampleAux2;
import org.itsnat.comp.ItsNatComponentManager;
import org.itsnat.comp.text.ItsNatHTMLInputText;
import org.itsnat.core.ItsNatServletRequest;
import org.itsnat.core.html.ItsNatHTMLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLDocument;

public class JProxyExampleDocument extends JProxyExampleDocumentBase
{
    protected ItsNatHTMLDocument itsNatDoc; // ItsNatHTMLDocument
    protected ItsNatHTMLInputText textInput; // ItsNatHTMLInputText
    protected Element resultsElem; // Element   

    public static class AuxMember 
    { 
        public static void log()
        {
            System.out.println(AuxMember.class.getName() + ": 1 " + AuxMember.class.getClassLoader().hashCode());
        }        
    }
           
    
    public JProxyExampleDocument(ItsNatServletRequest request,ItsNatHTMLDocument itsNatDoc,FalseDB db)
    {
        class AuxMemberInMethod 
        { 
            public void log()
            {
                System.out.println("JProxyExampleDocument.AuxMemberInMethod: 1 " + AuxMemberInMethod.class.getClassLoader().hashCode());
            }        
        }        
        
        this.itsNatDoc = itsNatDoc;

        if (db.getCityList().size() != 3) 
            throw new RuntimeException("Unexpected");

        HTMLDocument doc = itsNatDoc.getHTMLDocument();

        ItsNatComponentManager compMgr = itsNatDoc.getItsNatComponentManager();
        this.textInput = (ItsNatHTMLInputText)compMgr.createItsNatComponentById("inputId");

       EventListener listener = new EventListener()
       {    
            {
                System.out.println("JProxyExampleDocument Anonymous Inner 1 " + this.getClass().getClassLoader().hashCode());
            }
           
            @Override
            public void handleEvent(Event evt) 
            {
                String text = textInput.getText(); 
                resultsElem.setTextContent(text);
            }
        };
        
        Element buttonElem = doc.getElementById("buttonId");
        ((EventTarget)buttonElem).addEventListener("click",listener,false);

        this.resultsElem = doc.getElementById("resultsId");
        
        System.out.println("JProxyExampleDocument 1 " + this.getClass().getClassLoader().hashCode());        
        new AuxMemberInMethod().log();
        AuxMember.log();
        JProxyExampleAux.log();
        JProxyExampleAuxIgnored.log();
        JProxyExampleAux2.log();        
    }
}
