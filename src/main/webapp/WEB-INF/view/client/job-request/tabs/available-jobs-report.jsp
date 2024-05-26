<%@page contentType="text/html" pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@taglib prefix="t"
tagdir="/WEB-INF/tags" %>

<c:if test="${empty jobs}">
  <div class="container">
    <div class="row">
      <div class="col s12 spacing-buttons">
        <div class="none-profission center">
          <i class="material-icons large"> sentiment_dissatisfied </i>
          <p class="center text-form-dados">Nenhuma solicitação encontrada!</p>
        </div>
      </div>
    </div>
  </div>
</c:if>

<c:forEach var="jobRequest" items="${jobs}">
  <a href="minha-conta/cliente/meus-pedidos/${jobRequest.id}" class="card-link">
    <div class="col s12 m6">
      <div class="card">
        <div class="card-title">
          <p class="label_especialidade center">${jobRequest.expertise.name}</p>
        </div>

        <div class="card-image">
          <c:if test="${empty jobRequest.jobImages}">
            <div class="black-text text-darken-2 col-image-job-request">
              <span class="icone-default"><i class="material-icons right">dashboard</i></span>
            </div>
          </c:if>

          <c:if test="${not empty jobRequest.jobImages}">
            <c:forEach var="jobImage" items="${jobRequest.jobImages}">
              <div class="carousel-item blue white-text col-image-job-request">
                <img src="${jobImage.path}" width="150px" height="150px"
                     alt="image_job">
              </div>
            </c:forEach>
          </c:if>
        </div>

        <div class="card-content center">
          <p class="description-job-card"> ${jobRequest.description} </p>
          <span class="blue-text"> <strong>  Solicitado em: ${jobRequest.dateCreated} </strong> </span>
          <span class="right grey-text text-darken-4"> ${jobRequest.textualDate} </span>
        </div>

        <div class="card-footer progress" style="position: relative; height: 30px;">
          <div class="determinate" style="width: ${jobRequest.totalCandidates / jobRequest.quantityCandidatorsMax * 100}%; height: 100%;">
          </div>
          <div class="progress-text" style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); white-space: nowrap;">
              ${jobRequest.totalCandidates} de ${jobRequest.quantityCandidatorsMax}
          </div>
        </div>

      </div>
    </div>
  </a>



<%--  <div class="col s6 m6  spacing-standard-request">--%>
<%--    <div class="card ticky-action card-request1">--%>
<%--      <div class="card-image waves-effect waves-block waves-light">--%>
<%--        <h5 class="left black-text text-darken-2 no-margin">--%>
<%--            ${jobRequest.expertise.name}--%>
<%--        </h5>--%>
<%--      </div>--%>

<%--      <div class="card-action row">--%>
<%--        <div class="col s12 m12 l12 card-image black-text text-darken-2 col-image-job-request">--%>
<%--          <span class="icone-default"><i class="material-icons right">dashboard</i></span>--%>
<%--        </div>--%>

<%--        <div class="col s12 blue-text text-darken-2 right">--%>
<%--          <span class="right" style="font-size: .85rem">--%>
<%--            <strong>  Solicitado: ${jobRequest.dateCreated} </strong>--%>
<%--          </span>--%>
<%--        </div>--%>

<%--        <div class="col s12 no-margin">--%>
<%--          <h5 class="card-title activator grey-text text-darken-4"> ${jobRequest.description} </h5>--%>
<%--          <p class="truncate grey-text text-darken-4">--%>
<%--             ${jobRequest.textualDate}--%>
<%--          </p>--%>
<%--        </div>--%>
<%--      </div>--%>

<%--      <div class="card-action card-progress no-margin">--%>
<%--        <div class="row">--%>
<%--          <div class="col s10 m10 l10 progress">--%>
<%--            <div class="determinate" style="width: ${jobRequest.totalCandidates / jobRequest.quantityCandidatorsMax * 100}%">--%>
<%--              <span class="progress-text"></span>--%>
<%--            </div>--%>
<%--          </div>--%>

<%--          <div class="col s2 qtd_candidates">${jobRequest.totalCandidates} de ${jobRequest.quantityCandidatorsMax}</div>--%>

<%--          <div class="col s12 m12 l12">--%>
<%--            <a class="waves-effect waves-light btn btn-view-request" href="minha-conta/cliente/meus-pedidos/${jobRequest.id}">Visualizar </a>--%>
<%--          </div>--%>
<%--        </div>--%>
<%--      </div>--%>
<%--    </div>--%>
<%--  </div>--%>
</c:forEach>

<div class="container col s12 center-align">
  <t:pagination-tab-ajax pagination="${pagination}"></t:pagination-tab-ajax>
</div>
