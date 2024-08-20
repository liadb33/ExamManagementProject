package examManagement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StockManager {
	private static StockManager instance = null;
	private Scanner userInput;

	public StockManager(Scanner userInput) {
		this.userInput = userInput;
	}

	public static synchronized StockManager getInstance(Scanner userInput) {
		if (instance == null)
			instance = new StockManager(userInput);
		return instance;
	}
	
	
	public void printStocks() throws SQLException {
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Stock");
		while (rs.next())
			System.out.println("ID:" + rs.getInt("stockid") + " " + rs.getString("stockname"));
	}
	
	public void addSubject() {
		System.out.println("Enter the new subject name :");
		String newSubject = userInput.next();

		int rs = SqlManager.getInstance().updateQuery("INSERT INTO Stock (StockName) VALUES('" + newSubject + "')");
		if (rs != -1)
			System.out.println("Subject Added Succesfully");
	}
	
	public void removeSubject() throws Exception {

		int choice;

		System.out.println("Enter the ID of the subject you would like to remove");
		printStocks();
		choice = userInput.nextInt();
		int rs = SqlManager.getInstance().updateQuery("DELETE FROM Stock WHERE StockID = " + choice + "");
		if (rs == 0)
			System.out.println("There is no subject with that ID");
		else
			System.out.println("Subject removed successfully");
	}
	
	public int chooseSubject() throws Exception {
		int chosenSubject;

		if (getStockCount() == 0) {
			System.out.println("Stock is empty");
			return 0;
		}
		printStocks();
		System.out.println("Enter which subject you would like to work on");
		chosenSubject = userInput.nextInt();
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Stock WHERE stockID = " + chosenSubject);
		if (!rs.next()) {
			System.out.println("Invalid Choice,try again");
			return 0;
		}
		return chosenSubject;
	}
	
	private static int getStockCount() throws Exception {
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT COUNT(*) FROM Stock");
		rs.next();
		return rs.getInt("count");
	}
	
	public boolean isStockEmpty(int subjectId) throws Exception {

		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Question WHERE StockID = " + subjectId);
		if (!rs.next())
			return true;
		return false;
	}
}
