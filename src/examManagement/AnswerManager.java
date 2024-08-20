package examManagement;

import java.sql.ResultSet;
import java.util.Scanner;

public class AnswerManager {
	private static AnswerManager instance = null;
	private Scanner userInput;

	public AnswerManager(Scanner userInput) {
		this.userInput = userInput;
	}

	public static synchronized AnswerManager getInstance(Scanner userInput) {
		if (instance == null)
			instance = new AnswerManager(userInput);

		return instance;
	}
	
	public void printAnswers(int questionId) throws Exception {
		ResultSet answerRs = SqlManager.getInstance().selectQuery("SELECT * FROM Answer WHERE QuestionID = " + questionId);
		while (answerRs.next())
			System.out.println("ID:" + answerRs.getInt("answerid") + ", Answer: " + answerRs.getString("AnswerName")
					+ " (Correct: " + answerRs.getBoolean("IsCorrect") + ")");
		System.out.println();
		answerRs.close(); // Close the ResultSet for answers
	}
	
	/* Adds new answer to some question in the stock */
	public void addAnswerToStock(int subjectId) throws Exception {

		// prints all of the questions the subject has
		QuestionManager.getInstance(userInput).printQuestion(subjectId);

		System.out.println("Choose a question to add an answer to");
		int chosenQuestion = userInput.nextInt();

		// retrieves the kind of questions the user selected
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT kindOfQuestion FROM Question WHERE QuestionID = " + chosenQuestion
				+ " AND " + "stockID = " + subjectId);
		if (!rs.next()) {
			System.out.println("Invalid Choice, Try again");
			return;
		}

		String kindOfQuestion = rs.getString("kindOfQuestion");

		// checks if maxAnswers is exceeded
		int maxAnswers = kindOfQuestion.equals("Close") ? 10 : 1;

		rs = SqlManager.getInstance().selectQuery("SELECT COUNT(*) FROM Answer WHERE QuestionID = " + chosenQuestion);
		rs.next();
		if (rs.getInt("count") >= maxAnswers) {
			System.out.println("Cannot add an answer, Question is full of answers");
			return;
		}

		System.out.println("Whats your answer name to this question");
		String answerName = userInput.nextLine();
		answerName = userInput.nextLine();
		boolean isCorrect;

		if (kindOfQuestion.equals("Close")) {
			System.out.println("Is this answer correct? true or false");
			String isCorrectText = userInput.nextLine();
			isCorrect = Boolean.parseBoolean(isCorrectText);
		} else
			isCorrect = true;

		// fix println
		int result = SqlManager.getInstance().updateQuery("INSERT INTO Answer (AnswerName, IsCorrect, QuestionID) VALUES" + "('"
				+ answerName + "'," + isCorrect + ", " + chosenQuestion + ")");
		System.out.println("Answer Added successfully");

	}
	
	// edits an answer from the stock
	public void editAnswerFromStock(int subjectId) throws Exception {

		QuestionManager.getInstance(userInput).printQuestion(subjectId);
		int result = 0;
		System.out.println("Choose a question you want to see the answers");
		int qChoice = userInput.nextInt();

		ResultSet rs = SqlManager.getInstance().selectQuery(
				"SELECT * FROM Question WHERE QuestionID = " + qChoice + " AND " + "stockID = " + subjectId);
		if (!rs.next()) {
			System.out.println("Theres no such question.");
			return;
		}

		String kind = rs.getString("kindOfQuestion");
		rs.close();

		if (kind.equals("Open")) {
			System.out.println("This is an Open Question with the Answer:\n");
			rs = SqlManager.getInstance().selectQuery("SELECT * FROM Answer WHERE QuestionID = " + qChoice);
			rs.next();
			System.out.println(rs.getString("AnswerName"));
			System.out.println("Would you like to edit this answer? (1 - Yes,0 - No)");
			int choice = userInput.nextInt();

			if (choice == 1) {
				System.out.println("Enter the new Answer you want:");
				String editedAnswer = userInput.nextLine();
				editedAnswer = userInput.nextLine();
				result = SqlManager.getInstance().updateQuery(
						"UPDATE Answer SET AnswerName = '" + editedAnswer + "' WHERE " + "QuestionID = " + qChoice);
			} else if (choice == 0) {
				System.out.println("You chose not to edit the answser.");
				return;
			} else {
				System.out.println("wrong choice\n");
			}

		} else {
			System.out.println("Please Enter which answer you want to edit");
			printAnswers(qChoice);
			int aChoice = userInput.nextInt();

			System.out.println("Enter the new Answer you want:");
			String editedAnswer = userInput.nextLine();
			editedAnswer = userInput.nextLine();

			System.out.println("is This Answer true or false");
			boolean isCorrect = userInput.nextBoolean();

			result = SqlManager.getInstance().updateQuery("UPDATE Answer SET AnswerName = '" + editedAnswer + "', isCorrect = "
					+ isCorrect + " WHERE " + "AnswerID = " + aChoice + " AND " + "QuestionID = " + qChoice);
			if (result == 0) {
				System.out.println("Update answer failed, try again");
				return;
			}
		}
		if (result != 0)
			System.out.println("Answer Edited Succesfully");
		else
			System.out.println("You can't edit the answer");

	}
	
	/* Deletes Answer From some question in the Stock */
	public void deleteAnswerFromStock(int subjectId) throws Exception {

		int result = 0;
		QuestionManager.getInstance(userInput).printQuestion(subjectId);

		System.out.println("which question would you like to delete an answer to");
		int choiceQ = userInput.nextInt();

		ResultSet rs = SqlManager.getInstance().selectQuery(
				"SELECT * FROM Question WHERE QuestionID = " + choiceQ + " AND " + "stockID = " + subjectId);
		if (!rs.next()) {
			System.out.println("Theres no such question.");
			return;
		}

		String kind = rs.getString("kindOfQuestion");
		rs.close();

		rs = SqlManager.getInstance().selectQuery("SELECT COUNT(*) FROM Answer WHERE QuestionID = " + choiceQ);
		rs.next();
		if (rs.getInt("count") == 2 || rs.getInt("count") == 0) {
			System.out.println("There are no answers to delete");
			return;
		}
		rs.close();

		if (kind.equals("Open")) {
			System.out.println("This is an Open Question with the Answer:\n");
			printAnswers(choiceQ);
			System.out.println("Would you like to delete this answer? (1 - Yes,0 - No)");
			int choice = userInput.nextInt();

			if (choice > 1 || choice < 0) {
				System.out.println("Invalid choice, try again");
				return;
			} else if (choice == 1)
				result = SqlManager.getInstance().updateQuery("DELETE FROM Answer WHERE QuestionID = " + choiceQ);
		} else {
			printAnswers(choiceQ);

			System.out.println("Choose an answer you would like to delete");
			int choiceA = userInput.nextInt();

			result = SqlManager.getInstance().updateQuery(
					"DELETE FROM Answer WHERE QuestionID = " + choiceQ + " AND " + "AnswerID = " + choiceA);
		}

		if (result == 0)
			System.out.println("Answer hasn't been deleted, Try again");
		System.out.println("Answer Deleted Successfully");
	}

}
