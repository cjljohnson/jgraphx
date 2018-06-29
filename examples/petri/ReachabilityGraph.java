package petri;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;

import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class ReachabilityGraph extends mxGraph{

	private Map<Map<String, Integer>, mxCell> nodeMap;
	private int size;
	private PetriGraph graph;
	int i;
	
	public ReachabilityGraph(PetriGraph graph, int size) {
		nodeMap = new HashMap<Map<String, Integer>, mxCell>();
		this.size = size;
		this.graph = graph;
		i = 0;
		initStyles();
		
		setCellsEditable(false);
		
		calcReachability();
		JFrame frame = new JFrame("Reachability");
	}
	
	public boolean isCellSelectable(Object cell)
	{
	    if (getModel().isEdge(cell))
	    {
	        return false;
	    }

	    return super.isCellSelectable(cell);
	}
	
	
	public void calcReachability() {
		Queue<Map<String, Integer>> queue = new ArrayDeque<Map<String, Integer>>();
		i = 0;
		
		Map<String, Integer> s1 = graph.getPlaceTokens();
		queue.add(s1);
		
		try {
			getModel().beginUpdate();
			
			mxCell node = (mxCell)insertVertex(getDefaultParent(), null, s1, i * 50, i * 50,
					40, 40, "NODE");
			
			nodeMap.put(s1, node);
		
		
			while (queue.size() > 0 && i < size) {
				calcNodeReachability(queue.poll(), queue);
				i++;
			}
			graph.setPlaceTokens(s1);
		} finally {
			getModel().endUpdate();
		}
	}
	
	private void calcNodeReachability(Map<String, Integer> state, Queue<Map<String, Integer>> queue) {
		graph.setPlaceTokens(state);
		mxCell node1 = nodeMap.get(state);
		int j = 0;
		for (Object vertex : graph.getChildVertices(graph.getDefaultParent())) {
			if (vertex instanceof mxCell) {
				Object value = ((mxCell) vertex).getValue();
				if (value instanceof Element) {
					Element elt = (Element) value;
	                if (elt.getTagName().equalsIgnoreCase("transition") 
	                		&& graph.isFirable(vertex)) {
	                	graph.fireTransition(vertex);
	                	Map<String, Integer> newState = graph.getPlaceTokens();
	                	mxCell node = nodeMap.get(newState);
	                	if (node == null) {
	                		node = (mxCell)insertVertex(getDefaultParent(), null, newState, i * 50, j * 50,
	            					40, 40, "NODE");
	                		nodeMap.put(newState, node);
	                		queue.add(newState);
	                	}
	                	System.out.println(i + " " + j);
	                	insertEdge(getDefaultParent(), null, "" + ((mxCell) vertex).getId(), node1, node, null);
	                	graph.setPlaceTokens(state);
	                	j++;
	                }
				}
			}
		}
		setCellStyle("NODE;COMPLETE", new Object[] {node1});
	}
	
	private void initStyles() {
		mxStylesheet stylesheet = getStylesheet();
		Hashtable<String, Object> nodeStyle = new Hashtable<String, Object>();
		nodeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		nodeStyle.put(mxConstants.STYLE_OPACITY, 100);
		nodeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		nodeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		nodeStyle.put(mxConstants.STYLE_STROKECOLOR, "#FF0000");
		nodeStyle.put(mxConstants.STYLE_STROKEWIDTH, 2);
		//nodeStyle.put(mxConstants.STYLE_NOLABEL, true);
		stylesheet.putCellStyle("NODE", nodeStyle);
		
		Hashtable<String, Object> completeStyle = new Hashtable<String, Object>();
		completeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("COMPLETE", completeStyle);
		
		Map<String, Object> edge = new HashMap<String, Object>();
	    edge.put(mxConstants.STYLE_ROUNDED, true);
	    edge.put(mxConstants.STYLE_ORTHOGONAL, false);
	    edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
	    edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
	    edge.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);
	    edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_LEFT);
	    edge.put(mxConstants.STYLE_STROKECOLOR, "#000000");
	    edge.put(mxConstants.STYLE_STROKEWIDTH, 2);
	    edge.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		getStylesheet().setDefaultEdgeStyle(edge);
	}
}
