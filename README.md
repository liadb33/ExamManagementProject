# Exam Management System



## 📚 Description

The Exam Management System is an innovative tool designed to simplify the process of creating and managing exams. Our system offers a user-friendly interface for educators and administrators to efficiently handle various aspects of exam creation and management.

### 🌟 Key Features

- **Question Management**: Easily add, edit, and delete both multiple-choice and open-ended questions.
- **Flexible Exam Creation**: Choose between manual question selection or automatic exam generation from a question bank.
- **Question Categorization**: Organize questions effectively by subject and difficulty level.
- **Exam File Generation**: Produce final exam files ready for sharing or printing.

## 🏗 System Architecture

The system is built using the following technologies:
- **Backend**: Java with JDBC
- **Database**: PostgreSQL
- **Database Management**: pgAdmin 4

## 📊 Entity Relationship

Our system is composed of the following key entities:

### 1. Stock
- **Attributes**: StockID, StockName
- **Description**: Represents different subjects (e.g., Math, English, History) for question categorization.

### 2. Question
- **Attributes**: QuestionID, QuestionName, QuestionLevel, kindOfQuestion, StockID
- **Description**: Contains questions categorized by subject and difficulty level.

### 3. Answer
- **Attributes**: AnswerID, AnswerName, IsCorrect, QuestionID
- **Description**: Represents possible answers to questions, indicating correct options.

### 4. Exam
- **Attributes**: ExamID, Subject
- **Description**: Represents different exams or tests that include questions from various subjects.

### 5. ExamQuestion
- **Attributes**: ExamID, QuestionID
- **Description**: Junction table connecting exams and questions, allowing for a many-to-many relationship.

### 6. ExamAnswer
- **Attributes**: ExamAnswerID, ExamQuestionID, ExamAnswerName, ExamIsCorrect
- **Description**: Stores possible answers for each question in an exam, indicating correct options.

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK)
- PostgreSQL
- pgAdmin 4

### Installation
1. Clone the repository: git clone https://github.com/yourusername/exam-management-system.git

2. Set up the PostgreSQL database using the provided schema.
3. Configure the JDBC connection in the `config.properties` file.
4. Compile and run the Java application.

## 💡 Usage

1. **Adding Questions**: Navigate to the "Question Management" section to add new questions to the system.
2. **Creating Exams**: Use the "Exam Creation" module to either manually select questions or generate exams automatically.
3. **Generating Exam Files**: Once an exam is created, use the "File Generation" feature to produce printable or shareable exam documents.
