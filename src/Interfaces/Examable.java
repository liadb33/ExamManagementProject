package Interfaces;

import java.sql.SQLException;
import java.util.Scanner;

import Exceptions.LowNumOfAnswers;

import Exceptions.OutOfRangeQuestions;
import examManagement.*;

public interface Examable {

	void createExam(Scanner userInput,int subjectId,int examId) throws OutOfRangeQuestions, LowNumOfAnswers, SQLException;

}
