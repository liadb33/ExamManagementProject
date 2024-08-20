package examManagement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Exceptions.LowNumOfAnswers;
import Exceptions.OutOfRangeQuestions;
import Interfaces.Examable;

public abstract class Exam implements Examable {
	// data variables
	
	// constructors
	public Exam() {
	}

	public void addAnswerToExam(int examQuestionId, int answerId) throws SQLException {
		String insertAnswerQuery = "INSERT INTO ExamAnswer (ExamQuestionID, ExamAnswerName, ExamIsCorrect) "
				+ "SELECT ?, AnswerName, IsCorrect FROM Answer WHERE AnswerID = ?";
		PreparedStatement pstmt = SqlManager.getInstance().getConn().prepareStatement(insertAnswerQuery);
		pstmt.setInt(1, examQuestionId); // Sets the first ? with ExamQuestionID
		pstmt.setInt(2, answerId); // Sets the second ? with AnswerID
		pstmt.executeUpdate();

	}
	
	public int addQuestionToExam(int questionId, int examId) throws SQLException {
		// Insert the question into the ExamQuestion table
		String insertQuestionQuery = "INSERT INTO ExamQuestion (ExamQuestionName, ExamQuestionLevel, ExamkindOfQuestion, ExamID) "
				+ "SELECT QuestionName, QuestionLevel, kindOfQuestion, ? FROM Question WHERE QuestionID = ?";
		PreparedStatement insertStmt = SqlManager.getInstance().getConn().prepareStatement(insertQuestionQuery);
		insertStmt.setInt(1, examId); // Set the ExamID
		insertStmt.setInt(2, questionId); // Set the QuestionID
		insertStmt.executeUpdate();

	    // Retrieve the ExamQuestionID of the newly inserted question
	    String selectQuery = "SELECT ExamQuestionID FROM ExamQuestion WHERE ExamQuestionName = (SELECT QuestionName FROM Question WHERE QuestionID = ?) "
	                       + "AND ExamID = ? ORDER BY ExamQuestionID DESC LIMIT 1";
	    try (PreparedStatement selectStmt = SqlManager.getInstance().getConn().prepareStatement(selectQuery)) {
	        selectStmt.setInt(1, questionId);
	        selectStmt.setInt(2, examId);
	        ResultSet rs = selectStmt.executeQuery();

	        if (rs.next()) {
	            return rs.getInt("ExamQuestionID");
	        } else {
	            throw new SQLException("Failed to retrieve ExamQuestionID for the inserted question.");
	        }
	    }
	}
	public abstract String kindOfExam();


}
