//Datenbankverbindung aufbauen
$connection = mysqli_connect("Server","Benutzername","Passwort","Schema");
if (mysqli_connect_errno()) {
    echo mysql_errno($connection) . ": " . mysql_error($connection). "\n";
    die();
}
 
getAllPlants($connection);
 
//Liefert alle deutschen Pflanzennamen zurück.
function getAllPlants($connection){
  $sqlStmt = "SELECT * FROM Plants;";
  $result =  mysqli_query($connection,$sqlStmt);
  $data = array();
  if ($result = $connection->query($sqlStmt)) {
      while ($row = $result->fetch_assoc()) {
        $id = $row["id"];
        $de_name = $row["de_name"];
        array_push($data,array("ID"=> $id,"de_name"=>$de_name));  
      }
  // Das Objekt wieder freigeben.
  $result->free();
}
  closeConnection($connection);
  
  // Das Array durchlaufen und nur die deutschen Namen ausgeben.
  foreach ($data as $d){
    echo $d["de_name"];
    echo "|";
  }
  
  
}
 
//Verbindung schließen.
function closeConnection($connection){
  mysqli_close($connection);
}
?>
