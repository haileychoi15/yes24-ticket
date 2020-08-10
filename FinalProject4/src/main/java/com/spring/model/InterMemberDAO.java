package com.spring.model;

import java.util.HashMap;

import com.spring.model.MemberVO;

public interface InterMemberDAO {

	MemberVO getLoginMember(HashMap<String, String> paraMap); // 로그인 처리하기 
	void setLastLoginDate(HashMap<String, String> paraMap);   // 마지막으로 로그인 한 날짜시간 변경(기록)하기
	
	
	/////////////////// ~~ 카카오 ~~ ///////////////////////// 
	MemberVO kakaoMember(HashMap<String, String> paraMap); // 카카오 회원으로 등록이 되어있는지 확인
	
	String idDuplicateCheck(String userid); // 아이디 중복 유무
	
	int kakaoRegister(MemberVO membervo); // 카카오 회원가입
	
	String emailDuplicateCheck(String email); // 이메일 중복 유무
	
	int register(MemberVO membervo); // 일반 회원가입
	
	void kakaoStatus(String email); // 카카오 로그인시 kakaoStatus 1로 변경
	void naverStauts(String email); // 네이버 로그인시 naverStatus 1로 변경
	
	int naverRegister(MemberVO membervo); // 네이버 회원 가입
	
	
}
