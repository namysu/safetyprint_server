# safetyprint_server

### client
https://github.com/namysu/safetyprint_client

### API reference
```text
/v1/get/user
/v1/get/documents
/v1/get/files

/v1/get/user/{id}
-> response
    id: {
        id: String
        password: String
        name: String
        division: String
        documentlist: {
            (encrypt code)
        }
    }

/v1/get/document/{code}
-> response
    code: {
        openuser: String
        contactuser: String
        opendate: String(yyyy-mm-dd hh:mm)
        closedate: String(yyyy-mm-dd hh:mm)
        description: String
        scanneremail: String
        scanname: String
        data:{
            (filename)
        }
    }

/v1/get/document/{code}/scanname
-> response
{
    scanname: String
}

/v1/get/data/{code}/{filename}
-> response
    multi-part file data

/v1/post/user/login
-> require
    {
        id: String
        password: String
    }
-> response
if ok then 200, else 400, 404, 500..
{
    id: String
}

/v1/post/user/register
-> require
    {
        id: String
        password: String
        name: String
        division: String
    }
-> response
if ok then 200, user already 401, require miss 400
{
    status: code
}

/v1/post/document/create
-> require
    {
        openuser: String
        opendate: String(yyyy-mm-dd hh:mm)
        closedate: String(yyyy-mm-dd hh:mm)
        description: String
    }
-> response
if ok then 200, require miss 400
{
    status: code
}

/v1/post/document/addcontactuser
-> require
    {
        name: String
        code: String
    }
-> response
if ok then 200, require miss 400, code not found 404
{
    status: code
}

/v1/post/file/{code}/upload
-> require
    {
        filename: String
        filedata: hex?
    }
-> response
if ok then 200, require miss 400
{
    status: code
}

/v1/post/document/{code}/scanner/add
-> require
{
    email: String
    name: String
}
-> if ok then 200, require miss 400
{
    status: code
}
```