<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- session.jsp 경로 설정 -->
<%@ include file="../user/session.jsp" %>
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
			<div class="content-body">
				<div class="row">
					<div class="col-xs-12 col-lg-6 offset-lg-3">
					<div class="card">
				<div class="card-header">
					<h4 class="card-title" id="basic-layout-form">공지 작성</h4>
				</div>
				<div class="card-body collapse in">
					<div class="card-block">
						<form class="form" name="noticeForm" action="/notice/NoticeInsert.do" method="post">
							<input hidden="hidden" id="notice_content" name="notice_content">
							<div class="form-body">
								<div class="form-group">
									<label for="notice_title">공지 제목</label>
									<input type="text" id="notice_title" maxlength="50" class="form-control" placeholder="공지 제목을 입력해주세요" name="notice_title">
								</div>
								<div class="form-group">
                                    <input type="checkbox" name="top" value="Y" id="top" class="chk-remember">
                                    <label for="top">상단 공지에 등록</label>
                                </div>
								 
								<div class="form-group">
									<label for="notice_content">공지 내용</label>
									<textarea id="temp_content" onchange="checkBytes(this, 4000);" onKeyUp="checkBytes(this, 4000);" rows="10" class="form-control" placeholder="공지 내용을 입력해주세요"></textarea>
									<div class="float-xs-right"><span class="byte">0</span>/4000 bytes</div>
								</div>
							</div>

							<div class="form-actions">
								<button onclick="location.href='/notice/NoticeList.do'" type="button" class="btn btn-warning mr-1">
									<i class="icon-cross2"></i> 취소
								</button>
								<button type="button" class="btn btn-primary" onclick="submitNotice();">
									<i class="icon-check2"></i> 작성
								</button>
							</div>
						</form>
					</div>
				</div>
			</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script>
	var form = document.noticeForm
	function submitNotice(){
		$("#notice_content").val(document.getElementById('temp_content').value.trim().replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/\n/g, "<br>"));
		$("#notice_title").val($("#notice_title").val().replace(/<br>/g, " "));
		form.submit();
	}
	
	</script>
	
	
	
	
	
	
	<!-- footer.jsp 경로설정 -->
	<%@include file="../footer.jsp" %>
	<script src="/resources/js/bytechecker.js" type="text/javascript"></script>
</body>
</html>