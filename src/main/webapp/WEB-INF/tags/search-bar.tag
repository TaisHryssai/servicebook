<%@tag description="Servicebook - Search bar template" pageEncoding="UTF-8" %>
<%@attribute name="items" type="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="${pageContext.request.contextPath}/assets/resources/styles/visitor/visitor.css" rel="stylesheet">

<script
        src="https://code.jquery.com/jquery-3.7.0.min.js"
        integrity="sha256-2Pmvv0kuTBOenSvLm6bvfBSSHrUJ+3A7x6P5Ebd07/g="
        crossorigin="anonymous"></script>

<div>
    <div class="container center-align hide_simple">
        <div class="row">
            <div class="col s12">
                <h4 style="font-weight: bold ">O QUE VOCÊ PRECISA?</h4>
                <h6>Nos informe de qual serviço você precisa e escolha o melhor profissional!</h6>
            </div>
        </div>
        <c:if test="${not empty msg}">
            <div class="row">
                <div class="col s12">
                    <div class="card-panel green lighten-1 msg-view center-align">
                        <span class="white-text">${msg}</span>
                    </div>
                </div>
            </div>
        </c:if>

        <div class="row">
            <div class="col s12">
                <form action="profissionais/busca" method="get">
                    <div class="row">
                        <div class="col s12 input-field select-visitor">
                            <label for="category-select">Selecione uma categoria</label>
                            <select name="categoryId" id="category-select">
                                <option disabled selected>Selecione uma categoria</option>
                                <c:forEach var="category" items="${items}">
                                    <option class="option-select-visitor" value="${category.id}"
                                            <c:if test="${category.id == dto.id}">
                                                selected
                                            </c:if>>${category.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col s12 input-field select-visitor">
                            <label for="expertise-select">Selecione uma especialidade</label>
                            <select name="expertiseId" id="expertise-select" disabled>
                                <option disabled selected value="null">Selecione uma especialidade</option>
                            </select>
                            <input type="hidden" value="${dto_expertise.id}" id="dto_expertise">
                        </div>

                        <div class="col s12 input-field select-visitor">
                            <label for="expertise-select">Selecione um serviço</label>
                            <select name="serviceId" id="service-select" disabled required>
                                <option disabled selected>Selecione um serviço</option>
                            </select>
                            <input type="hidden" value="${dto_service.id}" id="dto_service">
                        </div>
                        <c:if test="${not empty dto_service.id}">
                            <div class="col s12 input-field right div-buttons" style="text-align: right">
                                <a href="/servicebook/requisicoes?passo=2&especialidade=4" class="waves-effect waves-teal btn-flat"> Criar Anúncio </a>
                                <button class="waves-effect waves-light btn-small">
                                    <i class="material-icons left">search</i>Buscar
                                </button>
                            </div>
                        </c:if>

                        <c:if test="${empty dto_service.id}">
                            <div class="col s12 input-field right div-buttons" style="text-align: right">
                                <a href="/servicebook/requisicoes?passo=2&especialidade=4" class="waves-effect waves-teal btn-flat"
                                   id="add_ads"> Criar Anúncio </a>
                                <button class="waves-effect waves-light btn-small" id="hide_simple_button">
                                    <i class="material-icons left">search</i>Buscar
                                </button>
                            </div>
                        </c:if>
                    </div>

                    <c:if test="${not empty dto_service.id}">
                        <div class="col s12 card center">
                            <p>
                                Crie um anúncio da sua solicitação para facilitar a comunicação com os profissionais.
                            </p>
                            <p>
                                Você descreve apenas uma vez e compartilha com os profissionais.
                            </p>
                            <a href="/servicebook/requisicoes?passo=2&especialidade=4" class="waves-effect waves-light btn-small"
                               style="margin-bottom: 15px;"> Criar Anúncio </a>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>

    </div>
</div>
<script src="assets/resources/scripts/search.js"></script>
