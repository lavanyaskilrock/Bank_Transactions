import java.util.regex.*;
import java.util.Scanner;
import exception.*;
import java.sql.*;
public class Main {
    static Scanner sc=new Scanner(System.in);
    public void createAccount() {
        try {
            System.out.println("1.Name\n2.Email\n3.Mobile\nEnter your details");
            String name = sc.next();
            String email = sc.next();
            String mobileNo = sc.next();

            if (!validateMobileNumber(mobileNo)) {
                throw new InvalidMobileNumberException("Invalid mobile number");
            }

            try (Connection conn = SQLConnection.getConnection()) {
                String sql = "INSERT INTO Customers(name, email, mobile, balance) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setString(1, name);
                    preparedStatement.setString(2, email);
                    preparedStatement.setString(3, mobileNo);
                    preparedStatement.setDouble(4, 0.0);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Account created successfully!");
                    } else {
                        throw new SQLException("Account creation failed, no rows affected.");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage() + " - Account Not Created");
        }
    }
    public Customer findCustomerByAccNo(int accNo) {

        try(Connection conn=SQLConnection.getConnection()){

            String sql="SELECT * FROM Customers WHERE account_number=?";

            try (PreparedStatement preparedStatement= conn.prepareStatement(sql)){

                preparedStatement.setInt(1,accNo);

                try(ResultSet rs=preparedStatement.executeQuery()){

                    if(rs.next()){

                        return new Customer(
                                rs.getInt("account_number"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("mobile"),
                                rs.getDouble("balance")
                        );
                    }
                }
            }
        }catch (SQLException e) {

            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }
    public boolean validateMobileNumber(String n){
        String r = "^[7-9][0-9]{9}$";
            if(!Pattern.matches(r,n)) {
                return false;
            }
        return true;
    }
    public Customer withdraw() {
        System.out.println("Enter your account number");
        int no=sc.nextInt();
        Customer c=findCustomerByAccNo(no);
        try {
            if (c == null) {
                throw new AccountNotFoundException("Account not found");
            }
            System.out.println("Enter the amount to be withdrawn");
            double sum = sc.nextDouble();
            if (c.amount - sum < 0) {
                throw new InsufficientBalanceException("Balance not sufficient");
            } else if (sum < 0) {
                throw new InvalidAmountException("Withdrawal amount can not be negative");
            } else {
                c.amount -= sum;
            }
            try(Connection conn=SQLConnection.getConnection()){
                String sql="UPDATE Customers SET balance=? WHERE account_number=?";
                try(PreparedStatement preparedStatement=conn.prepareStatement(sql)){
                    preparedStatement.setDouble(1,c.amount);
                    preparedStatement.setInt(2,c.accountNumber);
                    int updated=preparedStatement.executeUpdate();
                    if(updated>0){
                        System.out.println("Withdrawal Successful, Updated Balance: "+c.amount);
                    }else{
                        System.out.println("Withdrawal not successful");
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return c;
    }
    public void deposit() {
        System.out.println("Enter your account number");
        int no=sc.nextInt();
        Customer c=findCustomerByAccNo(no);
        try {
            if (c == null) {
                throw new AccountNotFoundException("Account not found");
            }
            System.out.println("Enter the amount to deposited");
            double sum = sc.nextInt();
            if (sum < 0) {
                throw new InvalidAmountException("Deposit can not be negative");
            } else {
                c.amount += sum;

            }
            try(Connection conn=SQLConnection.getConnection()){
                String sql="UPDATE Customers SET balance=? WHERE account_number=?";
                try(PreparedStatement preparedStatement=conn.prepareStatement(sql)){
                    preparedStatement.setDouble(1,c.amount);
                    preparedStatement.setInt(2,c.accountNumber);
                    int updated=preparedStatement.executeUpdate();
                    if(updated>0){
                        System.out.println("Balance Updated, New Balance: "+c.amount);
                    }else{
                        System.out.println("Failed to update balance");
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }

    }
    public void findAccount(){
        try(Connection conn=SQLConnection.getConnection()){
            System.out.println("Enter account number");
            int account=sc.nextInt();
            String sql="SELECT * FROM Customers WHERE account_number=?";
            try(PreparedStatement preparedStatement=conn.prepareStatement(sql)){
                preparedStatement.setInt(1,account);
                 try(ResultSet resultSet=preparedStatement.executeQuery()){
                        if(resultSet.next()){
                            Customer c=new Customer(resultSet.getInt("account_number"),resultSet.getString("name"),resultSet.getString("email"),resultSet.getString("mobile"),resultSet.getDouble("balance"));
                            System.out.println(c.toString());
                        }else{
                            throw new AccountNotFoundException("Invalid account number");
                        }
                 }
            }


        }catch(Exception e){
            System.out.println("Error: "+e);
        }
    }
    public void printAll(){
        try (Connection conn = SQLConnection.getConnection()) {
            String sql = "SELECT * FROM Customers";
            try (Statement statement = conn.createStatement()) {
                try (ResultSet rs = statement.executeQuery(sql)) {
                    while (rs.next()) {
                        System.out.println(new Customer(rs.getInt("account_number"),rs.getString("name"),rs.getString("email"),rs.getString("mobile"),rs.getDouble("balance")).toString());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Database Error " + e);
        }

    }
    public static void main(String[] args)throws Exception {
        int choice=0;
        Main ob=new Main();
        do{
            System.out.println("1.Create an account\n2.Withdraw\n3.Deposit\n4.View Details\n5.View All details\n6.Exit\nEnter your choice:\n");
            choice=sc.nextInt();
            switch (choice){
            case 1:
                ob.createAccount();
                break;
            case 2:
                ob.withdraw();
                break;
            case 3:
                ob.deposit();
                break;
            case 4:
                ob.findAccount();
                break;
            case 5:
                ob.printAll();
                break;
            case 6:
                System.out.println("Exiting..");
                break;
            default:
                System.out.println("Please enter a valid choice!");}
        }while(choice!=6);
    }
}