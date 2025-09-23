<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html>
<head>
  <title>Admin Excel Upload</title>
</head>
<body>
  <h2>Upload Employee Excel (.xlsx)</h2>
  <form method="post" action="${pageContext.request.contextPath}/upload" enctype="multipart/form-data">
    <label for="file">Excel file (.xlsx)</label><br/>
    <input type="file" name="file" id="file" accept=".xlsx" required/><br/><br/>
    <button type="submit">Upload and Process</button>
  </form>
  <hr/>
  <div id="result">
    <c:if test="${not empty requestScope.result}">
      <h3>Processing Summary</h3>
      <p><b>Inserted:</b> ${requestScope.result.inserted}</p>
      <p><b>Failed rows:</b> ${fn:length(requestScope.result.errors)}</p>
      <c:if test="${not empty requestScope.result.errors}">
        <h4>Invalid Rows Details</h4>
        <ul>
          <c:forEach var="e" items="${requestScope.result.errors}">
            <li>${e}</li>
          </c:forEach>
        </ul>
      </c:if>
    </c:if>
  </div>
</body>
</html>
