public class Customer{
    public int accountNumber;
    public String name;
    public String email;
    public String mobileNo;
    public double amount;
    public Customer(int accountNumber, String name, String email, String mobileNo, double amount){
        this.accountNumber = accountNumber;
        this.name=name;
        this.email=email;
        this.mobileNo=mobileNo;
        this.amount = amount;
    }
    public String toString(){
        return ("Account Number: "+ accountNumber +" Name: "+name+" Email: "+email+" Mobile Number: "+mobileNo+" Balance: "+ amount);
    }
}//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
