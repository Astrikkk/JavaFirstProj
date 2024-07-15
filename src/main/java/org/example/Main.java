package org.example;

import com.github.javafaker.Faker;

import java.sql.*;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    // JDBC URL, username, and password of PostgreSQL server
    private static final String URL = "jdbc:postgresql://localhost:5432/java_krot";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static void main(String[] args) {
        try {
// Open a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");
            // Create a statement object to send to the database
            var command = connection.createStatement();

            //createTableAnimals(command);
            //insertAnimal(connection);
            selectAnimal(command);
            //create100animals(connection);
            //updateAnimal(connection);


            // SQL query to select data



            command.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Begin working"+e.getMessage());
        }
    }
    private static void create100animals(Connection conn) throws SQLException {
        Faker faker = new Faker(new Locale("en-US"));
        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        for (int i = 0; i < 100; i++) {
            String name = faker.animal().name();
            String species = faker.animal().name();
            int age = faker.number().numberBetween(1, 15);
            double weight = faker.number().randomDouble(2, 1, 200);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, species);
            preparedStatement.setInt(3, age);
            preparedStatement.setBigDecimal(4, java.math.BigDecimal.valueOf(weight));

            preparedStatement.addBatch();
        }

        int[] rowsInserted = preparedStatement.executeBatch();
        System.out.println(rowsInserted.length + " animals inserted successfully!");

        preparedStatement.close();
    }
    private static void updateAnimal(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the ID of the animal to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new name (leave blank to keep current): ");
        String name = scanner.nextLine();

        System.out.print("Enter new species (leave blank to keep current): ");
        String species = scanner.nextLine();

        System.out.print("Enter new age (leave blank to keep current): ");
        String ageInput = scanner.nextLine();

        System.out.print("Enter new weight (leave blank to keep current): ");
        String weightInput = scanner.nextLine();

        StringBuilder sql = new StringBuilder("UPDATE animals SET ");
        boolean firstField = true;

        if (!name.isEmpty()) {
            sql.append("name = ?");
            firstField = false;
        }
        if (!species.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("species = ?");
            firstField = false;
        }
        if (!ageInput.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("age = ?");
            firstField = false;
        }
        if (!weightInput.isEmpty()) {
            if (!firstField) sql.append(", ");
            sql.append("weight = ?");
        }
        sql.append(" WHERE id = ?");

        PreparedStatement preparedStatement = conn.prepareStatement(sql.toString());

        int paramIndex = 1;
        if (!name.isEmpty()) {
            preparedStatement.setString(paramIndex++, name);
        }
        if (!species.isEmpty()) {
            preparedStatement.setString(paramIndex++, species);
        }
        if (!ageInput.isEmpty()) {
            preparedStatement.setInt(paramIndex++, Integer.parseInt(ageInput));
        }
        if (!weightInput.isEmpty()) {
            preparedStatement.setBigDecimal(paramIndex++, new java.math.BigDecimal(weightInput));
        }
        preparedStatement.setInt(paramIndex, id);

        int rowsUpdated = preparedStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Animal updated successfully!");
        } else {
            System.out.println("No animal found with the given ID.");
        }
        preparedStatement.close();
    }

    private static  void selectAnimal(Statement command) throws SQLException{
        String sql = "SELECT * FROM animals";

        // Execute the query and get a result set
        var resultSet = command.executeQuery(sql);

        // Process the result set
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String species = resultSet.getString("species");
            int age = resultSet.getInt("age");
            double weight = resultSet.getDouble("weight");

            System.out.println("ID: " + id + ", Name: " + name + ", Species: " + species + ", Age: " + age + ", Weight: " + weight);
        }
        resultSet.close();
    }
    private static void createTableAnimals(Statement command) throws SQLException {
        String sql = "CREATE TABLE animals (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(50) NOT NULL, " +
                "species VARCHAR(50) NOT NULL, " +
                "age INT NOT NULL, " +
                "weight DECIMAL(5, 2) NOT NULL" +
                ")";

        command.executeUpdate(sql);
    }
    private static void insertAnimal(Connection conn) throws SQLException {
        Animal animal = new Animal();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Вкажіть назву ->_");
        animal.setName(scanner.nextLine());

        System.out.print("Вкажіть вид тварини ->_");
        animal.setSpecies(scanner.nextLine());

        System.out.print("Вкажіть вік ->_");
        animal.setAge(scanner.nextInt());

        System.out.print("Вкажіть вагу ->_");
        animal.setWeight(scanner.nextDouble());

        String sql = "INSERT INTO animals (name, species, age, weight) VALUES (?, ?, ?, ?)";

        // Create a prepared statement object
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        // Set the values for the prepared statement
        preparedStatement.setString(1, animal.getName());
        preparedStatement.setString(2, animal.getSpecies());
        preparedStatement.setInt(3, animal.getAge());
        preparedStatement.setBigDecimal(4, java.math.BigDecimal.valueOf(animal.getWeight()));

        // Execute the query
        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Тваринку успішно додано!");
        }
        preparedStatement.close();
    }
}