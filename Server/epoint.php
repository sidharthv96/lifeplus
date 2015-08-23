<?php
$servername = "localhost";
$username = "root";
$password = "qazwsx123";
$dbname = "oscad";
$conn = new mysqli($servername, $username, $password, $dbname);
$sql = "update users set points=points".$_POST['op'].$_POST['point']" where id='".$_POST['id']."'";
$result = $conn->query($sql);
$conn->close();
?>

