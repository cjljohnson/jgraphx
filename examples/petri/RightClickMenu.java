package petri;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import org.w3c.dom.Element;

import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;

public class RightClickMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149390414490130748L;

	public RightClickMenu(final HelloWorld hello, int x, int y)
	{
//		boolean selected = !hello.getGraphComponent().getGraph()
//				.isSelectionEmpty();
	    
	    Object cell = hello.getGraphComponent().getCellAt(x, y);
	    
	    if (cell != null) {
	        Object value = ((mxCell)cell).getValue();
            if (value instanceof Element) {
                final Element el = (Element)value;
                if (el.getTagName().equalsIgnoreCase("place")) {
                    // Tokens
                    JPanel tokensPanel = new JPanel();
                    JLabel tokensL = new JLabel("Tokens:  ");
                    final JTextField tokensTF = new JTextField(5);
                    tokensTF.setText(el.getAttribute("tokens"));
                    tokensTF.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {

                            el.setAttribute("tokens", tokensTF.getText());  
                            hello.getGraphComponent().refresh();
                        }
                    });
                    tokensPanel.add(tokensL);
                    tokensPanel.add(tokensTF);
                    add(tokensPanel);
                    
                    // Capacity
                    JPanel capacityPanel = new JPanel();
                    JLabel capacityL = new JLabel("Capacity:");
                    final JTextField capacityTF = new JTextField(5);
                    capacityTF.setText(el.getAttribute("capacity"));
                    capacityTF.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {

                            el.setAttribute("capacity", capacityTF.getText());  
                            hello.getGraphComponent().refresh();
                        }
                    });
                    capacityPanel.add(capacityL);
                    capacityPanel.add(capacityTF);
                    add(capacityPanel);
                    
                    addSeparator();
                }
            }
	    }
	    
	    add(hello.bind("Place", PetriGraphActions.getCreatePlaceAction(x, y),
                        "/com/mxgraph/examples/swing/images/oval_start.gif"));
	    
	    add(hello.bind("Transition", PetriGraphActions.getCreateTransitionAction(x, y),
	                    "/com/mxgraph/examples/swing/images/oval_end.gif"));
	    
	    addSeparator();
	    
	    add(hello.bind("Reach", PetriGraphActions.getCreateReachabilityAction(),
                "/com/mxgraph/examples/swing/images/oval_end.gif"));
	    
	    addSeparator();

		add(hello.bind("undo", new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));
		
		add(hello.bind("Delete", mxGraphActions.getDeleteAction(),
                        "/com/mxgraph/examples/swing/images/delete.gif"))
                .setEnabled(true);
		
		add(hello.bind("Save As", new PetriGraphActions.SaveAction(true), "/com/mxgraph/examples/swing/images/save.gif"));
		
//		add(hello.bind("Load", new PetriGraphActions.LoadAction(true), "/com/mxgraph/examples/swing/images/load.gif"));
	}

}