package com.hackerrank.files.repository;

import com.hackerrank.files.model.XFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface XFileRepository extends JpaRepository<XFile, Long> {

    //don't delete
    List<XFile> findByFileGroup(String fileGroup);

    //don't delete
    XFile findByFileGroupAndFileName(String fileGroup, String fileName);
}
