java-webserver
==============

Betriebssysteme - Belegarbeit

	 __      __      ___.     _________                                
	/  \    /  \ ____\_ |__  /   _____/ ______________  __ ___________ 
	\   \/\/   // __ \| __ \ \_____  \_/ __ \_  __ \  \/ // __ \_  __ \
	 \        /\  ___/| \_\ \/        \  ___/|  | \/\   /\  ___/|  | \/
	  \__/\  /  \___  >___  /_______  /\___  >__|    \_/  \___  >__|   
	       \/       \/    \/        \/     \/                 \/       
							___.          
							\_ |__ ___.__.  
							 | __ <   |  | 
							 | \_\ \___  | 
							 |___  / ____|
							     \/\/      
	    .___                                 .___                       
	  __| _/______ ____   _____    ____    __| _/   ______ ____   ____  
	 / __ |/  ___// ___\  \__  \  /    \  / __ |   /  ___//    \_/ __ \ 
	/ /_/ |\___ \\  \___   / __ \|   |  \/ /_/ |   \___ \|   |  \  ___/ 
	\____ /____  >\___  > (____  /___|  /\____ |  /____  >___|  /\___  >
	     \/    \/     \/       \/     \/      \/       \/     \/     \/ 

=============================================================================
==== INDEX
=============================================================================
1. MUST HAVE (FEATURES)
2. ADDITIONAL FEATURES
3. DOCUMENTATION

=============================================================================
==== 1. MUST HAVE (FEATURES)
=============================================================================
- Bei jedem Request wird geprüft ob das gewünschte Dokument im Verzeichnis 
  DocDir existiert
- Im Verzeichnis LogDir wird jede Aktion in einer LogDatei vermerkt
- Log Informationen: Datum/Zeit, Dokument URL, Nachfrage URL, Status Code
- Jeden Tag ist eine neue Log Datei anzulegen
- Einträge in die Log Datei in synchronisierter Form
- Pfad von LogDir und DocDir ist in dem Server als Konstante festzulegen
-! Dokumentation:
	• Aufgabenstellung:
	• Konzeption: (Funktionsumfang)
	• Entwicklerhandbuch: (Realisierungsbeschreibung) Dateiformate, 
	  Datenmodelle, Klassenbeschreibungen, JavaDoc
	• Nutzerhandbuch:
	• Administratorhandbuch: Alles was ein Administrator zum Betrieb der 
	  Anwendung wissen muss.
-! Web Server muss auf der Turku lauffähig sein
-! Es ist ein Ordner mit der ausgedruckten Dokumentation und eine CD mit der 
   Dokumentation und dem Quellcode abzugeben.

=============================================================================
==== 2. ADDITIONAL FEATURES
=============================================================================
[ok]- server configuration file (as singelton)
[ok]- adjustable server configuration file (cmd/cli)
[ok]- support other files (gifs, jpgs, pngs, pdfs, …)
[ok]- log as singelton
[ok]- log - log levels
- log - zip old log files (automatic)
- implement a cache
- DOS attacks?
- .htaccess
- custom 404 page
- http://huschi.net/20_91_de.html
- execute php, perl, ruby, python, …

=============================================================================
==== DOCUMENTATION
=============================================================================
See JavaDoc!

=============================================================================

                ..oo$00ooo..                    ..ooo00$oo..
              .o$$$$$$$$$'                          '$$$$$$$$$o.
           .o$$$$$$$$$"             .   .              "$$$$$$$$$o.
         .o$$$$$$$$$$~             /$   $\              ~$$$$$$$$$$o.
       .{$$$$$$$$$$$.              $\___/$               .$$$$$$$$$$$}.
      o$$$$$$$$$$$$8              .$$$$$$$.               8$$$$$$$$$$$$o
     $$$$$$$$$$$$$$$              $$$$$$$$$               $$$$$$$$$$$$$$$
    o$$$$$$$$$$$$$$$.             o$$$$$$$o              .$$$$$$$$$$$$$$$o
    $$$$$$$$$$$$$$$$$.           o{$$$$$$$}o            .$$$$$$$$$$$$$$$$$
   ^$$$$$$$$$$$$$$$$$$.         J$$$$$$$$$$$L          .$$$$$$$$$$$$$$$$$$^
   !$$$$$$$$$$$$$$$$$$$$oo..oo$$$$$$$$$$$$$$$$$oo..oo$$$$$$$$$$$$$$$$$$$$$!
   {$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$}
   6$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$?
   '$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$'
    o$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$o
     $$$$$$$$$$$$$$;'~`^Y$$$7^''o$$$$$$$$$$$o''^Y$$$7^`~';$$$$$$$$$$$$$$$
     '$$$$$$$$$$$'       `$'    `'$$$$$$$$$'     `$'       '$$$$$$$$$$$$'
      !$$$$$$$$$7         !       '$$$$$$$'       !         V$$$$$$$$$!
       ^o$$$$$$!                   '$$$$$'                   !$$$$$$o^
         ^$$$$$"                    $$$$$                    "$$$$$^
           'o$$$`                   ^$$$'                   '$$$o'
             ~$$$.                   $$$.                  .$$$~
               '$;.                  `$'                  .;$'
                  '.                  !                  .`
