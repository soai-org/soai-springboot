package com.team1.soai.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class FindLvPatientVO {
    private String id;
    private MainDicomTagsVO mainDicomTagsVO;
}
