package com.spring.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.common.AES256;
import com.spring.model.MemberVO;
import com.spring.model.InterMemberDAO;

@Service  
public class MemberService implements InterMemberService {

	@Autowired
	private InterMemberDAO dao;
	
	@Autowired
	private AES256 aes;
	
	

	// === 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(HashMap<String, String> paraMap) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
		if(loginuser != null) {
			
			if(loginuser.getLastlogindategap() >= 12) {
				// 마지막으로 로그인 한 날짜시간이 현재일로 부터 1년(12개월)이 지났으면 해당 로그인 계정을 비활성화(휴면)시킨다.
				loginuser.setIdleStatus(true);
			}
			else {
				if(loginuser.getPwdchangegap() > 3) {
					// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
					loginuser.setRequirePwdChange(true);
				}
				 
				dao.setLastLoginDate(paraMap); // 마지막으로 로그인 한 날짜시간 변경(기록)하기
				
				try {
					loginuser.setEmail(aes.decrypt(loginuser.getEmail()));
					// loginuser 의 email을 복호화 하도록 한다.
				} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					e.printStackTrace();
				} 
			}
			
		}
		
		return loginuser;
	}
	
	
	

	/////////// ~~~ 카카오 ~~~ ///////////////
	// 카카오 회원으로 등록이 되어있는지 확인
	@Override
	public MemberVO kakaoMember(HashMap<String, String> paraMap) {
		
		
		//System.out.println("email test : " +paraMap.get("email"));
		
		MemberVO loginuser = dao.kakaoMember(paraMap);
		
		return loginuser;
	}

	// 아이디 중복 확인
	@Override
	public String idDuplicateCheck(String userid) {
		
		String id = dao.idDuplicateCheck(userid);
		
		return id;
	}

	// 카카오 회원가입
	@Override
	public int kakaoRegister(MemberVO membervo) {

		int n = dao.kakaoRegister(membervo);
		
		return n;
	}

	// 이메일 중복
	@Override
	public String emailDuplicateCheck(String email) {

		String i = dao.emailDuplicateCheck(email);
		
		return i;
	}


	// 일반 회원 가입
	@Override
	public int register(MemberVO membervo) {

		int n = dao.register(membervo);
		
		return n;
	}

	
	// 카카오 로그인시 kakaoStatus 1로 변경
	@Override
	public void kakaoStatus(String email) {
		
		dao.kakaoStatus(email);
		
	}

	// 네이버 로그인시 naverStatus 1fh 변경
	@Override
	public void naverStatus(String email) {
		dao.naverStauts(email);
	}

	// 네이버 회원가입
	@Override
	public int naverRegister(MemberVO membervo) {
		int n = dao.naverRegister(membervo);
		return n;
	}
	
	
	
	
}
