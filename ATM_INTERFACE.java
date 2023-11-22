//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.sql.*;
import java.util.*;

class UserInfo{
	
	public void userInformation(int userID, String username, String password, String fname, String lname, String contact) throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123");
		PreparedStatement preparedStatement = con.prepareStatement(
                "INSERT INTO user (userID, username, password, Fname, Lname, contact) VALUES (?, ?, ?, ?, ?, ?)");
		
		preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, fname);
        preparedStatement.setString(5, lname);
        preparedStatement.setString(6, contact);
        
        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0)
        {
            System.out.println("User registration successful.");
        } 
        else
        {
            System.out.println("User registration failed.");
        }
		
	}
}
//Class for Transaction History
class TransactionHistory {	
	public void viewHistory(Connection connection, String username, String password) {
		if (connection == null)
		{
            System.out.println("Database connection is not established.");
            return;
        }
		int userID = getUserId(connection, username, password);

        if (userID != -1)
        {
        	String selectQuery = "SELECT t.tranid, t.accountid, t.amount, t.type, t.targetaccountID " +
                    "FROM transaction t " +
                    "INNER JOIN accounts a ON t.accountID = a.accountID " +
                    "WHERE a.userID = ?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, userID);

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    List<String> transactions = new ArrayList<>();

                    while (resultSet.next())
                    {
                    	String transactionDetails = "Transaction ID: " + resultSet.getInt("tranid") +
                                ", Account ID: " + resultSet.getInt("accountid") +
                                ", Amount: $" + resultSet.getDouble("amount") +
                                ", Type: " + resultSet.getString("type") +
                                ", Target Account: " + resultSet.getInt("targetaccountID");

                        transactions.add(transactionDetails);
                    }

                    if (!transactions.isEmpty()) 
                    {
                        System.out.println("Transaction History for User ID: " + userID);
                        
                        for (String transaction : transactions) 
                        {
                            System.out.println(transaction);
                        }
                    } 
                    else 
                    {
                        System.out.println("No transactions found for User ID: " + userID);
                    }
                }
            } 
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else 
        {
            System.out.println("User not found. Unable to retrieve transaction history.");
        }
	}	
	
	private int getUserId(Connection connection, String username, String password) 
	{
        String selectUserQuery = "SELECT userID FROM user WHERE username = ? AND password = ?";

        try (PreparedStatement selectUserStatement = connection.prepareStatement(selectUserQuery))
        {
            selectUserStatement.setString(1, username);
            selectUserStatement.setString(2, password);

            try (ResultSet userResultSet = selectUserStatement.executeQuery())
            {
                if (userResultSet.next())
                {
                    return userResultSet.getInt("userID");
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}

//Class for Withdrawal
class Withdrawal {
	public void withdraw(int accountID) {
		Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the withdrawal amount: ");
        double amount = scanner.nextDouble();
        
		try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123"))
		{
            double currentBalance = getBalance(connection, accountID);
            System.out.println("Current Balance: $" + currentBalance);
            System.out.println("Withdrawal Amount: $" + amount);
            
            if (currentBalance >= amount)
            {
                String updateQuery = "UPDATE accounts SET balance = ? WHERE accountID = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery))
                {
                    updateStatement.setDouble(1, currentBalance - amount);
                    updateStatement.setInt(2, accountID);

                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0)
                    {
                        System.out.println("Withdrawal successful");
                        System.out.println(" Updated balance: $"+ (currentBalance - amount));
                    }
                    else 
                    {
                        System.out.println("Withdrawal failed. Please try again.");
                    }
                }
            }
            else
            
            {
                System.out.println("Withdrawal failed. Insufficient balance.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }

	private double getBalance(Connection connection, int accountID) 
	{
	    String selectQuery = "SELECT balance FROM accounts WHERE accountID = ?";

	    try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) 
	    {
	        selectStatement.setInt(1, accountID);

	        try (ResultSet resultSet = selectStatement.executeQuery()) 
	        {
	            if (resultSet.next())
	            {
	                return resultSet.getDouble("balance");
	            } 
	            else
	            {
	                System.out.println("Account not found for accountID: " + accountID);
	                return 0;
	            }
	        }
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	        return 0;
	    }
	}
}

//Class for Deposit
class Deposit {
	public void deposit(int accountID) {
		Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the deposit amount: ");
        double amount = scanner.nextDouble();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123"))
        {
            double currentBalance = getBalance(connection, accountID);
            System.out.println("Current Balance: $" + currentBalance);
            System.out.println("Deposit Amount: $" + amount);

            String updateQuery = "UPDATE accounts SET balance = ? WHERE accountID = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery))
            {
                updateStatement.setDouble(1, currentBalance + amount);
                updateStatement.setInt(2, accountID);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0)
                {
                    System.out.println("Deposit successful");
                    System.out.println(" Updated balance: $" + (currentBalance + amount));
                } 
                else
                {
                    System.out.println("Deposit failed. Please try again.");
                }
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
    }
	
	private double getBalance(Connection connection, int accountID) 
	{
	    String selectQuery = "SELECT balance FROM accounts WHERE accountID = ?";

	    try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) 
	    {
	        selectStatement.setInt(1, accountID);

	        try (ResultSet resultSet = selectStatement.executeQuery()) 
	        {
	            if (resultSet.next())
	            {
	                return resultSet.getDouble("balance");
	            } 
	            else
	            {
	                System.out.println("Account not found for accountID: " + accountID);
	                return 0;
	            }
	        }
	    }
	    catch (SQLException e) {
	        e.printStackTrace();
	        return 0;
	    }
	}
}

//Class for Transfer
class Transfer{
	public void transfer(int sourceAccountID, int targetAccountID) {
		 Scanner scanner = new Scanner(System.in);

	        System.out.println("Enter the transfer amount: ");
	        double amount = scanner.nextDouble();

	        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123")) {
	            double sourceBalance = getBalance(connection, sourceAccountID);

	            if (sourceBalance >= amount) {
	                String updateSourceQuery = "UPDATE accounts SET balance = ? WHERE accountID = ?";
	                String updateTargetQuery = "UPDATE accounts SET balance = ? WHERE accountID = ?";

	                try (PreparedStatement updateSourceStatement = connection.prepareStatement(updateSourceQuery);
	                     PreparedStatement updateTargetStatement = connection.prepareStatement(updateTargetQuery)) 
	                {

	                    // Deduct amount from source account
	                    updateSourceStatement.setDouble(1, sourceBalance - amount);
	                    updateSourceStatement.setInt(2, sourceAccountID);
	                    int sourceRowsAffected = updateSourceStatement.executeUpdate();

	                    // Add amount to target account
	                    double targetBalance = getBalance(connection, targetAccountID);
	                    updateTargetStatement.setDouble(1, targetBalance + amount);
	                    updateTargetStatement.setInt(2, targetAccountID);
	                    int targetRowsAffected = updateTargetStatement.executeUpdate();

	                    if (sourceRowsAffected > 0 && targetRowsAffected > 0)
	                    {
	                        System.out.println("Transfer successful");
	                        System.out.println("Source Account Updated Balance: $" + (sourceBalance - amount));
	                        System.out.println("Target Account Updated Balance: $" + (targetBalance + amount));
	                    } 
	                    else 
	                    {
	                        System.out.println("Transfer failed. Please try again.");
	                    }
	                }
	            }
	            else
	            {
	                System.out.println("Transfer failed. Insufficient balance in the source account.");
	            }
	        } 
	        catch (SQLException e)
	        {
	            e.printStackTrace();
	        }
		}
	private double getBalance(Connection connection, int accountID) 
	{
	    String selectQuery = "SELECT balance FROM accounts WHERE accountID = ?";

	    try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) 
	    {
	        selectStatement.setInt(1, accountID);

	        try (ResultSet resultSet = selectStatement.executeQuery()) 
	        {
	            if (resultSet.next())
	            {
	                return resultSet.getDouble("balance");
	            } 
	            else
	            {
	                System.out.println("Account not found for accountID: " + accountID);
	                return 0;
	            }
	        }
	    }
	    catch (SQLException e)
	    {
	        e.printStackTrace();
	        return 0;
	    }
	}

}

//Class for Quit
class Quit {
	public void quit() {
		System.out.println("Quitting the ATM system. Goodbye!");
	}
}

public class ATM_INTERFACE {

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		int max_attempt=3;
		int loginAttempts = 0;
        boolean loginSuccess = false;
        String enteredUsername;
        String enteredPassword ;
        int userAccountID = 0 ;
        Connection connection = null;
        System.out.println("Welcome to the ATM Interface!!!");
        
        try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123");
		} 
        catch (SQLException e)
        {	
			e.printStackTrace();
		}

        System.out.println("Do you already have an account? (yes/no)");
        String response = scanner.nextLine().toLowerCase();
        
        if (response.equals("yes")) {
            System.out.println("Great! Please log in.");
            
            do {
                System.out.println("Enter your username: ");
                enteredUsername = scanner.nextLine();

                System.out.println("Enter your password: ");
                enteredPassword = scanner.nextLine();

                loginSuccess = performLogin(enteredUsername, enteredPassword);

                if (!loginSuccess) {
                    loginAttempts++;
                    System.out.println("Login failed. Remaining attempts: " +
                            (max_attempt - loginAttempts));
                }
                

            } while (!loginSuccess && loginAttempts < max_attempt);
            
           if (loginSuccess)
           {
               System.out.println("Login successful! You can now proceed with ATM actions.");
               
               userAccountID = getUserAccountID(enteredUsername, enteredPassword);

               while (!exit) {
                   System.out.println("\nOptions:");
                   System.out.println("1. View Transaction History");
                   System.out.println("2. Withdraw");
                   System.out.println("3. Deposit");
                   System.out.println("4. Transfer");
                   System.out.println("5. Quit");

                   System.out.println("Enter your choice: ");
                   int choice = scanner.nextInt();
                   scanner.nextLine(); 

                   switch (choice) {
                       case 1:
                           TransactionHistory transactionHistory = new TransactionHistory();
                           transactionHistory.viewHistory(connection, enteredUsername, enteredPassword);
                           break;
                           
                       case 2:
                           Withdrawal withdrawal = new Withdrawal();
                           withdrawal.withdraw(userAccountID);
                           break;
                           
                       case 3:
                           Deposit deposit = new Deposit();
                           deposit.deposit(userAccountID);
                           break;
                           
                       case 4:
                    	   System.out.println("Enter the target account ID for the transfer: ");
                    	   int targetAccountID = scanner.nextInt();
                           Transfer transfer = new Transfer();
                           transfer.transfer(userAccountID, targetAccountID);
                           break;
                           
                       case 5:
                           System.out.println("Are you sure you want to quit? (yes/no)");
                           String quitConfirmation = scanner.nextLine().toLowerCase();
                           if (quitConfirmation.equals("yes")) {
                               exit = true;
                               Quit quit = new Quit();
                               quit.quit();
                           }
                           break;
                           
                       default:
                           System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                   }
               }
           }  
        }

        else if (response.equals("no")) 
        {
            // New user
            System.out.println("Let's create a new account for you.");
            UserInfo user = new UserInfo();
            System.out.println("Enter UserID: ");
            int userID = scanner.nextInt();
            scanner.nextLine(); 

            System.out.println("Enter username: ");
            String username = scanner.nextLine();

            System.out.println("Enter password: ");
            String password = scanner.nextLine();

            System.out.println("Enter first name: ");
            String fname = scanner.nextLine();

            System.out.println("Enter last name: ");
            String lname = scanner.nextLine();

            System.out.println("Enter contact number: ");
            String contact = scanner.nextLine();
            
            try 
            {    
                user.userInformation(userID, username, password, fname, lname, contact);

                System.out.println("User account created successfully. You can now log in.");
                while (!exit) {
                    System.out.println("\nOptions:");
                    System.out.println("1. View Transaction History");
                    System.out.println("2. Withdraw");
                    System.out.println("3. Deposit");
                    System.out.println("4. Transfer");
                    System.out.println("5. Quit");

                    System.out.println("Enter your choice: ");
                    int choice = scanner.nextInt();
                    scanner.nextLine(); 

                    switch (choice) {
                        case 1:
                            TransactionHistory transactionHistory = new TransactionHistory();
                            transactionHistory.viewHistory(connection, username, password);
                            break;
                            
                        case 2:
                            Withdrawal withdrawal = new Withdrawal();
                            withdrawal.withdraw(userAccountID);
                            break;
                            
                        case 3:
                            Deposit deposit = new Deposit();
                            deposit.deposit(userAccountID);
                            break;
                            
                        case 4:
                            Transfer transfer = new Transfer();
                            transfer.transfer(choice, choice);
                            break;
                            
                        case 5:
                            System.out.println("Are you sure you want to quit? (yes/no)");
                            String quitConfirmation = scanner.nextLine().toLowerCase();
                            if (quitConfirmation.equals("yes")) {
                                exit = true;
                                Quit quit = new Quit();
                                quit.quit();
                            }
                            break;
                            
                        default:
                            System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    }
                }

            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else 
        {
            System.out.println("Invalid response. Please enter 'yes' or 'no'.");
        }
    }

	private static int getUserAccountID(String enteredUsername, String enteredPassword) {
		
		int userAccountID = 0; 

	    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123"))
	    {
	        String query = "SELECT a.accountID FROM user u INNER JOIN accounts a ON u.userID = a.userID WHERE u.username = ? AND u.password = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) 
	        {
	            preparedStatement.setString(1, enteredUsername);
	            preparedStatement.setString(2, enteredPassword);

	            try (ResultSet resultSet = preparedStatement.executeQuery())
	            {
	                if (resultSet.next())
	                {
	                    userAccountID = resultSet.getInt("accountID");
	                }
	            }
	        }
	    } 
	    catch (SQLException e) 
	    {
	        e.printStackTrace();
	    }

	    return userAccountID;
	}

	private static boolean performLogin(String enteredUsername, String enteredPassword) {
        
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/intern_atm", "root", "Mskpp@123"))
        {
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) 
            {
                preparedStatement.setString(1, enteredUsername);
                preparedStatement.setString(2, enteredPassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) 
                {
                    return resultSet.next();
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace(); 
        }
        return false; 
	}
	
}

