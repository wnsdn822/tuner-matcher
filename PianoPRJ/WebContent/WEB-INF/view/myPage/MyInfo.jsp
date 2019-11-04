<%@page import="poly.dto.TunerDTO"%>
<%@page import="poly.dto.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- session.jsp 경로 설정 -->
<%@ include file="../user/session.jsp" %>

<%
	UserDTO uDTO = (UserDTO)request.getAttribute("uDTO");
	String userTypeKor = "일반 사용자";
	TunerDTO tDTO = null;
	if(user_type.equals("1")){
		userTypeKor = "조율사";
		tDTO = (TunerDTO)request.getAttribute("tDTO");
		
	}
	
%>
<!DOCTYPE html>
<html lang="en" data-textdirection="ltr" class="loading">
<head>

<meta charset="UTF-8">
<title>페이지 타이틀</title>
<!-- header.jsp 경로 설정 -->
<%@ include file="../header.jsp" %>
</head>
<body  data-open="click" data-menu="vertical-menu" data-col="2-columns" class="vertical-layout vertical-menu 2-columns  fixed-navbar">
	<!-- menu.jsp 경로설정 -->
	<%@ include file="../menu.jsp" %>
	
	<div class="app-content content container-fluid">
      <div class="content-wrapper">
        <div class="content-body"><!-- Basic example section start -->
<!-- Header footer section start -->
<section id="header-footer">
	<div class="row match-height">
		<div class="col-xs-12 col-md-8 offset-md-2 col-lg-6 offset-lg-3">
			<div class="card">
				<div class="card-header">
					<h4 class="card-title">개인정보</h4>
				</div>
				<div class="card-body">
					<div class="card-block text-xs-center">
					<img class="rounded mx-auto d-block" src="/resources/app-assets/images/portrait/small/avatar-s-2.png" alt="Card image cap">
						<br>
						<h5><%=uDTO.getUser_nick() %>(<%=userTypeKor %>)</h5>
					</div>
					<div class="card-block">
						<p class="card-text">이름 : <%=uDTO.getUser_name() %></p>
						<p class="card-text">이메일 : <%=uDTO.getEmail() %></p>
						<p class="card-text">전화번호 : <%=uDTO.getUser_tel() %></p>
						<%if(user_type.equals("1")){ %>
						<p class="card-text">자격증 등급 : <%=tDTO.getTuner_level().equals("0") ? "기능사" : "산업기사" %></p>
						<p class="card-text">소속 : <%=CmmUtil.revertXSS(tDTO.getAffiliation()) %></p>
						<p class="card-text">근무지 : <%=CmmUtil.revertXSS(tDTO.getAddr()) %></p>
						<p class="card-text">활동지역 : </p>
						<p class="card-text">한줄소개 : <%=CmmUtil.revertXSS(CmmUtil.nvl(tDTO.getTuner_comment(), "없음")) %></p>
						<p class="card-text">이력 : <%if(CmmUtil.nvl(tDTO.getTuner_exp()).equals("")){ out.print("없음");%></p>
						<%}else{ %>
						<p class="card-text"><%=CmmUtil.revertXSS(tDTO.getTuner_exp()) %></p>
						<%} %>
						<%} %>
					</div>
				</div>
				<div class="card-footer border-top-blue-grey border-top-lighten-5 text-muted">
					<span class="float-xs-right">
						<a href="#" class="button btn btn-sm btn-info">정보 수정 </a>
						<a href="#" class="button btn btn-sm btn-danger">회원 탈퇴 </a>
						
					</span>
				</div>
			</div>
		</div>
	</div>
</section>
<!-- Header footer section end -->

        </div>
      </div>
    </div>
	
	
	
	
	<!-- footer.jsp 경로설정 -->
	<%@include file="../footer.jsp" %>
</body>
</html>