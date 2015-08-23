<?php
$servername="localhost";
$username="root";
$password="qazwsx123";
$dbname="oscad";
$img=$_POST['Image'];
echo $img;
//$img = str_replace('data:image/;base64,', '', $img);
//$img = str_replace(' ', '+', $img);
$data = base64_decode($img);
$file ="/var/www/html/oskad/image.png";
//echo $success = file_put_contents($file, $data);
//file_put_c
echo "<img alt=\"Embedded Image\" src=\"data:image/png;base64,".$data."\"/>";
$conn = new mysqli($servername, $username, $password, $dbname);
$addresss="g";
$sql="insert into alert (id, name,type,status,loc,address, timel, times, speed,severity) values ('".$_POST['ID']."', '".$_POST['Name']."', '".$_POST['Type']."', '".$_POST['Status']."', '".$_POST['Loc']."', '".$addresss."', '".$_POST['TimeL']."', '".$_POST['TimeS']."', '".$_POST['Speed']."','".$_POST['Severity']."')";
$result=$conn->query($sql);
$conn->close();
//echo $sql;
//echo $data;
?>

