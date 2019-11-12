<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
    <title>Title</title>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<body>
<div class = "ml-3">
    <a href="auth/googleAuth" target="_blank" class="btn btn-primary">Google auth</a>

    <br/>
    <br/>

    JWT: <input type="text" id="Jwt" name="jwt"> <br/> <br/> <br/>
    <a class="btn btn-primary" onclick="files()">Show files</a>

    <br/>
    <br/>
    <br/>

    <form>
        File to upload: <input type="file" name="file" id="uploadFile"><br /><br />
        Name: <input type="text" name="name" id="uploadFilename"><br /><br />
        <input type="submit" value="Upload" onsubmit="upload()"> Press here to upload the file!
    </form>

    <br/>
    <br/>
    <br/>

    <form>
        Id: <input type="text" name="id" id="downloadFileId"><br /><br />
        <input type="submit" value="Download" onsubmit="download()"> Press here to download the file!
    </form>
</div>
</body>
</html>
<script>
    function authUrl() {
        $.get({
            url : "auth/googleAuth",
            success : function(data) {
                console.log(data)
            }
        })
    }

    function files() {
        $.post({
            url : "drive/files",
            headers : {
                "Authorization" : document.getElementById("Jwt").value
            },
            success : function(data) {
                console.log(data)
            }
        })
    }

    function download(){
        $.post({
            url : "drive/download",
            headers : {
                "Authorization" : document.getElementById("Jwt").value
            },
            data : {
                "id" : document.getElementById("downloadFileId").value
            },
            success : function(data) {
            }
        })
    }

    function upload() {
        $.post({
            url : "drive/upload",
            headers : {
                "Authorization" : document.getElementById("Jwt").value
            },
            data : {
                "file" : document.getElementById("uploadFile").value,
                "name" : document.getElementById("uploadFilename").value

            },
            enctype : "multipart/form-data",
            success : function(data) {
                console.log(data)
            }
        })
    }
</script>