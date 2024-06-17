<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<t:template title="Etapa 05">
    <jsp:body>

        <main>
            <div class="container">
                <c:if test="${not empty errors}">
                    <div class="card-panel red">
                        <c:forEach var="e" items="${errors}">
                            <span class="white-text">${e.getDefaultMessage()}</span><br>
                        </c:forEach>
                    </div>
                </c:if>

                <div class="section">
                    <div>
                        <h4 class="center"><strong>Estes ${professionalsAmount} profissionais tem a especialidade que
                            procura!</strong></h4>
                        <h5 class="center">Para ter acesso a lista completa de profissionais deste especialidade ou
                            melhor, para receber o contato de apenas profissionais interessados e disponíveis para a
                            data especificada, podendo você verificar a reputação dos mesmos e experiência, solicitar
                            orçamento e por fim, avaliar o serviço prestado.</h5>
                        <div class="container">
                            <div class="section">
                                <div class="row">
                                    <c:if test="${not empty professionals}">
                                        <c:forEach var="professional" items="${professionals}">

                                            <div class="col s12 m6 card-panel" style="padding: 0">
                                                <a href="profissionais/detalhes/${professional.id}">
                                                    <div>
                                                        <div class="card-title" style="margin: 0">
                                                            <p class="label_especialidade upper-case"
                                                               truncate>${professional.name}</p>
                                                        </div>
                                                        <div class="center-align div-image">
                                                            <c:choose>
                                                                <c:when test="${professional.profilePicture != null}">
                                                                    <img src="${professional.profilePicture}"
                                                                         class="avatar" alt="Foto de perfil">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="assets/resources/images/no-photo.png"
                                                                         class="avatar"
                                                                         alt="Sem foto de perfil">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <div class="divider"></div>
                                                        </div>
                                                        <div class="center-align">
                                                            <c:forEach var="expertise"
                                                                       items="${professional.expertises}">
                                                                <div class="col expertise-label">${expertise.name}</div>
                                                            </c:forEach>
                                                        </div>
                                                    </div>
                                                </a>
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${empty professionals}">
                                        <div class="container">
                                            <div class="row">
                                                <div class="col s12 spacing-buttons     ">
                                                    <div class="none-profission center">
                                                        <i class="material-icons large">
                                                            sentiment_dissatisfied </i>
                                                        <p class="center text-form-dados">
                                                            Nenhum profissional encontrado!
                                                        </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                    <sec:authorize access="!isAuthenticated()">
                        <div>
                            <div class="row center-align">
                                <p> LOGIN PARA EFETIVAR O SEU PEDIDO E RECEBER OS CONTATOS DOS PROFISSIONAIS!</p>
                                <a class="waves-effect waves-light btn" href="login">Entrar</a>
                            </div>

                            <div class="divider"></div>

                            <div class="row center-align">
                                <p>VOCÊ AINDA NÃO TEM UMA CONTA?</p>
                                <a class="waves-effect waves-light btn" href="cadastrar-se">Cadastrar-se</a>
                            </div>
                        </div>
                    </sec:authorize>
                </div>
            </div>
        </main>

    </jsp:body>
</t:template>
