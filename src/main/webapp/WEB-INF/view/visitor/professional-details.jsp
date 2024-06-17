<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<head>
    <meta property="og:title" content="${professional.description}"/>
    <meta property="og:type" content="website"/>
    <meta property="og:description" content="SERVICEBOOK - O MELHOR PROFISSIONAL QUE VOCÊ PRECISA ESTÁ AQUI!"/>
    <meta property="og:image" content="${professional.profilePicture}"/>
    <meta property="og:site_name" content="Servicebook"/>

    <meta name="twitter:card" content="photo">
    <meta name="twitter:url" content="$(location).attr('href')">
    <meta name="twitter:title" content="${professional.description}">
    <meta name="twitter:description" content="SERVICEBOOK - O MELHOR PROFISSIONAL QUE VOCÊ PRECISA ESTÁ AQUI!">
    <meta name="twitter:image " content="${professional.profilePicture}">
    <!-- Funciona apenas com caminho absoluto porque é renderizado antes da tag base -->
    <link href="${pageContext.request.contextPath}/assets/resources/styles/visitor/visitor.css" rel="stylesheet">
</head>

<t:template title="Servicebook - Início">
    <jsp:body>
        <div id="" class="blue lighten-1">
            <div class="section no-pad-bot">
                <div class="container">
                    <div class="row center-align">
                        <c:choose>
                            <c:when test="${professional.profilePicture != null}">
                                <img src="${professional.profilePicture}" class="avatar" alt="Foto de perfil">
                            </c:when>
                            <c:otherwise>
                                <img src="assets/resources/images/no-photo.png" class="avatar" alt="Sem foto de perfil">
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <sec:authorize access="isAuthenticated()">
                <div class="col s12 center">
                    <c:if test="${!isFollow}">
                        <form method="post" id="follow-form">
                            <input type="hidden" name="professional" value="${professional.id}"/>
                            <input type="hidden" name="client" value="${client.id}"/>
                            <button alt="seguir" type="button"
                                    class="waves-effect waves-light btn" id="follow-button">Seguir
                            </button>
                        </form>
                    </c:if>
                    <c:if test="${isFollow}">
                        <button type="button" data-professional="${professional.id}"
                                class="waves-effect waves-light btn"
                                id="unfollow-button">Deixar de Seguir
                        </button>
                    </c:if>
                </div>
            </sec:authorize>

            <div class="tertiary-background-color white-text center-align">
                <h5 class="upper-case mb-1 mt-1">${professional.name}</h5>
            </div>
        </div>

        <div class="container">
            <div class="section">

                <div class="row">
                    <div class="col s12 m6">
                        <p class="text-area-info-cli  left">
                            <i class="small material-icons dark-color-text">mail</i>
                            <span class="phone-text">
                                    ${professional.email}
                            </span>
                        </p>
                    </div>

                    <div class="col s12 m6">
                        <p class="text-area-info-cli  right">
                            <i class="small material-icons dark-color-text">phone</i>
                            <span class="phone-text">
                                    ${professional.phoneNumber}
                            </span>
                        </p>
                    </div>

                    <div class="col s12">
                        <p class="text-area-info-cli"><i
                                class="small material-icons dark-color-text">location_on</i> ${professional.address.street}, ${professional.address.neighborhood}, ${professional.address.number}
                        </p>
                    </div>

                </div>

                <hr>

                <div class="row">
                    <div class="col s12">
                        <h5><strong>Descrição geral</strong></h5>
                        <p class="dark-color-text">${professional.description}</p>
                    </div>
                </div>

                <hr>


                <div class="row">
                    <h5><strong>${service.expertise.name}</strong></h5>
                    <div class="col s12">
                        <p class="dark-color-text">${service.expertise.description}</p>
                    </div>
                </div>

                <hr>

                <div class="row">
                    <h5><strong>${service.name}</strong></h5>
                    <div class="col s12">
                        <p class="dark-color-text">${service.description}</p>
                    </div>
                </div>

                <hr>
                <c:forEach var="entry" items="${servicesByExpertise.entrySet()}">
                    <div class="row">
                        <h5><strong>Outros serviços: </strong></h5>


                        <div class="col s12">
                            <c:forEach var="service" items="${entry.value}">
                                <div class="col s12 m6">
                                    <t:service-card edit="false" serviceOffering="${service}"/>
                                </div>
                            </c:forEach>
                        </div>

                    </div>
                </c:forEach>

                <c:if test="${not empty oProfessionalServiceOffering}">
                    <div class="row center">
                        <div class="col m4"></div>
                        <div class="col s12 m4 l4">
                            <div class="card">
                                <div class="card-title">
                                    <p class="label_especialidade">${oProfessionalServiceOffering.service.expertise.name}</p>
                                    <p class="label_duration"> ${oProfessionalServiceOffering.service.name} </p>
                                </div>
                                <div class="card-image">
                                    <c:choose>
                                        <c:when test="${oProfessionalServiceOffering.user.profilePicture != null}">
                                            <img src="${oProfessionalServiceOffering.user.profilePicture}"
                                                 class="image-professional"
                                                 alt="Foto de perfil">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="assets/resources/images/no-photo.png" class=""
                                                 alt="Sem foto de perfil">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="card-content center">
                                    <p><strong> ${oProfessionalServiceOffering.user.name} </strong></p>
                                    <span class="description-job-card"> ${oProfessionalServiceOffering.description} </span>
                                </div>

                                <div class="card-footer">
                                    <p class="label_price"><fmt:formatNumber
                                            value="${oProfessionalServiceOffering.price}" type="currency"/></p>
                                    <p class="label_especialidade">${oProfessionalServiceOffering.duration}</p>
                                </div>
                            </div>

                        </div>
                    </div>
                </c:if>

                <div class="row center-align" style="margin-top: 50px">
                    <div class="col s12 m6">
                        <a href="https://web.whatsapp.com/send?phone=55${professional.getOnlyNumbersFromPhone()}"
                           target="_blank">
                            <div class="row waves-effect waves-light btn-large green accent-3 white-text">
                                <div class="col s2">
                                    <img src="assets/resources/images/whatsapp-logo.png" class="whatsapp-icon mt-1"
                                         alt="">
                                </div>
                                <div class="col s10"><strong>Chamar no whatsapp</strong></div>
                            </div>
                        </a>
                    </div>

                    <div class="s12 m6">
                        <form action="minha-conta/cliente/contratar-servico/profissional/${professional.id}"
                              method="POST">
                            <input type="hidden" value="${service.expertise.id}" name="expertiseId">
                            <input type="hidden" value="${oProfessionalServiceOffering.id}"
                                   name="professionalServiceOfferingId">

                            <button type="submit"
                                    class="btn waves-effect waves-light btn-large blue accent-3 white-text">
                                <strong>Contratar serviço do profissional </strong>
                            </button>
                        </form>
                    </div>

                </div>

                <c:if test="${not empty professionalServiceOfferings}">
                    <div class="row">
                        <div class="col s12">
                            <p class="text-area-info-cli primary-color-text">
                                <i class="material-icons small"> next_week </i>
                                <span class="phone-text title-section">
                                            Outros Serviços
                                        </span>
                            </p>
                            <hr>
                        </div>
                        <c:forEach var="service" items="${professionalServiceOfferings}">
                            <a href="profissionais/detalhes/${service.id}/profissional/${service.user.id}/servico/${service.service.id}"
                               class="card-link">
                                <div class="col s12 m4 l4">
                                    <div class="card">
                                        <div class="card-title">
                                            <p class="label_especialidade">${service.service.expertise.name}</p>
                                            <p class="label_duration"> ${service.service.name} </p>
                                        </div>
                                        <div class="card-image">
                                            <c:choose>
                                                <c:when test="${service.user.profilePicture != null}">
                                                    <img src="${service.user.profilePicture}"
                                                         class="image-professional"
                                                         alt="Foto de perfil">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="assets/resources/images/no-photo.png" class=""
                                                         alt="Sem foto de perfil">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="card-content center">
                                            <p><strong> ${service.user.name} </strong></p>
                                            <span class="description-job-card"> ${service.description} </span>
                                        </div>

                                        <div class="card-footer">
                                            <p class="label_price"><fmt:formatNumber
                                                    value="${service.price}" type="currency"/></p>
                                            <p class="label_especialidade">${service.duration}</p>
                                        </div>
                                    </div>

                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
                <br>

                <c:if test="${not empty assessment}">
                    <div class="row area-assesment">
                        <div class="col s12">
                            <p class="text-area-info-cli primary-color-text">
                                <i class="material-icons small"> comment </i>
                                <span class="phone-text title-section">
                                            Avaliações
                                        </span>
                            </p>
                            <hr>
                        </div>
                        <c:forEach var="assessment" items="${assessment}">
                            <div class="col s12">
                                <div class="profile">
                                    <c:choose>
                                        <c:when test="${assessment.client.profilePicture != null}">
                                            <img src="${assessment.client.profilePicture}" class="avatar"
                                                 alt="${assessment.client.name}" style="width: 100px; height: 5rem">
                                        </c:when>
                                        <c:otherwise>
                                            <svg style="width:100px;height:5rem" viewBox="0 0 24 24"
                                                 class="profile-photo">
                                                <path class="dark-color-icon"
                                                      d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z"/>
                                            </svg>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="profile-info">
                                        <p class="profile-name">${assessment.client.name}</p>
                                        <div class="profile-rating">
                                            <c:forEach var="star" begin="1" end="5">
                                                <c:if test="${star <= (assessment.quality)}">
                                                    <i class="material-icons yellow-text small">star</i>
                                                </c:if>
                                                <c:if test="${star > (assessment.quality)}">
                                                    <i class="material-icons dark-text small">star_border</i>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <small class="profile-date">${assessment.date}</small>
                                    </div>
                                </div>

                                <p>
                                    <span> ${assessment.comment} </span>
                                </p>
                                <div>
                                    <c:if test="${not empty assessment.assessmentProfessionalFiles}">
                                        <img src="${assessment.assessmentProfessionalFiles.image}" class="avatar"
                                             alt="avaliacao" style="width: 100px; height: 5rem">
                                    </c:if>
                                </div>

                                <div class="row div-evaluates">
                                    <div class="col 12">
                                        <p class="response-evaluation">Resposta:</p>
                                        <small>
                                                ${assessment.assessmentResponses.response}
                                        </small>
                                    </div>
                                </div>
                                <hr>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
                <sec:authorize access="!isAuthenticated()">
                    <div class="divider"></div>

                    <div class="row center-align">
                        <h4 class="center-align">ACESSE O <span class="facebook-color-text">SERVICEBOOK</span></h4>
                        PARA TER MAIORES INFORMAÇÕES ANTES DE ENTRAR EM CONTATO COM O PROFISSIONAL OU EMPRESA
                        </br>
                        <p class="primary-color-text">
                            HISTÓRICO DE SERVIÇOS REALIZADOS E AVALIAÇÕES COM COMENTÁRIOS FEITAS POR
                            CLIENTES REAIS
                        </p>
                        OU
                        </br>
                        PRINCIPALMENTE PARA SIMPLIFICAR A TAREFA DE ENCONTRAR O MELHOR PROFISSIONAL OU EMPRESA PARA
                        REALIZAR O SEU SERVICO.
                        </br>
                        DE FORMA GRATUITA!
                        </br>
                        DESCREVA O SERVIÇO A SER REALIZADO E RECEBA AS INFORMAÇÕES DE CONTATO APENAS DE PROFISSIONAIS OU
                        EMPRESAS COM
                        </br>
                        <p class="primary-color-text">
                            INTERESSE NO SERVIÇO, DISPONÍVEL PARA A DATA ESPECIFICADA E COM EXPERIÊNCIA NO TIPO DE
                            SERVIÇO QUE VOCÊ PRECISA!
                        </p>

                        <a class="waves-effect waves-light btn" href="login">Entrar</a>
                    </div>

                    <div class="divider"></div>

                    <div class="row center-align">
                        <p>VOCÊ AINDA NÃO TEM UMA CONTA?</p>
                        <a class="waves-effect waves-light btn" href="cadastrar-se">Cadastrar-se</a>
                    </div>
                </sec:authorize>
            </div>
        </div>

    </jsp:body>
</t:template>
<script type="text/javascript"
        src="https://platform-api.sharethis.com/js/sharethis.js#property=64931a737674a9001261149d&product=sticky-share-buttons&source=platform"
        async="async"></script>
<script src="assets/resources/scripts/follow-professional.js"></script>
