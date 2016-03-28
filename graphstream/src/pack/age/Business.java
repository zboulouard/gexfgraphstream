package pack.age;

import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.graphicGraph.GraphPosLengthUtils;
import org.graphstream.ui.view.Viewer;

public class Business {
	
	private Graph g;
	
	public Business() {
		g = new SingleGraph("g");
	}
	
	public void applyFForce() throws Exception {
		Collection<Node> nodes = g.getNodeSet();
		Double tol = 0.01;
		Double moy = moyenne(g);
		Double dist = 0.0;
		Viewer viewer = g.display(true);
		ProxyPipe pipe = viewer.newViewerPipe();
		pipe.addAttributeSink(g);
		System.out.println("Tol * Moy : " + tol * moy);
		Double fforce = 0.0;
		while (Math.abs(fforce) < tol * moy) {
			Thread.sleep(100);
			pipe.pump();
			for (int i = 0; i < nodes.size(); i++) {
				Node n1 = g.getNode(i);
				for (int j = i + 1; j < nodes.size(); j++) {
					Node n2 = g.getNode(j);
					fforce = FForce(n1, n2);
					setCoords(n1, fforce);
					setCoords(n2, fforce);
					
				}
			}
		}
		//viewer.enableAutoLayout();
	}
	
	private Double FForce(Node n1, Node n2) {
		Double d0 = moyenne(g);
		Double d = distance(n1, n2);
		Integer w1 = n1.getAttribute("weight");
		Integer w2 = n2.getAttribute("weight");
		Edge e = n1.getEdgeBetween(n2);
		Double fforce = 0.0;
		if (e != null) {
			Integer matrixValue = e.getAttribute("matrixValue");
			double b = (w1 * matrixValue) / (w1 + w2);
			fforce = -(d0 / d) + b * (d - d0) + b * Math.pow(((d / d0) - 1), 3);
		}
		System.out.println(fforce);
		return fforce;
	}
	
	private Double comparePositions(Node oldNode, Node newNode) {
		Double diff = 0.0;
		Double[] oldCoords = getCoords(oldNode);
		Double oldX = oldCoords[0];
		Double oldY = oldCoords[1];
		Double[] newCoords = getCoords(newNode);
		Double newX = newCoords[0];
		Double newY = newCoords[1];
		diff = Math.sqrt((Math.pow(oldX - newX, 2) + Math.pow(oldY - newY, 2)));
		return diff;
	}
	
	private Double[] getCoords(Node n) {
		Double[] coord = {0.0, 0.0};
		Object[] attributes = n.getAttribute("xyz");
		Double x = (Double) attributes[0];
		Double y = (Double) attributes[1];
		coord[0] = x;
		coord[1] = y;
		return coord;
	}
	
	private void setCoords(Node n, Double value) {
		Object[] attributes = n.getAttribute("xyz");
		Double x = (Double) attributes[0];
		Double y = (Double) attributes[1];
		x = x + value;
		y = y + value;
		n.setAttribute("xyz", (Object) x, (Object) y);
	}
	
	private String showCoords(Node n) {
		Double[] coords = getCoords(n);
		String coordinates = coords[0] + " , " +coords[1];
		System.out.println(coordinates);
		return coordinates;
	}
	
	private void setAttributes() {
		Random random = new Random();
		random = random == null ? new Random(
				System.currentTimeMillis()) : random;
		Collection<Node> nodes = g.getNodeSet();
		for (Node node : nodes) {
			node.setAttribute("xyz", random.nextDouble()*100, random.nextDouble()*100);
			node.addAttribute("weight", 1);
		}
		Collection<Edge> edges = g.getEdgeSet();
		for (Edge edge : edges) {
			edge.addAttribute("matrixValue", 1);
		}
	}
	
	private Double moyenne(Graph graph) {
		Double dist = 0.0;
		Double moy = 0.0;
		Collection<Node> nodeList = graph.getNodeSet();
		for(int i=0; i<nodeList.size(); i++) {
			for(int j=i+1; j<nodeList.size(); j++) {
				dist += distance(graph.getNode(i), graph.getNode(j));
			}
		}
		Collection<Edge> edgeList = g.getEdgeSet();
		moy = dist / edgeList.size();
		return moy;
	}
	
	// Distance entre deux noeuds
	private Double distance(Node n1, Node n2) {
		Double d = 0.0;
		Edge e = n1.getEdgeBetween(n2);
		if(e!=null) {
			try {
				d = GraphPosLengthUtils.edgeLength(e);
			} catch (Exception e2) {}
		}
		return d;
	}

	public void graphFromFile(String filePath) throws Exception {
		FileSource fs = FileSourceFactory.sourceFor(filePath);
		g.addAttribute("ui.stylesheet", "node { text-visibility-mode: hidden; }");
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");
		fs.addSink(g);

		try {
			fs.begin(filePath);

			while (fs.nextEvents()) {
				// Optionally some code here ...
			}
		} catch( IOException e) {
			e.printStackTrace();
		}

		try {
			fs.end();
		} catch( IOException e) {
			e.printStackTrace();
		} finally {
			fs.removeSink(g);
		}
		//g.display(false);
//		g.display();
		setAttributes();
		
	}
	
}
