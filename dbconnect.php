URL url = new URL("URL zur PHP Datei");
// Verbindung aufbauen.
URLConnection conn = url.openConnection();
conn.setDoOutput(true);
BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
StringBuilder sb = new StringBuilder();
String line = null;
while ((line = reader.readLine()) != null) {
   sb.append(line);
}
