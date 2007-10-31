/*
 *  Copyright 2007 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package groovy.prefuse;

import groovy.util.BuilderSupport;
import java.util.Map;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
        
/**
 * Provides a Groovy Builder for the Prefuse Visualization Library
 * 
 * @author Craig MacKay
 */
public class PrefuseBuilder extends BuilderSupport {

    private Graph graph;
    private Visualization visualization;
    
    protected void setParent(Object parent, Object child) {
        if (parent instanceof Node && child instanceof Node) {
            graph.addEdge((Node)parent, (Node)child);
        }
    }

    protected Object createNode(Object name) {
        return createNode(name, null, null);
    }

    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value);
    }

    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null);
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        Object builderNode = null;
        if (name.equals("node")) {
            Node node = graph.addNode();
            node.setString("name", (String)value);
            builderNode = node;
        }
        if (name.equals("graph")) {
            graph = new Graph();
            graph.addColumn("name", String.class);
            visualization = new Visualization();
            visualization.add("graph", graph);
            LabelRenderer labelRenderer = new LabelRenderer("name");
            labelRenderer.setRoundedCorner(8, 8);
            visualization.setRendererFactory(new DefaultRendererFactory(labelRenderer));
            ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(190,190,255));
            ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
            ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
            ActionList color = new ActionList(Activity.INFINITY);
            color.add(fill);
            color.add(text);
            color.add(edges);
            ActionList layout = new ActionList(Activity.INFINITY);
            layout.add(new ForceDirectedLayout("graph", true));
            layout.add(new RepaintAction());
            visualization.putAction("color", color);
            visualization.putAction("layout", layout);
            Display display = new Display(visualization);
            display.setSize(720, 500);
            display.addControlListener(new DragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
            display.setHighQuality(true);
            visualization.run("color");
            visualization.run("layout");
            builderNode = display;
        }
        return builderNode;
    }

}
