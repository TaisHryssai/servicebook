<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<head>
    <!-- Funciona apenas com caminho absoluto porque é renderizado antes da tag base -->
    <link href="${pageContext.request.contextPath}/assets/resources/styles/visitor/visitor.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/resources/styles/professional/professional.css"
          rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/resources/styles/professional/ads.css"
          rel="stylesheet">
</head>

<t:template title="Servicebook - Início">
    <jsp:body>
        <div class="row">
            <div class="col s12" id="painel">
                <div class="city-name">
                    <div class="container cityNameContent">
                        <span id="select-city-name" class="chip"></span>
                        <a id="toggle-city-id">ALTERAR A CIDADE</a>
                    </div>
                </div>
            </div>
        </div>

        <div id="city-panel">
            <t:banner cities="${cities}"></t:banner>
        </div>
        <t:search-bar items="${categoryDTOs}"></t:search-bar>
        <div class="container">
            <div class="section">
                <div class="row">
                    <div class="col s12" style="margin-top: 30px">
                        <span class="left-align"
                              style="font-weight: bold; font-size: 1.5rem">ENCONTRADO ${count_results} RESULTADO(S)</span>
                        <hr>
                    </div>

                    <c:forEach var="professional" items="${professionalServiceOfferingDTOS}">
                        <a href="profissionais/detalhes/${professional.id}/profissional/${professional.user.id}/servico/${professional.service.id}" class="card-link">
                            <div class="col s12 m6 l4">
                                <div class="card">
                                    <div class="card-title">
                                        <p class="label_especialidade">${professional.service.expertise.name}</p>
                                        <p class="label_duration"> ${professional.service.name} </p>
                                    </div>
                                    <div class="card-image">
                                        <c:choose>
                                            <c:when test="${professional.user.profilePicture != null}">
                                                <img src="${professional.user.profilePicture}" class="image-professional"
                                                     alt="Foto de perfil">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="assets/resources/images/no-photo.png" class=""
                                                     alt="Sem foto de perfil">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-content center">
                                        <p> <strong> ${professional.user.name} </strong> </p>
                                        <span class="description-job-card"> ${professional.description} </span>
                                    </div>

                                    <div class="card-footer">
                                        <p class="label_price"> <fmt:formatNumber value="${professional.price}"   type = "currency"/> </p>
                                        <p class="label_especialidade">${professional.duration}</p>
                                    </div>
                                </div>

                            </div>
                        </a>
                    </c:forEach>

<%--                    <div class="col s12">--%>
<%--                        <div class="row">--%>
<%--                            <c:if test="${not empty professionalServiceOfferingDTOS}">--%>
<%--                            <c:forEach var="professional" items="${professionalServiceOfferingDTOS}">--%>
<%--                            <div class="col s4 div_cards_services">--%>
<%--                                <div class="card" style="margin: 0">--%>
<%--                                    <div class="label_duration">--%>
<%--                                        <span class=" white-text">${professional.service.expertise.name}</span>--%>
<%--                                    </div>--%>
<%--                                    <div class="card-content">--%>
<%--                                        <div class="row center-align">--%>
<%--                                            <c:choose>--%>
<%--                                                <c:when test="${professional.user.profilePicture != null}">--%>
<%--                                                    <img src="${professional.user.profilePicture}" class="avatar"--%>
<%--                                                         alt="Foto de perfil">--%>
<%--                                                </c:when>--%>
<%--                                                <c:otherwise>--%>
<%--                                                    <img src="assets/resources/images/no-photo.png" class="avatar"--%>
<%--                                                         alt="Sem foto de perfil">--%>
<%--                                                </c:otherwise>--%>
<%--                                            </c:choose>--%>
<%--                                            <div class="col s12">--%>
<%--                                                <h6 class="truncate">${professional.user.name}</h6>--%>
<%--                                                <small> ${professional.description} </small>--%>
<%--                                            </div>--%>
<%--                                            <div class="col s12 button-profile">--%>
<%--                                                <a href="profissionais/detalhes/${professional.user.id}"--%>
<%--                                                   class="waves-effect waves-light btn-small white-text text-lighten-1"><strong>Ver--%>
<%--                                                    perfil</strong></a>--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                    <div class="label_price">--%>
<%--                                        <p class=" white-text" style="margin: 0"><fmt:formatNumber--%>
<%--                                                value="${professional.price}" type="currency"/></p>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                        </c:forEach>--%>
<%--                        </c:if>--%>
<%--                        <c:if test="${empty professionalServiceOfferingDTOS}">--%>
<%--                            <div class="container">--%>
<%--                                <div class="row">--%>
<%--                                    <div class="col s12 spacing-buttons     ">--%>
<%--                                        <div class="none-profission center">--%>
<%--                                            <i class="material-icons large"> sentiment_dissatisfied </i>--%>
<%--                                            <p class="center text-form-dados">--%>
<%--                                                Nenhum profissional encontrado!--%>
<%--                                            </p>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </c:if>--%>
<%--                    </div>--%>

                    <c:if test="${empty professionalServiceOfferingDTOS}">
                        <div class="container">
                            <div class="row">
                                <div class="col s12 spacing-buttons     ">
                                    <div class="none-profission center">
                                        <i class="material-icons large"> sentiment_dissatisfied </i>
                                        <p class="center text-form-dados">
                                            Nenhum profissional encontrado!
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${logged == false}">
                        <div class="container col s12 center-align">
                            <p class="center-align">Entre ou cadastre-se para ter acesso ao demais profissionais</p>
                        </div>
                    </c:if>
                    <c:if test="${logged == true}">
                        <div class="container col s12 center-align">
                            <t:pagination pagination="${pagination}" isParam="${isParam}"></t:pagination>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <script>
            $(document).ready(function () {
                $('.tooltipped').tooltip();
                // $('.hide_descriptive').hide();
                //
                // $("#hide_simple_button").click(function(){
                //     $('.hide_descriptive').show();
                //     $('.hide_simple').hide();
                // });
            });
        </script>

    </jsp:body>
</t:template>

