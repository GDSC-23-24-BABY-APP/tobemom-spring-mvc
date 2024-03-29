package com.app.premom.repository;

import com.app.premom.entity.Diary;
import com.app.premom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByOrderByModifiedAtDesc();
    List<Diary> findByAuthor(User author);
}