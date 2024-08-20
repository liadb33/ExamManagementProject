package examManagement;

import java.beans.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Exceptions.LowNumOfAnswers;
import Exceptions.OutOfRangeQuestions;

public class ManualExam extends Exam {

	public ManualExam() {
		super();
	}

	public void createExam(Scanner userInput,int subjectId, int examId) throws OutOfRangeQuestions, SQLException {

		// Query to get questions for the given subjectId
		String questionQuery = "SELECT * FROM Question WHERE StockID = ?";
		PreparedStatement questionStmt = SqlManager.getInstance().getConn().prepareStatement(questionQuery);
		questionStmt.setInt(1, subjectId);
		ResultSet questionRs = questionStmt.executeQuery();

		List<Integer> questionIds = new ArrayList<>();
		while (questionRs.next()) {
			int questionId = questionRs.getInt("QuestionID");
			String questionName = questionRs.getString("QuestionName");
			String questionType = questionRs.getString("kindOfQuestion");
			System.out.println("ID: " + questionId + " " + questionName + " Type: " + questionType);
			questionIds.add(questionId);
		}
		questionRs.close();

		System.out.println("Enter how many questions would you like, there are " + questionIds.size() + " questions.");
		int numOfQuestions = userInput.nextInt();

		while (numOfQuestions > 10 || numOfQuestions <= 0 || numOfQuestions > questionIds.size()) {
			System.out.println("You've exceeded the number of questions you can choose");
			numOfQuestions = userInput.nextInt();
		}

		List<Integer> selectedQuestionIds = new ArrayList<>();

		while (numOfQuestions > 0) {
			System.out.println("Enter which question ID would you like to add to the exam");
			int choiceQ = userInput.nextInt();

			while (!questionIds.contains(choiceQ)) {
				System.out.println("Wrong choice, enter which question would you like");
				choiceQ = userInput.nextInt();
			}

			if (!selectedQuestionIds.contains(choiceQ)) {
				selectedQuestionIds.add(choiceQ);

				int currentExamQuestionId = addQuestionToExam(choiceQ, examId);
				
				// Query to get answers for the selected question
				String answerQuery = "SELECT * FROM Answer WHERE QuestionID = ?";
				PreparedStatement answerStmt = SqlManager.getInstance().getConn().prepareStatement(answerQuery);
				answerStmt.setInt(1, choiceQ);
				ResultSet answerRs = answerStmt.executeQuery();

				List<Integer> answerIds = new ArrayList<>();
				while (answerRs.next()) {
					int answerId = answerRs.getInt("AnswerID");
					String answerName = answerRs.getString("AnswerName");
					System.out.println("ID: "  + answerId + " " + answerName);
					answerIds.add(answerId);
				}
				answerRs.close();

				// Allow user to select answers
				int numOfChosenA = 0;
				while (numOfChosenA < 6 && numOfChosenA < answerIds.size()) {
					System.out.println("Enter the answers you want, press 0 to stop choosing");
					int choiceA = userInput.nextInt();

					if (choiceA == 0)
						break;

					while (!answerIds.contains(choiceA)) {
						System.out.println("Wrong choice, enter the answers you want, press 0 to stop");
						choiceA = userInput.nextInt();
					}

					// insert answer to the corresponding to ExamQuestion
					addAnswerToExam(currentExamQuestionId, choiceA);

					numOfChosenA++;
				}

				numOfQuestions--;
			} else {
				System.out.println("Question exists already");
			}
		}
	}


	@Override
	public String kindOfExam() {
		return "Manual";
	}


}
