<?php
include 'config.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    $username = $data['username'];
    $product = $data['product'];
    $price = $data['price'];
    $otype = $data['otype'];

    $sql = "INSERT INTO cart (username, product, price, otype) VALUES ('$username', '$product', '$price', '$otype')";
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Added to cart"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
    }
} elseif ($method == 'GET') {
    $action = isset($_GET['action']) ? $_GET['action'] : '';
    if ($action == 'check') {
        $username = $_GET['username'];
        $product = $_GET['product'];
        $sql = "SELECT * FROM cart WHERE username='$username' AND product='$product'";
        $result = $conn->query($sql);
        if ($result->num_rows > 0) {
            echo json_encode(["success" => true, "data" => 1]);
        } else {
            echo json_encode(["success" => true, "data" => 0]);
        }
    } elseif ($action == 'get') {
        $username = $_GET['username'];
        $otype = $_GET['otype'];
        $sql = "SELECT * FROM cart WHERE username='$username' AND otype='$otype'";
        $result = $conn->query($sql);
        $data = [];
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $data[] = $row['product'] . "VNĐ" . $row['price'];
            }
        }
        echo json_encode(["success" => true, "data" => $data]);
    }
} elseif ($method == 'DELETE') {
    $username = $_GET['username'];
    $otype = $_GET['otype'];
    $sql = "DELETE FROM cart WHERE username='$username' AND otype='$otype'";
    if ($conn->query($sql) === TRUE) {
        echo json_encode(["success" => true, "message" => "Removed from cart"]);
    } else {
        echo json_encode(["success" => false, "message" => "Error: " . $conn->error]);
    }
}

$conn->close();
?>