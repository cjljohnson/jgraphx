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
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPopupMenu;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxKeyboardHandler;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxEventSource.mxIEventListener;
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
	
	static
    {
        try
        {
            mxResources.add("com/mxgraph/examples/swing/resources/editor");
        }
        catch (Exception e)
        {
            // ignore
        }
    }

	public HelloWorld()
	{
		super("Petri Netter");
		
		//Hardcode test elements
		Document xmlDocument = mxDomUtils.createDocument();
		
		// Places
		Element place1 = xmlDocument.createElement("Place");
		place1.setAttribute("tokens", "5");
		place1.setAttribute("capacity", "10");
		Element place2 = xmlDocument.createElement("Place");
		place2.setAttribute("tokens", "3");
		place2.setAttribute("capacity", "20");
		xmlDocument.appendChild(place1);
		place1.appendChild(place2);
		
		// Transitions
		Element transition1 = xmlDocument.createElement("Transition");
		Element transition2 = xmlDocument.createElement("Transition");
		
		// Arcs
		Element arc1 = xmlDocument.createElement("Arc");
		Element arc2 = xmlDocument.createElement("Arc");
		Element arc3 = xmlDocument.createElement("Arc");
		Element arc4 = xmlDocument.createElement("Arc");
		arc1.setAttribute("weight", "3");
		arc2.setAttribute("weight", "2");
		arc3.setAttribute("weight", "2");
		arc4.setAttribute("weight", "4");
		
		System.out.println(arc4.getAttribute("weight"));
		

		final PetriGraph graph = new PetriGraph(xmlDocument);
		Object parent = graph.getDefaultParent();
		
//		mxSwingConstants.SHADOW_COLOR = null;
//		mxSwingConstants.DEFAULT_VALID_COLOR = null;
//		mxSwingConstants.DEFAULT_INVALID_COLOR = null;
//		mxSwingConstants.RUBBERBAND_BORDERCOLOR = null;
//		mxSwingConstants.RUBBERBAND_FILLCOLOR = null;
//		mxSwingConstants.HANDLE_BORDERCOLOR = null;
//		mxSwingConstants.HANDLE_FILLCOLOR = null;
//		mxSwingConstants.LABEL_HANDLE_FILLCOLOR = null;
//		mxSwingConstants.LOCKED_HANDLE_FILLCOLOR = null;
//		mxSwingConstants.CONNECT_HANDLE_FILLCOLOR = null;
//		mxSwingConstants.EDGE_SELECTION_COLOR = null;
//		mxSwingConstants.VERTEX_SELECTION_COLOR = null;

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
		placeStyle.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		//perimeter=ellipsePerimeter
		stylesheet.putCellStyle("PLACE", placeStyle);
		
		Hashtable<String, Object> placeCapacityStyle = new Hashtable<String, Object>();
		placeCapacityStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeCapacityStyle.put(mxConstants.STYLE_FILLCOLOR, "none");
		placeCapacityStyle.put(mxConstants.STYLE_STROKECOLOR, "none");
		stylesheet.putCellStyle("CAPACITY", placeCapacityStyle);
		
		Hashtable<String, Object> transitionStyle = new Hashtable<String, Object>();
		transitionStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		transitionStyle.put(mxConstants.STYLE_OPACITY, 100);
		transitionStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		transitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		transitionStyle.put(mxConstants.STYLE_NOLABEL, true);
		transitionStyle.put(mxConstants.STYLE_PERIMETER_SPACING, 4);
		stylesheet.putCellStyle("TRANSITION", transitionStyle);
		

        Hashtable<String, Object> activeTransitionStyle = new Hashtable<String, Object>();
        activeTransitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
        stylesheet.putCellStyle("ACTIVETRANSITION", activeTransitionStyle);
		
		Hashtable<String, Object> arcStyle = new Hashtable<String, Object>();
		arcStyle.put(mxConstants.STYLE_OPACITY, 100);
		arcStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		arcStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_STROKEWIDTH, 5);
		stylesheet.putCellStyle("ARC", arcStyle);
		
		applyEdgeDefaults(graph);
		
		
		
		
		try
		{
			Object v1 = graph.addPlace(5, 10, 20, 20);
			Object v2 = graph.addTransition(240, 150);
			Object v3 = graph.addTransition(140, 150);
			Object t3 = graph.addTransition(60, 200);
			Object v4 = graph.addPlace(3, 20, 280, 280);
			graph.insertEdge(parent, null, arc1, v1, v2, null);
			graph.insertEdge(parent, null, arc2, v3, v1, null);
			graph.insertEdge(parent, null, arc3, v2, v4, null);
			graph.insertEdge(parent, null, arc4, v4, v3, null);
			
			graph.insertEdge(parent, null, arc4, v4, t3, null);
			graph.insertEdge(parent, null, arc4, t3, v1, null);
			
			graph.setCellsResizable(false);
			graph.setMultigraph(false);
			graph.setAllowDanglingEdges(false);
			graph.checkEnabledTransitions();
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		
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
		
		
		
//		JPanel panel = new JPanel();
//		getContentPane().add(panel);
		
		graphComponent = new mxGraphComponent(graph);
//		panel.add(graphComponent);
		initialiseGraphComponent(graphComponent);
		getContentPane().add(graphComponent);
		
		graph.addListener(mxEvent.CELL_CONNECTED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                mxCell connectionCell = (mxCell) evt.getProperty("edge");
                if (connectionCell.getSource() != null && connectionCell.getTarget() != null) {
                    graph.checkEnabledFromEdge(connectionCell);
                }
                
            }
        });
		
		graph.addListener(mxEvent.CELLS_REMOVED, new mxIEventListener() {
            public void invoke(Object sender, mxEventObject evt) {
                Object[] cells = (Object[]) evt.getProperty("cells");
                for (Object cell : cells) {
                	graph.checkEnabledFromEdge(cell);
                }
            }
        });
		
		
//		PetriGraph graph2 = new PetriGraph(xmlDocument);
//		graph2.addCells(graph.cloneCells(graph.getChildCells(graph.getDefaultParent())));
//		mxStylesheet stylesheet2 = graph2.getStylesheet();
//		stylesheet2.putCellStyle("PLACE", placeStyle);
//		stylesheet2.putCellStyle("TRANSITION", transitionStyle);
//		stylesheet2.putCellStyle("ACTIVETRANSITION", activeTransitionStyle);
//		stylesheet2.putCellStyle("ARC", arcStyle);
//		
//		
//		mxGraphComponent graphComponent2 = new mxGraphComponent(graph2);
//		initialiseGraphComponent(graphComponent2);
//		graphComponent2.refresh();
//		panel.add(graphComponent2);
		
		
		Map<String, Integer> tokenMap = graph.getPlaceTokens();
		tokenMap.put("6", 10);
		graph.setPlaceTokens(tokenMap);
		graphComponent.refresh();
		
		pack();
	}
	
	public void initialiseGraphComponent(final mxGraphComponent graphComponent) {
		final PetriGraph graph = (PetriGraph) graphComponent.getGraph();
		
		//getContentPane().add(graphComponent);
		graphComponent.setGridVisible(true);
		// Sets the background to white
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		graphComponent.setBackground(Color.WHITE);
		graphComponent.setEnterStopsCellEditing(true);

		//final mxGraph graph2 = new PetriGraph(xmlDocument);
		//graphComponent.setGraph(graph2);

		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{

			public void mouseReleased(MouseEvent e)
			{
				Object obj = graphComponent.getCellAt(e.getX(), e.getY());

				if (obj != null && obj instanceof mxCell && e.getClickCount() == 2)
				{
					Object value = ((mxCell) obj).getValue();
					if (value instanceof Element)
					{
						if (((Element) value).getTagName().equalsIgnoreCase("transition"))
						{
							if (graph.fireTransition(obj)) {
								graph.checkEnabledFromTransition(obj);
								graphComponent.refresh();
							}
						}
					}
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
		RightClickMenu menu = new RightClickMenu(this, pt.x, pt.y);
		menu.show(graphComponent, pt.x, pt.y);

		e.consume();
	}
	
	/**
     * 
     * @param name
     * @param action
     * @return a new Action bound to the specified string name
     */
    public Action bind(String name, final Action action)
    {
        return bind(name, action, null);
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
	    //edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CURVE);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");

	    graph.getStylesheet().setDefaultEdgeStyle(edge);
	}
	
	public final mxGraphComponent getGraphComponent() {
	    return graphComponent;
	}
	
	

	public static void main(String[] args)
	{
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(400, 400);
		frame.setVisible(true);
		
//		HelloWorld frame2 = new HelloWorld();
//		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		//frame.setSize(400, 400);
//		frame2.setVisible(true);
	}

}
