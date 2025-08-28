<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>환자 검색</title>
<style>
    body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f8f9fa;
        margin: 0;
        padding: 20px;
    }

    .container {
        max-width: 1200px;
        margin: auto;
    }

    h2 {
        color: #004085;
        border-bottom: 2px solid #007bff;
        padding-bottom: 10px;
        margin-bottom: 20px;
    }

    form.search-form {
        display: flex;
        gap: 10px;
        margin-bottom: 20px;
    }

    form.search-form input[type=text] {
        flex: 1;
        padding: 10px;
        border: 1px solid #ced4da;
        border-radius: 6px;
        font-size: 1rem;
    }

    form.search-form input[type=submit] {
        padding: 10px 20px;
        border: none;
        background-color: #007bff;
        color: white;
        font-weight: bold;
        border-radius: 6px;
        cursor: pointer;
        transition: background 0.2s;
    }

    form.search-form input[type=submit]:hover {
        background-color: #0056b3;
    }

    .error-message {
        color: red;
        font-weight: bold;
        margin-bottom: 15px;
    }

    .patient-card {
        background-color: #ffffff;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        margin-bottom: 25px;
    }

    .patient-card h4 {
        color: #007bff;
        margin-top: 0;
    }

    .patient-info {
        margin-bottom: 10px;
        color: #495057;
    }

    .study-info {
        font-size: 0.9rem;
        color: #6c757d;
        margin-bottom: 15px;
    }

    .images-container {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
    }

    .images-container form {
        display: inline-block;
        cursor: pointer;
        border-radius: 6px;
        overflow: hidden;
        transition: transform 0.2s, box-shadow 0.2s;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .images-container form:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 12px rgba(0,0,0,0.15);
    }

    .images-container img {
        display: block;
        width: 280px;
        height: auto;
        border-bottom: 1px solid #dee2e6;
    }

    hr {
        border: none;
        border-top: 1px solid #dee2e6;
        margin: 25px 0;
    }
</style>
</head>
<body>
<div class="container">
    <h2>환자 검색</h2>

    <!-- 검색 폼 -->
    <form action="dashboard" method="post" class="search-form">
        <input type="text" name="name" placeholder="환자 이름 입력" required />
        <input type="submit" value="검색" />
    </form>

    <!-- 오류 메시지 -->
    <c:if test="${not empty error}">
        <div class="error-message">${error}</div>
    </c:if>

    <!-- 검색 결과 -->
    <c:if test="${not empty studyData}">
        <h3>검색 결과</h3>
        <c:forEach var="study" items="${studyData}">
            <div class="patient-card">
                <h4>환자: ${study.patient.patientName} (ID: ${study.patient.patientId})</h4>
                <div class="patient-info">
                    생년월일: ${study.patient.patientBirthDate} | 성별: ${study.patient.patientSex}
                </div>
                <div class="study-info">
                    Study ID: ${study.studyId} | Series ID: ${study.seriesId}
                </div>

                <!-- 이미지 출력 -->
                <div class="images-container">
                    <c:forEach var="img" items="${study.images}">
                        <c:set var="instanceId" value="${fn:split(img,'/')[3]}"/>
                        <form action="segmentation" method="post">
                            <input type="hidden" name="patientId" value="${study.patient.patientId}"/>
                            <input type="hidden" name="studyId" value="${study.studyId}"/>
                            <input type="hidden" name="seriesId" value="${study.seriesId}"/>
                            <input type="hidden" name="instanceId" value="${instanceId}"/>
                            <input type="image" src="${img}" alt="AI 분석"/>
                        </form>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>
</body>
</html>
