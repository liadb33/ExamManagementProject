package examManagement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.postgresql.util.PSQLException;

import Exceptions.LowNumOfAnswers;
import Exceptions.OutOfRangeQuestions;
import java.sql.*;

public class Main {
	public static final int MAX_ANSWERS = 10;
	public static Scanner userInput;
	public static SqlManager sqlManager = null;
	public static ExamManager examManager;
	public static QuestionManager questionManager;
	public static AnswerManager answerManager;
	public static StockManager stockManager;

	public static void main(String[] args) throws Exception {
		sqlManager = SqlManager.getInstance();
		userInput = new Scanner(System.in);
		examManager = ExamManager.getInstance(userInput);
		questionManager = QuestionManager.getInstance(userInput);
		answerManager = AnswerManager.getInstance(userInput);
		stockManager = StockManager.getInstance(userInput);
		startProgram();
	}

	private static void startProgram() throws Exception {
		int choice = 0;

		do {
			System.out.println();
			System.out.println("Start:");
			System.out.println("1. Show All Subjects");
			System.out.println("2. Choose Subject");
			System.out.println("3. Add new Subject");
			System.out.println("4. Remove a subject incl. questions");
			System.out.println("Exit 0");

			choice = userInput.nextInt();
			switch (choice) {
			case 1:
				stockManager.printStocks();
				break;
			case 2:
				int chosenSubject = stockManager.chooseSubject();
				if(chosenSubject > 0)
					subjectMenu(chosenSubject);
				break;
			case 3:
				stockManager.addSubject();
				break;
			case 4:
				stockManager.removeSubject();
				break;
			default:
				if (choice != 0)
					System.out.println("Invalid Choice");
				break;
			}
		} while (choice != 0);

	}


	private static void subjectMenu(int subjectId) throws Exception {
		int choice;
		boolean isStockEmpty;

		do {

			isStockEmpty = stockManager.isStockEmpty(subjectId);

			if (isStockEmpty) {
				System.out.println();
				System.out.println("The Stock is empty , you have only 2 options:");
				System.out.println("Enter 2 to Add a new question in stock");
				System.out.println("Enter 0 to return to previous menu");
			} else {
				System.out.println();
				System.out.println("Menu:");
				System.out.println("1. Show all data of the stock");
				System.out.println("2. Add a new question in stock");
				System.out.println("3. Add new answer to a specific question in stock");
				System.out.println("4. Delete a question incl. answers in stock");
				System.out.println("5. Delete an answer from a question in stock");
				System.out.println("6. Edit a question name");
				System.out.println("7. Edit an answer name of a question");
				System.out.println("8. Create an exam file from StockQ&A file + solutions file");
				System.out.println("9. Change Subject");
				System.out.println("Enter 0 to exit program");
			}
			
			choice = userInput.nextInt();

			if (!isStockEmpty || (isStockEmpty && choice == 2)) {
				switch (choice) {
				case 1:
					questionManager.printQuestionAndAnswerStock(subjectId);
					break;
				case 2:
					questionManager.addQuestionToStock(subjectId);
					break;
				case 3:
					answerManager.addAnswerToStock(subjectId);
					break;
				case 4:
					questionManager.deleteQuestionFromStock(subjectId);
					break;
				case 6:
					questionManager.editQuestionFromStock(subjectId);
					break;
				case 7:
					answerManager.editAnswerFromStock(subjectId);
					break;
				case 5:
					answerManager.deleteAnswerFromStock(subjectId);
					break;
				case 8:
					examManager.createExamQandA(subjectId);
					break;
				case 9:
					break;
				default:
					if (choice != 0)
						System.out.println("Invalid Choice");
					break;
				}
			}
		} while (choice != 0 && choice != 9);
	}


}
