<?php
include 'config.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    $username = $data['username'];
    $fullname = $data['fullname'];
    $address = $data['address'];
    $contact = $data['contact'];
    $pincode = $data['pincode'];
    $date = $data['date'];
    $time = $data['time'];
    $amount = $data['amount'];
    $otype = $data['otype'];

    $sql = "INSERT INTO order1 (username, fullname, address, contact, pincode, date, time, amount, otype) VALUES ('$username', '$fullname', '$address', '$contact', '$pincode', '$date', '$time', '$amount', '$otype')";
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Order added"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
    }
} elseif ($method == 'GET') {
    $action = isset($_GET['action']) ? $_GET['action'] : '';
    if ($action == 'get') {
        $username = $_GET['username'];
        $sql = "SELECT * FROM order1 WHERE username='$username'";
        $result = $conn->query($sql);
        $data = [];
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $data[] = $row['fullname'] . "$" . $row['address'] . "$" . $row['contact'] . "$" . $row['pincode'] . "$" . $row['date'] . "$" . $row['time'] . "$" . $row['amount'] . "$" . $row['otype'];
            }
        }
        echo json_encode(["success" => true, "data" => $data]);
    } elseif ($action == 'check') {
        $username = $_GET['username'];
        $fullname = $_GET['fullname'];
        $address = $_GET['address'];
        $contact = $_GET['contact'];
        $date = $_GET['date'];
        $time = $_GET['time'];
        $sql = "SELECT * FROM order1 WHERE username='$username' AND fullname='$fullname' AND address='$address' AND contact='$contact' AND date='$date' AND time='$time'";
        $result = $conn->query($sql);
        if ($result->num_rows > 0) {
            echo json_encode(["success" => true, "data" => 1]);
        } else {
            echo json_encode(["success" => true, "data" => 0]);
        }
    }
} elseif ($method == 'DELETE') {
    $fullname = $_GET['fullname'];
    $otype = $_GET['otype'];
    $address = $_GET['address'];
    $sql = "DELETE FROM order1 WHERE fullname='$fullname' AND otype='$otype' AND address='$address'";
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Order removed"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
    }
}

$conn->close();
?>