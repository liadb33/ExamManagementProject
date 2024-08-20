package examManagement;

import java.sql.ResultSet;
import java.util.Scanner;

public class QuestionManager {
	private static QuestionManager instance = null;
	private Scanner userInput;

	public QuestionManager(Scanner userInput) {
		this.userInput = userInput;
	}

	public static synchronized QuestionManager getInstance(Scanner userInput) {
		if (instance == null)
			instance = new QuestionManager(userInput);

		return instance;
	}

	public void printQuestionAndAnswerStock(int subjectId) throws Exception {
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Question WHERE stockID = " + subjectId);
		while (rs.next()) {
			int questionId = rs.getInt("QuestionID");
			System.out.println("Question ID: " + questionId + "\n" + rs.getString("QuestionName") + " Level: "
					+ rs.getString("QuestionLevel") + ". Type: " + rs.getString("kindOfQuestion") + "\n");

			// Retrieve and print answers for the current question
			AnswerManager.getInstance(userInput).printAnswers(questionId);
		}
		rs.close(); // Close the ResultSet for questions
	}

	public void printQuestion(int subjectId) throws Exception {
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Question WHERE stockID = " + subjectId);
		while (rs.next())
			System.out.println("Question ID: " + rs.getInt("QuestionID") + "\n" + rs.getString("QuestionName")
					+ " Level: " + rs.getString("QuestionLevel") + ". Type: " + rs.getString("kindOfQuestion") + "\n");
		rs.close();

	}

	/* Adds new question to the stock */
	public void addQuestionToStock(int subjectId) {

		System.out.println("Enter which kind of question would you like to add, Open or Close");
		String questionKind = userInput.next();

		if (!questionKind.equals("Open") && !questionKind.equals("Close")) {
			System.out.println("Invalid kind of question, try again. Keep in mind Case Sensitivity.");
			return;
		}

		System.out.println("Enter the question name you would like to add");
		String newQuestionText = userInput.nextLine();
		newQuestionText = userInput.nextLine();

		System.out.println("Enter which kind of question would you like, Easy,Medium,Hard");
		String kindOfQuestion = userInput.next();

		if (!kindOfQuestion.equals("Easy") && !kindOfQuestion.equals("Medium") && !kindOfQuestion.equals("Hard")) {
			System.out.println("Invalid kind of question, try again. Keep in mind Case Sensitivity.");
			return;
		}

		int rs = SqlManager.getInstance()
				.updateQuery("INSERT INTO Question (QuestionName, QuestionLevel, kindOfQuestion, stockID) VALUES" + "('"
						+ newQuestionText + "', '" + kindOfQuestion + "', '" + questionKind + "'" + ", " + subjectId
						+ ")");
		if (rs == 1)
			System.out.println("Question Added successfully");
		else if (rs == SqlManager.DUPLICATE_FAULT)
			System.out.println("The question already exist");
		else
			System.out.println("Failed to add the question");

	}

	// edits a question from the stock
	public void editQuestionFromStock(int subjectId) throws Exception {

		printQuestion(subjectId);

		System.out.println("Choose a question ID you want to edit.");
		int chosenQuestion = (userInput.nextInt());

		System.out.println("Enter the new question you want:");
		String editedQuestion = userInput.nextLine();
		editedQuestion = userInput.nextLine();

		int rs = SqlManager.getInstance().updateQuery("UPDATE Question SET QuestionName = '" + editedQuestion + "' WHERE "
				+ "QuestionID = " + chosenQuestion + " AND " + "stockID = " + subjectId);
		if (rs == 0) {
			System.out.println("Edit failed");
			return;
		}
		System.out.println("Updated Question Successfully");

	}
	
	/* Deletes Question From The Stock */
	public void deleteQuestionFromStock(int subjectId) throws Exception {

		printQuestion(subjectId);

		System.out.println("which question would you like to delete");
		int chosenQuestion = userInput.nextInt();

		int rs = SqlManager.getInstance().updateQuery(
				"DELETE FROM Question WHERE stockID = " + subjectId + " AND " + "QuestionID = " + chosenQuestion);
		if (rs == 0) {
			System.out.println("Theres no Question with this ID");
			return;
		}
		System.out.println("Question Deleted successfully");
	}

}
