package com.app.premom.service;

import com.app.premom.dto.CalCheckListResultDto;
import com.app.premom.dto.CheckListCalResponseDto;
import com.app.premom.dto.CheckListResponseDto;
import com.app.premom.entity.CheckList;
import com.app.premom.entity.CheckListResult;
import com.app.premom.entity.User;
import com.app.premom.repository.CheckListRepository;
import com.app.premom.repository.CheckListResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CheckListService {

    private final CheckListRepository checkListRepository;
    private final CheckListResultRepository checkListResultRepository;

    public CheckListResponseDto findByNum(int num) {
        CheckList checkList = checkListRepository.findByNum(num).orElseThrow(()-> new IllegalArgumentException("해당 num인 체크리스트가 존재하지 않습니다."));
        return new CheckListResponseDto(checkList);
    }


    // 체크리스트 계산 결과 정보 조회
    public CheckListCalResponseDto getResultInfo(CalCheckListResultDto dto) {
        CheckList checkList = checkListRepository.findByNum(dto.getCheckListNum()).orElseThrow(()-> new IllegalArgumentException("해당 id 의 체크리스트가 존재하지 않슴니다."));

        int result = 0;
        List<String> checkedQ = new ArrayList<>();
        for (int i = 0; i < checkList.getQuestions().size(); i++) {
            log.info("질문 개수: " + checkList.getQuestions().size());
            int risk = checkList.getQuestions().get(i).getRisk();
            int isChecked = dto.getAnswerList().get(i);
            if (isChecked > 0) {
                checkedQ.add(checkList.getQuestions().get(i).getSymptom());
            }

            result += risk * isChecked;
        }

        // 체크리스트 num 값이 0이면 초기(유산 질문), 1이면 중기, 2이면 후기(사산 질문)


        // result 값이 몇 이상인지에 따라 사산 위험(4), 사산 위험 의심(3), 유산 위험(2), 유산 위험 의심 (1), 건강함(0) 중 하나로 결과 보냄.
        if (result >= 10) {
            //...
            if (checkList.getNum() == 0) {
                // 초기, 유산 위험
                return new CheckListCalResponseDto(2, checkedQ);
            }

            else if (checkList.getNum() > 0 ) {
                // 중기 및 후기, 사산 위험
                return new CheckListCalResponseDto(4, checkedQ);
            }
        }
        else if (result >= 7) {
            //...
            if (checkList.getNum() == 0) {
                // 초기, 유산 위험 의심
                return new CheckListCalResponseDto(1, checkedQ);
            }

            else if (checkList.getNum() > 0 ) {
                // 중기 및 후기, 사산 위험 의심
                return new CheckListCalResponseDto(3, checkedQ);
            }
        }

        // 건강함
        return new CheckListCalResponseDto(0, checkedQ);

    }

    @Transactional
    public Long saveCheckListResult(User user, int result) {
        CheckListResult checkListResult = CheckListResult.createCheckListResult(user, result);
        return checkListResultRepository.save(checkListResult).getId();
    }

    public int findResultByUserAndDate(User user, LocalDateTime dateTime) {
        return checkListResultRepository.findByUserAndCreatedAt(user, dateTime).orElseThrow(() -> new IllegalArgumentException("해당 유저와 작성시간의 체크리스트 결과 정보가 존재하지 않습니다.")).getResult();
    }

//    @Transactional
//    public List<CheckListResponseDto> getPosts() {
//        return checkListRepository.findAllByOrderModifiedAtDesc().stream().map(CheckListResponseDto::new).toList();
//    }
//
//    @Transactional
//    public Long save(User user, CheckListSaveRequestDto dto) {
//        CheckList checkList = dto.toEntity(user);
//        CheckList savedCheckList = checkListRepository.save(checkList);
//        return savedCheckList.getId();
//    }
}