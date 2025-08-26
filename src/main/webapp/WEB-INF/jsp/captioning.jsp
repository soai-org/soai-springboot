<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>AI Captioning 결과</title>
<style>
    body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f8f9fa;
        margin: 0;
        padding: 0 30px;
    }

    h2 {
        color: #004085;
        margin-top: 30px;
        border-bottom: 2px solid #007bff;
        padding-bottom: 10px;
    }

    .patient-info {
        margin-top: 20px;
        background-color: #e9ecef;
        padding: 15px 20px;
        border-radius: 8px;
        display: flex;
        gap: 40px;
        font-weight: bold;
        color: #495057;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .image-container {
        display: flex;
        gap: 30px;
        margin-top: 25px;
        flex-wrap: wrap;
    }

    .image-box {
        background-color: #ffffff;
        padding: 15px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        text-align: center;
        flex: 1 1 500px;
    }

    .image-box p {
        font-weight: bold;
        margin-bottom: 10px;
        color: #007bff;
    }

    .image-box img {
        border-radius: 6px;
        max-width: 100%;
        height: auto;
        border: 1px solid #dee2e6;
        box-shadow: inset 0 0 5px rgba(0,0,0,0.05);
    }

    .caption-box {
        margin-top: 30px;
        padding: 20px;
        border-left: 5px solid #007bff;
        border-radius: 6px;
        background-color: #f1f7ff;
        box-shadow: 0 3px 6px rgba(0,0,0,0.1);
        color: #212529;
        font-size: 1.1rem;
    }

    .caption-box h3 {
        margin-top: 0;
        margin-bottom: 10px;
        color: #004085;
    }

    .btn-back {
        display: inline-block;
        margin-top: 30px;
        padding: 10px 20px;
        background-color: #007bff;
        color: #fff;
        border-radius: 6px;
        text-decoration: none;
        font-weight: bold;
        box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        transition: all 0.2s ease-in-out;
    }

    .btn-back:hover {
        background-color: #0056b3;
        transform: translateY(-2px);
        box-shadow: 0 4px 6px rgba(0,0,0,0.25);
    }
</style>
</head>
<body>

<h2>AI Captioning 결과</h2>

<!-- 환자 정보 -->
<div class="patient-info">
    <div>환자 ID: ${patientId}</div>
    <div>Study ID: ${studyId}</div>
    <div>Series ID: ${seriesId}</div>
    <div>Instance ID: ${instanceId}</div>
</div>

<!-- 이미지 섹션 -->
<div class="image-container">
    <div class="image-box">
        <p>원본 이미지</p>
        <img src="${instancePreviewUrl}" alt="Original Image"/>
    </div>

    <c:if test="${not empty segmentationImage}">
        <div class="image-box">
            <p>Segmentation Overlay</p>
            <img src="${segmentationImage}" alt="Segmentation Image"/>
        </div>
    </c:if>
</div>

<!-- Captioning 결과 -->
<c:if test="${not empty transcript}">
    <div class="caption-box">
        <h3>📄 Captioning 결과</h3>
        <p>${transcript}</p>
    </div>
</c:if>

<!-- 뒤로가기 버튼 -->
<a href="${pageContext.request.contextPath}/dashboard" class="btn-back">◀ dashboard 페이지로 돌아가기</a>

</body>
</html>
