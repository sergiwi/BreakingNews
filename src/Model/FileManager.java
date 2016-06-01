package Model;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;

/**
 * Übernimmt die Funktionen rund um die Datei-Behandlung.
 * 
 * @author mkanis
 **/
public class FileManager {

	// Konstruktor
	public FileManager() {

	}

	/**
	 * Liest eine XML-Datei und erstellt daraus ein JSONObject.
	 * 
	 * @param file
	 *            : Vollständiger Pfad zur XML-Datei
	 * @return Gibt das umgewandelte JSON-Object als String zurück.
	 * 		Gibt "FEHLER" zurück, wenn ein Fehler aufgetreten ist.
	 */
	public String readXML(String file) {
		Document doc = null;
		File f = new File(file);
		JSONObject jsonObj = new JSONObject();

		try {
			// Das Dokument erstellen
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(f);
			XMLOutputter fmt = new XMLOutputter();

			// komplettes Dokument ausgeben
			//fmt.output(doc, System.out);
			
			//Wurzelelement holen
			Element rootElement = doc.getRootElement(); //root ist <rss>
			//Erstes Child-Element holen
			Element channelElement = rootElement.getChild("channel"); //<channel>
			//Erstes Child vom Channel-Element holen
			Element itemElement = channelElement.getChild("item"); //<item>
			
			//JSONObject füllen
			jsonObj.append("SourceTitle", channelElement.getChild("title").getValue());
			jsonObj.append("Category", getCategory(file));
			jsonObj.append("Title", itemElement.getChild("title").getValue());
			jsonObj.append("Description", itemElement.getChild("description").getValue());
			jsonObj.append("ExtractedText", itemElement.getChild("ExtractedText").getValue());
			jsonObj.append("SourceLink", itemElement.getChild("link").getValue());
			jsonObj.append("Date", convertDate(itemElement.getChild("pubDate").getValue()));

			return jsonObj.toString(4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "FEHLER";
	}

	/**
	 * Liest eine Datei zeilenweise aus und übergibt sie den Methoden readXML
	 * und convertToJSON.
	 * 
	 * @param fullPath
	 *            : Vollständiger Pfad zur Datei, die gelesen werden soll
	 */
	public void readFile(String fullPath) {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileInputStream(new File(fullPath)));

			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				// Pro Zeile die Datei auslesen und in JSON umwandeln
				String xmlContent = readXML(line);
				System.out.println("JSON aus der XML: \n" + xmlContent);
				
				System.out.println();

				// -------------------------------------------------------------------------------------------//
				// ToDo: Wohin mit dem JSON-String
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sc.close();
	}

	/**
	 * Bestimmt anhand des Pfades, zu welcher Kategorie die Nachricht gehört
	 * 
	 * @param xmlPath
	 *            : Vollständiger Pfad zur XML-Datei (Die Pfade aus den Dateien
	 *            im Ordner viewernotification).
	 * @return Gibt die Kategorie als String zurück.
	 */
	public String getCategory(String xmlPath) {
		return Paths.get(xmlPath).subpath(6, 7).toString();
	}
	
	/**
	 * Konvertiert das Datum aus der XML in ein besser lesbares Format
	 * 
	 * @param date : unformatiertes Datum aus der XML
	 * @return Gibt das formatierte Datum zurück
	 */
	private String convertDate(String date){
		//Ausgangsformat erzeugen
		DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		Date dateToConvert = null;
		
		try {
			dateToConvert = (Date)formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//Mit der Klasse Calendar das Endformat festlegen
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToConvert);
		String formatedDate = cal.get(Calendar.DATE) + "." + (cal.get(Calendar.MONTH) + 1) + "." + 
									cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE);
		return formatedDate;
	}
}

//ToDo: JSONObject: Bei "Description" unnötige Sachen rausfiltern
//ToDo: JSONObject: Bei "Datum" vorangegangene "Nullen" (0) auffüllen. 
//		1:7 wird zu 01:07 Uhr -- 1.9.2016 wird zu 01.09.2016





// Wahrscheinlich nicht benötigt
/**
 * Konvertiert einen XML-konformen Text in eine gültige JSON-Datei. Benutzt die
 * java-json.jar als externe Library.
 * 
 * @param xml
 *            : der Inhalt einer XML-Datei als String
 * @return Gibt den String als konvertiertes JSON-Format zurück.
 */
/*
 * public String convertToJSON(String xml) { int pretty_print_indent_factor = 4;
 * String jsonPrettyPrintString = ""; try { JSONObject xmlJSONObject =
 * XML.toJSONObject(xml); jsonPrettyPrintString = xmlJSONObject
 * .toString(pretty_print_indent_factor); } catch (Exception e) {
 * e.printStackTrace(); } return jsonPrettyPrintString; }
 */

