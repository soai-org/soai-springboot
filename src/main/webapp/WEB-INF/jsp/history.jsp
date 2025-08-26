<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>의사 상담 기록</title>
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background-color: #edf2f7;
            margin: 0;
            padding: 20px;
            display: flex;
            justify-content: center;
        }

        .history-container {
            width: 100%;
            max-width: 900px;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 6px 12px rgba(0,0,0,0.1);
            overflow: hidden;
            display: flex;
            flex-direction: column;
            height: 80vh;
        }

        .history-header {
            background-color: #0056b3;
            color: #ffffff;
            padding: 15px 20px;
            text-align: center;
            font-size: 1.5em;
            font-weight: bold;
            border-bottom: 2px solid #004080;
        }

        .history-body {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #f7f9fc;
        }

        .history-entry {
            margin-bottom: 20px;
            padding: 15px;
            border-radius: 8px;
            background-color: #ffffff;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
            border-left: 4px solid #0056b3;
        }

        .user-message {
            font-weight: bold;
            color: #007bff;
            margin-bottom: 5px;
        }

        .bot-message {
            color: #212529;
            margin-bottom: 8px;
        }

        .timestamp {
            font-size: 0.85em;
            color: #6c757d;
            text-align: right;
        }

        .no-history {
            text-align: center;
            color: #6c757d;
            font-style: italic;
            margin-top: 50px;
        }

        .back-link {
            text-align: center;
            padding: 15px;
            background-color: #f1f3f6;
            border-top: 1px solid #dee2e6;
        }

        .back-link a {
            color: #0056b3;
            text-decoration: none;
            font-weight: bold;
            padding: 8px 16px;
            border: 1px solid #0056b3;
            border-radius: 6px;
            transition: all 0.2s;
        }

        .back-link a:hover {
            background-color: #0056b3;
            color: #ffffff;
        }
    </style>
</head>
<body>
    <div class="history-container">
        <div class="history-header">
            의사 상담 기록
        </div>
        <div class="history-body">
            <c:forEach var="history" items="${historyList}">
                <div class="history-entry">
                    <div class="user-message">의사: ${history.userMessage}</div>
                    <div class="bot-message">AI: ${history.botResponse}</div>
                    <div class="timestamp">
                        <fmt:formatDate value="${history.createdAt}" pattern="yyyy-MM-dd HH:mm:ss" timeZone="Asia/Seoul"/>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty historyList}">
                <div class="no-history">현재 상담 기록이 없습니다.</div>
            </c:if>
        </div>
        <div class="back-link">
            <a href="/">대화 화면으로 돌아가기</a>
        </div>
    </div>
</body>
</html>
