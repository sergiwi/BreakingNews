package Test;

import Model.Observer;

/**
 * TESTKLASSE zum Testen der Methoden der Klasse Observer.java
 * 
 * @author mkanis
 */
public class TestObserver {

	private static Observer observer;
	
	public static void main(String[] args) {
		observer = new Observer();
		
		testObserve("D:\\BreakingNews\\Crawler\\RSSArchive\\RSS\\viewernotification");
	}
	
	private static void testObserve(String viewernotPath){
		observer.observe(viewernotPath);
	}

}
