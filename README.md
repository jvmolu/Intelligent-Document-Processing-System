# Intelligent Document Processing RAG System 🔎📚

This document processing system is designed to efficiently analyze user documents and provide accurate responses to user queries related to the content. Powered by advanced algorithms, it offers a seamless experience for users seeking insights or information within their documents. Whether you're searching for specific details, or extracting key information, this system is equipped to handle diverse document processing tasks with precision and speed.

## Installation:

Prerequisites: Java 17 or higher, Maven should be set up in the system for the backend to work.

1. Clone the repository
2. Download the Glove word embeddings using this link: http://nlp.stanford.edu/data/glove.6B.zip and extract the zip file
3. Navigate to the project directory
4. Setup the application.properties file in the project in its default location [A Template for application.properties is provided in the repository]
6. Build the project: _mvn clean install_
7. Start the spring project
8. Open the frontend.html file, located in the 'frontend' folder in your browser.

## Usage:

**Enter Username:** Begin by typing your username into the designated field.

Now, you can either submit a new document or query your submitted documents

### ---- **Submit a File**

**Choose File:** Click the "Choose File" button to select the file you want to upload.

**Get Text from File:** After selecting the file, click the "Get Text from File" button. The chatbot will preview the text data extracted from the file below.

**Submit File:** If you're satisfied with the text preview and want to ask questions based on the file content, click the "Submit File" button.

### ---- **Query your files**

**Hit Query Button:** Once you've entered your data, click on the "Hit Query" button to submit your query to the chatbot.

**View Results:** The chatbot will process your query and display the result in the result box below. You can then review the response provided by the chatbot.

## Future Enhancements:

1. Support for multiple file formats.
2. Add a loading icon in the front end, signifying when the user request is being processed.
3. Better prompt engineering to make query answers accurate and more aligned with the input data.
