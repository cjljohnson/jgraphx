package petri;

import javax.swing.JPopupMenu;

import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class RightClickMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149390414490130748L;

	public RightClickMenu(HelloWorld hello, int x, int y)
	{
//		boolean selected = !hello.getGraphComponent().getGraph()
//				.isSelectionEmpty();
	    
	    add(hello.bind("Place", PetriGraphActions.getCreatePlaceAction(x, y),
                        "/com/mxgraph/examples/swing/images/oval_start.gif"));
	    
	    add(hello.bind("Transition", PetriGraphActions.getCreateTransitionAction(x, y),
	                    "/com/mxgraph/examples/swing/images/oval_end.gif"));
	    
	    addSeparator();

		add(hello.bind("undo", new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));
		
		add(hello.bind("Delete", mxGraphActions.getDeleteAction(),
                        "/com/mxgraph/examples/swing/images/delete.gif"))
                .setEnabled(true);
		
		add(hello.bind("Save As", new PetriGraphActions.SaveAction(true), "/com/mxgraph/examples/swing/images/save.gif"));
	}

}