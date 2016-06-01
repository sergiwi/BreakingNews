package Model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * Der Observer �berwacht den Ordner viewernotification, in dem neue Nachrichten
 * vom Crawler gecrawlt werden. �nderungen in diesem Ordner k�nnen so
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
	 * Stellt die Funktionalit�t des �berwachens bereit. Hier kann mithilfe von
	 * Java WatchService �nderungen im Ordner feststellen und darauf reagieren.
	 * 
	 * @param folder
	 *            : Der Ordner, der �berwacht werden soll.
	 */
	public void observe(String folder) {
		// Ordner, der �berwacht werden soll
		Path folderPath = Paths.get(folder);

		try {
			WatchService ws = FileSystems.getDefault().newWatchService();

			// Events registern, die der WatchService �berwachen soll
			folderPath.register(ws, StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			System.out.println("--�BERWACHUNG WIRD GESTARTET--\n");

			// Endlosschleife erzeugen
			while (true) {
				// Mit WatchKeys pollen

				/*
				 * Ein WatchKey kann in einem Intervall pollen Bsp: WatchKey key
				 * = ws.poll(5, TimeUnit.SECONDS);
				 * 
				 * WatchKeys k�nnen aber auch automatisiert laufen; Es wird
				 * gewartet bis ein Key signalisiert wird Bsp: WatchKey key =
				 * ws.take();
				 */
				WatchKey key = ws.take();
				/*
				 * Ein Watchkey kann 3 Zust�nde haben: Ready: WatchKey ist
				 * bereit events zu akzeptieren Signaled: Mindestens ein Event,
				 * das vorgekommen ist und gequeued wurde Invalid: Key ist nicht
				 * l�nger valide
				 */

				// Anstehende Events von den Watchkeys auslesen - DEQUEUE
				WatchEvent.Kind<?> kind = null;
				for (WatchEvent<?> watchEvent : key.pollEvents()) {
					kind = watchEvent.kind();

					switch (kind.name()) {
					// -----------------CREATE WIRD EVTL. NICHT
					// BEN�TIGT---------------
					case "ENTRY_CREATE":
						// Neuer Path wurde erzeugt
						// Path newPath = ((WatchEvent<Path>) watchEvent)
						// .context();
						// System.out.println("Neuer Path wurde erzeugt: "
						// + newPath);
						break;
					case "ENTRY_MODIFY":
						// Path hat sich ge�ndert
						Path changedPath = ((WatchEvent<Path>) watchEvent)
								.context();
						System.out.println("Datei hat sich ge�ndert: "
								+ changedPath);
						handleEventModify(Paths.get(folderPath.toString(),
								changedPath.toString()));
						break;
					case "ENTRY_DELETE":
						// Aus dem Pfad wurde etwas gel�scht
						Path deletedPath = ((WatchEvent<Path>) watchEvent)
								.context();
						System.out.println("Ein Path wurde gel�scht: "
								+ deletedPath);
						break;
					default:
						// In Default kommt er nur, wenn man oben ein Intervall
						// einstellt
						System.out
								.println("Am Ordner hat sich nichts ge�ndert.");
					}

					// Overflow wei� ich noch nicht ob das unbedingt notwendig
					// ist
					if (kind == StandardWatchEventKinds.OVERFLOW) {
						continue; // LOOP
					}
				}
				// WatchKey zur�cksetzen
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
	 *            : Pfad der Datei, die ge�ndert wurde.
	 */
	private void handleEventModify(Path path) {
		fileManager.readFile(path.toString());
	}
}

/*
 * ZUSATZ: ES kann Zum Beispiel �berpr�ft werden, ob es sich bei einer neuen
 * Datei um eine Textdatei usw. handelt: Code:
 * Path.Files.probeContentType(FILE).equals("text/plain"))
 */

