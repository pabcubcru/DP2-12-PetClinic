<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="shops">
    <h2>
        <c:if test="${discount['new']}">New </c:if> Discount
    </h2>
     <table class="table table-striped">
   		<tr>
            <th>Name</th>
            <td><c:out value="${product.name}"/></td>
        </tr>
        <tr>
            <th>Price</th>
            <td><c:out value="${product.price}"/></td>
        </tr>
        <tr>
            <th>Stock</th>
            <td><c:out value="${product.stock}"/></td>
        </tr>
    </table>
    <br>
    <form:form modelAttribute="discount" class="form-horizontal" id="add-discount-form">
        <div class="form-group has-feedback">
       		<petclinic:inputField label="Percentage" name="percentage"/>
            <petclinic:inputField label="Start Date" name="startDate"/>
            <petclinic:inputField label="Finish Date" name="finishDate"/>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
               <button class="btn btn-default" type="submit">Add Discount</button>
            </div>
        </div>
    </form:form>
</petclinic:layout>