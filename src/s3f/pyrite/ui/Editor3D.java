/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s3f.pyrite.ui;

import com.falstad.circuit.CircuitElm;
import com.falstad.circuit.CircuitNode;
import com.falstad.circuit.CircuitNodeLink;
import com.falstad.circuit.CircuitSimulator;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import quickp3d.DrawingPanel3D.Scene3D;
import s3f.core.plugin.Data;
import s3f.core.plugin.Plugabble;
import s3f.core.project.Editor;
import s3f.core.project.Element;
import s3f.core.project.editormanager.TextFile;
import s3f.core.ui.tab.TabProperty;
import s3f.pyrite.core.Circuit;
import s3f.pyrite.core.Component;
import s3f.pyrite.core.Connection;
import s3f.pyrite.types.Position3DFile;
import s3f.pyrite.ui.components.MyLogicInputElm;
import s3f.pyrite.ui.components.MyLogicOutputElm;
import s3f.pyrite.ui.components.SubCircuitElm;
import s3f.pyrite.ui.drawing3d.Circuit3DEditPanel;

/**
 *
 * @author anderson
 */
public class Editor3D extends DockingWindowAdapter implements Editor {

//    private static final ImageIcon ICON = new ImageIcon(ModularCircuitEditor.class.getResource("/resources/icons/fugue/block.png"));
    private final Data data;
    private Position3DFile textFile;
    private Circuit3DEditPanel drawingPanel;
    private Scene3D applet;
    private float[] eye = null;

    JRootPane p = new JRootPane();

    public Editor3D() {
        data = new Data("editorTab", "s3f.core.code", "Editor Tab");
        createApplet();
        TabProperty.put(data, "Editor", null, "Editor de código", p);
    }

    private void createApplet() {
        p.getContentPane().removeAll();
        if (drawingPanel == null) {
            drawingPanel = new Circuit3DEditPanel(null);
        } else {
            eye = drawingPanel.getEye();
            drawingPanel = new Circuit3DEditPanel(drawingPanel.getCircuit());
            drawingPanel.setEye(eye);
        }
        applet = drawingPanel.getApplet();
        applet.init();
        p.getContentPane().add(applet);
    }

    @Override
    public void setContent(Element content) {
        if (content instanceof Position3DFile) {
            textFile = (Position3DFile) content;
        }

        if (content instanceof TextFile) {
            TextFile textFile = (TextFile) content;
            Circuit circuit = parseString(textFile.getText());
            drawingPanel.setCircuit(circuit);
            showGraph(createGraph(circuit), true);
            data.setProperty(TabProperty.TITLE, content.getName());
            data.setProperty(TabProperty.ICON, content.getIcon());
        }
    }

    private static CircuitSimulator createDummyCS(String text) {
        JApplet window = new JApplet();
        CircuitSimulator cs = new CircuitSimulator();
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        cs.updateCircuit(null);
        return cs;
    }

    public static CircuitSimulator createCS(String text) {
        JApplet window = new JApplet();
        CircuitSimulator cs = new CircuitSimulator();
        cs.setContainer(window.getContentPane());
        cs.startCircuitText = text;
        {//TODO
            cs.register(MyLogicInputElm.class);
            cs.register(MyLogicOutputElm.class);
            cs.register(SubCircuitElm.class);
        }
        cs.init();
        window.setJMenuBar(cs.getGUI().createGUI(true));
        cs.posInit();
        cs.analyzeCircuit();
        cs.updateCircuit(null);
        cs.updateCircuit(null);
        JFrame f = new JFrame();
        f.setContentPane(window);
        f.setSize(new Dimension(400, 400));
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        return cs;
    }

    public static void main(String[] args) {
        String hard = "$ 1 5.0E-6 10.20027730826997 54.0 5.0 50.0\n"
                + "t 80 176 80 256 0 1 0.587480887764175 -3.7727338911607884 100.0\n"
                + "t 96 256 144 256 0 1 0.6027189712514504 0.6397852210750364 100.0\n"
                + "L 64 256 0 256 0 1 false 5.0 0.0\n"
                + "r 80 176 80 96 0 4700.0\n"
                + "R 80 96 0 96 0 0 40.0 5.0 0.0 0.0 0.5\n"
                + "w 80 96 144 96 0\n"
                + "t 240 176 240 256 0 1 0.5908812846401537 0.5911300429119931 100.0\n"
                + "t 256 256 304 256 0 1 -0.03681749155174671 2.4875827183941954E-4 100.0\n"
                + "r 240 96 240 176 0 4700.0\n"
                + "w 144 96 240 96 0\n"
                + "w 240 96 304 96 0\n"
                + "r 304 96 304 176 0 1000.0\n"
                + "w 304 176 368 176 0\n"
                + "M 368 176 416 176 0 2.5\n"
                + "L 224 256 176 256 0 0 false 5.0 0.0\n"
                + "r 144 96 144 176 0 1000.0\n"
                + "w 144 176 144 208 0\n"
                + "w 304 176 304 240 0\n"
                + "w 144 208 368 208 0\n"
                + "w 368 208 368 176 0\n"
                + "w 144 208 144 240 0\n"
                + "g 144 272 144 304 0\n"
                + "g 304 272 304 304 0\n";

        String def = "$ 1 5.0E-6 10.20027730826997 52 5.0 50.0\n"
                + "t 80 208 128 208 0 1 0.6381941100809847 0.6478969866591134 100.0\n"
                + "t 176 208 224 208 0 1 -0.009702876570569624 7.55914423854251E-12 100.0\n"
                + "t 272 208 320 208 0 1 -0.009702876570569624 7.55914423854251E-12 100.0\n"
                + "w 128 160 128 192 0\n"
                + "w 224 160 224 192 0\n"
                + "w 128 160 224 160 0\n"
                + "w 224 160 320 160 0\n"
                + "w 320 160 320 192 0\n"
                + "r 80 208 80 272 0 470.0\n"
                + "r 176 208 176 272 0 470.0\n"
                + "r 272 208 272 272 0 470.0\n"
                + "+ 80 272 80 304 0 1 false 3.6 0.0 a\n"
                + "+ 176 272 176 304 0 0 false 3.6 0.0 b\n"
                + "+ 272 272 272 304 0 0 false 3.6 0.0 c\n"
                + "w 128 224 128 336 0\n"
                + "w 224 224 224 336 0\n"
                + "w 320 224 320 336 0\n"
                + "w 320 336 224 336 0\n"
                + "w 224 336 128 336 0\n"
                + "r 320 160 320 64 0 640.0\n"
                + "g 320 336 320 368 0\n"
                + "R 320 64 288 64 0 0 40.0 3.6 0.0 0.0 0.5\n"
                + "- 352 160 400 160 0 2.5 s\n"
                + "w 320 160 352 160 0\n";

        String simple = "$ 1 5.0E-6 10.20027730826997 52.0 5.0 50.0\n"
                + "t 224 192 272 192 0 1 -3.5999999999125003 2.3499999999562566E-11 100.0\n"
                + "w 272 144 272 176 0\n"
                + "r 224 192 224 256 0 470.0\n"
                + "+ 224 256 224 288 0 0 false 3.6 0.0 c\n"
                + "w 272 208 272 320 0\n"
                + "r 272 144 272 48 0 640.0\n"
                + "g 272 320 272 352 0\n"
                + "R 272 48 240 48 0 0 40.0 3.6 0.0 0.0 0.5\n"
                + "- 304 144 352 144 0 2.5 s\n"
                + "w 272 144 304 144 0\n";
        Circuit c = createCircuit2(createCS(def));
        showGraph(createGraph(c), true);
        String s = dumpCircuit(c, createDummyCS(""));
        createCS(s);
    }

    private static void testJoin() {
        Circuit cir = new Circuit();

        Component a = new Component();
        Component b = new Component();
        Component c = new Component();
        Component d = new Component();

        Connection x = a.createConnection(b);
        Connection y = a.createConnection(c);
        Connection z = a.createConnection(d);

        x.setTerminalA("" + 0);
        y.setTerminalA("" + 1);
        z.setTerminalA("" + 2);

        cir.addComponent(a);
        cir.addComponent(b);
        cir.addComponent(c);
        cir.addComponent(d);

        cir.addConnection(x);
        cir.addConnection(y);
        cir.addConnection(z);

        Component b2 = new Component();
        Component c2 = new Component();
        Component d2 = new Component();

        Connection x2 = b2.createConnection(b);
        Connection y2 = c2.createConnection(c);
        Connection z2 = d2.createConnection(d);

        cir.addComponent(b2);
        cir.addComponent(c2);
        cir.addComponent(d2);

        cir.addConnection(x2);
        cir.addConnection(y2);
        cir.addConnection(z2);

        cir.removeConnection(a.appendAndConsume(b));
        cir.removeComponent(b);

        showGraph(createGraph(cir), true);
    }

    private static Circuit createCircuit2(CircuitSimulator cs) {
        Circuit cir = new Circuit();

        HashMap<Point, Component> allNodes = new HashMap<>();
        ArrayList<ArrayList<Component>> w = new ArrayList<>();

        //create one node for each terminal of each component
        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            ArrayList<Component> s = new ArrayList<>();
            for (int j = 0; j < elm.getPostCount(); j++) {
                Point p = elm.getPost(j);
                if (!allNodes.containsKey(p)) {
                    Component c = new Component();
                    s.add(c);
                    cir.addComponent(c);
                    allNodes.put(p, c);
                }
            }
            w.add(s);
        }

        //wire everthing up
        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            for (int j = 0; j < elm.getPostCount(); j++) {
                Point p = elm.getPost(j);
                Component c = allNodes.get(p);
                Component c2 = allNodes.get(elm.getPost(0));
                if (c2 != null && c2 != c) {
                    Connection con = c2.createConnection(c);
                    con.setSubComponent(elm.dump());
                    if (elm.getPostCount() > 2) {
                        con.setTerminalA("" + j);
                    } else {
                        con.setTerminalA("" + 0);
                    }
                    cir.addConnection(con);
                }
            }
        }

        //defines what's node and what's edge
        ArrayList<CircuitElm> nodes = new ArrayList<>();
        ArrayList<CircuitElm> edges = new ArrayList<>();
        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            if (elm.getPostCount() == 2) {
                edges.add(elm);
            } else {
                nodes.add(elm);
            }
        }

        //join all the terminal-nodes created before of each component into a single component 
        for (CircuitElm elm : nodes) {
            Component c = null;
            for (int i = 0; i < elm.getPostCount(); i++) {
                Component t = allNodes.get(elm.getPost(i));
                if (c == null) {
                    c = t;
                } else {
                    cir.removeConnection(c.appendAndConsume(t));
                    cir.removeComponent(t);
                }
            }
            c.setData(elm.dump());
            c.setName(elm.getClass().getSimpleName());
        }
        if (true) {
            return cir;
        }

        System.out.println(allNodes.size());

        for (int i = 0; i < cs.elmListSize(); i++) {
            CircuitElm elm = cs.getElm(i);
            for (int j = 0; j < elm.getPostCount(); j++) {
                Point p = elm.getPost(j);
                Component c = allNodes.get(p);
                for (int k = 0; k < ((CircuitElm) c.getData()).getPostCount(); k++) {
                    Component c2 = allNodes.get(elm.getPost(k));
                    if (c2 != null) {
                        Connection con = c2.createConnection(c);
                        cir.addConnection(con);
                    }
                }
            }
        }

//        ArrayList<CircuitElm> nodes = new ArrayList<>();
//        ArrayList<CircuitElm> edges = new ArrayList<>();
//        for (int i = 0; i < cs.elmListSize(); i++) {
//            CircuitElm elm = cs.getElm(i);
//            if (elm.getPostCount() == 2) {
//                edges.add(elm);
//            } else {
//                nodes.add(elm);
//            }
//        }
        for (ArrayList<Component> s : w) {
            Component c = null;
            for (Component t : s) {
                if (c == null) {
                    c = t;
                } else {
                    c.appendAndConsume(t);
                    cir.removeComponent(t);
                }
            }
            //c.setData(elm.dump());
        }

//        for (CircuitElm elm : nodes) {
//            Component c = null;
//            for (int i = 0; i < elm.getPostCount(); i++) {
//                Component t = allNodes.get(elm.getPost(i));
//                if (c == null) {
//                    c = t;
//                } else {
//                    c.appendAndConsume(t);
//                    cir.removeComponent(t);
//                }
//            }
//            c.setData(elm.dump());
//        }
//
//        n:
//        for (int i = 0; i < cs.nodeListSize(); i++) {
//            CircuitNode cn = cs.getCircuitNode(i);
//            if (cn.internal) {
//                continue;
//            }
//
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                if (elm.getPostCount() != 2) {
//                    continue n;
//                }
//            }
//
//            Component c = null;
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                Component t = s.get(w.indexOf(elm));
//                if (t == null) {
//                    System.out.println(elm);
//                    continue;
//                }
//
//                if (c == null) {
//                    c = t;
//                } else {
//                    c.appendAndConsume(t);
//                    cir.removeComponent(t);
//                }
//            }
//        }
//        n:
//        for (int i = 0; i < cs.nodeListSize(); i++) {
//            CircuitNode cn = cs.getCircuitNode(i);
//            if (cn.internal) {
//                continue;
//            }
//
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                if (elm.getPostCount() != 2){
//                    continue n;
//                }
//            }
//            
//            for (CircuitNodeLink link : cn.links) {
//                CircuitElm elm = link.elm;
//                
//            }
//        }
        if (true) {
            return cir;
        }

        HashMap<Point, Object[]> nodeComponents = new HashMap<>();
        for (CircuitElm elm : nodes) {
            Component c = new Component();
            c.setData(elm.dump());
            cir.addComponent(c);
            for (int i = 0; i < elm.getPostCount(); i++) {
                nodeComponents.put(elm.getPost(i), new Object[]{c, elm, i});
            }
        }

        for (int i = 0; i < cs.nodeListSize(); i++) {
            CircuitNode circuitNode = cs.getCircuitNode(i);
            if (circuitNode.internal) {
                continue;
            }
            Component c = new Component();
            for (int j = 0; j < circuitNode.links.size(); j++) {
                CircuitElm elm = circuitNode.links.get(j).elm;
                if (elm.getPostCount() != 2) {
                    for (int k = 0; k < elm.getPostCount(); k++) {
                        Object[] v = nodeComponents.get(elm.getPost(k));
                        if (v != null) {
                            Component c2 = (Component) v[0];
                            String t2 = ((Integer) v[2]).toString();
                            Connection con = new Connection(c, "", c2, t2, "");
                            cir.addConnection(con);
                            cir.addComponent(c);
                        }
                    }

                }
            }
        }

        for (CircuitElm edge : edges) {
            Object[] v1 = nodeComponents.get(edge.getPost(0));
            Object[] v2 = nodeComponents.get(edge.getPost(1));

//            if (v1 == null) {
//                ArrayList<CircuitElm> list = map.get(edge.getPost(0));
//                if (list != null) {
//                    for (CircuitElm elm : list) {
//                        if (elm.getPostCount() != 2) {
//                            Component c = new Component();
//                            c.setData(elm.dump());
//                            cir.addComponent(c);
//                            for (int i = 0; i < elm.getPostCount(); i++) {
//                                Point p = elm.getPost(i);
//                                Object[] o = new Object[]{c, elm, i};
//                                nodeComponents.put(p, o);
//                                if (edge.getPost(0) == p) {
//                                    v1 = o;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (v2 == null) {
//                ArrayList<CircuitElm> list = map.get(edge.getPost(1));
//                if (list != null) {
//                    for (CircuitElm elm : list) {
//                        if (elm.getPostCount() != 2) {
//                            Component c = new Component();
//                            c.setData(elm.dump());
//                            cir.addComponent(c);
//                            for (int i = 0; i < elm.getPostCount(); i++) {
//                                Point p = elm.getPost(i);
//                                Object[] o = new Object[]{c, elm, i};
//                                nodeComponents.put(p, o);
//                                if (edge.getPost(0) == p) {
//                                    v2 = o;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
            if (v1 != null && v2 != null) {
                Component c1 = (Component) v1[0];
                Component c2 = (Component) v2[0];

                String t1 = ((Integer) v1[2]).toString();
                String t2 = ((Integer) v1[2]).toString();

                Connection c = new Connection(c1, t1, c2, t2, edge.dump());
                cir.addConnection(c);
            } else {

            }
        }

        return cir;
    }

    private static Circuit createCircuit(CircuitSimulator cs) {
        Circuit cir = new Circuit();

        HashMap<Point, Component> jointMap = new HashMap<>();
        HashMap<CircuitElm, Component> compMap = new HashMap<>();
        HashMap<CircuitElm, ArrayList<CircuitNode>> kMap = new HashMap<>();

        for (int i = 0; i < cs.nodeListSize(); i++) {
            CircuitNode node = cs.getCircuitNode(i);
            if (node.internal || node.links.isEmpty()) {
                System.out.println("*");
                continue;
            }
//            
            ArrayList<CircuitElm> ts = new ArrayList<>();
            for (CircuitNodeLink link : node.links) {
                //if (link.elm.getPostCount() > 2) {
                if (link.elm.getPostCount() != 2) {
                    CircuitElm t = link.elm;
                    ts.add(t);
                    if (!kMap.containsKey(t)) {
                        kMap.put(t, new ArrayList<CircuitNode>());
                    }
                    if (!kMap.get(t).contains(node)) {
                        kMap.get(t).add(node);
                    }
//                    break;
                }
            }

            if (ts.isEmpty()) {
                //junta simples
                Component j = new Component();
                //j.name = "j";

                cir.addComponent(j);
                jointMap.put(new Point(node.x, node.y), j);
                for (CircuitNodeLink link : node.links) {
                    CircuitElm e = link.elm;
                    if (e.getPostCount() == 2) {
                        Component c1 = jointMap.get(e.getPost(0));
                        Component c2 = jointMap.get(e.getPost(1));
                        System.out.println("***" + e.dump());
                        if (c1 != null && c2 != null) {
                            Connection con = c1.createConnection(c2);
                            con.whut = e;
                            con.setSubComponent(e.dump());
                            cir.addConnection(con);
                        }
                    }
                }
            } else {
                for (CircuitElm t : ts) {
                    if (!compMap.containsKey(t)) {
                        //transistor e outros
                        Component j = new Component();
                        j.whut = t;
                        String n = t.dump();
                        j.setName(t.dump());
                        j.setData(t.dump());
                        if (n.startsWith("-")) {
                            String name = t.dump();
                            for (int k = 0; k < 7; k++) {
                                name = name.substring(name.indexOf(' ') + 1);
                            }
                            j.setName(name);
                            cir.addComponent(j, Circuit.OUTPUT);
                        } else if (n.startsWith("+")) {
                            String name = t.dump();
                            for (int k = 0; k < 10; k++) {
                                name = name.substring(name.indexOf(' ') + 1);
                            }
                            j.setName(name);
                            cir.addComponent(j, Circuit.INPUT);
                        } else {
                            cir.addComponent(j);
                        }
                        compMap.put(t, j);
                        if (t.getPostCount() == 1 && !jointMap.containsValue(j)) {
                            jointMap.put(new Point(node.x, node.y), j);
                        }
                    }
                }
            }
        }

        for (Map.Entry<CircuitElm, Component> entry : compMap.entrySet()) {
            CircuitElm t = entry.getKey();
            for (int i = 0; i < t.getPostCount(); i++) {
                Point p = t.getPost(i);
                String terminal = "";
                switch (i) {
                    case 0:
                        terminal = "b";
                        break;
                    case 1:
                        terminal = "c";
                        break;
                    case 2:
                        terminal = "e";
                        break;
                }
                boolean ok = false;
                for (CircuitNode node : kMap.get(t)) {
                    for (CircuitNodeLink link : node.links) {
                        CircuitElm e = link.elm;
                        if (e.getPostCount() == 2) {
                            Component c = null;
                            if (p.equals(e.getPost(0))) {
                                c = jointMap.get(e.getPost(1));
                            } else if (p.equals(e.getPost(1))) {
                                c = jointMap.get(e.getPost(0));
                            } else {
//                                System.out.println("Hfail: " + t + " " + i);
                                continue;
                            }
                            if (c != null) {
                                Connection con = entry.getValue().createConnection(c);
                                con.whut = e;
                                con.setTerminalA(terminal);
                                con.setSubComponent(e.dump());
                                cir.addComponent(entry.getValue());
                                cir.addComponent(c);
                                cir.addConnection(con);
                                System.out.println(entry.getValue().getName());
                                System.out.println(c.getName());
                                System.out.println("=");
                                entry.getValue().setName(t.getClass().getSimpleName());
                                ok = true;
                            } else {
//                                System.out.println("fail: " + e);
                            }
                        }
                    }
                }
                if (!ok) {
                    System.out.println("Hfail: " + t + " " + i + " " + kMap.get(t).size());
                }
            }
        }
        return cir;
    }

    private static Circuit parseString(String text) {
        return createCircuit2(createCS(text));
    }

    private static Graph<String, String> createGraph(Circuit circuit) {
        SparseMultigraph<String, String> graph = new SparseMultigraph<>();

        //adiciona vertices
        for (Component v : circuit.getComponents()) {
            graph.addVertex(v.toString());
        }

        //adciona arestas
        for (Connection c : circuit.getConnections()) {
            graph.addEdge(c.toString(), c.getA().toString(), c.getB().toString());
        }
        return graph;
    }

    private static void showGraph(Graph<String, String> graph, boolean show) {

        KKLayout<String, String> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(400, 400));

        if (show) {
            VisualizationViewer<String, String> vv = new VisualizationViewer<>(layout);
            vv.setPreferredSize(new Dimension(400, 400));

            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller() {
//                @Override
//                public String transform(Object v) {
//                    String s = v.toString();
//                    return s.substring(0, s.indexOf('['));
//                }
            });

            vv.setEdgeToolTipTransformer(new ToStringLabeller() {
                @Override
                public String transform(Object v) {
                    String s = v.toString();
                    return s;
                }
            });

            vv.setVertexToolTipTransformer(new ToStringLabeller() {
                @Override
                public String transform(Object v) {
                    String s = v.toString();
                    return s;
                }
            });
            DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
            gm.setMode(ModalGraphMouse.Mode.PICKING);
            vv.setGraphMouse(gm);
            JFrame frame = new JFrame("Interactive Graph 2D View - DUMP");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(vv);
            frame.pack();
            frame.setVisible(true);
        }
    }

//    public static void main(String[] args) {
//        String s = dumpCircuit(parseString(Position3DFile.DUMMY), createDummyCS(""));
//        System.out.println(">>\n" + s);
//        createCS(s);
//    }
    private static String dumpCircuit(Circuit circuit, CircuitSimulator cs) {
        StringBuilder sb = new StringBuilder();

        Graph<String, String> graph = createGraph(circuit);
        KKLayout<String, String> layout = new KKLayout(graph);//new FRLayout(graph);
        layout.setSize(new Dimension(400, 400));

        HashMap<Component, Point> pointMap = new HashMap<>();

        for (Component v : circuit.getComponents()) {
            Point2D p = layout.transform(v.toString());
            int x = ((int) p.getX() / 15) * 16;
            int y = ((int) p.getY() / 15) * 16;
            pointMap.put(v, new Point(x, y));
        }

        sb.append("$ 1 5.0E-6 10 54 5.0\n");
        HashMap<Connection, Point> postA = new HashMap<>();
        HashMap<Connection, Point> postB = new HashMap<>();

//        System.out.println("nodes: " + circuit.getComponents().size() + " " + graph.getVertexCount());
//        System.out.println("edges: " + circuit.getConnections().size() + " " + graph.getEdgeCount());
        for (Component a : circuit.getComponents()) {
            Point p = pointMap.get(a);
            if (a.getData() != null) {
                CircuitElm comp = CircuitSimulator.createElm(dumpString(p.x, p.y, p.x + 32, p.y, "" + a.getData()), cs);

                sb.append(comp.dump());
                sb.append('\n');

                for (Connection c : a.getConnections()) {
                    int post = 0;
                    String t = c.getTerminal(a);
                    switch (t) {
                        case "":
                            break;
                        case "b":
                            post = 0;
                            break;
                        case "c":
                            post = 1;
                            break;
                        case "e":
                            post = 2;
                            break;
                        default:
                            post = Integer.parseInt(t);
                    }

                    if (c.getA() == a) {
                        postA.put(c, comp.getPost(post));
                    } else {
                        postB.put(c, comp.getPost(post));
                    }
                }
            } else {
                for (Connection c : a.getConnections()) {
                    if (c.getA() == a) {
                        postA.put(c, p);
                    } else {
                        postB.put(c, p);
                    }
                }
            }
        }

        for (Connection c : circuit.getConnections()) {
            Point p1 = postA.get(c);
            Point p2 = postB.get(c);

            if (p1 == null || p2 == null) {
                continue;
            }

            sb.append(dumpString(p1.x, p1.y, p2.x, p2.y, "" + c.getSubComponent()));
            sb.append('\n');
        }

        return sb.toString();
    }

    private static String dumpString(int x1, int y1, int x2, int y2, String data) {
        StringBuilder sb = new StringBuilder();
        String type;
        String flags;
        if (data != null && data.length() > 8) {
//            System.out.println(data);
            type = data.substring(0, data.indexOf(' '));
            flags = data;
            for (int i = 0; i < 5; i++) {
                flags = flags.substring(flags.indexOf(' ') + 1);
            }
        } else {
//            System.out.println(data);
            type = "w";
            flags = "0";
        }
        sb.append(type).append(" ");
        sb.append(x1).append(" ");
        sb.append(y1).append(" ");
        sb.append(x2).append(" ");
        sb.append(y2).append(" ");
        sb.append(flags);
        return sb.toString();
    }

    @Override
    public Element getContent() {
        return textFile;
    }

    @Override
    public void update() {
        for (Object o : textFile.getExternalResources()) {
            if (o instanceof TextFile) {
                TextFile textFile = (TextFile) o;
                setContent(textFile);
            }
        }
    }

    @Override
    public void selected() {

    }

    @Override
    public void windowShown(DockingWindow dw) {
        createApplet();
    }

    @Override
    public void windowHidden(DockingWindow dw) {
        if (applet != null) {
            applet.stop();
        }
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void init() {

    }

    @Override
    public Plugabble createInstance() {
        return new Editor3D();
    }
}
