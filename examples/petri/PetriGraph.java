package petri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mxgraph.model.mxCell;
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
		if (cell instanceof mxCell)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Element)
			{
				Element elt = (Element) value;

				if (elt.getTagName().equalsIgnoreCase("place"))
				{
					return true;
				}
				
				if (getModel().isEdge(cell)) return true;
			}
		}
		return false;
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
					String tokens = elt.getAttribute("tokens");

					return tokens;
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
						int tokens = Integer.parseInt(label);
						if (tokens < 0) return;
					} catch (Exception e) {
						return;
					}

					// Clones the value for correct undo/redo
					elt = (Element) elt.cloneNode(true);

					elt.setAttribute("tokens", label);

					newValue = elt;
				}
				
				if (elt.getTagName().equalsIgnoreCase("arc"))
				{
					try {
						int weight = Integer.parseInt(label);
						if (weight < 0
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
	}
	
	public void fireTransition(Object obj)
	{
		if (!(obj instanceof mxCell))
		{
			return;
		}
		mxCell t = (mxCell) obj;
		System.out.println(t.getEdgeCount());
		Object[] edges = getAllEdges(new Object[] {obj});
		
		// Check if valid
		for (Object o : edges) 
		{
			mxCell edge = (mxCell)o;
			System.out.println(o);
			if (t.equals(edge.getSource()))
			{
				System.out.println("Is source");
				mxCell out = (mxCell)edge.getTarget();
				Element value = ((Element)out.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				int capacity = Integer.parseInt(value.getAttribute("capacity"));
				if (capacity == -1) continue;
				System.out.println("yee");
				int arcWeight = 1;
				Object edgeValue = edge.getValue();
				if (edgeValue instanceof Element)
				{
					if (((Element)edgeValue).getAttribute("weight").length() > 0)
					{
						arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
					}
				}
				if (capacity < arcWeight + tokens) return;
			} else
			{
				System.out.println("Is destination");
				mxCell in = (mxCell)edge.getSource();
				Element value = ((Element)in.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				System.out.println("yee");
				int arcWeight = 1;
				Object edgeValue = edge.getValue();
				if (edgeValue instanceof Element)
				{
					if (((Element)edgeValue).getAttribute("weight").length() > 0)
					{
						arcWeight = Integer.parseInt(((Element)edgeValue).getAttribute("weight"));
					}
				}
				if (tokens - arcWeight < 0) return;
			}
		}
		
		// Fire Transition
		for (Object o : edges) 
		{
			mxCell edge = (mxCell)o;
			System.out.println(o);
			if (t.equals(edge.getSource()))
			{
				System.out.println("Is source");
				mxCell out = (mxCell)edge.getTarget();
				Element value = ((Element)out.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				System.out.println("yee");
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
				System.out.println("Is destination");
				mxCell in = (mxCell)edge.getSource();
				Element value = ((Element)in.getValue());
				int tokens = Integer.parseInt(value.getAttribute("tokens"));
				System.out.println("yee");
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
	}
	
	public void addPlace(int tokens, int capacity) {
		try {
			getModel().beginUpdate();
		
		Element place1 = xmlDocument.createElement("Place");
		place1.setAttribute("tokens", "" + tokens);
		place1.setAttribute("capacity", "" + capacity);
		insertVertex(getDefaultParent(), null, place1, 0, 0,
				40, 40, "PLACE");
		} finally
		{
			getModel().endUpdate();
		}
	}
	
	public void addTransition() {
		try {
			getModel().beginUpdate();
		
		Element transition1 = xmlDocument.createElement("Transition");
		insertVertex(getDefaultParent(), null, transition1, 0, 0,
				40, 40, "TRANSITION");
		} finally
		{
			getModel().endUpdate();
		}
	}
	
	public Object createEdge(Object parent, String id, Object value,
			Object source, Object target, String style)
	{
		Element arc = xmlDocument.createElement("Arc");
		arc.setAttribute("weight", "1");

		return super.createEdge(parent, id, arc, source, target, "ARC");
	}

}
