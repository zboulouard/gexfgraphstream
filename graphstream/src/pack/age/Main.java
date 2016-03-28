package pack.age;

public class Main {

	public static void main(String[] args) throws Exception {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		//String filePath = "src/pack/age/sources/small-world-1.gexf";
		String filePath = "src/pack/age/sources/small-world-2.gexf";
		//String filePath = "src/pack/age/sources/small-world-3.gexf";
		//String filePath = "src/pack/age/sources/facebook-2.gexf";
		Business business = new Business();
		business.graphFromFile(filePath);
		business.applyFForce();
	}
	

}
