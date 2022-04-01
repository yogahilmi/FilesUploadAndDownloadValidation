package com.hackerrank.files;

import com.hackerrank.files.repository.XFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ApplicationTests {
    @Autowired
    XFileRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testMultipleFileUpload() {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);
        Map<String, byte[]> files = new HashMap<>();

        for (int i = 0; i < getRandom(10); i++) {
            files.put("file_" + getRandom(Integer.MAX_VALUE) + ".txt", new byte[getRandom(100) * 1024]);
        }

        ResponseEntity<Void> response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(files.size(), repository.findByFileGroup(fileGroup).size());
        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            assertEquals(kv.getValue().length, repository.findByFileGroupAndFileName(fileGroup, kv.getKey()).getFile().length);
        }
    }

    @Test
    public void testDuplicateFileUpload() {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);

        Map<String, byte[]> files = new HashMap<>();
        String fileName = "file_" + getRandom(Integer.MAX_VALUE) + ".txt";
        byte[] file = new byte[getRandom(100) * 1024];
        files.put(fileName, file);
        ResponseEntity<Void> response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(files.size(), repository.findByFileGroup(fileGroup).size());
        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            assertEquals(kv.getValue().length, repository.findByFileGroupAndFileName(fileGroup, kv.getKey()).getFile().length);
        }

        file = new byte[getRandom(99) * 1024];
        files.put(fileName, file);
        response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(files.size(), repository.findByFileGroup(fileGroup).size());
        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            assertEquals(kv.getValue().length, repository.findByFileGroupAndFileName(fileGroup, kv.getKey()).getFile().length);
        }
    }

    @Test
    public void testFileSizeExceeds() {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);

        Map<String, byte[]> files = new HashMap<>();
        String fileName1 = "file_" + getRandom(Integer.MAX_VALUE) + ".txt";
        String fileName2 = "file_" + getRandom(Integer.MAX_VALUE) + ".txt";
        byte[] file1 = new byte[getRandom(100) * 1024]; //valid
        byte[] file2 = new byte[getRandom(1) * 1024 * 1024]; //exceeds size
        files.put(fileName1, file1);
        files.put(fileName2, file2);

        ResponseEntity<String> response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(0, repository.findByFileGroup(fileGroup).size());
    }

    @Test
    public void testMultipleFileDownload() throws IOException {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);

        Map<String, byte[]> files = new HashMap<>();
        for (int i = 0; i < getRandom(10); i++) {
            files.put("file_" + getRandom(Integer.MAX_VALUE), new byte[getRandom(100) * 1024]);
        }

        ResponseEntity<Void> response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(files.size(), repository.findByFileGroup(fileGroup).size());
        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            assertEquals(kv.getValue().length, repository.findByFileGroupAndFileName(fileGroup, kv.getKey()).getFile().length);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> fileResponse = restTemplate.exchange("/downloader?fileGroup=" + fileGroup, HttpMethod.GET, entity, byte[].class);

        assertEquals(HttpStatus.OK, fileResponse.getStatusCode());
        Map<String, byte[]> dFiles = ZipHelper.unzip(fileResponse.getBody());
        for (String file : dFiles.keySet()) {
            assertEquals(files.get(file).length, dFiles.get(file).length);
        }
    }

    @Test
    public void testSingleFileDownload() throws IOException {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);

        Map<String, byte[]> files = new HashMap<>();
        files.put("file_" + getRandom(Integer.MAX_VALUE), new byte[getRandom(100) * 1024]);

        ResponseEntity<Void> response = restTemplate.postForEntity("/uploader", prepareRequest(fileGroup, files), Void.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(files.size(), repository.findByFileGroup(fileGroup).size());
        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            assertEquals(kv.getValue().length, repository.findByFileGroupAndFileName(fileGroup, kv.getKey()).getFile().length);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> fileResponse = restTemplate.exchange("/downloader?fileGroup=" + fileGroup, HttpMethod.GET, entity, byte[].class);

        assertEquals(HttpStatus.OK, fileResponse.getStatusCode());
        assertEquals(files.values().iterator().next().length, fileResponse.getBody().length);
    }

    @Test
    public void test404FileDownload() throws IOException {
        String fileGroup = "fileGroup_" + getRandom(Integer.MAX_VALUE);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.ALL));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> fileResponse = restTemplate.exchange("/downloader?fileGroup=" + fileGroup, HttpMethod.GET, entity, byte[].class);

        assertEquals(HttpStatus.NOT_FOUND, fileResponse.getStatusCode());
    }

    private HttpEntity<MultiValueMap<String, Object>> prepareRequest(String fileGroup, Map<String, byte[]> files) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("fileGroup", fileGroup);

        for (Map.Entry<String, byte[]> kv : files.entrySet()) {
            MultiValueMap<String, String> fileInfo = new LinkedMultiValueMap<>();
            ContentDisposition fileDetails = ContentDisposition
                    .builder("form-data")
                    .name("files")
                    .filename(kv.getKey())
                    .build();
            fileInfo.add(HttpHeaders.CONTENT_DISPOSITION, fileDetails.toString());
            HttpEntity<byte[]> fileEntity = new HttpEntity<>(kv.getValue(), fileInfo);

            parameters.add("files", fileEntity);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, headers);

        return requestEntity;
    }

    private int getRandom(int upperBoundary) {
        Random random = new Random(1);
        return random.nextInt(upperBoundary) + 1;
    }
}
