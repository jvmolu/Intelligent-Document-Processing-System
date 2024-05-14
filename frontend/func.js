// <!DOCTYPE html>
{/* <html>
<head>
    <title>Sunbase RAG Assignment</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>

<body>
    <div class="container">
        <h1>Sunbase Assignment</h1>
        <form action="/query" method="POST">

            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required><br><br>
            
            <label for="upload">Upload File:</label>
            <input type="file" id="upload" name="upload" accept=".txt" required><br><br>

            <label for="text">File Preview:</label>
            <textarea id="text" name="text" rows="10" cols="50"></textarea><br><br>
            
            <button type="button" onclick="getTextFromFile()">Get Text From File</button><br><br>
            <button type="button" onclick="uploadText()">Submit File</button><br><br>

            <label for="query">Query:</label>
            <input type="text" id="query" name="query" required><br><br>
            <button type="button" onclick="hitQuery()">Hit Query</button><br><br>

            <label for="result">Result:</label>
            <textarea id="result" name="result" rows="10" cols="50"></textarea>

        </form>
    </div>
</body>

<script src="func.js"></script>

</html> */}



// When file is uploaded populate the text box with the content of the file
function getTextFromFile() {
    document.getElementById('text').value = '';
    var file = document.getElementById('upload').files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        document.getElementById('text').value = e.target.result;
    };
    reader.readAsText(file);
}


// When submit file button is clicked, send the file content to the server
function uploadText() {

    console.log('Uploading file...');

    var text = document.getElementById('text').value;
    var username = document.getElementById('username').value;
    
    // If any of the fields are empty, alert the user
    if (username === '') {
        alert('Please fill in username');
        return;
    }
    else if (text === '') {
        alert('Please upload a file');
        return;
    }

    var file_name = "";
    try {
        file_name = document.getElementById('upload').files[0].name;
    } catch (e) {
        alert('Please upload a file');
        return;
    }

    var response = fetch('http://localhost:8080/api/sunbaseRag/uploadText', {
        method: 'POST',
        headers: {
            'userId': username,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            file_name: file_name,
            file_content: text
        })
    })

    // If the response is successful, alert the user
    response.then((res) => {
        if (res.status === 200) {
            res.text().then((data) => {
                alert("File Upload: " + data);
            })
        } else {
            alert('Error uploading file');
        }
    })
}


// When hit query button is clicked, send the query to the server
function hitQuery() {

    document.getElementById('result').value = '';
    console.log('Hitting query...');

    var query = document.getElementById('query').value;
    var username = document.getElementById('username').value;

    // If any of the fields are empty, alert the user
    if (username === '') {
        alert('Please fill in username');
        return;
    }
    else if (query === '') {
        alert('Please fill in query');
        return;
    }

    var response = fetch('http://localhost:8080/api/sunbaseRag/query?query=' + query, {
        method: 'GET',
        headers: {
            'userId': username
        }
    })

    // If the response is successful, populate the result text box
    response.then((res) => {
        if (res.status === 200) {
            res.json().then((data) => {
                document.getElementById('result').value += '\nAnswer:\n';
                document.getElementById('result').value += '----------------------------------------\n';
                document.getElementById('result').value += data.response;
                document.getElementById('result').value += '\n----------------------------------------\n';
                // If there are citations, display them
                if (data.citations.length > 0) {
                    document.getElementById('result').value += '\n\nCitations:\n';
                    for (var i = 0; i < data.citations.length; i++) {
                        // Add a dotted line
                        document.getElementById('result').value += '----------------------------------------\n';
                        document.getElementById('result').value += 'File: ' + data.citations[i].fileName + '\n';
                        document.getElementById('result').value += data.citations[i].chunk + '\n\n';
                    }
                    // Add a dotted line
                    document.getElementById('result').value += '----------------------------------------\n';
                }
            })
        }
    })
}
