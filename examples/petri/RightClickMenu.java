package petri;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorMenuBar;
import com.mxgraph.examples.swing.editor.EditorActions.HistoryAction;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxResources;

public class RightClickMenu extends JPopupMenu
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149390414490130748L;

	public RightClickMenu(HelloWorld hello)
	{
//		boolean selected = !hello.getGraphComponent().getGraph()
//				.isSelectionEmpty();

		add(hello.bind("undo", new HistoryAction(true),
				"/com/mxgraph/examples/swing/images/undo.gif"));
	}

}