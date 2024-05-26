<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <!-- Funciona apenas com caminho absoluto porque é renderizado antes da tag base -->
    <link href="${pageContext.request.contextPath}/assets/resources/styles/professional/professional.css" rel="stylesheet">
</head>

<t:template title="Meu anúncio">
    <jsp:body>

        <main>
            <div class="row">
                <div class="container">
                    <div class="row">
                        <div class="col s12 l12 breadcrumbs" style="margin-top: 20px">
                            <a href="${pageContext.request.contextPath}/">Início</a> &gt;
                            <a href="${pageContext.request.contextPath}/minha-conta/perfil">Meu Perfil</a> &gt;
                            Meu Anúncio
                        </div>
                        <div class="col s12 l12">
                            <h3>Nos conte sobre o seu perfil profissional</h3>
                            <h6>Descreva suas experiências, sua forma de trabalho e especialidades. Enfim, este é o seu espaço para o marketing profissional.</h6>
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${user.description == ''}">
                            <div class="row">
                                <form class="col s12" action="${pageContext.request.contextPath}/minha-conta/cadastra-descricao/${user.id}" method="post">
                                    <input type="hidden" name="_method" value="PATCH"/>
                                    <div class="row">
                                        <div class="input-field col s12">
                                            <textarea name="description" cols="10" rows="5" class="browser-default"></textarea>
                                        </div>
                                        <button class="waves-effect waves-light btn right btn-save">salvar</button>
                                    </div>
                                </form>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row">
                                <div class="texto">
                                    <a href="#" class="waves-effect waves-light btn exibir">Editar</a>
                                    <div class="exibir">
                                        <p>${user.description}</p>
                                    </div>
                                </div>
                                <div class="formulario">
                                    <form class="col s12" action="${pageContext.request.contextPath}/minha-conta/cadastra-descricao/${user.id}" method="post">
                                        <input type="hidden" name="_method" value="PATCH"/>
                                        <div class="row">
                                            <div class="input-field col s12">
                                                <textarea name="description" cols="10" rows="5" class="browser-default">${user.description}</textarea>
                                            </div>
                                            <button class="waves-effect waves-light btn">salvar</button>
                                            <a href="#" id="ocultar">x</a>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </main>

    </jsp:body>
</t:template>
<script>
    $(document).ready(function(){
        $(".exibir").click(function (e) {
            $(".texto").hide();
            $(".formulario").show();
            e.preventDefault();
        });
        $("#ocultar").click(function (e) {
            $(".formulario").hide();
            $(".texto").show();
            e.preventDefault();
        });
    });
</script>
