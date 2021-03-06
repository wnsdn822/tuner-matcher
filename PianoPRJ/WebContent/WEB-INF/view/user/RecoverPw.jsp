<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>암호 초기화</title>
	<%@include file="../header.jsp" %>
</head>
<body>

	<div class="app-content content container-fluid">
      <div class="content-wrapper">
        <div class="content-header row">
        </div>
        <div class="content-body"><section class="flexbox-container">
    <div class="col-md-4 offset-md-4 col-xs-10 offset-xs-1 box-shadow-2 p-0">
        <div class="card border-grey border-lighten-3 px-2 py-2 m-0">
            <div class="card-header no-border pb-0">
                <div class="card-title text-xs-center">
                    <img src="../../app-assets/images/logo/logo.png" alt="branding logo">
                </div>
                <h6 class="card-subtitle line-on-side text-muted text-xs-center font-small-3 pt-2"><span>암호 초기화</span></h6>
            </div>
            <div class="card-body collapse in">
                <div class="card-block">
                    <form class="form-horizontal" action="/user/RecoverPwProc.do" method="post">
                        <fieldset class="form-group position-relative has-icon-left">
                            <input type="text" class="form-control form-control-lg input-lg" id="id" name="id" placeholder="아이디 입력" required>
                            <div class="form-control-position">
                                <i class="icon-head"></i>
                            </div>
                        </fieldset>
                        <button type="submit" class="btn btn-primary btn-lg btn-block"><i class="icon-lock4"></i>암호 초기화</button>
                    </form>
                </div>
            </div>
            <div class="card-block text-xs-center">
                	<button type="button" class="btn btn-primary btn-lg" onclick="location.href='/user/UserLogin.do'">처음으로</button>
                </div>
            <div class="card-footer no-border">
                <p class="float-sm-left text-xs-center"><a href="login.do" class="card-link">로그인</a></p>
                <p class="float-sm-right text-xs-center">회원이 아니신가요? <a href="UserReg.do" class="card-link">회원가입</a></p>
            </div>
        </div>
    </div>
</section>

        </div>
      </div>
    </div>
</body>
</html>