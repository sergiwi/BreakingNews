package Test;

import Model.FileManager;

/**
 * TESTKLASSE zum Testen der Methoden vom FileManager
 * 
 * @author mkanis
 */
public class TestFileManager {
	
	private String myViewerPath = "C:\\Users\\Lenovo\\Desktop\\Informatik\\Semester 4\\"
			+ "Praktikum Swe goebel\\PraktikumSE\\RSSArchive\\RSS\\viewernotification";

	private static FileManager fileManager;
//	private String viewernotPath = "D:\\BreakingNews\\Crawler\\RSSArchive\\RSS\\viewernotification";

	
	public static void main(String[] args) {
		fileManager =  new FileManager();
//		testGetCategory();
//		testXMLfileReader();
	}
	
//	private static void testGetCategory(){
//		System.out.println("**************TEST_GET_CATEGORY**************");
//		String filePath = "D:/BreakingNews/Crawler/RSSArchive/RSS/rssfilesgermany/de/science/WissenFAZNET/y2016/m5/d6/RSS-775075651.xml";
//		System.out.println(fileManager.getCategory(filePath));
//	}
	
	public String testXMLfileReader(){
		System.out.println("**************TEST_XMLFILE_READER**************");
		String file = "C:/Users/Lenovo/Desktop/Informatik/Semester 4/Praktikum Swe goebel/PraktikumSE/RSSArchive/RSS/rssfiles/germany/de/science/WissenFAZNET/y2016/m2/d26/RSS1081890068.xml";
		return fileManager.readXML(file);
	}

}
