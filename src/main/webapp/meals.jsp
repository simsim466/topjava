<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="java.util.List" %>
<%@ page import="jdk.nashorn.internal.ir.debug.JSONWriter" %><%--
  Created by IntelliJ IDEA.
  User: simonov
  Date: 07.02.2021
  Time: 14:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
    <h3><a href="index.html">Home</a></h3>
<hr>

    <h2>Meals</h2>
    <table border= "1" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
            <%
                List<MealTo> list = (List<MealTo>) request.getAttribute("mealsTo");
                if ( list != null && !list.isEmpty() )  {
                    for (MealTo meal : list)    {
                        out.print("<tr>");
                        out.print("<td>" + meal.getDateTime().toString().replace('T', ' ') + "</td>");
                        out.print("<td>" + meal.getDescription() + "</td>");
                        out.print("<td>" + meal.getCalories() + "</td>");
                        out.print("<td><h3><a href=\"index.html\">Update</a></h3></td>");
                        out.println("</tr>");
                    }
                }
            %>
    </table>
</body>
</html>
