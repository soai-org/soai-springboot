<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>의사 LLM AI</title>
<style>
    body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #edf2f7;
        margin: 0;
        padding: 20px;
        display: flex;
        justify-content: center;
    }

    .chat-container {
        width: 100%;
        max-width: 650px;
        background-color: #ffffff;
        border-radius: 10px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        display: flex;
        flex-direction: column;
        height: 85vh;
        overflow: hidden;
    }

    .chat-header {
        background-color: #0056b3;
        color: #ffffff;
        padding: 14px 20px;
        font-size: 1.3em;
        font-weight: bold;
        display: flex;
        justify-content: space-between;
        align-items: center;
        border-bottom: 2px solid #004080;
    }

    .header-link a {
        color: #ffffff;
        text-decoration: none;
        font-size: 0.9em;
        padding: 6px 10px;
        border-radius: 5px;
        background-color: #004080;
        transition: all 0.2s;
    }

    .header-link a:hover {
        background-color: #003060;
    }

    .chat-body {
        flex: 1;
        padding: 20px;
        overflow-y: auto;
        background-color: #f7f9fc;
    }

    .message {
        margin-bottom: 15px;
        padding: 12px 16px;
        border-radius: 8px;
        max-width: 75%;
        word-wrap: break-word;
        font-size: 0.95em;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }

    .user-message {
        background-color: #0056b3;
        color: #ffffff;
        margin-left: auto;
        text-align: right;
    }

    .bot-message {
        background-color: #e3e9f2;
        color: #212529;
        margin-right: auto;
    }

    .chat-footer {
        padding: 12px 16px;
        border-top: 1px solid #d0d7de;
        background-color: #f1f3f6;
        display: flex;
        flex-direction: column;
        gap: 10px;
    }

    .chat-form {
        display: flex;
        gap: 10px;
    }

    textarea {
        flex: 1;
        padding: 10px;
        border: 1px solid #cfd3d9;
        border-radius: 6px;
        resize: none;
        font-size: 0.95em;
        outline: none;
        background-color: #ffffff;
    }

    textarea:focus {
        border-color: #0056b3;
        box-shadow: 0 0 0 2px rgba(0,86,179,0.2);
    }

    input[type="submit"] {
        padding: 10px 22px;
        background-color: #0056b3;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.95em;
        font-weight: bold;
        transition: all 0.2s;
    }

    input[type="submit"]:hover {
        background-color: #003d80;
    }

    .history-link {
        text-align: center;
        font-size: 0.9em;
    }

    .history-link a {
        color: #0056b3;
        text-decoration: none;
    }

    .history-link a:hover {
        text-decoration: underline;
    }
</style>
</head>
<body>
    <div class="chat-container">
        <div class="chat-header">
            의사 LLM
            <div class="header-link">
                <a href="/history">상담 기록 보기</a>
            </div>
        </div>
        <div class="chat-body">
            <% if (request.getAttribute("message") != null) { %>
                <div class="message user-message">
                    ${message}
                </div>
            <% } %>
            <% if (request.getAttribute("response") != null) { %>
                <div class="message bot-message">
                    ${response}
                </div>
            <% } %>
        </div>
        <div class="chat-footer">
            <form method="post" action="${pageContext.request.contextPath}/chat" class="chat-form">
                <textarea id="message" name="message" rows="2" placeholder="메시지를 입력하세요...">${message}</textarea>
                <input type="submit" value="전송">
            </form>
            <div class="history-link">
                <a href="/history">상담 기록 보기</a>
            </div>
        </div>
    </div>
</body>
</html>
