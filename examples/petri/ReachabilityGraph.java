package petri;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;

import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
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
			setCellStyle(node.getStyle() + ";CURRENT", new Object[] {node});
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
	                	Object[] edges = graph.getEdgesBetween(node1, node, true);
	                	if (edges.length > 0) {
	                		System.out.println(Arrays.toString(edges));
	                		mxCell edge = (mxCell) edges[0];
	                		String label = (String) edge.getValue();
	                		label += "; t" + graph.getCellMarkingName(vertex);
	                		edge.setValue(label);
	                	} else {
	                		insertEdge(getDefaultParent(), null, "t" + graph.getCellMarkingName(vertex), node1, node, null);
	                	}
	                	graph.setPlaceTokens(state);
	                	j++;
	                }
				}
			}
		}
		setCellStyle(node1.getStyle() + ";COMPLETE", new Object[] {node1});
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
		
		Hashtable<String, Object> currentStyle = new Hashtable<String, Object>();
        currentStyle.put(mxConstants.STYLE_FILLCOLOR, "#00FF00");
        stylesheet.putCellStyle("CURRENT", currentStyle);
		
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
	
	public void setActiveState(Object obj) {
	    if (obj instanceof mxCell) {
	        mxCell vertex = (mxCell)obj;
	        if (vertex.getValue() instanceof Map<?,?>) {
	            Map<String, Integer> currentState = graph.getPlaceTokens();
	            mxCell currentCell = nodeMap.get(currentState);
	            String style = currentCell.getStyle().replaceFirst(";CURRENT", "");
	            setCellStyle(style, new Object[] {currentCell});
	            setCellStyle(vertex.getStyle() + ";CURRENT", new Object[] {vertex});
	            Map<String, Integer> state = (Map<String, Integer>)vertex.getValue();
	            graph.setPlaceTokens(state);
	            graph.checkEnabledTransitions();
	            graph.refresh();
	            
	        }
	    }
	    
	}
	
	@Override
	public String convertValueToString(Object cell)
	{
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Map<?,?>)
			{	
				Map<String, Integer> map = (Map<String, Integer>)value;
				StringBuilder sb = new StringBuilder();
				for (String id : map.keySet()) {
					Object vertex = ((mxGraphModel)graph.getModel()).getCell(id);
					sb.append('p');
					sb.append(graph.getCellMarkingName(vertex));
					sb.append(": ");
					sb.append(map.get(id));
					sb.append('\n');
				}
				return sb.toString();
			}
		}

		return super.convertValueToString(cell);
	}
}
