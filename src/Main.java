import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import exception.*;
import java.sql.*;
public class Main {
    static Scanner sc=new Scanner(System.in);
    String name="";
    String email="";
    String mobileNo="";
    int amount =0;
    int count=0;
    int sum=0;
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
                String sql = "INSERT INTO Customers(name, email, mobile_no, balance) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setString(2, email);
                    ps.setString(3, mobileNo);
                    ps.setDouble(4, 0.0);

                    int rowsAffected = ps.executeUpdate();

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
    }   public Customer findCustomerByAccNo(int accNo) {
//        for (Customer item:customerList) {
//            if (accNo==item.getAccountNumber()) {
//                return item;
//            }
//        }
        return null;
    }

    public boolean validateMobileNumber(String n){
        String r = "^[7-9][0-9]{9}$";


            if(!Pattern.matches(r,n)) {
                return false;
            }
        return true;

    }
    public Customer findAccount() {
        System.out.println("Enter your account number");
        int no=sc.nextInt();
        Customer c=findCustomerByAccNo(no);
        try {

            System.out.println("Enter the amount to be withdrawn");
            sum = sc.nextInt();

            if (c != null) {
                System.out.println(c.toString());

            } else {
                throw new AccountNotFoundException("Account not found");
            }
        } catch (Exception e) {
            System.out.println(e);

        }
        return c;
    }
    public Customer withdraw() {
        System.out.println("Enter your account number");
        int no=sc.nextInt();
        Customer c=findCustomerByAccNo(no);
        try {

            System.out.println("Enter the amount to be withdrawn");
            sum = sc.nextInt();
            if (c.amount - sum < 0) {
                throw new InsufficientBalanceException("Balance not sufficient");
            } else if (sum < 0) {
                throw new InvalidAmountException("Withdrawal amount can not be negative");
            } else {
                c.amount -= sum;
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
            sum = sc.nextInt();
            if (sum < 0) {
                throw new InvalidAmountException("Deposit can not be negative");
            } else {
                c.amount += sum;

            }
        }catch (Exception e){
            System.out.println(e);
        }

    }
    public void printAll(){
//        for(Customer item:customerList) {
//            System.out.println(item.toString());
//        }
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