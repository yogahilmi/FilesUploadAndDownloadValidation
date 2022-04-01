## Environment:
- Java version: 1.8
- Maven version: 3.*
- Spring Boot version: 2.2.1.RELEASE

## Read-Only Files:
- src/test/*

## Data:
Structure of a file object when stored in database:
```json
{
   "id": 1,
   "fileGroup": "certificates",
   "fileName": "certificate1",
   "file": "bytes of file data"
}
```

## Requirements:
The REST service needs to expose 2 API endpoints for files upload and download.
Uploaded files needs to be stored in database. By default it supports H2 database.

Validations to be performed:
* If a file size exceeds 100KB, return status code INTERNAL_SERVER_ERROR and don't store file in database.
* Here you are expected to use file size limit constraint using configuration instead of doing programmatically.


Required API endpoints to be exposed

`POST` request to `/uploader` :
* this endpoint should be able to receive two parameters `fileGroup` and `files`
* store the files into database and return status code 201 as response
* if the user uploads same name's file with the same `fileGroup` again, the previous file should be replaced with the latest one and return status code 201.

`GET` request to `/downloader`:
* accept the `fileGroup` as a request parameter
* if there are more than one files under that `fileGroup` then it should return all the files
     of that group as a single zipped file like 'file_group_name.zip' with status code 200.
* if there is only one file then return that single file like `fileName` without zipping with status code 200.
* if there is no any files under that `fileGroup` then return 404 status code.

Your task is to complete the given project so that it passes all the test cases when running the provided unit tests. Required model class `XFile` for file has been already provided. You just need to implement the 2 endpoints in the controller class.

## Commands
- run: 
```bash
mvn clean package; java -jar target/project_jar-1.0.jar
```
- install: 
```bash
mvn clean install
```
- test: 
```bash
mvn clean test
```
