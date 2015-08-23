<?php
header("Access-Control-Allow-Origin *");
$servername="localhost";
$username="root";
$password="qazwsx123";
$dbname="oscad";

$conn = new mysqli($servername, $username, $password, $dbname);
        $sql="SELECT * FROM alert ORDER BY slno DESC";
        $result=$conn->query($sql);
$ret= "{";
$i=1;

while($row=$result->fetch_assoc()){
   $ret=$ret."\"".$i++."\":".json_encode( $row).",";
    
};
$ret=rtrim($ret, ",");
echo $ret.",\"length\":\"".($i-1)."\"}";
?>
