package petri;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPopupMenu;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxMultiplicity;
import com.mxgraph.view.mxStylesheet;

public class HelloWorld extends JFrame
{
	mxGraphComponent graphComponent;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public HelloWorld()
	{
		super("Hello, World!");
		
		//Hardcode test elements
		Document xmlDocument = mxDomUtils.createDocument();
		
		// Places
		Element place1 = xmlDocument.createElement("Place");
		place1.setAttribute("tokens", "5");
		place1.setAttribute("capacity", "10");
		Element place2 = xmlDocument.createElement("Place");
		place2.setAttribute("tokens", "3");
		place2.setAttribute("capacity", "20");
		
		// Transitions
		Element transition1 = xmlDocument.createElement("Transition");
		Element transition2 = xmlDocument.createElement("Transition");
		System.out.println(place1.getTagName());
		
		// Arcs
		Element arc1 = xmlDocument.createElement("Arc");
		Element arc2 = xmlDocument.createElement("Arc");
		Element arc3 = xmlDocument.createElement("Arc");
		Element arc4 = xmlDocument.createElement("Arc");
		arc1.setAttribute("weight", "1");
		arc2.setAttribute("weight", "2");
		arc3.setAttribute("weight", "2");
		arc4.setAttribute("weight", "4");

		final PetriGraph graph = new PetriGraph(xmlDocument);
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> placeStyle = new Hashtable<String, Object>();
		placeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_OPACITY, 100);
		placeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		placeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		placeStyle.put(mxConstants.STYLE_NOLABEL, false);
		stylesheet.putCellStyle("PLACE", placeStyle);
		
		Hashtable<String, Object> transitionStyle = new Hashtable<String, Object>();
		transitionStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		transitionStyle.put(mxConstants.STYLE_OPACITY, 100);
		transitionStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		transitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		transitionStyle.put(mxConstants.STYLE_NOLABEL, true);
		stylesheet.putCellStyle("TRANSITION", transitionStyle);
		
		Hashtable<String, Object> arcStyle = new Hashtable<String, Object>();
		arcStyle.put(mxConstants.STYLE_OPACITY, 100);
		arcStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		arcStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("ARC", arcStyle);
		
		applyEdgeDefaults(graph);
		
		
		try
		{
			mxCell v1 = (mxCell)graph.insertVertex(parent, null, place1, 20, 20, 40,
					40, "PLACE");
					System.out.println(v1);
			Object v2 = graph.insertVertex(parent, null, transition1, 240, 150,
					40, 40, "TRANSITION");
			Object v3 = graph.insertVertex(parent, null, transition2, 140, 150,
					40, 40, "TRANSITION");
			Object v4 = (mxCell)graph.insertVertex(parent, null, place2, 280, 280, 40,
					40, "PLACE");
			graph.insertEdge(parent, null, arc1, v1, v2, null);
			graph.insertEdge(parent, null, arc2, v3, v1, null);
			graph.insertEdge(parent, null, arc3, v2, v4, null);
			graph.insertEdge(parent, null, arc4, v4, v3, null);
			graph.setCellsResizable(false);
			graph.setMultigraph(false);
			graph.setAllowDanglingEdges(false);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		graph.addPlace(3, 20);
		graph.addTransition();
		
		// Set bipartite restrictions
		mxMultiplicity[] multiplicities = new mxMultiplicity[2];

		// Source nodes needs 1..2 connected Targets
		multiplicities[0] = new mxMultiplicity(true, "Place", null, null, 0,
				"n", Arrays.asList(new String[] { "Transition" }),
				null,
				"Places can only connect to Transitions", true);


		// Target needs exactly one incoming connection from Source
		multiplicities[1] = new mxMultiplicity(true, "Transition", null, null, 0,
				"n", Arrays.asList(new String[] { "Place" }),
				null, "Transitions can only connect to Places",
				true);

		graph.setMultiplicities(multiplicities);
		
		
		

		graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		graphComponent.setGridVisible(true);
		// Sets the background to white
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
		
			public void mouseReleased(MouseEvent e)
			{
				Object obj = graphComponent.getCellAt(e.getX(), e.getY());
				
				if (obj != null && obj instanceof mxCell)
				{
					Object value = ((mxCell) obj).getValue();
					if (value instanceof Element)
					{
						if (((Element) value).getTagName().equalsIgnoreCase("transition"))
						{
							((PetriGraph) graph).fireTransition(obj);
							graphComponent.refresh();
						}
					}
					System.out.println("cell="+graph.getLabel(obj));
				}
			}
		});
		
		// Installs the popup menu in the graph component
				graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
				{

					/**
					 * 
					 */
					public void mousePressed(MouseEvent e)
					{
						// Handles context menu on the Mac where the trigger is on mousepressed
						mouseReleased(e);
					}

					/**
					 * 
					 */
					public void mouseReleased(MouseEvent e)
					{
						if (e.isPopupTrigger())
						{
							showGraphPopupMenu(e);
						}
					}

				});

		
		
		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
	}
	
	protected void showGraphPopupMenu(MouseEvent e)
	{
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				graphComponent);
		RightClickMenu menu = new RightClickMenu(this);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
	
	@SuppressWarnings("serial")
	public Action bind(String name, final Action action, String iconUrl)
	{
		AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
				BasicGraphEditor.class.getResource(iconUrl)) : null)
		{
			public void actionPerformed(ActionEvent e)
			{
				action.actionPerformed(new ActionEvent(graphComponent, e
						.getID(), e.getActionCommand()));
			}
		};
		
		newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
		
		return newAction;
	}
	
	private void applyEdgeDefaults(mxGraph graph) {
	    // Settings for edges
	    Map<String, Object> edge = new HashMap<String, Object>();
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");

	    graph.getStylesheet().setDefaultEdgeStyle(edge);
	}

	public static void main(String[] args)
	{
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setVisible(true);
	}

}
