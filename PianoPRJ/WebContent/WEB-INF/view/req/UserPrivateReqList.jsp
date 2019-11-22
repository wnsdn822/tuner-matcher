<%@page import="poly.dto.ReqDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="poly.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- session.jsp 경로 설정 -->
<%@ include file="../user/session.jsp" %>
<%
	ArrayList<ReqDTO> reqList = (ArrayList<ReqDTO>)request.getAttribute("reqList");
%>
<!DOCTYPE html>
<html lang="en" data-textdirection="ltr" class="loading">
<head>

<meta charset="UTF-8">
<title>1:1 요청 목록 - 도와조율</title>
<!-- header.jsp 경로 설정 -->
<%@ include file="../header.jsp" %>
<style>
	.title{
		max-width: 40%;
	    overflow: hidden;
	    text-overflow: ellipsis;
	    white-space: nowrap;
	    
	}
</style>
</head>
<body  data-open="click" data-menu="vertical-menu" data-col="2-columns" class="vertical-layout vertical-menu 2-columns  fixed-navbar">
	<!-- menu.jsp 경로설정 -->
	<%@ include file="../menu.jsp" %>
	<div class="app-content content container-fluid">
      <div class="content-wrapper">
        <div class="content-body"><!-- Basic Tables start -->
<div class="row">
    <div class="col-xs-12">
        <div class="card">
            <div class="card-header">
                <h4 class="card-title">1:1 요청서 목록</h4>
                <a class="heading-elements-toggle"><i class="icon-ellipsis font-medium-3"></i></a>
            </div>
            <div class="card-body collapse in">
                <div class="card-block card-dashboard">
                    <div class="table-responsive">
                        <table class="table table-bordred table-striped">
                            <thead>
                                <tr class="row">
                                    <th>피아노 이름</th>
                                    <th>요청서 제목</th>
                                    <th>게시일</th>
                                    <th>조율사</th>
                                    <th>답변여부</th>
                                </tr>
                            </thead>
                            <tbody>
                            <%for(ReqDTO rDTO : reqList){ %>
                                <tr>
                                    <td><%=CmmUtil.nvl(rDTO.getPiano_name())%></td>
                                    <td><a href="/req/PrivateReqDetail.do?req_seq=<%=rDTO.getReq_seq()%>"><%=CmmUtil.nvl(rDTO.getReq_title()) %></a></td>
                                    
                                    <td><%=rDTO.getRegdate().substring(0,11) %></td>
                                    <td><%=rDTO.getPrivate_tuner_name() %></td>
                                    <td><%=rDTO.getBids().equals("0") ? "미답변" : "답변완료" %></td>
                                </tr>
                            <%} %>
                            </tbody>
                        </table>
                    </div>
                <div class="float-xs-right">
                <button class="btn btn-primary" onclick="location.href='/req/NewPrivateReq.do'">새  1:1 요청</button>
                
                </div>
                </div>
                
               

            
            </div>
            
        </div>
    </div>
</div>
<!-- Basic Tables end -->
        </div>
      </div>
    </div>
	
	
	
	
	
	<!-- footer.jsp 경로설정 -->
	<%@include file="../footer.jsp" %>
</body>
</html>