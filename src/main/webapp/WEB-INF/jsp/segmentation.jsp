<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>AI 분석 페이지</title>
<link rel="icon" href="${pageContext.request.contextPath}/tracking.png" type="image/x-icon">
<style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    .image-container { display: flex; gap: 20px; margin-top: 20px; }
    .image-box { text-align: center; }
    .image-box img { border: 1px solid #ccc; }
    .btn-container { margin-top: 30px; }
    .btn-container form { display: inline; }
    .btn {
        padding: 10px 20px;
        font-size: 16px;
        border-radius: 6px;
        border: none;
        cursor: pointer;
        background-color: #007bff;
        color: #fff;
    }
    .btn:hover {
        background-color: #0056b3;
    }
</style>
</head>
<body>
<h2>AI 분석 페이지</h2>

<p>환자 ID: ${patientId}</p>
<p>Study ID: ${studyId}</p>
<p>Series ID: ${seriesId}</p>
<p>Instance ID: ${instanceId}</p>

<!-- 원본 이미지 & Segmentation Overlay 나란히 -->
<div class="image-container">
    <div class="image-box">
        <p>원본 이미지</p>
        <img src="${instancePreviewUrl}" width="500"/>
    </div>

    <c:if test="${not empty segmentationImage}">
        <div class="image-box">
            <p>Segmentation Overlay</p>
            <img src="${segmentationImage}" width="500"/>
        </div>
    </c:if>
</div>

<c:if test="${not empty segmentationError}">
    <p style="color:red;">${segmentationError}</p>
</c:if>

<!-- Captioning 페이지로 이동 버튼 -->
<div class="btn-container">
    <form action="/x-ray/captioning" method="post">
        <input type="hidden" name="patientId" value="${patientId}" />
        <input type="hidden" name="studyId" value="${studyId}" />
        <input type="hidden" name="seriesId" value="${seriesId}" />
        <input type="hidden" name="instanceId" value="${instanceId}" />
        <button type="submit" class="btn">📄 Captioning 실행</button>
    </form>
</div>

</body>
</html>
