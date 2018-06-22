package com.mxgraph.examples.swing;

import java.awt.Color;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.JFrame;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707712944901661771L;

	public HelloWorld()
	{
		super("Hello, World!");
		
		Document xmlDocument = mxDomUtils.createDocument();
		Element placeNode = xmlDocument.createElement("Place");
		Element transitionNode = xmlDocument.createElement("Transition");

		mxGraph graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> placeStyle = new Hashtable<String, Object>();
		placeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
		placeStyle.put(mxConstants.STYLE_OPACITY, 100);
		placeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		placeStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		placeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("PLACE", placeStyle);
		
		Hashtable<String, Object> transitionStyle = new Hashtable<String, Object>();
		transitionStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		transitionStyle.put(mxConstants.STYLE_OPACITY, 100);
		transitionStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		transitionStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		transitionStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("TRANSITION", transitionStyle);
		
		Hashtable<String, Object> arcStyle = new Hashtable<String, Object>();
		arcStyle.put(mxConstants.STYLE_OPACITY, 100);
		arcStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		arcStyle.put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
		arcStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
		stylesheet.putCellStyle("ARC", arcStyle);
		
		
		try
		{
			Object v1 = graph.insertVertex(parent, null, placeNode, 20, 20, 40,
					40, "PLACE");
			Object v2 = graph.insertVertex(parent, null, transitionNode, 240, 150,
					40, 40, "TRANSITION");
			Object v3 = graph.insertVertex(parent, null, transitionNode, 140, 150,
					40, 40, "TRANSITION");
			graph.insertEdge(parent, null, null, v1, v2, "ARC");
			graph.setCellsResizable(false);
			graph.setMultigraph(false);
			graph.setAllowDanglingEdges(false);
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
		
		
		

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		graphComponent.setGridVisible(true);
		// Sets the background to white
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);
		
		
		new mxRubberband(graphComponent);
		new mxKeyboardHandler(graphComponent);
	}

	public static void main(String[] args)
	{
		HelloWorld frame = new HelloWorld();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 320);
		frame.setVisible(true);
	}

}
