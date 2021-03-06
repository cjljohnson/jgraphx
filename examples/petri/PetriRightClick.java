package petri;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import org.w3c.dom.Element;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class PetriRightClick extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149390414490130748L;

	public PetriRightClick(final HelloWorld hello, int x, int y)
	{
//		boolean selected = !hello.getGraphComponent().getGraph()
//				.isSelectionEmpty();
	    
	    final Object cell = hello.getGraphComponent().getCellAt(x, y);
	    
	    if (cell != null) {
	        Object value = ((mxCell)cell).getValue();
            if (value instanceof Element) {
                final Element el = (Element)value;
                if (el.getTagName().equalsIgnoreCase("place")) {
                	placeMenu(hello, cell, el);
                } else if (el.getTagName().equalsIgnoreCase("arc")) {
                	arcMenu(hello, cell, el);
                } else if (el.getTagName().equalsIgnoreCase("transition")) {
                    transitionMenu(hello, cell, el);
                }
            }
	    }
	    
	    add(hello.bind("Place", PetriGraphActions.getCreatePlaceAction(x, y),
                        "/petri/images/place.gif"));
	    
	    add(hello.bind("Transition", PetriGraphActions.getCreateTransitionAction(x, y),
	                    "/petri/images/transition.gif"));
	    
	    addSeparator();
	    
//	    add(hello.bind("Reach", PetriGraphActions.getCreateReachabilityAction(),
//	            "/petri/images/reach.gif"));
	    
	    add(hello.bind2("Reach", PetriGraphActions.getCreateReachabilityAction(),
	            "/petri/images/reach.gif"));
	    
	    addSeparator();

		add(hello.bind("undo", new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));
		
		add(hello.bind("Delete", mxGraphActions.getDeleteAction(),
                        "/com/mxgraph/examples/swing/images/delete.gif"))
                .setEnabled(true);
		
		addSeparator();
		
		add(hello.bind("New", new PetriGraphActions.NewAction(), 
				"/com/mxgraph/examples/swing/images/new.gif"));
		
		add(hello.bind("Save As", new PetriGraphActions.SaveAction(true), 
				"/com/mxgraph/examples/swing/images/save.gif"));
		
		add(hello.bind("Open", new PetriGraphActions.OpenAction(),
				"/com/mxgraph/examples/swing/images/open.gif"));
		
//		add(hello.bind("Load", new PetriGraphActions.LoadAction(true), "/com/mxgraph/examples/swing/images/load.gif"));
	}

    private void placeMenu(final HelloWorld hello, final Object cell, final Element el) {
		
            // Tokens
            JPanel tokensPanel = new JPanel();
            JLabel tokensL = new JLabel("Tokens:  ");
            final JTextField tokensTF = new JTextField(5);
            tokensTF.setText(el.getAttribute("tokens"));
            tokensTF.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	int newTokens;
                	try {
                		newTokens = Integer.parseInt(tokensTF.getText());
                		int capacity = Integer.parseInt(el.getAttribute("capacity"));
                		if (newTokens >= 0 && (newTokens <= capacity || capacity == -1)) {
                    		el.setAttribute("tokens", "" + newTokens);
                    		((PetriGraph)hello.getGraphComponent().getGraph()).checkEnabledFromPlace(cell);
                    		hello.getGraphComponent().refresh();
                    		return;
                    	}
                	} catch (Exception exc) {
                		
                	}
                	tokensTF.setText(el.getAttribute("tokens"));
            		return;
                	
                }
            });
            tokensPanel.add(tokensL);
            tokensPanel.add(tokensTF);
            add(tokensPanel);
            
            // Capacity
            JPanel capacityPanel = new JPanel();
            JLabel capacityL = new JLabel("Capacity:");
            final JTextField capacityTF = new JTextField(5);
            if (el.getAttribute("capacity").equalsIgnoreCase("-1")) {
                capacityTF.setText("n");
            } else {
                capacityTF.setText(el.getAttribute("capacity"));
            }
            capacityTF.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	int newCapacity;
                	try {
                	    if (capacityTF.getText().equalsIgnoreCase("n")) {
                	        newCapacity = -1;
                	    } else {
                	        newCapacity = Integer.parseInt(capacityTF.getText());
                	    }
                		if ((newCapacity > 0 && newCapacity >= Integer.parseInt(el.getAttribute("tokens"))) 
                    			|| newCapacity == -1) {
                    		el.setAttribute("capacity", "" + newCapacity);
                    		((PetriGraph)hello.getGraphComponent().getGraph()).checkEnabledFromPlace(cell);
                    		hello.getGraphComponent().refresh();
                    	}
                	} catch (Exception exc) {
                		
                	}
                	if (el.getAttribute("capacity").equalsIgnoreCase("-1")) {
                        capacityTF.setText("n");
                    } else {
                        capacityTF.setText(el.getAttribute("capacity"));
                    }
            		return;
                	
                }
            });
            capacityPanel.add(capacityL);
            capacityPanel.add(capacityTF);
            add(capacityPanel);
            
            addSeparator();
	}
	
	private void arcMenu(final HelloWorld hello, final Object cell, final Element el) {
		// Weight
        JPanel weightPanel = new JPanel();
        JLabel weightL = new JLabel("Weight:");
        final JTextField weightTF = new JTextField(5);
        weightTF.setText(el.getAttribute("weight"));
        weightTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
            	int newWeight;
            	try {
            		newWeight = Integer.parseInt(weightTF.getText());
            		if (newWeight > 0) {
                		el.setAttribute("weight", "" + newWeight);
                		((PetriGraph)hello.getGraphComponent().getGraph()).checkEnabledFromEdge(cell);
                		hello.getGraphComponent().refresh();
                		return;
                	}
            	} catch (Exception exc) {
            		
            	}
            	weightTF.setText(el.getAttribute("weight"));
        		return;
            	
            }
        });
        weightPanel.add(weightL);
        weightPanel.add(weightTF);
        add(weightPanel);
        
        addSeparator();
	}
	
	private void transitionMenu(HelloWorld hello, Object cell, Element el) {
        
	    boolean isFirable = ((PetriGraph)hello.getGraphComponent().getGraph()).isFirable(cell);
	    
	    add(
                hello.bind("Fire Transition", new PetriGraphActions.FireTransitionAction(cell),
                        "/com/mxgraph/examples/swing/images/classic_end.gif"))
                .setEnabled(isFirable);
	    
	    addSeparator();
    }

}