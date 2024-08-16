<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- header.jsp -->
<%@include file="/WEB-INF/view/layout/header.jsp"%>

<!-- start of content.jsp(xxx.jsp) -->
<div class="col-sm-8">
	<h2>로그인</h2>
	<h5>Bank App에 오신걸 환영합니다</h5>
	<!-- 로그인은 예외적으로 보안 때문에 post로 처리하는것이 좋다 -->
	<form action="/user/sign-in" method="post">
		<div class="form-group">
			<label for="username">username:</label> <input type="text" class="form-control" placeholder="Enter username" id="username" name="username" value="길동">
		</div>
		<div class="form-group">
			<label for="pwd">Password:</label> <input type="password" class="form-control" placeholder="Enter password" id="pwd" name="password" value="1234">
		</div>
		<div>
		<button type="submit" class="btn btn-primary">로그인</button>
			<a href="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=c416e84616208b50d0318d27f1301c29&redirect_uri=http://localhost:8080/user/kakao"> <img
				alt="소셜로그인이미지" src="/images/kakao_login_small.png">
			</a>
		</div>
	</form>
</div>
<!-- end of col-sm-8 -->
</div>
</div>
<!-- end of content.jsp(xxx.jsp) -->

<!-- footer.jsp -->
<%@include file="/WEB-INF/view/layout/footer.jsp"%>
