package Model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Der Observer überwacht den Ordner viewernotification, in dem neue Nachrichten
 * vom Crawler gecrawlt werden. Änderungen in diesem Ordner können so
 * nachverfolgt werden.
 * 
 * @author mkanis
 **/
public class Observer {
	private FileManager fileManager;

	// Konstruktor
	public Observer() {
		fileManager = new FileManager();
	}

	/**
	 * Stellt die Funktionalität des Überwachens bereit. Hier kann mithilfe von
	 * Java WatchService Änderungen im Ordner feststellen und darauf reagieren.
	 * 
	 * @param folder
	 *            : Der Ordner, der überwacht werden soll.
	 */
	public void observe(String folder) {
		// Ordner, der überwacht werden soll
		Path folderPath = Paths.get(folder);

		try {
			WatchService ws = FileSystems.getDefault().newWatchService();

			// Events registern, die der WatchService überwachen soll
			folderPath.register(ws, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			System.out.println("--ÜBERWACHUNG WIRD GESTARTET--\n");

			// Endlosschleife erzeugen
			while (true) {
				// Mit WatchKeys pollen

				/*
				 * Ein WatchKey kann in einem Intervall pollen Bsp: WatchKey key
				 * = ws.poll(5, TimeUnit.SECONDS);
				 * 
				 * WatchKeys können aber auch automatisiert laufen; Es wird
				 * gewartet bis ein Key signalisiert wird Bsp: WatchKey key =
				 * ws.take();
				 */
				WatchKey key = ws.take();
				/*
				 * Ein Watchkey kann 3 Zustände haben: Ready: WatchKey ist
				 * bereit events zu akzeptieren Signaled: Mindestens ein Event,
				 * das vorgekommen ist und gequeued wurde Invalid: Key ist nicht
				 * länger valide
				 */

				// Anstehende Events von den Watchkeys auslesen - DEQUEUE
				WatchEvent.Kind<?> kind = null;
				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					kind = watchEvent.kind();

					switch (kind.name()) {
					// -----------------CREATE WIRD EVTL. NICHT
					// BENÖTIGT---------------
					case "ENTRY_CREATE":
						// Neuer Path wurde erzeugt
						// Path newPath = ((WatchEvent<Path>) watchEvent)
						// .context();
						// System.out.println("Neuer Path wurde erzeugt: "
						// + newPath);
						break;
					case "ENTRY_MODIFY":
						// Path hat sich geändert
						Path changedPath = ((WatchEvent<Path>) watchEvent)
								.context();
						System.out.println("Datei hat sich geändert: "
								+ changedPath);
						handleEventModify(Paths.get(folderPath.toString(),
								changedPath.toString()));
						break;
					case "ENTRY_DELETE":
						// Aus dem Pfad wurde etwas gelöscht
						Path deletedPath = ((WatchEvent<Path>) watchEvent)
								.context();
						System.out.println("Ein Path wurde gelöscht: "
								+ deletedPath);
						break;
					default:
						// In Default kommt er nur, wenn man oben ein Intervall
						// einstellt
						System.out
								.println("Am Ordner hat sich nichts geändert.");
					}

					// Overflow weiß ich noch nicht ob das unbedingt notwendig
					// ist
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue; // LOOP
					}
				}
				// WatchKey zurücksetzen
				key.reset();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wird aufgerufen, wenn beim WatchService das Event ENTRY_MODIFY eintritt.
	 * 
	 * @param path
	 *            : Pfad der Datei, die geändert wurde.
	 */
	private void handleEventModify(Path path) {
		fileManager.readFile(path.toString());
	}
}

/*
 * ZUSATZ: ES kann Zum Beispiel überprüft werden, ob es sich bei einer neuen
 * Datei um eine Textdatei usw. handelt: Code:
 * Path.Files.probeContentType(FILE).equals("text/plain"))
 */

