package petri;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.view.mxGraph;

public class PetriGraph extends mxGraph{
	
	private Document xmlDocument;
	
	public PetriGraph(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	
	// Overrides method to disallow edge label editing
	@Override
	public boolean isCellEditable(Object cell)
	{
//		if (cell instanceof mxCell)
//		{
//			Object value = ((mxCell) cell).getValue();
//
//			if (value instanceof Element)
//			{
//				Element elt = (Element) value;
//
//				if (elt.getTagName().equalsIgnoreCase("place"))
//				{
//					return true;
//				}
//				
				if (getModel().isEdge(cell)) return true;
//			}
//		}
		return false;
	}
	
	@Override
	public boolean isCellSelectable(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Element)
			{
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{
					if (getCellGeometry(cell).isRelative())
						return false;
				}
				
				//if (getModel().isEdge(cell)) return true;
			}
		}
		return super.isCellSelectable(cell);
	}
	
	public boolean isCellFoldable(Object cell, boolean collapse)
	{
		return false;
	}
	
	public boolean isCellConnectable(Object cell) {
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Element)
			{
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{
					if (getCellGeometry(cell).isRelative())
						return false;
				}
				
				//if (getModel().isEdge(cell)) return true;
			}
		}
		return super.isCellSelectable(cell);
	}
	
	@Override
	public String convertValueToString(Object cell)
	{
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Element)
			{
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{
					if (getCellGeometry(cell).isRelative()) { // Capacity label
						String capacity = elt.getAttribute("capacity");
						return "k = " + capacity;
					} else {
					String tokens = elt.getAttribute("tokens");

					    return tokens;
					}
				}
				else if (elt.getTagName().equalsIgnoreCase("arc"))
				{
					return elt.getAttribute("weight");
				}

			}
		}

		return super.convertValueToString(cell);
	}
	
	// Overrides method to store a cell label in the model
	public void cellLabelChanged(Object cell, Object newValue,
			boolean autoSize)
	{
		if (cell instanceof mxCell && newValue != null)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Node)
			{
				String label = newValue.toString().trim();
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{

					try {
						int val = Integer.parseInt(label);
						if (!getCellGeometry(cell).isRelative()) { // Capacity label
							if (val < 0) return;
						} else {
							if (val < -1) return;
						}
					} catch (Exception e) {
						return;
					}

					// Clones the value for correct undo/redo
					//elt = (Element) elt.cloneNode(true);
					
					if (!getCellGeometry(cell).isRelative()) { // Capacity label
						elt.setAttribute("tokens", label);
					} else {
						elt.setAttribute("capacity", label);	
					}

					newValue = elt;
										
				}
				
				if (elt.getTagName().equalsIgnoreCase("arc"))
				{
					try {
						int weight = Integer.parseInt(label);
						if (weight < 1
								) return;
					} catch (Exception e) {
						return;
					}

					// Clones the value for correct undo/redo
					elt = (Element) elt.cloneNode(true);

					elt.setAttribute("weight", label);

					newValue = elt;
				}
			}
		}

		super.cellLabelChanged(cell, newValue, autoSize);
		
		// Update active transitions
		if (cell instanceof mxCell && newValue != null)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Node)
			{
				String label = newValue.toString().trim();
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{
					if (!getCellGeometry(cell).isRelative()) { // Capacity label
						checkEnabledFromPlace(cell);
						
					} else {
						checkEnabledFromPlace(((mxCell)cell).getParent());
					}
					
				} else if (elt.getTagName().equalsIgnoreCase("arc"))
				{
					checkEnabledFromEdge(cell);
				}
			}
		}
	}
	
	public String validateEdge(Object edge, Object source, Object target)
	{
	    if (source != null && source instanceof mxCell
	            && target != null && target instanceof mxCell) {
            Object sourceValue = ((mxCell)source).getValue();
            Object targetValue = ((mxCell)target).getValue();
            if (sourceValue instanceof Element 
                    && targetValue instanceof Element) {
                final Element sourceEl = (Element)sourceValue;
                final Element targetEl = (Element)targetValue;
                if (sourceEl.getTagName().equalsIgnoreCase("place")
                        && targetEl.getTagName().equalsIgnoreCase("transition")) {
                    return null;
                } else if (sourceEl.getTagName().equalsIgnoreCase("transition")
                        && targetEl.getTagName().equalsIgnoreCase("place")) {
                    return null;
                }
            }
	    }
	    return "";
	}
	
	public boolean fireTransition(Object obj)
	{
		if (!(obj instanceof mxCell))
		{
			return false;
		}
		mxCell t = (mxCell) obj;
		if (!isFirable(obj)) return false;
		Object[] edges = getAllEdges(new Object[] {obj});
		
		// Fire Transition
		for (Object o : edges) 
		{
			mxCell edge = (mxCell)o;
			if (t.equals(edge.getSource()))
			{
				mxCell out = (mxCell)edge.getTarget();
				Element value = ((Element)out.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				int arcWeight = 1;
				Object edgeValue = edge.getValue();
				if (edgeValue instanceof Element)
				{
					if (((Element)edgeValue).getAttribute("weight").length() > 0)
					{
						arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
					}
				}
				value.setAttribute("tokens", "" + (tokens +arcWeight));
			} else
			{
				mxCell in = (mxCell)edge.getSource();
				Element value = ((Element)in.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				int arcWeight = 1;
				Object edgeValue = edge.getValue();
				if (edgeValue instanceof Element)
				{
					if (((Element)edgeValue).getAttribute("weight").length() > 0)
					{
						arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
					}
				}
				value.setAttribute("tokens", "" + (tokens - arcWeight));
			}
		}
		return true;
	}
	
	public Object addPlace(int tokens, int capacity) {
        return addPlace(tokens, capacity, 0, 0);
    }
	
	public Object addPlace(int tokens, int capacity, int x, int y) {
	    Object cell;
		try {
			getModel().beginUpdate();
		
			Element place1 = xmlDocument.createElement("Place");
			place1.setAttribute("tokens", "" + tokens);
			place1.setAttribute("capacity", "" + capacity);
			cell = insertVertex(getDefaultParent(), null, place1, x, y,
				40, 40, "PLACE");
			mxGeometry geo1 = new mxGeometry(0, 1, 40,
					20);
			geo1.setRelative(true);
			mxCell capLabel = new mxCell(place1, geo1,
					"CAPACITY");
			capLabel.setVertex(true);
			addCell(capLabel, cell);
		} finally
		{
			getModel().endUpdate();
		}
		return cell;
	}
	
	public Object addTransition() {
        return addTransition(0, 0);
    }
	
	public Object addTransition(int x, int y) {
	    Object cell;
		try {
			getModel().beginUpdate();
		
		Element transition1 = xmlDocument.createElement("Transition");
		cell = insertVertex(getDefaultParent(), null, transition1, x, y,
				40, 40, "TRANSITION");
		} finally
		{
			getModel().endUpdate();
		}
		return cell;
	}
	
	public Object createEdge(Object parent, String id, Object value,
			Object source, Object target, String style)
	{
		if (! (value instanceof Element)) {
			Element arc = xmlDocument.createElement("Arc");
			arc.setAttribute("weight", "1");
			value = arc;
		}

		return super.createEdge(parent, id, value, source, target, "");
	}
	
	public void checkEnabledTransitions() {
	    Object[] cells = getChildVertices(getDefaultParent());
	    Object[] edges = getAllEdges(cells);
	    
	    for (Object o : cells) {
	        checkEnabledTransition(o);
	    }
	    
	    for (Object edge : edges ) {
	    	checkEnabledEdge(edge);
	    }
	}
	
	/*
	 * Checks the enabled status of adjacent transitions to a place
	 */
	public void checkEnabledFromPlace(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Element) {
                Element elt = (Element) value;
                if (elt.getTagName().equalsIgnoreCase("place")) {
                	mxCell place = (mxCell) o;
                	
                	// Get adjacent transitions
                	List<Object> transitions = new ArrayList<Object>();
                	for (int i = 0; i < place.getEdgeCount(); i++) {
                		// Updated connected edges as well
                		checkEnabledEdge(place.getEdgeAt(i));
                		
                		Object transition = getVertexFromEdge(place.getEdgeAt(i), true);
                		if (transition != null)
                			transitions.add(transition);
                	}
                	
                	// Check adjacent transitions
                	for (Object transition : transitions) {
                		checkEnabledTransition(transition);
                	}
                }
			}
		}
	}
	/*
	 * Checks the enabled status of transitions affected by firing a transition
	 */
	public void checkEnabledFromTransition(Object o) {
		Set<Object> transitions = new HashSet<Object>();
		if (o instanceof mxCell) {
			mxCell t = (mxCell) o;
			Object value = t.getValue();
			if (value instanceof Element) {
                Element elt = (Element) value;
                if (elt.getTagName().equalsIgnoreCase("transition")) {
                	// Add self
                	transitions.add(o);
                	
                	// Get adjacent places
                	for (int i = 0; i < t.getEdgeCount(); i++) {
                		// Update connected edge styles as well
                		checkEnabledEdge(t.getEdgeAt(i));
                		
                		mxCell place = (mxCell) getVertexFromEdge(t.getEdgeAt(i),false);
                		if (place != null) {
                			// Get transitions connected to adjacent places
                			for (int j = 0; j < place.getEdgeCount(); j++) {
                				// Update connected edge styles as well
                        		checkEnabledEdge(place.getEdgeAt(j));
                				Object transition = getVertexFromEdge(place.getEdgeAt(j),true);
                				if (transition != null)
                					transitions.add(transition);
                			}
                		}	
                	}
                	
                	// Check affected transitions
                	for (Object transition : transitions) {
                		checkEnabledTransition(transition);
                	}
                }
			}
		}
	}
	
	/*
	 * Checks the enabled status of adjacent transitions to an arc
	 */
	public void checkEnabledFromEdge(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Element) {
                Element elt = (Element) value;
                if (elt.getTagName().equalsIgnoreCase("arc")) {
                	mxCell edge = (mxCell) o;
                	Object transition = getVertexFromEdge(o, true);
                	checkEnabledTransition(transition);
                	checkEnabledEdge(o);
                }
			}
		}
	}
	
	public void checkEnabledTransition(Object o) {
	    if (o instanceof mxCell) {
            Object value = ((mxCell) o).getValue();

            if (value instanceof Element)
            {
                Element elt = (Element) value;

                if (elt.getTagName().equalsIgnoreCase("transition"))
                {
                    if (isFirable(o)) {
                        setCellStyle("TRANSITION;ACTIVETRANSITION", new Object[] {o});
                    } else {
                        setCellStyle("TRANSITION", new Object[] {o});
                    }
                }
            }
        }
	}
	
	/*
	 * Checks whether an edge is enabled and adds or removes the enabled edge style as appropriate
	 * This is based off the current state 
	 */
	public void checkEnabledEdge(Object o) {
		if (o instanceof mxCell) {
			Object value = ((mxCell) o).getValue();
			if (value instanceof Element)
            {
                Element elt = (Element) value;

                if (elt.getTagName().equalsIgnoreCase("arc"))
                {
                	if (isFirableArc(o))
            		{
                		setCellStyle("ACTIVETRANSITION", new Object[] {o});
            		} else {
                        setCellStyle(null, new Object[] {o});
                    }
                }
            }
		}
		
	}
	
	public boolean isFirable(Object obj) {
	    
	    mxCell t;
	    
	    if (obj instanceof mxCell) {
	        t = (mxCell) obj;
	        Object value = ((mxCell) obj).getValue();

            if (value instanceof Element)
            {
                Element elt = (Element) value;

                if (elt.getTagName().equalsIgnoreCase("transition"))
                {
                    
                } else {
                    return false;
                }

            } else {
                return false;
            }
	    } else {
	        return false;
	    }
	    
	    Object[] edges = getAllEdges(new Object[] {obj});
	    // Can't fire if not connected
	    if (edges.length == 0) return false;
        
        // Check if valid
        for (Object o : edges) 
        {
            if (!isFirableArc(o))
            	return false;
        }
        
        
        return true;
	    
	}
	
	public boolean isFirableArc(Object o) {
		if (o instanceof mxCell) {
			mxCell edge = (mxCell)o;

			Object e = ((mxCell) o).getValue();

			if (e instanceof Element)
			{
				Element elt = (Element) e;

				if (elt.getTagName().equalsIgnoreCase("arc"))
				{
					mxCell t = (mxCell) getVertexFromEdge(o, true);

					if (t.equals(edge.getSource()))
					{
						// Outbound arc
						mxCell out = (mxCell)edge.getTarget();
						Element value = ((Element)out.getValue());
						int tokens = Integer.parseInt(value.getAttribute("tokens"));
						int capacity = Integer.parseInt(value.getAttribute("capacity"));
						if (capacity == -1) return true;  // Infinite capacity
						int arcWeight = 1;
						Object edgeValue = edge.getValue();
						if (edgeValue instanceof Element)
						{
							if (((Element)edgeValue).getAttribute("weight").length() > 0)
							{
								arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
							}
						}
						return capacity >= (arcWeight + tokens);
					} else
					{
						mxCell in = (mxCell)edge.getSource();
						Element value = ((Element)in.getValue());
						int tokens = Integer.parseInt(value.getAttribute("tokens"));
						int arcWeight = 1;
						Object edgeValue = edge.getValue();
						if (edgeValue instanceof Element)
						{
							if (((Element)edgeValue).getAttribute("weight").length() > 0)
							{
								arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
							}
						}
						return (tokens - arcWeight) >= 0;
					}
				}
			}
		}
		return false;
	}

	public Object getVertexFromEdge(Object o, boolean transition) {
		String tagName = transition ? "transition" : "place";
		if (o != null && o instanceof mxCell) {
			mxCell edge = (mxCell)o;
			mxCell source = (mxCell) edge.getSource();
			mxCell target = (mxCell) edge.getTarget();
			
			// transition
			if (source != null && 
					((Element)source.getValue()).getTagName().equalsIgnoreCase(tagName)) {
				return source;
			} else if (target != null && 
					((Element)target.getValue()).getTagName().equalsIgnoreCase(tagName)) {
				return target;
			} else {
				return null;
			}
		}
		return null;
	}
	
	public Map<String, Integer> getPlaceTokens() {
		mxGraphModel model = (mxGraphModel) getModel();
		Map<String, Object> cells = model.getCells();
		Map<String, Integer> tokenMap = new TreeMap<String, Integer>();
		for (String id : cells.keySet()) {
			mxCell cell = (mxCell)cells.get(id);
			Object value = cell.getValue();
			if (value instanceof Element) {
				Element el = (Element)value;
				if (el.getTagName().equalsIgnoreCase("place") && !getCellGeometry(cell).isRelative()) {
					int tokens = Integer.parseInt(el.getAttribute("tokens"));
					tokenMap.put(id, tokens);
				}
			}
		}
		System.out.println(tokenMap.toString());
		return tokenMap;
	}
	
	public void setPlaceTokens(Map<String, Integer> tokenMap) {
		mxGraphModel model = (mxGraphModel) getModel();
		for (String id : tokenMap.keySet()) {
			mxCell cell = (mxCell)model.getCell(id);
			if (cell != null) {
				Object value = cell.getValue();
				if (value instanceof Element) {
					Element el = (Element)value;
					if (el.getTagName().equalsIgnoreCase("place")) {
						el.setAttribute("tokens", "" + tokenMap.get(id));
					}
				}
			}
		}
	}
	

}
