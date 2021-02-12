<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Meal list</title>
</head>
<body>
    <h3><a href="index.html">Home</a></h3>
<hr>

    <h2>Meals</h2>
    <h3><span style="font-size: 75%"><a href="edit_meal">Add meal</a></span></h3>
    <table border="1" cellspacing="0" align="center" cellpadding="10">
        <tr style="height:60px">
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
            <%
                List<MealTo> list = (List<MealTo>) request.getAttribute("mealsTo");
                if ( list != null && !list.isEmpty() )  {
                    for (MealTo meal : list)    {
                        StringBuilder colorTag = new StringBuilder("<font color=\"");
                        colorTag.append(meal.isExcess() ? "red\">" : "green\">");
                        out.print("<tr align=\"center\">");
                        out.print("<td>" + colorTag.toString() + meal.getDateTime().toString().replace('T', ' ') + "</font></td>");
                        out.print("<td>" + colorTag.toString() + meal.getDescription() + "</font></td>");
                        out.print("<td>" + colorTag.toString() + meal.getCalories() + "</font></td>");
                        out.print("<td><h3><a href=\"\">Update</a></h3></td>");
                        out.print("<td><h3><a href=\"\">Delete</a></h3></td>");
                        out.println("</tr>");
                    }
                }
            %>
    </table>
</body>
</html>
