README fuer das Projekt GameServer (Angepasst an Implementierung von Crazyhouse)

Dieses Projekt dient als Einstieg in die Implementierungsaufgabe (HA2 SWTPP). 

Die Aufgabe soll als Servlet auf einem Apache Tomcat Web-Server abgeliefert werden. In eclipse ist dies 
ein "dynamic web project". 

Im Folgenden wird ein Ueberblick ueber die schon vorgegebenen Ressourcen 
gegeben. Danach folgen noch ein paar Erklaerungen zur Funktionsweise des Servlets. Ausserdem sollten 
die Kommentare in den bestehenden Dateien beachtet werden. Hier sind auch noch einige Hinweise zu finden.

1) Projekt-Ueberblick
--------------------

---| GameServer(-Full oder Vorgabe-Ordner je nach Variante moeglich)
   Der uebergeordnete Projekt-Ordner. Hier befinden sich die Eclipse-Konfiguration und die Projekt-Dateien, 
   ausserdem natuerlich alle im Folgenden beschriebenen Ordner.
   
   ---| src
      Hier befinden sich alle reinen Java-Source-Code-Dateien, aufgeteilt in packets. In eclipse sind die 
      packets als Ganzes zu sehen (getrennt mit Punkten). Im Dateisystem sind das jeweils Unter-Ordner.
   
      ---| de.tuberlin.sese.swtpp.gameserver.control
         Packet fuer die Controller-Klassen des Klassenmodells.
         
      ---| de.tuberlin.sese.swtpp.gameserver.model
         Packet fuer die Entity-Klassen des akstrakten Klassenmodells, d.h. das eigentliche Datenmodell des 
         Servers ohne konkrete Spiel-Implementierung

      ---| de.tuberlin.sese.swtpp.gameserver.model.crazyhouse
         Packet fuer die Entity-Klassen des Klassenmodells, d.h. das Datenmodell des konkreten Spiels
         
      ---| de.tuberlin.sese.swtpp.gameserver.test.crazyhouse
         Hier befinden sich die jUnit Testcases und Testsuiten, die von euch anzupassen sind.
         
      ---| de.tuberlin.sese.swtpp.gameserver.swtpp.web
         Hier befindet sich die Klasse GameServerServlet, welche die zentrale Komponente in die Web-Anwendung auf Server-Seite ist. 
         Zu dem Servlet spaeter mehr.
         
   ---| build
      Hier landen die kompilierten Klassen
      
   ---| WebContent
      In diesem Ordner werden die Web-Ressourcen des Projekts gespeichert, die der Apache Tomcat Server dann wie 
      ein Web-Server zur Verfuegung stellt. Bilder, html-Seiten, css-Dateien sind also ueber eine URL erreichbar (je nach ProjektName
      z.B. http://localhost:8080/GameServerServlet/...) mit dem Pfad innerhalb dieses Ordners erreichbar. Wenn die Ressource
      innerhalb einer Page auf dem Tomcat verlinkt wird kann auch mit einem relativen Pfad gearbeitet werden. Z.B. <a href="meinbild.jpg">Bild</>
  
2) Implementierungsaufgabe

Wir haben die Web-Funktionalitaet in dem gegebenen Projekt schon erledigt. Die Interaktion mit den Requests ist also fuer alle Use-Cases bereits moeglich. Die GUI (HTML/Javascript) 
fuer den Client-Browser) ist auch bereits erledigt. Das Gleiche gilt fuer die Server-Verwaltung, das abstrakte Spiel und einen grossen Teil des konkreten Spiels. Fuer euch bleibt nur die Implementierung einiger 
Funktionen in der Klasse CrazyhouseGame und die dazugehoerigen Tests (Siehe Aufgabenblatt). Bestehender Code darf nicht geaendert werden. 
Zur Orientierung und Erklaerung der bereits erledigten Anteile dienen die beiden folgenden Kapitel.   
  
3) Das Servlet

Ein Servlet ist ein "Behaeltnis" f�r eine dynamische Web Anwendung, die auf einem Application Server wie dem Apache Tomcat 
ausgefuehrt werden kann. Kern davon ist die Servlet-Klasse (in unserem Fall GameServerServlet), die von der Klasse HttpServlet erbt. Sie 
ist im deployment descriptor (Konfiguration des Projekts) registriert. Hierdurch wird dem Apache 
Web Server signalisiert, dass er das Servlet fuer HTTP-requests bereit stellen soll. Alle Anfragen (HTTP requests) des
Benutzers an die URL des Servlets (http://localhost:8080/GameServer/GameServerServlet) werden an die Servlet-Klasse weitergeleitet.
Die Anfragen im Beispiel werden an die doGet()-Methode von GameServerServlet weitergeleitet.

Wichtig: 
Die Servlet-Klasse wird einmal vom Server instanziiert. Egal welcher Benutzer/welche Session/welcher Browser etc. - man landet immer im gleichen
Servlet-Objekt. Die Attribute sind also immer sichtbar. Sie eignen sich nur fuer globale Variablen und die Daten, die nicht vom aktuellen Zustand der
Session eines Benutzers abhaengen.

3) Die Client-Seite

Die Client-Seite besteht in diesem Projekt aus HTML-Dateien, deren dynamische Inhalte mit Javascript erstellt werden. Die Javascript-Funktionen senden Requests 
an das Servlet, welches wiederum mit den geforderten Daten antwortet soll. Kern der Anwendung sind die Bibliotheken von chessboardjs.com (angepasst an das jeweilige Spiel), welches uns das Spielbrett liefert.