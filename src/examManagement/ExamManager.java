package examManagement;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ExamManager {

	private static ExamManager instance = null;
	private Scanner userInput;

	public ExamManager(Scanner userInput) {
		super();
		this.userInput = userInput;
	}

	public static synchronized ExamManager getInstance(Scanner userInput) {
		if (instance == null)
			instance = new ExamManager(userInput);

		return instance;
	}

	public void createExamQandA(int subjectId) throws Exception {

		System.out.println("Enter which kind of Exam would you like, Manual or Automatic");
		String line = userInput.next().toLowerCase();
		while (!line.equals("manual") && !line.equals("automatic")) {
			System.out.println("Wrong input, Manual or Automatic");
			line = userInput.next().toLowerCase();
		}
		System.out.println("Enter Exam Name");
		String examName = userInput.nextLine();
		examName = userInput.nextLine();
		SqlManager.getInstance().updateQuery("INSERT INTO Exam (Subject) VALUES" + "('" + examName + "')");
		ResultSet rs = SqlManager.getInstance().selectQuery("SELECT * FROM Exam WHERE Subject LIKE '" + examName + "'");
		rs.next();
		int examId = rs.getInt("ExamID");

		if (line.equals("manual")) {
			Exam manualExam = new ManualExam();
			manualExam.createExam(userInput, subjectId, examId);
		} else {
			Exam automaticExam = new AutomaticExam();
			automaticExam.createExam(userInput, subjectId, examId);
		}
		printExamAndSolutions(examId);

	}

	private static void printExamAndSolutions(int examId) throws SQLException, IOException {
		String format = "__YYYY_MM_dd_hh_mm";
		SimpleDateFormat df = new SimpleDateFormat(format);
		File examFile = new File("src\\Exams\\exam" + df.format(new Date()) + ".txt");
		File solutionFile = new File("src\\ExamsSolutions\\solution" + df.format(new Date()) + ".txt");
		examFile.createNewFile();
		solutionFile.createNewFile();

		PrintWriter examWriter = new PrintWriter(examFile);
		PrintWriter solutionWriter = new PrintWriter(solutionFile);

		// Retrieve and print the exam name
		String examNameQuery = "SELECT Subject FROM Exam WHERE ExamID = ?";
		PreparedStatement examNameStmt = SqlManager.getInstance().getConn().prepareStatement(examNameQuery);
		examNameStmt.setInt(1, examId);
		ResultSet examNameRs = examNameStmt.executeQuery();
		if (examNameRs.next()) {
			String examName = examNameRs.getString("Subject");
			examWriter.println("Exam: " + examName);
		}

		// Print introductory sentence
		examWriter.println("Answer the following questions:");
		examWriter.println(); // Add a blank line for separation

		String examQuery = "SELECT * FROM ExamQuestion WHERE ExamID = ?";
		PreparedStatement examStmt = SqlManager.getInstance().getConn().prepareStatement(examQuery);
		examStmt.setInt(1, examId);
		ResultSet examRs = examStmt.executeQuery();

		int questionNumber = 1; // Initialize question number counter

		while (examRs.next()) {
			String questionName = examRs.getString("ExamQuestionName");
			String kindOfQuestion = examRs.getString("ExamkindOfQuestion");

			// Print the question number and details
			examWriter.println("Question " + questionNumber + ": " + questionName + " Type: " + kindOfQuestion + "\n");

			// Get answers for each question directly from the database
			int examQuestionId = examRs.getInt("ExamQuestionID");
			String answerQuery = "SELECT ExamAnswerName, ExamIsCorrect FROM ExamAnswer WHERE ExamQuestionID = ?";
			PreparedStatement answerStmt = SqlManager.getInstance().getConn().prepareStatement(answerQuery);
			answerStmt.setInt(1, examQuestionId);
			ResultSet answerRs = answerStmt.executeQuery();

			int answerNumber = 1; // Initialize answer number counter
			while (answerRs.next()) {
				String answerName = answerRs.getString("ExamAnswerName");
				boolean isCorrect = answerRs.getBoolean("ExamIsCorrect");

				// Print the answer number and details
				if (kindOfQuestion.equals("Close")) 
					examWriter.println(answerNumber + ". Answer: " + answerName);
				if (isCorrect) 
					solutionWriter.println("Question " + questionNumber + ": " + questionName + " Correct Answer: "
							+ answerNumber + ". " + answerName);
				answerNumber++; // Increment answer number
			}
			examWriter.println();
			questionNumber++;
		}
		examWriter.close();
		solutionWriter.close();

		System.out.println("Exam and solutions have been created.");
	}

}
