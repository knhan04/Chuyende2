<?php
include 'config.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    $action = isset($_GET['action']) ? $_GET['action'] : '';

    if ($action == 'register') {
        $username = $data['username'];
        $email = $data['email'];
        $password = $data['password'];

        $sql = "INSERT INTO users (username, email, password) VALUES ('$username', '$email', '$password')";
        if ($conn->query($sql) === TRUE) {
            echo json_encode(["success" => true, "message" => "User registered successfully"]);
        } else {
            echo json_encode(["success" => false, "message" => "Error: " . $sql . "<br>" . $conn->error]);
        }
    } elseif ($action == 'login') {
        $username = $data['username'];
        $password = $data['password'];

        $sql = "SELECT * FROM users WHERE username='$username' AND password='$password'";
        $result = $conn->query($sql);
        if ($result->num_rows > 0) {
            echo json_encode(["success" => true, "data" => 1]);
        } else {
            echo json_encode(["success" => true, "data" => 0]);
        }
    }
}

$conn->close();
?>