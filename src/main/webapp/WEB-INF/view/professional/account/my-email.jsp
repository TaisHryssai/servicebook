<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:template title="Meu email">
    <jsp:body>

        <main>
            <div class="container">
                <div class="row">
                    <div class="col s12 l6 offset-l3 spacing-buttons">
                        <c:if test="${not empty msg}">
                            <div class="card-panel green lighten-1 msg-view center-align">
                                <span class="white-text">${msg}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty msgError}">
                            <div class="card-panel red msg-view center-align">
                                <span class="white-text">${msgError}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty errors}">
                            <div class="card-panel red msg-view center-align">
                                <c:forEach var="e" items="${errors}">
                                    <span class="white-text">${e.getDefaultMessage()}</span><br>
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                </div>
                <div class="row">
                    <div class="col s12 l6 offset-l3">
                        <h3 class="secondary-color-text">Meu email</h3>
                    </div>
                    <form id="email-form" class="col s12 l6 offset-l3" action="minha-conta/edita-email/${user.id}" method="post">
                        <input type="hidden" name="_method" value="PATCH"/>
                        <input type="hidden" name="id" value="${user.id}"/>

                        <div class="input-field">
                            <input id="email" name="email" type="email" value="${user.email}"
                                   class="validate" required disabled>
                            <label for="email">Email</label>
                        </div>
                        <div class="right">
                            <button id="edit-button" class="waves-effect waves-light btn" type="button">Editar</button>
                            <button id="save-button" class="waves-effect waves-light btn" type="submit" style="display: none">Salvar</button>
                        </div>
                    </form>
                </div>
            </div>

            <c:if test="${!user.emailVerified}">
                <div class="row">
                    <h3 class="center secondary-color-text">
                        Vamos validar o seu email?
                    </h3>
                    <h5 class="center secondary-color-text">
                        Enviamos um código para o seu email. Por favor, digite o código para validar este endereço
                        de email.
                    </h5>

                    <form method="post" action="minha-conta/valida-email/${user.id}">
                        <div class="row">
                            <div class="input-field col s10 m8 l6 xl6 offset-s1 offset-m2 offset-l3 offset-xl3">
                                <input id="code" name="code" type="text" class="validate" required>
                                <label for="code">Código</label>
                            </div>
                        </div>
                        <div class="row col s12">
                            <div class="sendEmail col s6 center">
                                <button class="waves-effect waves-light btn" type="button">
                                    Reenviar email
                                </button>
                            </div>
                            <div class="center col s6">
                                <button class="waves-effect waves-light btn" type="submit">Salvar</button>
                            </div>
                        </div>
                    </form>
                </div>
            </c:if>
        </main>

    </jsp:body>
</t:template>
<script>
    $(document).ready(function() {
        $('#edit-button').click(function(){
            console.log($(this))
            $('#email-form input').prop('disabled', false);
            $(this).hide();
            $('#save-button').show();
        });
    });

    $('.sendEmail').click(function () {
        $.post("minha-conta/salvar-email/${professional.id}", {email: "${professional.email}"}).done(function () {
            console.log('foi no evento');
            swal({
                title: "Deu certo!",
                text: "Foi enviado um email para ${professional.email}.",
                icon: "success",
            });
        });
    });
</script>
