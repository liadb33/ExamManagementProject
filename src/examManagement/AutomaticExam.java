package examManagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import Exceptions.LowNumOfAnswers;
import Exceptions.OutOfRangeQuestions;

public class AutomaticExam extends Exam {

	public AutomaticExam() {
		super();
	}

	@Override
	public void createExam(Scanner userInput, int subjectId, int examId) throws OutOfRangeQuestions, SQLException {
		Connection conn = SqlManager.getInstance().getConn();
		Random random = new Random();

		// Step 1: Retrieve questions for the given subjectId
		String questionQuery = "SELECT * FROM Question WHERE StockID = ?";
		List<Integer> questionIds = new ArrayList<>();

		PreparedStatement questionStmt = conn.prepareStatement(questionQuery);
		questionStmt.setInt(1, subjectId);
		ResultSet questionRs = questionStmt.executeQuery();

		while (questionRs.next())
			questionIds.add(questionRs.getInt("QuestionID"));

		System.out.println("Enter how many questions would you like, there are " + questionIds.size() + " questions.");
		int numOfQuestions = userInput.nextInt();

		if (numOfQuestions > 10 || numOfQuestions <= 0 || numOfQuestions > questionIds.size())
			System.out.println("You've exceeded the number of questions you can choose");
			
		List<Integer> selectedQuestionIds = new ArrayList<>();

		// Step 2: Randomly select questions
		while (selectedQuestionIds.size() < numOfQuestions) {
			int randomIndex = random.nextInt(questionIds.size());
			int questionId = questionIds.get(randomIndex);

			if (!selectedQuestionIds.contains(questionId)) {
				selectedQuestionIds.add(questionId);

				// Use existing method to insert question into ExamQuestion
				int currentExamQuestionId = addQuestionToExam(questionId, examId);

				//Retrieve answers for the selected question
				String answerQuery = "SELECT AnswerID, AnswerName, IsCorrect FROM Answer WHERE QuestionID = ?";
				List<Integer> answerIds = new ArrayList<>();

				PreparedStatement answerStmt = conn.prepareStatement(answerQuery);
				answerStmt.setInt(1, questionId);
				ResultSet answerRs = answerStmt.executeQuery();

				while (answerRs.next())
					answerIds.add(answerRs.getInt("AnswerID"));

				List<Integer> selectedAnswerIds = new ArrayList<>();
				//Randomly select answers for "Close" questions
				if (answerIds.size() >= 2) {
					Collections.shuffle(answerIds);
					boolean foundCorrect = false;

					for (int answerId : answerIds) {
						if (selectedAnswerIds.size() < 5) {
							selectedAnswerIds.add(answerId);

							// Check if the answer is correct
							String checkCorrectQuery = "SELECT IsCorrect FROM Answer WHERE AnswerID = ?";
							PreparedStatement checkStmt = conn.prepareStatement(checkCorrectQuery);
							checkStmt.setInt(1, answerId);
							ResultSet checkRs = checkStmt.executeQuery();
							if (checkRs.next() && checkRs.getBoolean("IsCorrect"))
								foundCorrect = true;
						}
					}
					// Ensure at least one correct answer is included
					if (!foundCorrect) {
						for (int answerId : answerIds) {
							String checkCorrectQuery = "SELECT IsCorrect FROM Answer WHERE AnswerID = ?";
							PreparedStatement checkStmt = conn.prepareStatement(checkCorrectQuery);
							checkStmt.setInt(1, answerId);
							ResultSet checkRs = checkStmt.executeQuery();
							if (checkRs.next() && checkRs.getBoolean("IsCorrect")) {
								// Randomly replace one of the current IDs with this correct answer
								int replaceIndex = random.nextInt(selectedAnswerIds.size());
								selectedAnswerIds.set(replaceIndex, answerId);
								break; // Exit after replacing one
							}
						}
					}
				} else if (answerIds.size() == 1)
					selectedAnswerIds.add(answerIds.get(0));

				//Use existing method to insert selected answers into ExamAnswer
				for (int answerId : selectedAnswerIds) 
					addAnswerToExam(currentExamQuestionId, answerId);
			}
		}
	}

	@Override
	public String kindOfExam() {
		return "Automatic";
	}

}
